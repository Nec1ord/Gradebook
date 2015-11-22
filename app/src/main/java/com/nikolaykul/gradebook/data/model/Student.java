package com.nikolaykul.gradebook.data.model;

public class Student {
    private long mId;
    private long mGroupId;
    private String mFullName;

    public Student(long id, long groupId, String fullName) {
        mId = id;
        mGroupId = groupId;
        mFullName = fullName;
    }

    public long getId() {
        return mId;
    }

    public Student setId(long id) {
        this.mId = id;
        return this;
    }

    public long getGroupId() {
        return mGroupId;
    }

    public Student setGroupId(long groupId) {
        this.mGroupId = groupId;
        return this;
    }

    public String getFullName() {
        return mFullName;
    }

    public Student setFullName(String fullName) {
        this.mFullName = fullName;
        return this;
    }

}
