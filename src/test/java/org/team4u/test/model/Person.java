package org.team4u.test.model;

import org.team4u.diff.definiton.Definition;

import java.util.List;

/**
 * @author Jay Wu
 */
@Definition("个人")
public class Person {
    private String login;
    @Definition(value = "姓名", formatter = "${c(value,def,'*')}")
    private String name;
    @Definition(value = "房间列表1", refer = Room.class)
    private List<Room> room1List;
    @Definition(value = "房间列表2", refer = Room.class)
    private List<Room> room2List;
    @Definition(value = "房间列表3", refer = Room.class)
    private List<Room> room3List;

    public Person(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public List<Room> getRoom1List() {
        return room1List;
    }

    public Person setRoom1List(List<Room> room1List) {
        this.room1List = room1List;
        return this;
    }

    public List<Room> getRoom2List() {
        return room2List;
    }

    public Person setRoom2List(List<Room> room2List) {
        this.room2List = room2List;
        return this;
    }

    public List<Room> getRoom3List() {
        return room3List;
    }

    public Person setRoom3List(List<Room> room3List) {
        this.room3List = room3List;
        return this;
    }
}
