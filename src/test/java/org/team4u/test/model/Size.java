package org.team4u.test.model;

import org.team4u.diff.definiton.Definition;
import org.team4u.diff.definiton.Renderable;
import org.team4u.kit.core.util.ValueUtil;

/**
 * @author Jay Wu
 */
@Definition("大小")
public class Size implements Renderable {
    @Definition("大小")
    private int size;

    @Definition("附加信息")
    private String desc;

    public int getSize() {
        return size;
    }

    public Size setSize(int size) {
        this.size = size;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public Size setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    @Override
    public String toString() {
        return renderWholeValue();
    }

    @Override
    public String renderWholeValue() {
        return String.format("大小：%s, 附加信息：%s",
                ValueUtil.defaultIfNull(size, "无"),
                ValueUtil.defaultIfNull(desc, "无"));
    }

    @Override
    public String renderKeyValue() {
        return size + "";
    }
}
