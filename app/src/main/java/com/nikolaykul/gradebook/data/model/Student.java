package com.nikolaykul.gradebook.data.model;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.event.StudentAddedEvent;
import com.nikolaykul.gradebook.event.StudentDeletedEvent;
import com.squareup.otto.Bus;

public class Student implements Model {
    private long mId;
    private long mGroupId;
    private String mFullName;

    public Student() {/* empty */}

    public Student(long groupId, String fullName) {
        mGroupId = groupId;
        mFullName = fullName;
    }

    @Override public void insertInDatabase(Database database) {
        database.insertStudent(this);
    }

    @Override public void removeFromDatabase(Database database) {
        database.removeStudent(mId);
    }

    @Override public void notifyInserted(Bus bus) {
        bus.post(new StudentAddedEvent());
    }

    @Override public void notifyRemoved(Bus bus) {
        bus.post(new StudentDeletedEvent());
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
