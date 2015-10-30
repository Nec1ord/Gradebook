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
        db.beginTransaction();
        try {
            db.execSQL(Db.StudentTable.CREATE);
            db.execSQL(Db.StudentAttendanceTable.CREATE);
            db.execSQL(Db.StudentPrivateTaskTable.CREATE);
            db.execSQL(Db.StudentTestTable.CREATE);
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
            db.execSQL(drop + Db.StudentAttendanceTable.TABLE_NAME);
            db.execSQL(drop + Db.StudentPrivateTaskTable.TABLE_NAME);
            db.execSQL(drop + Db.StudentTestTable.TABLE_NAME);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }
}
