package com.nikolaykul.gradebook.data.model;

import org.joda.time.DateTime;

public class Information {
    private long mId;
    private long mStudentId;
    private DateTime mDate;
    private String mTitle;
    private String mContent;
    private boolean mPassed;

    public Information() {
    }

    public Information(long studentId, DateTime date, String title, String content, boolean passed) {
        mStudentId = studentId;
        mDate = date;
        mTitle = title;
        mContent = content;
        mPassed = passed;
    }

    public long getId() {
        return mId;
    }

    public Information setId(long id) {
        mId = id;
        return this;
    }

    public long getStudentId() {
        return mStudentId;
    }

    public Information setStudentId(long studentId) {
        mStudentId = studentId;
        return this;
    }

    public DateTime getDate() {
        return mDate;
    }

    public Information setDate(DateTime date) {
        mDate = date;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Information setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Information setContent(String content) {
        mContent = content;
        return this;
    }

    public boolean isPassed() {
        return mPassed;
    }

    public Information setPassed(boolean passed) {
        mPassed = passed;
        return this;
    }
}
