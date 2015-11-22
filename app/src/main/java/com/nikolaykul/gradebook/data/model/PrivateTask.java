package com.nikolaykul.gradebook.data.model;

import org.joda.time.DateTime;

public class PrivateTask {
    private long mId;
    private long mStudentId;
    private DateTime mDate;
    private boolean mPassed;

    public PrivateTask(long studentId, DateTime date, boolean passed) {
        mStudentId = studentId;
        mDate = date;
        mPassed = passed;
    }

    public long getId() {
        return mId;
    }

    public PrivateTask setId(long id) {
        mId = id;
        return this;
    }

    public long getStudentId() {
        return mStudentId;
    }

    public PrivateTask setStudentId(long studentId) {
        mStudentId = studentId;
        return this;
    }

    public DateTime getDate() {
        return mDate;
    }

    public PrivateTask setDate(DateTime date) {
        mDate = date;
        return this;
    }

    public boolean isPassed() {
        return mPassed;
    }

    public PrivateTask setPassed(boolean passed) {
        mPassed = passed;
        return this;
    }

}
