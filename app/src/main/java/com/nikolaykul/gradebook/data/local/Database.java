package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.data.model.Student;
import com.nikolaykul.gradebook.data.model.Group;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import timber.log.Timber;

public class Database {
    public static final short STUDENT_ATTENDANCE = 0;
    public static final short STUDENT_CONTROL_TASK = 1;
    public static final short STUDENT_TEST = 2;
    private BriteDatabase mDatabase;

    public Database(Context context) {
        mDatabase = SqlBrite.create().wrapDatabaseHelper(new DbOpenHelper(context));
    }

    // Group

    public long insertGroup(Group group) {
        long id = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            id = mDatabase.insert(
                    Db.GroupTable.TABLE_NAME,
                    Db.GroupTable.toContentValues(group));
            if (id >= 0) {
                group.setId(id);
                transaction.markSuccessful();
            }
        } finally {
            transaction.end();
        }
        return id;
    }

    public void removeGroup(long id) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            clearAllStudents(id);
            final String where = Db.GroupTable.COLUMN_ID + " =? ";
            final String[] whereArgs = {"" + id};
            mDatabase.delete(Db.GroupTable.TABLE_NAME, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<Group> getGroups() {
        final String sql = "select * from " + Db.GroupTable.TABLE_NAME +
                " order by " + Db.GroupTable.COLUMN_NAME;
        return Db.GroupTable.parseCursor(mDatabase.query(sql));
    }

    // Student

    public long insertStudent(Student student) {
        long id = -1;
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            id = mDatabase.insert(
                    Db.StudentTable.TABLE_NAME,
                    Db.StudentTable.toContentValues(student));
            if (id >= 0) {
                student.setId(id);
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
                " where " + Db.StudentTable.COLUMN_GROUP_ID + " =? " +
                " order by " + Db.StudentTable.COLUMN_FULL_NAME;
        return Db.StudentTable.parseCursor(mDatabase.query(sql, "" + groupId));
    }

    // Information

    /**
     * @param info with pre-filled fields {date, title, content}.
     */
    public void insertInformation(Information info, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            info.setPassed(false);
            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                info.setStudentId(student.getId());
                mDatabase.insert(
                        getInformationTableName(table),
                        Db.InformationTable.toContentValues(info));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void removeInformation(Information info, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                String where = Db.InformationTable.COLUMN_STUDENT_ID + " =? " +
                        " and " + Db.InformationTable.COLUMN_DATE + " =? " +
                        " and " + Db.InformationTable.COLUMN_TITLE + " =? ";
                String[] whereArgs = {"" + student.getId(),
                        "" + info.getDate().getMillis(),
                        info.getTitle()};
                mDatabase.delete(getInformationTableName(table), where, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<Information> getInformation(long studentId, short table) {
        String sql = "select * from " + getInformationTableName(table) +
                " where " + Db.InformationTable.COLUMN_STUDENT_ID + " =? ";
        String[] args = {"" + studentId};
        return Db.InformationTable.parseCursor(mDatabase.query(sql, args));
    }

    public void updateInformation(Information info, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.InformationTable.COLUMN_ID + " =? ";
            String[] whereArgs = {""+info.getId()};
            mDatabase.update(
                    getInformationTableName(table),
                    Db.InformationTable.toContentValues(info),
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
                removeStudent(student.getId());
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    /**
     *  Clear information from {@link Db.InformationTable} where {@param studentId}.
     */
    private void clearAllStudentInfo(long studentId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.InformationTable.COLUMN_STUDENT_ID + " =? ";
            String[] whereArgs = {"" + studentId};
            mDatabase.delete(Db.InformationTable.TABLE_ATTENDANCE, where, whereArgs);
            mDatabase.delete(Db.InformationTable.TABLE_CONTROL_TASK, where, whereArgs);
            mDatabase.delete(Db.InformationTable.TABLE_TEST, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    /**
     *  Get a {@link Student} from the same {@link Group} as {@param student}'s.
     *  Then insert new {@link Information} with the same values as someStudent's
     *  but with different {@link Information#mStudentId} and {@link Information#mPassed}
     *  for every {@link Db.InformationTable}.
     */
    private void addEmptyStudentInfo(Student student) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            // get a student from the same group
            Student someStudent = getStudents(student.getGroupId()).get(0);
            long someStudentId = someStudent.getId();

            Information newInfo = new Information()
                    .setStudentId(student.getId())
                    .setPassed(false);

            // insert at every table

            List<Information> infoList = getInformation(someStudentId, STUDENT_ATTENDANCE);
            for (Information info : infoList) {
                newInfo.setDate(info.getDate())
                        .setTitle(info.getTitle())
                        .setContent(info.getContent());
                mDatabase.insert(
                        Db.InformationTable.TABLE_ATTENDANCE,
                        Db.InformationTable.toContentValues(newInfo));
            }

            infoList = getInformation(someStudentId, STUDENT_CONTROL_TASK);
            for (Information info : infoList) {
                newInfo.setDate(info.getDate())
                        .setTitle(info.getTitle())
                        .setContent(info.getContent());
                mDatabase.insert(
                        Db.InformationTable.TABLE_CONTROL_TASK,
                        Db.InformationTable.toContentValues(newInfo));
            }

            infoList = getInformation(someStudentId, STUDENT_TEST);
            for (Information info : infoList) {
                newInfo.setDate(info.getDate())
                        .setTitle(info.getTitle())
                        .setContent(info.getContent());
                mDatabase.insert(
                        Db.InformationTable.TABLE_TEST,
                        Db.InformationTable.toContentValues(newInfo));
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private String getInformationTableName(short table) {
        switch (table) {
            case STUDENT_ATTENDANCE:    return Db.InformationTable.TABLE_ATTENDANCE;
            case STUDENT_CONTROL_TASK:  return Db.InformationTable.TABLE_CONTROL_TASK;
            case STUDENT_TEST:          return Db.InformationTable.TABLE_TEST;
            default:
                Timber.e("Wrong table name!");
                return Db.InformationTable.TABLE_TEST;
        }
    }

}
