package com.nikolaykul.gradebook.data.model;

import org.joda.time.DateTime;

public class Attendance {
    private long mId;
    private long mStudentId;
    private DateTime mDate;
    private boolean mAbsent;

    public Attendance() {
    }

    public Attendance(long id, long studentId, DateTime date, boolean absent) {
        mId = id;
        mStudentId = studentId;
        mDate = date;
        mAbsent = absent;
    }

    public long getId() {
        return mId;
    }

    public Attendance setId(long id) {
        this.mId = id;
        return this;
    }

    public long getStudentId() {
        return mStudentId;
    }

    public Attendance setStudentId(long studentId) {
        this.mStudentId = studentId;
        return this;
    }

    public DateTime getDate() {
        return mDate;
    }

    public Attendance setDate(DateTime date) {
        this.mDate = date;
        return this;
    }

    public boolean isAbsent() {
        return mAbsent;
    }

    public Attendance setAbsent(boolean absent) {
        mAbsent = absent;
        return this;
    }

}
