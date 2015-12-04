package com.nikolaykul.gradebook.data.model;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.event.GroupDeletedEvent;
import com.squareup.otto.Bus;

public class Group implements Model {
    private long mId;
    private String mName;

    public Group() {/* empty */}

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

    @Override public String getTitle() {
        return mName;
    }

    @Override public void insertInDatabase(Database database) {
        database.insertGroup(this);
    }

    @Override public void removeFromDatabase(Database database) {
        database.removeGroup(mId);
    }

    @Override public void notifyInserted(Bus bus) {/* no event */}

    @Override public void notifyRemoved(Bus bus) {
        bus.post(new GroupDeletedEvent(this));
    }

}
