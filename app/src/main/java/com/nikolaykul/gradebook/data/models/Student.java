package com.nikolaykul.gradebook.data.models;

public class Student {
    public long id;
    public long groupId;
    public String fullName;

    public Student() {
    }

    public Student(long groupId, String studentFullName) {
        this.groupId = groupId;
        this.fullName = studentFullName;
    }
}
