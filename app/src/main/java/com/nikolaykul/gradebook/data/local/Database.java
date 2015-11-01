package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentAttendance;
import com.nikolaykul.gradebook.data.models.StudentPrivateTask;
import com.nikolaykul.gradebook.data.models.StudentTest;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

public class Database {
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

    // StudentAttendance

    public void setAttendances(List<StudentAttendance> attendanceList) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            mDatabase.delete(Db.StudentAttendanceTable.TABLE_NAME, null);
            for (StudentAttendance attendance : attendanceList) {
                mDatabase.insert(
                        Db.StudentAttendanceTable.TABLE_NAME,
                        Db.StudentAttendanceTable.toContentValues(attendance));
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
    }

    public List<StudentAttendance> getAttendances(long studentId) {
        String sql = "select * from " + Db.StudentAttendanceTable.TABLE_NAME +
                " where " + Db.StudentAttendanceTable.COLUMN_STUDENT_ID + " =? ";
        String[] args = {"" + studentId};
        return Db.StudentAttendanceTable.parseCursor(mDatabase.query(sql, args));
    }

    // StudentPrivateTask

    public void setPrivateTasks(List<StudentPrivateTask> taskList) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            mDatabase.delete(Db.StudentPrivateTaskTable.TABLE_NAME, null);
            for (StudentPrivateTask task : taskList) {
                mDatabase.insert(
                        Db.StudentPrivateTaskTable.TABLE_NAME,
                        Db.StudentPrivateTaskTable.toContentValues(task));
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
    }

    public List<StudentPrivateTask> getPrivateTasks(long studentId) {
        String sql = "select * from " + Db.StudentPrivateTaskTable.TABLE_NAME +
                " where " + Db.StudentPrivateTaskTable.COLUMN_STUDENT_ID + " =? ";
        String[] args = {"" + studentId};
        return Db.StudentPrivateTaskTable.parseCursor(mDatabase.query(sql, args));
    }

    // StudentTest

    public void setTests(List<StudentTest> testList) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            mDatabase.delete(Db.StudentTestTable.TABLE_NAME, null);
            for (StudentTest test : testList) {
                mDatabase.insert(
                        Db.StudentTestTable.TABLE_NAME,
                        Db.StudentTestTable.toContentValues(test));
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
    }

    public List<StudentTest> getTests(long studentId) {
        String sql = "select * from " + Db.StudentTestTable.TABLE_NAME +
                " where " + Db.StudentTestTable.COLUMN_STUDENT_ID + " =? ";
        String[] args = {"" + studentId};
        return Db.StudentTestTable.parseCursor(mDatabase.query(sql, args));
    }

}
