package com.nikolaykul.gradebook.data.model;

import org.joda.time.DateTime;

public class Attendance {
    private long mId;
    private long mStudentId;
    private DateTime mDate;
    private boolean wasAbsent;

    public Attendance(long studentId, DateTime date, boolean wasAbsent) {
        this.mStudentId = studentId;
        this.mDate = date;
        this.wasAbsent = wasAbsent;
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

    public boolean isWasAbsent() {
        return wasAbsent;
    }

    public Attendance setWasAbsent(boolean wasAbsent) {
        this.wasAbsent = wasAbsent;
        return this;
    }
}
