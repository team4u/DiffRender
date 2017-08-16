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
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;
import org.team4u.kit.core.util.MapUtil;
import org.team4u.kit.core.util.ValueUtil;

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

            parseId(value, id, MODE.CREATED);
            allValues.add(value);
        }

        return distinctValues(allValues);
    }

    public List<ChangeValues.Value> renderRemovedValues(List<ObjectRemoved> objectRemovedList) {
        List<ChangeValues.Value> allValues = CollectionUtil.newArrayList();
        for (ObjectRemoved objectRemoved : objectRemovedList) {
            String id = objectRemoved.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOldValue(objectRemoved.getAffectedObject().get());
            parseId(value, id, MODE.REMOVED);
            allValues.add(value);
        }

        return distinctValues(allValues);
    }

    public List<ChangeValues.Value> renderChangeValues(List<ValueChange> valueChanges) {
        List<ChangeValues.Value> result = CollectionUtil.newArrayList();

        for (ValueChange valueChange : valueChanges) {
            String id = valueChange.getAffectedGlobalId().value();
            ChangeValues.Value value = new ChangeValues.Value()
                    .setPropertyId(id)
                    .setOwner(valueChange.getAffectedObject().get())
                    .setNewValue(valueChange.getRight())
                    .setOldValue(valueChange.getLeft());

            DefinitionModel lastDefinitionModel = parseId(value, id, MODE.CHANGED);
            lastDefinitionModel = findDefinition(lastDefinitionModel, valueChange.getPropertyName());
            initChangeValue(value, lastDefinitionModel, valueChange.getPropertyName());

            result.add(value);
        }

        return result;
    }

    /**
     * 去除重复节点
     */
    private List<ChangeValues.Value> distinctValues(List<ChangeValues.Value> allValues) {
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

    private DefinitionModel parseId(ChangeValues.Value value, String id, MODE mode) {
        List<String> paths = StrUtil.split(id, '/', true, true);
        DefinitionModel lastDefinitionModel = null;

        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            // Root
            if (i == 0) {
                lastDefinitionModel = findDefinition(lastDefinitionModel, path);
                initChangeValue(value, lastDefinitionModel, path);
                continue;
            }

            // 集合
            if (path.startsWith("#")) {
                lastDefinitionModel = findDefinition(lastDefinitionModel, path.substring(1));
                initChangeValue(value, lastDefinitionModel, path);
                continue;
            }

            // 集合内某元素
            if (Validator.isNumber(path)) {
                initListNode(value, lastDefinitionModel, path, mode);
                continue;
            }

            // 普通属性
            lastDefinitionModel = findDefinition(lastDefinitionModel, path);
            initChangeValue(value, lastDefinitionModel, path);
        }

        return lastDefinitionModel;
    }

    private void initListNode(ChangeValues.Value value, DefinitionModel definition, String path, MODE mode) {
        if (definition != null) {
            definition = definitions.get(definition.getReferId());
            value.getPropertyIdFragments().add(path);

            String name = definition.getName();
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
    }

    private void initChangeValue(ChangeValues.Value value, DefinitionModel definition, String key) {
        value.getPropertyIdFragments().add(key);

        if (definition == null) {
            value.getPropertyNameFragments().add(key);
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
            definition = definitions.get(parentDefinition.getReferId());
            if (definition != null) {
                return ValueUtil.defaultIfNull(definition.find(key), definition);
            }
        }

        return parentDefinition.find(key);
    }

    private enum MODE {
        CREATED, REMOVED, CHANGED
    }
}