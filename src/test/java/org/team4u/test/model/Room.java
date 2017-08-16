package org.team4u.test.model;

import org.team4u.diff.definiton.Definition;
import org.team4u.diff.definiton.Renderable;
import org.team4u.kit.core.util.ValueUtil;

/**
 * @author Jay Wu
 */
@Definition("房间")
public class Room implements Renderable {

    @Definition("名称")
    private String name;

    @Definition("高度")
    private Size height;

    @Definition("宽度")
    private Size weight;

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

    public Size getWeight() {
        return weight;
    }

    public Room setWeight(Size weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return renderWholeValue();
    }

    @Override
    public String renderWholeValue() {
        return String.format("名称：%s， 高度大小：%s， 宽度大小：%s",
                ValueUtil.defaultIfNull(name, "无"),
                ValueUtil.defaultIfNull(height, "无"),
                ValueUtil.defaultIfNull(weight, "无"));
    }

    @Override
    public String renderKeyValue() {
        return name;
    }
}