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

    public static Diff createDiff() {
        Javers javers = JaversBuilder
                .javers()
//                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();

        Person tommyOld = new Person("tommy", "Tommy Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("c")))
                .setRoom2List(CollectionUtil.newArrayList(
                        new Room().setName("a2").setHeight(new Size().setSize(1)),
                        new Room().setName("a3").setHeight(new Size().setSize(2)).setWeight(new Size().setSize(4))))
                .setRoom3List(CollectionUtil.newArrayList(new Room().setName("a3").setHeight(new Size().setSize(3))));

        Person tommyNew = new Person("tommy", "Tommy C. Smart")
                .setRoom1List(CollectionUtil.newArrayList(new Room().setName("a"), new Room().setName("b"), new Room().setName("d")))
                .setRoom2List(CollectionUtil.newArrayList(
                        new Room().setName("a2").setHeight(new Size().setSize(2)),
                        new Room().setName("a3").setHeight(new Size().setSize(3)).setWeight(new Size().setSize(3)),
                        new Room().setName("a4").setHeight(new Size().setSize(3)).setWeight(new Size().setSize(3))));

        Diff diff = javers.compare(tommyOld, tommyNew);
        System.out.println(diff);
        return diff;
    }
}