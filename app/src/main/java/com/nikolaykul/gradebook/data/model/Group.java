package com.nikolaykul.gradebook.data.model;

public class Group {
    private long mId;
    private String mName;

    public Group() {
    }

    public Group(String name) {
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public Group setId(long id) {
        this.mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Group setName(String name) {
        this.mName = name;
        return this;
    }

}
