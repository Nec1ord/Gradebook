package com.nikolaykul.gradebook.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.event.GroupDeletedEvent;
import com.squareup.otto.Bus;

public class Group implements Model, Parcelable {
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

    // Model impl

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

    @Override public void notifyClicked(Bus bus) {
        bus.post(this);
    }

    // Parcelable impl

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    private Group(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
    }

}
