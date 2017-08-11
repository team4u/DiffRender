package org.team4u.test.model;

import org.team4u.diff.definiton.Definition;

/**
 * @author Jay Wu
 */
@Definition("大小")
public class Size {
    @Definition("大小")
    private int size;

    public int getSize() {
        return size;
    }

    public Size setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(size);
    }
}
