package org.team4u.test.formatter;

import org.team4u.diff.render.ChangeValues;

/**
 * @author Jay Wu
 */
public class MyValueFormatter {

    public static void c(ChangeValues.Value value, String prefix) {
        if (value.getNewValue() != null) {
            value.setNewValue(prefix + value.getNewValue());
        }
    }
}