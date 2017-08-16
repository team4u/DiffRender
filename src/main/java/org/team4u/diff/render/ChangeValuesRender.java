package org.team4u.diff.render;

import com.xiaoleilu.hutool.lang.Dict;
import com.xiaoleilu.hutool.lang.Validator;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.team4u.diff.definiton.DefinitionModel;
import org.team4u.kit.core.action.Callback;
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;
import org.team4u.kit.core.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class ChangeValuesRender {

    private Map<String, DefinitionModel> definitions;

    private Object oldBean;
    private Object newBean;

    public ChangeValuesRender(Map<String, DefinitionModel> definitions, Object oldBean, Object newBean) {
        this.definitions = definitions;
        this.oldBean = oldBean;
        this.newBean = newBean;
    }

    public Map<String, ?> renderToPathMap() {
        ChangeValues values = render();
        return toPathMap(values);
    }

    public ChangeValues render() {
        Javers javers = JaversBuilder.javers().build();
        Diff diff = javers.compare(oldBean, newBean);

        ChangeValues changeValues = new ChangeValues();
        changeValues.setNewValues(renderNewValues(diff.getChangesByType(NewObject.class)));
        changeValues.setChangeValues(renderChangeValues(diff.getChangesByType(ValueChange.class)));
        changeValues.setRemovedValues(renderRemovedValues(diff.getChangesByType(ObjectRemoved.class)));
        return changeValues;
    }

    public List<ChangeValues.Value> renderNewValues(List<NewObject> newObjects) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (int i = 0; i < newObjects.size(); i++) {
            NewObject newObject = newObjects.get(i);
            String id = newObject.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setNewValue(newObject.getAffectedObject().get());

            initValueWithPath(value, id, MODE.CREATED);
            allValues.add(value);
        }

        return distinctValues(allValues, new Callback<ChangeValues.Value>() {
            @Override
            public void invoke(ChangeValues.Value value) {
            }
        });
    }

    public Map<String, ?> toPathMap(ChangeValues values) {
        List<ChangeValues.Value> allValues = new ArrayList<>();
        allValues.addAll(values.getChangeValues());
        allValues.addAll(values.getNewValues());
        allValues.addAll(values.getRemovedValues());

        Dict nameValues = new Dict();
        for (ChangeValues.Value value : allValues) {
            nameValues.set(StrUtil.join("|", value.getPropertyNameFragments()), value);
        }

        return MapUtil.toPathMap(nameValues, '|', MapUtil.PathMapBuilder.DEFAULT_LIST_SEPARATOR);
    }

    public List<ChangeValues.Value> renderRemovedValues(List<ObjectRemoved> objectRemovedList) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (ObjectRemoved objectRemoved : objectRemovedList) {
            String id = objectRemoved.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOldValue(objectRemoved.getAffectedObject().get());
            initValueWithPath(value, id, MODE.REMOVED);
            allValues.add(value);
        }

        return distinctValues(allValues, new Callback<ChangeValues.Value>() {
            @Override
            public void invoke(ChangeValues.Value value) {
            }
        });
    }

    public List<ChangeValues.Value> renderChangeValues(
            List<ValueChange> valueChanges) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();

        for (ValueChange valueChange : valueChanges) {
            String id = valueChange.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOwner(valueChange.getAffectedObject().get())
                    .setNewValue(valueChange.getRight())
                    .setOldValue(valueChange.getLeft());

            DefinitionModel lastDefinitionModel = initValueWithPath(value, id, MODE.CHANGED);
            lastDefinitionModel = findDefinition(lastDefinitionModel, valueChange.getPropertyName());
            initValueProperty(value, lastDefinitionModel, valueChange.getPropertyName());

            result.add(value);
        }

        return result;
    }

    private List<ChangeValues.Value> distinctValues(List<ChangeValues.Value> allValues, Callback<ChangeValues.Value> callback) {
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

    private DefinitionModel initValueWithPath(
            ChangeValues.Value value,
            String id, MODE mode) {
        List<String> paths = StrUtil.split(id, '/', true, true);
        DefinitionModel lastDefinitionModel = null;

        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            // Root
            if (i == 0) {
                lastDefinitionModel = findDefinition(lastDefinitionModel, path);
                initValueProperty(value, lastDefinitionModel, path);
                continue;
            }

            // 集合
            if (path.startsWith("#")) {
                lastDefinitionModel = findDefinition(lastDefinitionModel, path.substring(1));
                initValueProperty(value, lastDefinitionModel, path);
                continue;
            }

            // 集合内某元素
            if (Validator.isNumber(path)) {
                if (lastDefinitionModel != null) {
                    lastDefinitionModel = definitions.get(lastDefinitionModel.getReferId());
                    value.getPropertyIdFragments().add(path);

                    String name = lastDefinitionModel.getName();
                    switch (mode) {
                        case REMOVED:
                            name = BeanRender.renderKeyValue(value.getOldValue());
                            break;

                        case CREATED:
                            name = BeanRender.renderKeyValue(value.getNewValue());
                            break;

                        case CHANGED:
                            name = NodePathValueRender.render(oldBean,
                                    StrUtil.join("/", value.getPropertyIdFragments()));
                            break;
                    }

                    value.getPropertyNameFragments().add(name);
                }

                continue;
            }

            // 普通属性
            lastDefinitionModel = findDefinition(lastDefinitionModel, path);
            initValueProperty(value, lastDefinitionModel, path);
        }

        return lastDefinitionModel;
    }

    private void initValueProperty(ChangeValues.Value value,
                                   DefinitionModel definition,
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

    private DefinitionModel findDefinition(DefinitionModel parentDefinition, String key) {
        DefinitionModel definition = definitions.get(key);
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

    private enum MODE {
        CREATED, REMOVED, CHANGED
    }
}