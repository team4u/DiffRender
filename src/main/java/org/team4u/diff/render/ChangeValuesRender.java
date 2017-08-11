package org.team4u.diff.render;

import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.lang.Dict;
import com.xiaoleilu.hutool.lang.Validator;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.ReflectUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.team4u.diff.definiton.PropertyDefinition;
import org.team4u.kit.core.action.Callback;
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;
import org.team4u.kit.core.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay Wu
 */
public class ChangeValuesRender {

    public static Map<String, ?> renderToPathMap(Map<String, PropertyDefinition> definitions, Diff diff) {
        ChangeValues values = ChangeValuesRender.render(definitions, diff);
        return ChangeValuesRender.toPathMap(values);
    }

    public static ChangeValues render(Map<String, PropertyDefinition> definitions, Diff diff) {
        ChangeValues changeValues = new ChangeValues();
        changeValues.setNewValues(renderNewValues(definitions, diff.getChangesByType(NewObject.class)));
        changeValues.setChangeValues(renderChangeValues(definitions, diff.getChangesByType(ValueChange.class)));
        changeValues.setRemovedValues(renderRemovedValues(definitions, diff.getChangesByType(ObjectRemoved.class)));
        return changeValues;
    }

    public static List<ChangeValues.Value> renderNewValues(Map<String, PropertyDefinition> definitions,
                                                           List<NewObject> newObjects) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (int i = 0; i < newObjects.size(); i++) {
            NewObject newObject = newObjects.get(i);
            String id = newObject.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setNewValue(newObject.getAffectedObject().get());

            initValueWithPath(definitions, value, id);
            allValues.add(value);
        }

        AtomicInteger count = new AtomicInteger();
        return distinctValues(allValues, new Callback<ChangeValues.Value>() {
            @Override
            public void invoke(ChangeValues.Value value) {
                value.getPropertyIdFragments().add("+");
                value.getPropertyNameFragments().add("新增:" + count.getAndIncrement());
            }
        });
    }

    public static Map<String, ?> toPathMap(ChangeValues values) {
        List<ChangeValues.Value> allValues = new ArrayList<>();
        allValues.addAll(values.getChangeValues());
        allValues.addAll(values.getNewValues());
        allValues.addAll(values.getRemovedValues());

        Dict nameValues = new Dict();
        for (ChangeValues.Value value : allValues) {
            nameValues.set(StrUtil.join(",", value.getPropertyNameFragments()), value);
        }

        return MapUtil.toPathMap(nameValues, ',', MapUtil.PathMapBuilder.DEFAULT_LIST_SEPARATOR);
    }

    public static List<ChangeValues.Value> renderRemovedValues(Map<String, PropertyDefinition> definitions,
                                                               List<ObjectRemoved> objectRemoveds) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (ObjectRemoved objectRemoved : objectRemoveds) {
            String id = objectRemoved.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOldValue(objectRemoved.getAffectedObject().get());
            initValueWithPath(definitions, value, id);
            allValues.add(value);
        }

        AtomicInteger count = new AtomicInteger();

        return distinctValues(allValues, new Callback<ChangeValues.Value>() {
            @Override
            public void invoke(ChangeValues.Value value) {
                value.getPropertyIdFragments().add("-");
                value.getPropertyNameFragments().add("删除:" + count.getAndIncrement());
            }
        });
    }

    public static List<ChangeValues.Value> renderChangeValues(Map<String, PropertyDefinition> definitions,
                                                              List<ValueChange> valueChanges) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();

        for (ValueChange valueChange : valueChanges) {
            String id = valueChange.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOwner(valueChange.getAffectedObject().get())
                    .setNewValue(valueChange.getRight())
                    .setOldValue(valueChange.getLeft());

            PropertyDefinition lastPropertyDefinition = initValueWithPath(definitions, value, id);

            lastPropertyDefinition = findPropertyDefinition(definitions, lastPropertyDefinition, valueChange.getPropertyName());
            initValueProperty(value, lastPropertyDefinition, valueChange.getPropertyName());

            result.add(value);
        }

        return result;
    }

    private static List<ChangeValues.Value> distinctValues(List<ChangeValues.Value> allValues, Callback<ChangeValues.Value> callback) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();

        for (ChangeValues.Value value : allValues) {
            // 判断父类是否已移除
            boolean isParentValueRemoved = CollectionExUtil.any(allValues, new Function<ChangeValues.Value, Boolean>() {
                @Override
                public Boolean invoke(ChangeValues.Value v) {
                    return !value.getPropertyId().equals(v.getPropertyId()) &&
                            value.getPropertyId().startsWith(v.getPropertyId());
                }
            });

            if (isParentValueRemoved) {
                continue;
            }

            callback.invoke(value);
            result.add(value);
        }

        return result;
    }

    private static PropertyDefinition initValueWithPath(Map<String, PropertyDefinition> definitions,
                                                        ChangeValues.Value value,
                                                        String id) {
        List<String> paths = StrUtil.split(id, '/', true, true);
        PropertyDefinition lastPropertyDefinition = null;
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            if (i == 0) {
                // Root
                lastPropertyDefinition = definitions.get(path);
                initValueProperty(value, lastPropertyDefinition, path);
            } else if (path.startsWith("#")) {
                // 集合
                path = path.substring(1);
                lastPropertyDefinition = lastPropertyDefinition.find(path);
                initValueProperty(value, lastPropertyDefinition, path);
            } else if (Validator.isNumber(path)) {
                // 集合内某元素
                if (lastPropertyDefinition != null) {
                    lastPropertyDefinition = definitions.get(lastPropertyDefinition.getReferId());
                    if (value.getOwner() != null) {
                        initValueProperty(value, lastPropertyDefinition, path);
                        String name = CollectionExUtil.getLast(value.getPropertyNameFragments()) + ":" + i;
                        value.getPropertyNameFragments().set(value.getPropertyNameFragments().size() - 1, name);
                    }
                }
            } else {
                lastPropertyDefinition = findPropertyDefinition(definitions, lastPropertyDefinition, path);
                initValueProperty(value, lastPropertyDefinition, path);
            }
        }

        return lastPropertyDefinition;
    }

    private static void initValueProperty(ChangeValues.Value value,
                                          PropertyDefinition definition,
                                          String key) {
        value.getPropertyIdFragments().add(key);

        if (definition == null) {
            value.getPropertyNameFragments().add(null);
            return;
        }

        String name = definition.getName();
        if (definition.isClass() && !definition.getIdForPropertyNames().isEmpty()) {
            name = key;
        }

        if (!definition.isClass() && definition.getIdForPropertyNames().contains(definition.getId())) {
            name = Convert.toStr(ReflectUtil.getFieldValue(value.getOwner(), definition.getId()));
        }
        value.getPropertyNameFragments().add(name);

        if (definition.getFormatter() != null) {
            ValueFormatterRegistry.INSTANCE.renderWithContent(
                    Dict.create().set("value", value).set("def", definition),
                    definition.getFormatter()
            );
        }
    }

    private static PropertyDefinition findPropertyDefinition(Map<String, PropertyDefinition> definitions,
                                                             PropertyDefinition parentDefinition,
                                                             String key) {
        PropertyDefinition definition = definitions.get(key);
        if (definition != null) {
            return definition;
        }

        if (parentDefinition == null) {
            return null;
        }

        if (parentDefinition.getReferId() != null) {
            return definitions.get(parentDefinition.getReferId());
        }

        return parentDefinition.find(key);
    }
}
