package org.team4u.diff.definiton;

/**
 * @author Jay Wu
 */
public interface Renderable {

    /**
     * 渲染完整数据
     */
    String renderWholeValue();

    /**
     * 渲染关键数据
     */
    String renderKeyValue();
}