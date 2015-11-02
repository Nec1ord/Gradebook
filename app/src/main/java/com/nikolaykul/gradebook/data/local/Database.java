package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentInfo;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

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
            if (id >= 0) {
                addEmptyStudentInfo(id);
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
        return id;
    }

    public void removeStudent(long id) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            clearAllStudentInfo(id);
            String where = Db.StudentTable.COLUMN_ID + " =? ";
            String[] whereArgs = {"" + id};
            mDatabase.delete(Db.StudentTable.TABLE_NAME, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<Student> getStudents() {
        final String sql = "select * from " + Db.StudentTable.TABLE_NAME;
        return Db.StudentTable.parseCursor(mDatabase.query(sql));
    }

    // StudentInfo

    public void insertStudentInfo(Date date, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            StudentInfo newInfo = new StudentInfo();
            newInfo.wasGood = false;
            newInfo.date = date;

            List<Student> studentList = getStudents();
            for (Student student : studentList) {
                newInfo.studentId = student.id;
                mDatabase.insert(
                        getTableName(table),
                        Db.StudentInformation.toContentValues(newInfo));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void removeStudentInfo(Date date, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            List<Student> studentList = getStudents();
            for (Student student : studentList) {
                List<StudentInfo> infoList = getStudentInfo(student.id, table);

                String where = Db.StudentInformation.COLUMN_STUDENT_ID + " =? " +
                        " and " + Db.StudentInformation.COLUMN_DATE + " =? ";
                String[] whereArgs = { "" + student.id, "" + date.getTime()};
                mDatabase.delete(getTableName(table), where, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<StudentInfo> getStudentInfo(long studentId, short table) {
        String sql = "select * from " + getTableName(table) +
                " where " + Db.StudentInformation.COLUMN_STUDENT_ID + " =? " +
                " order by " + Db.StudentInformation.COLUMN_DATE;
        String[] args = {"" + studentId};
        return Db.StudentInformation.parseCursor(mDatabase.query(sql, args));
    }

    // Additional

    private void clearAllStudentInfo(long studentId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.StudentInformation.COLUMN_STUDENT_ID + " =? ";
            String[] whereArgs = {"" + studentId};
            mDatabase.delete(Db.StudentInformation.TABLE_ATTENDANCE, where, whereArgs);
            mDatabase.delete(Db.StudentInformation.TABLE_PRIVATE_TASKS, where, whereArgs);
            mDatabase.delete(Db.StudentInformation.TABLE_TESTS, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private void addEmptyStudentInfo(long studentId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            StudentInfo newInfo = new StudentInfo();
            newInfo.studentId = studentId;
            newInfo.wasGood = false;

            List<StudentInfo> tempList = getStudentInfo(0, STUDENT_ATTENDANCE);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInformation.TABLE_ATTENDANCE,
                        Db.StudentInformation.toContentValues(newInfo));
            }

            tempList = getStudentInfo(0, STUDENT_PRIVATE_TASK);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInformation.TABLE_PRIVATE_TASKS,
                        Db.StudentInformation.toContentValues(newInfo));
            }

            tempList = getStudentInfo(0, STUDENT_TEST);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInformation.TABLE_TESTS,
                        Db.StudentInformation.toContentValues(newInfo));
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private String getTableName(short table) {
        switch (table) {
            case STUDENT_ATTENDANCE:    return Db.StudentInformation.TABLE_ATTENDANCE;
            case STUDENT_PRIVATE_TASK:  return Db.StudentInformation.TABLE_PRIVATE_TASKS;
            case STUDENT_TEST:          return Db.StudentInformation.TABLE_TESTS;
            default:
                Timber.e("Wrong table name!");
                return Db.StudentInformation.TABLE_ATTENDANCE;
        }
    }

}
