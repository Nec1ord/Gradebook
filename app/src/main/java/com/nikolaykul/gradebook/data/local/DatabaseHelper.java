package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.models.Student;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

public class DatabaseHelper {
    private BriteDatabase mDatabase;

    public DatabaseHelper(Context context) {
        mDatabase = SqlBrite.create().wrapDatabaseHelper(new DbOpenHelper(context));
    }

    public long insertStudent(Student student) {
        long id = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            id = mDatabase.insert(
                    Db.StudentTable.TABLE_NAME, Db.StudentTable.toContentValues(student));
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
        return id;
    }

    public int removeStudent(long id) {
        int i = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.StudentTable.COLUMN_ID + " =? ";
            String[] whereArgs = {""+id};
            i = mDatabase.delete(Db.StudentTable.TABLE_NAME, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
        return i;
    }

    public List<Student> getStudents() {
        final String sql = "select * from " + Db.StudentTable.TABLE_NAME;
        return Db.StudentTable.parseCursor(mDatabase.query(sql));
    }

}
