package org.team4u.test;

import com.xiaoleilu.hutool.util.CollectionUtil;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.team4u.test.model.Person;
import org.team4u.test.model.Room;
import org.team4u.test.model.Size;

/**
 * @author Jay Wu
 */
public class TestUtil {

    public static Person createPerson1() {
        return new Person("tommy", "Tommy Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("604")))
                .setRoom2List(CollectionUtil.newArrayList(
                        new Room().setName("101").setHeight(new Size().setSize(1)),
                        new Room().setName("201").setHeight(new Size().setSize(2).setDesc("测试1")).setWeight(new Size().setSize(4))))
                .setRoom3List(CollectionUtil.newArrayList(new Room().setName("201").setHeight(new Size().setSize(3))));
    }

    public static Person createPerson2() {
        return new Person("tommy", "Tommy C. Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("601"), new Room().setName("602"), new Room().setName("603")))
                .setRoom2List(CollectionUtil.newArrayList(
                        new Room().setName("101").setHeight(new Size().setSize(2)),
                        new Room().setName("201").setHeight(new Size().setSize(3)).setWeight(new Size().setSize(3)),
                        new Room().setName("401").setHeight(new Size().setSize(3)).setWeight(new Size().setSize(3))));
    }

    public static Diff createDiff() {
        Javers javers = JaversBuilder
                .javers()
//                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();

        Person tommyOld = createPerson1();

        Person tommyNew = createPerson2();

        Diff diff = javers.compare(tommyOld, tommyNew);
        System.out.println(diff);
        return diff;
    }
}