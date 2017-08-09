package org.team4u.diff;

import com.xiaoleilu.hutool.lang.Dict;
import com.xiaoleilu.hutool.lang.Validator;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.team4u.diff.definiton.PropertyDefinition;
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class ChangeValuesRender {

    public static ChangeValues map(Map<String, PropertyDefinition> definitions, Diff diff) {
        ChangeValues changeValues = new ChangeValues();
        changeValues.setNewValues(renderNewValues(definitions, diff.getChangesByType(NewObject.class)));
        changeValues.setChangeValues(renderChangeValues(definitions, diff.getChangesByType(ValueChange.class)));
        changeValues.setRemovedValues(renderRemovedValues(definitions, diff.getChangesByType(ObjectRemoved.class)));
        return changeValues;
    }

    public static List<ChangeValues.Value> renderNewValues(Map<String, PropertyDefinition> definitions,
                                                           List<NewObject> newObjects) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();
        for (NewObject newObject : newObjects) {
            String id = newObject.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setNewValue(newObject.getAffectedObject().get());
            result.add(value);

            initValueWithPath(definitions, value, id);
        }

        return result;
    }

    public static List<ChangeValues.Value> renderRemovedValues(Map<String, PropertyDefinition> definitions,
                                                               List<ObjectRemoved> objectRemoveds) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (ObjectRemoved objectRemoved : objectRemoveds) {
            String id = objectRemoved.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOldValue(objectRemoved.getAffectedObject().get());
            allValues.add(value);

            initValueWithPath(definitions, value, id);
        }

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

            result.add(value);
        }


        return result;
    }

    public static List<ChangeValues.Value> renderChangeValues(Map<String, PropertyDefinition> definitions,
                                                              List<ValueChange> valueChanges) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();

        for (ValueChange valueChange : valueChanges) {
            String id = valueChange.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setNewValue(valueChange.getRight())
                    .setOldValue(valueChange.getLeft());
            result.add(value);

            PropertyDefinition lastPropertyDefinition = initValueWithPath(definitions, value, id);

            lastPropertyDefinition = findPropertyDefinition(definitions, lastPropertyDefinition, valueChange.getPropertyName());
            initValueProperty(value, lastPropertyDefinition, valueChange.getPropertyName());
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
