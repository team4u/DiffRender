package org.team4u.diff.render;

import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.team4u.kit.core.util.CollectionExUtil;

import java.util.List;

/**
 * @author Jay Wu
 */
public class ChangeValues {

    private List<Value> newValues;

    private List<Value> changeValues;

    private List<Value> removedValues;

    public List<Value> getNewValues() {
        return newValues;
    }

    public ChangeValues setNewValues(List<Value> newValues) {
        this.newValues = newValues;
        return this;
    }

    public List<Value> getChangeValues() {
        return changeValues;
    }

    public ChangeValues setChangeValues(List<Value> changeValues) {
        this.changeValues = changeValues;
        return this;
    }

    public List<Value> getRemovedValues() {
        return removedValues;
    }

    public ChangeValues setRemovedValues(List<Value> removedValues) {
        this.removedValues = removedValues;
        return this;
    }

    public static class Value {

        private String propertyId;

        private List<String> propertyIdFragments = CollectionUtil.newArrayList();

        private List<String> propertyNameFragments = CollectionUtil.newArrayList();

        private Object owner;

        // 调整前值
        private Object oldValue;

        // 调整后值
        private Object newValue;

        // 其他描述信息
        private String description;

        public boolean isDisplay() {
            return StrUtil.isNotEmpty(CollectionExUtil.getLast(propertyNameFragments));
        }

        public String getPropertyId() {
            return propertyId;
        }

        public Value setPropertyId(String propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public Object getOwner() {
            return owner;
        }

        public Value setOwner(Object owner) {
            this.owner = owner;
            return this;
        }

        public List<String> getPropertyIdFragments() {
            return propertyIdFragments;
        }

        public Value setPropertyIdFragments(List<String> propertyIdFragments) {
            this.propertyIdFragments = propertyIdFragments;
            return this;
        }

        public List<String> getPropertyNameFragments() {
            return propertyNameFragments;
        }

        public Value setPropertyNameFragments(List<String> propertyNameFragments) {
            this.propertyNameFragments = propertyNameFragments;
            return this;
        }

        public Value setNewValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Value setOldValue(Object oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Object getNewValue() {
            return newValue;
        }

        public Value setNewValue(Object newValue) {
            this.newValue = newValue;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Value setDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "propertyId='" + propertyId + '\'' +
                    '}';
        }
    }
}