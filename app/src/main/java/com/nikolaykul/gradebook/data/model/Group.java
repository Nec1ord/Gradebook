package com.nikolaykul.gradebook.data.model;

public class Group {
    private long id;
    private String name;

    public Group(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Group setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

}
