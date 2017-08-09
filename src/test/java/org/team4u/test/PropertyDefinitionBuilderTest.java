package org.team4u.test;

import org.junit.Test;
import org.team4u.diff.definiton.PropertyDefinitionBuilder;

/**
 * @author Jay Wu
 */
public class PropertyDefinitionBuilderTest {

    @Test
    public void render() {
        PropertyDefinitionBuilder builder = new PropertyDefinitionBuilder("org.team4u.test.model");
        System.out.println(builder.build());
    }
}