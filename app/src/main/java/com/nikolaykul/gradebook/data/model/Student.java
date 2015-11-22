package com.nikolaykul.gradebook.data.model;

public class Student {
    private long id;
    private long groupId;
    private String fullName;

    public Student(long groupId, String studentFullName) {
        this.groupId = groupId;
        this.fullName = studentFullName;
    }

    public long getId() {
        return id;
    }

    public Student setId(long id) {
        this.id = id;
        return this;
    }

    public long getGroupId() {
        return groupId;
    }

    public Student setGroupId(long groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public Student setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

}
