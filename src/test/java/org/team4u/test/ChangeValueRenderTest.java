package org.team4u.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.team4u.diff.definiton.DefinitionBuilder;
import org.team4u.diff.definiton.DefinitionModel;
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

    private Map<String, DefinitionModel> definitionMap =
            new DefinitionBuilder("org.team4u.test.model").build();

    private ChangeValuesRender render = new ChangeValuesRender(definitionMap, TestUtil.createPerson1(), TestUtil.createPerson2());

    @BeforeClass
    public static void beforeClass() {
        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);
    }

    @Test
    public void renderChangeValues() {
        List<ChangeValues.Value> result = render.render().getChangeValues();

        Assert.assertEquals("[org.team4u.test.model.Person, name]",
                result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 姓名]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals("Tommy Smart", result.get(0).getOldValue());
        Assert.assertEquals("*Tommy C. Smart", result.get(0).getNewValue());

        Assert.assertEquals("[org.team4u.test.model.Person, #room1List, 0, name]",
                result.get(1).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表1, 604, 名称]",
                result.get(1).getPropertyNameFragments().toString());
        Assert.assertEquals("604", result.get(1).getOldValue());
        Assert.assertEquals("601", result.get(1).getNewValue());

        Assert.assertEquals("[org.team4u.test.model.Person, #room2List, 0, height, size]",
                result.get(2).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表2, 101, 高度, 大小]",
                result.get(2).getPropertyNameFragments().toString());
        Assert.assertEquals(1, result.get(2).getOldValue());
        Assert.assertEquals(2, result.get(2).getNewValue());
    }

    @Test
    public void renderNewValues() {
        List<ChangeValues.Value> result = render.render().getNewValues();

        Assert.assertEquals("[org.team4u.test.model.Person, #room1List, 1]",
                result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表1, 602]",
                result.get(0).getPropertyNameFragments().toString());

        Assert.assertEquals(null, result.get(0).getOldValue());
        Assert.assertEquals("名称：602， 高度大小：无， 宽度大小：无", result.get(0).getNewValue().toString());
    }

    @Test
    public void renderRemovedValues() {
        List<ChangeValues.Value> result = render.render().getRemovedValues();

        Assert.assertEquals("[org.team4u.test.model.Person, #room3List, 0]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表3, 201]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals("名称：201， 高度大小：大小：3， 附加信息：无， 宽度大小：无", result.get(0).getOldValue().toString());
        Assert.assertEquals(null, result.get(0).getNewValue());
    }

    @Test
    public void toPathMap() {
        Map<String, ?> result = render.renderToPathMap();
        System.out.println(result);
    }
}