package org.team4u.diff.definiton;

import com.xiaoleilu.hutool.util.ClassUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.team4u.kit.core.util.CollectionExUtil;
import org.team4u.kit.core.util.ValueUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class PropertyDefinitionBuilder {

    private String packageName;

    public PropertyDefinitionBuilder(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, PropertyDefinition> build() {
        Map<String, PropertyDefinition> result = CollectionUtil.newHashMap();

        for (Class<?> definitionClass : ClassUtil.scanPackageByAnnotation(packageName, Definition.class)) {
            Definition definition = definitionClass.getAnnotation(Definition.class);
            PropertyDefinition pd = buildPropertyDefinition(definition);
            pd.setId(definitionClass.getName());
            pd.setClass(true);

            for (Field field : ClassUtil.getDeclaredFields(definitionClass)) {
                PropertyDefinition child = buildFiledPropertyDefinition(field);
                if (child == null) {
                    continue;
                }

                pd.getIdForPropertyNames().addAll(child.getIdForPropertyNames());
                pd.getChildren().add(child);
            }

            result.put(pd.getId(), pd);
        }

        return result;
    }

    private PropertyDefinition buildFiledPropertyDefinition(Field field) {
        Definition definition = field.getAnnotation(Definition.class);

        if (definition == null) {
            return null;
        }

        PropertyDefinition pd = buildPropertyDefinition(definition);
        pd.setId(field.getName());
        if (definition.id()) {
            pd.getIdForPropertyNames().add(pd.getId());
        }
        pd.setReferId(ValueUtil.defaultIfNull(pd.getReferId(), field.getType().getName()));
        return pd;
    }

    private PropertyDefinition buildPropertyDefinition(Definition definition) {
        PropertyDefinition pd = new PropertyDefinition();
        pd.setName(definition.value());

        pd.setFormatter(ValueUtil.defaultIfEmpty(definition.formatter(), (String) null));

        Class<?> referClass = CollectionExUtil.getFirst(definition.refer());
        if (referClass != null) {
            pd.setReferId(referClass.getName());
        }

        return pd;
    }
}