package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentInfo;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

public class Database {
    public static final short STUDENT_ATTENDANCE = 0;
    public static final short STUDENT_PRIVATE_TASK = 1;
    public static final short STUDENT_TEST = 2;
    private BriteDatabase mDatabase;

    public Database(Context context) {
        mDatabase = SqlBrite.create().wrapDatabaseHelper(new DbOpenHelper(context));
    }

    // Student

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

    // StudentInfo

    public void setInfo(List<StudentInfo> infoList, short table) {
        String tableName = Db.StudentInformation.TABLE_ATTENDANCE; // default
        switch (table) {
            case STUDENT_ATTENDANCE: tableName = Db.StudentInformation.TABLE_ATTENDANCE; break;
            case STUDENT_PRIVATE_TASK: tableName = Db.StudentInformation.TABLE_PRIVATE_TASKS; break;
            case STUDENT_TEST: tableName = Db.StudentInformation.TABLE_TESTS; break;
        }
        setStudentInfo(infoList, tableName);
    }

    public List<StudentInfo> getInfo(long studentId, short table) {
        String tableName = Db.StudentInformation.TABLE_ATTENDANCE; // default
        switch (table) {
            case STUDENT_ATTENDANCE: tableName = Db.StudentInformation.TABLE_ATTENDANCE; break;
            case STUDENT_PRIVATE_TASK: tableName = Db.StudentInformation.TABLE_PRIVATE_TASKS; break;
            case STUDENT_TEST: tableName = Db.StudentInformation.TABLE_TESTS; break;
        }
        return getStudentInfo(studentId, tableName);
    }

    private void setStudentInfo(List<StudentInfo> infoList, String tableName) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            mDatabase.delete(tableName, null);
            for (StudentInfo info : infoList) {
                mDatabase.insert(
                        tableName,
                        Db.StudentInformation.toContentValues(info));
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
    }

    private List<StudentInfo> getStudentInfo(long studentId, String tableName) {
        String sql = "select * from " + tableName +
                " where " + Db.StudentInformation.COLUMN_STUDENT_ID + " =? ";
        String[] args = {"" + studentId};
        return Db.StudentInformation.parseCursor(mDatabase.query(sql, args));
    }

}
