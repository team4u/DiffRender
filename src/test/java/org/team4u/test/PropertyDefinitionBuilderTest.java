package org.team4u.test;

import org.junit.Assert;
import org.junit.Test;
import org.team4u.diff.definiton.PropertyDefinitionBuilder;

/**
 * @author Jay Wu
 */
public class PropertyDefinitionBuilderTest {

    @Test
    public void render() {
        PropertyDefinitionBuilder builder = new PropertyDefinitionBuilder("org.team4u.test.model");

        Assert.assertEquals("{org.team4u.test.model.Room=PropertyDefinition{id='org.team4u.test.model.Room', name='房间', referId='null', children=[PropertyDefinition{id='name', name='名称', referId='java.lang.String', children=[]}, PropertyDefinition{id='height', name='高度', referId='org.team4u.test.model.Size', children=[]}]}, org.team4u.test.model.Person=PropertyDefinition{id='org.team4u.test.model.Person', name='个人', referId='null', children=[PropertyDefinition{id='name', name='姓名', referId='java.lang.String', children=[]}, PropertyDefinition{id='room1List', name='房间列表1', referId='org.team4u.test.model.Room', children=[]}, PropertyDefinition{id='room2List', name='房间列表2', referId='org.team4u.test.model.Room', children=[]}, PropertyDefinition{id='room3List', name='房间列表3', referId='org.team4u.test.model.Room', children=[]}]}, org.team4u.test.model.Size=PropertyDefinition{id='org.team4u.test.model.Size', name='大小', referId='null', children=[PropertyDefinition{id='size', name='大小', referId='int', children=[]}]}}",
                builder.build().toString());
    }
}