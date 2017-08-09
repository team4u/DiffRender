package org.team4u.diff.definiton;

import com.xiaoleilu.hutool.util.CollectionUtil;
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;

import java.util.List;

/**
 * @author Jay Wu
 */
public class PropertyDefinition {

    private String id;
    private String name;
    private String referId;
    private String formatter;
    private List<PropertyDefinition> children = CollectionUtil.newArrayList();

    public PropertyDefinition find(final String id) {
        return CollectionExUtil.find(children, new Function<PropertyDefinition, Boolean>() {
            @Override
            public Boolean invoke(PropertyDefinition definition) {
                return definition.id.equals(id);
            }
        });
    }

    public String getId() {
        return id;
    }

    public PropertyDefinition setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PropertyDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public String getReferId() {
        return referId;
    }

    public PropertyDefinition setReferId(String referId) {
        this.referId = referId;
        return this;
    }

    public List<PropertyDefinition> getChildren() {
        return children;
    }

    public PropertyDefinition setChildren(List<PropertyDefinition> children) {
        this.children = children;
        return this;
    }

    public String getFormatter() {
        return formatter;
    }

    public PropertyDefinition setFormatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public String toString() {
        return "PropertyDefinition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", referId='" + referId + '\'' +
                ", children=" + children +
                '}';
    }
}