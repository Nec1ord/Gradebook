package com.nikolaykul.gradebook.data.model;

import org.joda.time.DateTime;

public class Information {
    private static final String DEFAULT_TITLE = "Default title";
    private static final String DEFAULT_CONTENT = "Default content";
    private long mId;
    private long mStudentId;
    private DateTime mDate;
    private String mTitle;
    private String mContent;    // unused for now
    private boolean mPassed;

    public Information() {
        mDate = new DateTime(System.currentTimeMillis());
        mTitle = DEFAULT_TITLE;
        mContent = DEFAULT_CONTENT;
        mPassed = false;
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

    public void update(Information newInfo) {
        mDate    = newInfo.getDate();
        mTitle   = newInfo.getTitle();
        mContent = newInfo.getContent();
        mPassed  = newInfo.isPassed();
    }

    @Override public boolean equals(Object o) {
        if (null == o || !(o instanceof Information)) return false;
        Information info = (Information) o;
        return this == o || this.mId == info.getId();
    }
}
