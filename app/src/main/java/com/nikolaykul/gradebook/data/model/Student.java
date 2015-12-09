package com.nikolaykul.gradebook.data.model;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.event.StudentAddedEvent;
import com.nikolaykul.gradebook.event.StudentDeletedEvent;
import com.nikolaykul.gradebook.event.StudentUpdatedEvent;
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

    @Override public String setTitle(String newTitle) {
        return mFullName = newTitle;
    }

    @Override public String getTitle() {
        return mFullName;
    }

    @Override public void insertInDatabase(Database database) {
        database.insertStudent(this);
    }

    @Override public void removeFromDatabase(Database database) {
        database.removeStudent(mId);
    }

    @Override public void updateInDatabase(Database database) {
        database.updateStudent(this);
    }

    @Override public void notifyInserted(Bus bus) {
        bus.post(new StudentAddedEvent());
    }

    @Override public void notifyRemoved(Bus bus) {
        bus.post(new StudentDeletedEvent());
    }

    @Override public void notifyUpdated(Bus bus) {
        bus.post(new StudentUpdatedEvent());
    }

    @Override public void notifyClicked(Bus bus) {
        bus.post(this);
    }

}
