package org.team4u.test;

import org.junit.Test;
import org.team4u.diff.definiton.DefinitionBuilder;

/**
 * @author Jay Wu
 */
public class PropertyDefinitionBuilderTest {

    @Test
    public void render() {
        DefinitionBuilder builder = new DefinitionBuilder("org.team4u.test.model");
        System.out.println(builder.build().toString());
    }
}