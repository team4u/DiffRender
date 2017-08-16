package org.team4u.diff.render;

import org.team4u.diff.definiton.Renderable;

/**
 * @author Jay Wu
 */
public class BeanRender {

    public static String renderKeyValue(Object bean) {
        if (bean instanceof Renderable) {
            return ((Renderable) bean).renderKeyValue();
        }

        return bean.toString();
    }

    public static String renderWholeValue(Object bean) {
        if (bean instanceof Renderable) {
            return ((Renderable) bean).renderWholeValue();
        }

        return bean.toString();
    }
}