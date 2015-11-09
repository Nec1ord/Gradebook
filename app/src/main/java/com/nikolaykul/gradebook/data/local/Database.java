package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentGroup;
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

    // StudentGroup

    public long insertStudentGroup(StudentGroup group) {
        long id = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            id = mDatabase.insert(
                    Db.StudentGroupTable.TABLE_NAME, Db.StudentGroupTable.toContentValues(group));
            if (id >= 0) {
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
        return id;
    }

    public void removeStudentGroup(long id) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            clearAllStudents(id);
            final String where = Db.StudentGroupTable.COLUMN_ID + " =? ";
            final String[] whereArgs = {"" + id};
            mDatabase.delete(Db.StudentGroupTable.TABLE_NAME, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<StudentGroup> getStudentGroups() {
        final String sql = "select * from " + Db.StudentGroupTable.TABLE_NAME;
        return Db.StudentGroupTable.parseCursor(mDatabase.query(sql));
    }

    // Student

    public long insertStudent(Student student) {
        long id = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            id = mDatabase.insert(
                    Db.StudentTable.TABLE_NAME, Db.StudentTable.toContentValues(student));
            if (id >= 0) {
                addEmptyStudentInfo(student);
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

    public List<Student> getStudents(long groupId) {
        final String sql = "select * from " + Db.StudentTable.TABLE_NAME +
                " where " + Db.StudentTable.COLUMN_GROUP_ID + " =? ";

        return Db.StudentTable.parseCursor(mDatabase.query(sql, "" + groupId));
    }

    // StudentInfo

    public void insertStudentInfo(Date date, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            StudentInfo newInfo = new StudentInfo();
            newInfo.wasGood = false;
            newInfo.date = date;

            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                newInfo.studentId = student.id;
                mDatabase.insert(
                        getTableName(table),
                        Db.StudentInfoTable.toContentValues(newInfo));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void removeStudentInfo(Date date, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                String where = Db.StudentInfoTable.COLUMN_STUDENT_ID + " =? " +
                        " and " + Db.StudentInfoTable.COLUMN_DATE + " =? ";
                String[] whereArgs = { "" + student.id, "" + date.getTime()};
                mDatabase.delete(getTableName(table), where, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<StudentInfo> getStudentInfos(long studentId, short table) {
        String sql = "select * from " + getTableName(table) +
                " where " + Db.StudentInfoTable.COLUMN_STUDENT_ID + " =? " +
                " order by " + Db.StudentInfoTable.COLUMN_DATE;
        String[] args = {"" + studentId};
        return Db.StudentInfoTable.parseCursor(mDatabase.query(sql, args));
    }

    public void updateStudentInfo(StudentInfo info, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.StudentInfoTable.COLUMN_ID + " =? ";
            String[] whereArgs = {""+info.id};
            mDatabase.update(
                    getTableName(table),
                    Db.StudentInfoTable.toContentValues(info),
                    where,
                    whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    // Additional

    private void clearAllStudents(long groupId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            List<Student> students = getStudents(groupId);
            for (Student student : students) {
                removeStudent(student.id);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private void clearAllStudentInfo(long studentId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.StudentInfoTable.COLUMN_STUDENT_ID + " =? ";
            String[] whereArgs = {"" + studentId};
            mDatabase.delete(Db.StudentInfoTable.TABLE_ATTENDANCE, where, whereArgs);
            mDatabase.delete(Db.StudentInfoTable.TABLE_PRIVATE_TASKS, where, whereArgs);
            mDatabase.delete(Db.StudentInfoTable.TABLE_TESTS, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private void addEmptyStudentInfo(Student student) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            long someStudentId = getStudents(student.groupId).get(0).id;

            StudentInfo newInfo = new StudentInfo();
            newInfo.studentId = student.id;
            newInfo.wasGood = false;

            List<StudentInfo> tempList = getStudentInfos(someStudentId, STUDENT_ATTENDANCE);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInfoTable.TABLE_ATTENDANCE,
                        Db.StudentInfoTable.toContentValues(newInfo));
            }

            tempList = getStudentInfos(someStudentId, STUDENT_PRIVATE_TASK);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInfoTable.TABLE_PRIVATE_TASKS,
                        Db.StudentInfoTable.toContentValues(newInfo));
            }

            tempList = getStudentInfos(someStudentId, STUDENT_TEST);
            for (StudentInfo info : tempList) {
                newInfo.date = info.date;
                mDatabase.insert(
                        Db.StudentInfoTable.TABLE_TESTS,
                        Db.StudentInfoTable.toContentValues(newInfo));
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private String getTableName(short table) {
        switch (table) {
            case STUDENT_ATTENDANCE:    return Db.StudentInfoTable.TABLE_ATTENDANCE;
            case STUDENT_PRIVATE_TASK:  return Db.StudentInfoTable.TABLE_PRIVATE_TASKS;
            case STUDENT_TEST:          return Db.StudentInfoTable.TABLE_TESTS;
            default:
                Timber.e("Wrong table name!");
                return Db.StudentInfoTable.TABLE_ATTENDANCE;
        }
    }

}
