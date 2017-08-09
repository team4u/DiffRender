package org.team4u.test;

import com.xiaoleilu.hutool.util.CollectionUtil;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.Assert;
import org.junit.Test;
import org.team4u.diff.ChangeValues;
import org.team4u.diff.ChangeValuesRender;
import org.team4u.diff.ValueFormatterRegistry;
import org.team4u.diff.definiton.PropertyDefinition;
import org.team4u.diff.definiton.PropertyDefinitionBuilder;
import org.team4u.test.formatter.MyValueFormatter;
import org.team4u.test.model.Person;
import org.team4u.test.model.Room;
import org.team4u.test.model.Size;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class ChangeValueRenderTest {

    private Map<String, PropertyDefinition> definitionMap =
            new PropertyDefinitionBuilder("org.team4u.test.model").build();

    private Diff diff = createDiff();

    @Test
    public void renderChangeValues() {
        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);
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

        Assert.assertEquals("[org.team4u.test.model.Person, room1List]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表1]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals(null, result.get(0).getOldValue());
        Assert.assertEquals("Room{name='b', height=null}", result.get(0).getNewValue().toString());
    }

    @Test
    public void renderRemovedValues() {
        List<ChangeValues.Value> result = ChangeValuesRender.renderRemovedValues(definitionMap, diff.getChangesByType(ObjectRemoved.class));

        Assert.assertEquals("[org.team4u.test.model.Person, room3List]", result.get(0).getPropertyIdFragments().toString());
        Assert.assertEquals("[个人, 房间列表3]", result.get(0).getPropertyNameFragments().toString());
        Assert.assertEquals("Room{name='a3', height=Size{size=3}}", result.get(0).getOldValue().toString());
        Assert.assertEquals(null, result.get(0).getNewValue());
    }

    private Diff createDiff() {
        Javers javers = JaversBuilder
                .javers()
//                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();

        Person tommyOld = new Person("tommy", "Tommy Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("c")))
                .setRoom2List(CollectionUtil.newArrayList(new Room().setName("a2").setHeight(new Size().setSize(1))))
                .setRoom3List(CollectionUtil.newArrayList(new Room().setName("a3").setHeight(new Size().setSize(3))));

        Person tommyNew = new Person("tommy", "Tommy C. Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("a"), new Room().setName("b")))
                .setRoom2List(CollectionUtil.newArrayList(new Room().setName("a2").setHeight(new Size().setSize(2))));

        Diff diff = javers.compare(tommyOld, tommyNew);
        System.out.println(diff);
        return diff;
    }
}