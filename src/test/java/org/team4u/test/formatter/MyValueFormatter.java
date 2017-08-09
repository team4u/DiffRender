package org.team4u.test.formatter;

import org.team4u.diff.ChangeValues;
import org.team4u.diff.definiton.PropertyDefinition;

/**
 * @author Jay Wu
 */
public class MyValueFormatter {

    public static void c(ChangeValues.Value value, PropertyDefinition definition, String prefix) {
        value.getPropertyNameFragments().set(value.getPropertyNameFragments().size() - 1, prefix + definition.getName());
        value.setNewValue(prefix + value.getNewValue());
    }
}