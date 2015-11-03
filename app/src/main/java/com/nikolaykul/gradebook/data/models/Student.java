package com.nikolaykul.gradebook.data.models;

public class Student {
    public long id;
    public String fullName;

    public Student() {
    }

    public Student(String fullName) {
        this.fullName = fullName;
    }
}
