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
public class DefinitionBuilder {

    private String packageName;

    public DefinitionBuilder(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, DefinitionModel> build() {
        Map<String, DefinitionModel> result = CollectionUtil.newHashMap();

        for (Class<?> definitionClass : ClassUtil.scanPackageByAnnotation(packageName, Definition.class)) {
            Definition definition = definitionClass.getAnnotation(Definition.class);
            DefinitionModel pd = buildPropertyDefinition(definition);
            pd.setId(definitionClass.getName());

            for (Field field : ClassUtil.getDeclaredFields(definitionClass)) {
                DefinitionModel child = buildFiledPropertyDefinition(field);
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

    private DefinitionModel buildFiledPropertyDefinition(Field field) {
        Definition definition = field.getAnnotation(Definition.class);

        if (definition == null) {
            return null;
        }

        DefinitionModel pd = buildPropertyDefinition(definition);
        pd.setId(field.getName());
        pd.setReferId(ValueUtil.defaultIfNull(pd.getReferId(), field.getType().getName()));
        return pd;
    }

    private DefinitionModel buildPropertyDefinition(Definition definition) {
        DefinitionModel pd = new DefinitionModel();
        pd.setName(definition.value());

        pd.setFormatter(ValueUtil.defaultIfEmpty(definition.formatter(), (String) null));

        Class<?> referClass = CollectionExUtil.getFirst(definition.refer());
        if (referClass != null) {
            pd.setReferId(referClass.getName());
        }

        return pd;
    }
}