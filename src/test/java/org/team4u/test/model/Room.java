package org.team4u.test.model;

import org.team4u.diff.definiton.Definition;

/**
 * @author Jay Wu
 */
@Definition("房间")
public class Room {

    @Definition("名称")
    private String name;

    @Definition("高度")
    private Size height;

    public String getName() {
        return name;
    }

    public Room setName(String name) {
        this.name = name;
        return this;
    }

    public Size getHeight() {
        return height;
    }

    public Room setHeight(Size height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", height=" + height +
                '}';
    }
}
