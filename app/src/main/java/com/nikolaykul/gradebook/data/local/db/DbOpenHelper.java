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
        String tblPrivateTasks = Db.PrivateTaskTable.TABLE_CONTROL_TASK;
        String tblTests = Db.PrivateTaskTable.TABLE_TEST;

        db.beginTransaction();
        try {
            db.execSQL(Db.GroupTable.CREATE);
            db.execSQL(Db.StudentTable.CREATE);
            db.execSQL(Db.AttendanceTable.CREATE);
            db.execSQL(Db.PrivateTaskTable.createTable(tblPrivateTasks));
            db.execSQL(Db.PrivateTaskTable.createTable(tblTests));
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
            db.execSQL(drop + Db.GroupTable.TABLE_NAME);
            db.execSQL(drop + Db.StudentTable.TABLE_NAME);
            db.execSQL(drop + Db.AttendanceTable.TABLE_NAME);
            db.execSQL(drop + Db.PrivateTaskTable.TABLE_CONTROL_TASK);
            db.execSQL(drop + Db.PrivateTaskTable.TABLE_TEST);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }
}
