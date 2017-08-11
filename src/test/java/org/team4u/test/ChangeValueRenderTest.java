package org.team4u.test;

import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.team4u.diff.definiton.PropertyDefinition;
import org.team4u.diff.definiton.PropertyDefinitionBuilder;
import org.team4u.diff.render.ChangeValues;
import org.team4u.diff.render.ChangeValuesRender;
import org.team4u.diff.render.ValueFormatterRegistry;
import org.team4u.test.formatter.MyValueFormatter;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class ChangeValueRenderTest {

    private Map<String, PropertyDefinition> definitionMap =
            new PropertyDefinitionBuilder("org.team4u.test.model").build();

    private Diff diff = TestUtil.createDiff();

    @BeforeClass
    public static void beforeClass() {
        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);
    }

    @Test
    public void renderChangeValues() {
        List<ChangeValues.Value> result = ChangeValuesRender.renderChangeValues(definitionMap, diff.getChangesByType(ValueChange.class));

        Assert.assertEquals("[org.team4u.test.model.Person, name]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, *姓名]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals("Tommy Smart", result.get(0).getOldValue());
        Assert.assertEquals("*Tommy C. Smart", result.get(0).getNewValue());

        Assert.assertEquals("[org.team4u.test.model.Person, room1List, name]", result.get(1).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表1, 名称]", result.get(1).getPropertyNameFragments().toString());
        Assert.assertEquals("c", result.get(1).getOldValue());
        Assert.assertEquals("a", result.get(1).getNewValue());

        Assert.assertEquals("[org.team4u.test.model.Person, room2List, height, size]", result.get(2).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表2, 高度, 大小]", result.get(2).getPropertyNameFragments().toString());
        Assert.assertEquals(1, result.get(2).getOldValue());
        Assert.assertEquals(2, result.get(2).getNewValue());
    }

    @Test
    public void renderNewValues() {
        List<ChangeValues.Value> result = ChangeValuesRender.renderNewValues(definitionMap, diff.getChangesByType(NewObject.class));

        Assert.assertEquals("[org.team4u.test.model.Person, room1List, +]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表1, 新增]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals(null, result.get(0).getOldValue());
        Assert.assertEquals("Room{name='b', height=null}", result.get(0).getNewValue().toString());
    }

    @Test
    public void renderRemovedValues() {
        List<ChangeValues.Value> result = ChangeValuesRender.renderRemovedValues(definitionMap, diff.getChangesByType(ObjectRemoved.class));

        Assert.assertEquals("[org.team4u.test.model.Person, room3List, -]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表3, 删除]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals("Room{name='a3', height=Size{size=3}}", result.get(0).getOldValue().toString());
        Assert.assertEquals(null, result.get(0).getNewValue());
    }

    @Test
    public void toPathMap() {
        Map<String, ?> result = ChangeValuesRender.renderToPathMap(definitionMap, diff);
        System.out.println(result);
    }
}