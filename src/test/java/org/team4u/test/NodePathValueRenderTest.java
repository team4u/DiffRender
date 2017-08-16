package org.team4u.test;

import org.junit.Assert;
import org.junit.Test;
import org.team4u.diff.render.NodePathValueRender;

/**
 * @author Jay Wu
 */
public class NodePathValueRenderTest {

    @Test
    public void render() {
        Assert.assertEquals("2",
                NodePathValueRender.render(
                        TestUtil.createPerson1(),
                        "Person/#room2List/1/height/size"));
    }
}