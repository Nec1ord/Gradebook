package com.nikolaykul.gradebook.data.model;

public class StudentGroup {
    private long id;
    private String name;

    public StudentGroup(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public StudentGroup setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public StudentGroup setName(String name) {
        this.name = name;
        return this;
    }

}
