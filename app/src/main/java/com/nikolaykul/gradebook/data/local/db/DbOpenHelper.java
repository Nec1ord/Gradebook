package com.nikolaykul.gradebook.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gradebook.db";
    public static final int DATABASE_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tblAttendance = Db.StudentInformation.TABLE_ATTENDANCE;
        String tblPrivateTasks = Db.StudentInformation.TABLE_PRIVATE_TASKS;
        String tblTests = Db.StudentInformation.TABLE_TESTS;

        db.beginTransaction();
        try {
            db.execSQL(Db.StudentTable.CREATE);
            db.execSQL(Db.StudentInformation.createInfoTable(tblAttendance));
            db.execSQL(Db.StudentInformation.createInfoTable(tblPrivateTasks));
            db.execSQL(Db.StudentInformation.createInfoTable(tblTests));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "drop table if exists ";
        db.beginTransaction();
        try {
            db.execSQL(drop + Db.StudentTable.TABLE_NAME);
            db.execSQL(drop + Db.StudentInformation.TABLE_ATTENDANCE);
            db.execSQL(drop + Db.StudentInformation.TABLE_PRIVATE_TASKS);
            db.execSQL(drop + Db.StudentInformation.TABLE_TESTS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }
}
