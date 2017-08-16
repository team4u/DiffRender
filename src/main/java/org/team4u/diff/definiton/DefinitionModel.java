package org.team4u.diff.definiton;

import com.xiaoleilu.hutool.util.CollectionUtil;
import org.team4u.kit.core.action.Function;
import org.team4u.kit.core.util.CollectionExUtil;

import java.util.List;

/**
 * @author Jay Wu
 */
public class DefinitionModel {

    private String id;
    private String name;
    private List<String> idForPropertyNames = CollectionUtil.newArrayList();
    private String referId;
    private String formatter;
    private List<DefinitionModel> children = CollectionUtil.newArrayList();

    public DefinitionModel find(final String id) {
        return CollectionExUtil.find(children, new Function<DefinitionModel, Boolean>() {
            @Override
            public Boolean invoke(DefinitionModel definition) {
                return definition.id.equals(id);
            }
        });
    }

    public String getId() {
        return id;
    }

    public DefinitionModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DefinitionModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getReferId() {
        return referId;
    }

    public DefinitionModel setReferId(String referId) {
        this.referId = referId;
        return this;
    }

    public List<DefinitionModel> getChildren() {
        return children;
    }

    public DefinitionModel setChildren(List<DefinitionModel> children) {
        this.children = children;
        return this;
    }

    public String getFormatter() {
        return formatter;
    }

    public DefinitionModel setFormatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    public List<String> getIdForPropertyNames() {
        return idForPropertyNames;
    }

    public DefinitionModel setIdForPropertyNames(List<String> idForPropertyNames) {
        this.idForPropertyNames = idForPropertyNames;
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