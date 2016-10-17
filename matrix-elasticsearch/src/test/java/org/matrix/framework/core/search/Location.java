package org.matrix.framework.core.search;

import io.searchbox.annotations.JestId;

public class Location {

    @JestId
    private String name;
    private int age;
    private float source;

    public Location() {

    }

    public Location(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getSource() {
        return source;
    }

    public void setSource(float source) {
        this.source = source;
    }

}
