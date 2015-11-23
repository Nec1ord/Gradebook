package com.nikolaykul.gradebook.data.local;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.db.Db;
import com.nikolaykul.gradebook.data.local.db.DbOpenHelper;
import com.nikolaykul.gradebook.data.model.Attendance;
import com.nikolaykul.gradebook.data.model.PrivateTask;
import com.nikolaykul.gradebook.data.model.Student;
import com.nikolaykul.gradebook.data.model.Group;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import timber.log.Timber;

public class Database {
    public static final short STUDENT_CONTROL_TASK = 0;
    public static final short STUDENT_TEST = 1;
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

    // Attendance

    public void insertAttendance(Attendance attendance, long studentsGroupId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            attendance.setAbsent(false);
            List<Student> students = getStudents(studentsGroupId);
            for (Student student : students) {
                attendance.setStudentId(student.getId());
                mDatabase.insert(
                        Db.AttendanceTable.TABLE_NAME,
                        Db.AttendanceTable.toContentValues(attendance));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void removeAttendance(Attendance attendance, long studentsGroupId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            final String dateMills = "" + attendance.getDate().getMillis();
            List<Student> students = getStudents(studentsGroupId);
            for (Student student : students) {
                String where = Db.AttendanceTable.COLUMN_STUDENT_ID + " =? " +
                        " and " + Db.AttendanceTable.COLUMN_DATE + " =? ";
                String[] whereArgs = { "" + student.getId(), dateMills};
                mDatabase.delete(Db.AttendanceTable.TABLE_NAME, where, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<Attendance> getAttendances(long studentId) {
        String sql = "select * from " + Db.AttendanceTable.TABLE_NAME +
                " where " + Db.AttendanceTable.COLUMN_STUDENT_ID + " =? " +
                " order by " + Db.AttendanceTable.COLUMN_DATE;
        String[] args = {"" + studentId};
        return Db.AttendanceTable.parseCursor(mDatabase.query(sql, args));
    }

    // PrivateTask

    /**
     * @param task with pre-filled fields {date, title, content}.
     */
    public void insertPrivateTask(PrivateTask task, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            task.setPassed(false);
            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                task.setStudentId(student.getId());
                mDatabase.insert(
                        getTableName(table),
                        Db.PrivateTaskTable.toContentValues(task));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void removePrivateTask(PrivateTask task, long studentsGroupId, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            List<Student> studentList = getStudents(studentsGroupId);
            for (Student student : studentList) {
                String where = Db.PrivateTaskTable.COLUMN_STUDENT_ID + " =? " +
                        " and " + Db.PrivateTaskTable.COLUMN_TITLE + " =? ";
                String[] whereArgs = { "" + student.getId(), task.getTitle()};
                mDatabase.delete(getTableName(table), where, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public List<PrivateTask> getPrivateTasks(long studentId, short table) {
        String sql = "select * from " + getTableName(table) +
                " where " + Db.PrivateTaskTable.COLUMN_STUDENT_ID + " =? " +
                " order by " + Db.PrivateTaskTable.COLUMN_DATE;
        String[] args = {"" + studentId};
        return Db.PrivateTaskTable.parseCursor(mDatabase.query(sql, args));
    }

    public void updateStudentInfo(PrivateTask task, short table) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.PrivateTaskTable.COLUMN_ID + " =? ";
            String[] whereArgs = {""+task.getId()};
            mDatabase.update(
                    getTableName(table),
                    Db.PrivateTaskTable.toContentValues(task),
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
     *  Clear information from
     * {@link com.nikolaykul.gradebook.data.local.db.Db.AttendanceTable} and
     * {@link com.nikolaykul.gradebook.data.local.db.Db.PrivateTaskTable} where {@param studentId}.
     */
    private void clearAllStudentInfo(long studentId) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            String where = Db.PrivateTaskTable.COLUMN_STUDENT_ID + " =? ";
            String[] whereArgs = {"" + studentId};
            mDatabase.delete(Db.AttendanceTable.TABLE_NAME, where, whereArgs);
            mDatabase.delete(Db.PrivateTaskTable.TABLE_CONTROL_TASK, where, whereArgs);
            mDatabase.delete(Db.PrivateTaskTable.TABLE_TEST, where, whereArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    /**
     *  Add empty information at
     * {@link com.nikolaykul.gradebook.data.local.db.Db.AttendanceTable} and
     * {@link com.nikolaykul.gradebook.data.local.db.Db.PrivateTaskTable}.
     */
    private void addEmptyStudentInfo(Student student) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            // get a student from the same group
            Student someStudent = getStudents(student.getGroupId()).get(0);
            long someStudentId = someStudent.getId();

            Attendance newAttendance = new Attendance()
                    .setStudentId(student.getId())
                    .setAbsent(true);

            PrivateTask newTask = new PrivateTask()
                    .setStudentId(student.getId())
                    .setPassed(false);

            // get attendances for this group
            // and insert new ones with the same information except studentId
            List<Attendance> attendances = getAttendances(someStudentId);
            for (Attendance attendance : attendances) {
                newAttendance.setDate(attendance.getDate());
                mDatabase.insert(
                        Db.AttendanceTable.TABLE_NAME,
                        Db.AttendanceTable.toContentValues(newAttendance));
            }

            // get control tasks from this group
            // and insert new ones with the same information except studentId
            List<PrivateTask> tasks = getPrivateTasks(someStudentId, STUDENT_CONTROL_TASK);
            for (PrivateTask task : tasks) {
                newTask.setDate(task.getDate())
                        .setTitle(task.getTitle())
                        .setContent(task.getContent());
                mDatabase.insert(
                        Db.PrivateTaskTable.TABLE_CONTROL_TASK,
                        Db.PrivateTaskTable.toContentValues(newTask));
            }

            // get control tasks from this group
            // and insert new ones with the same information except studentId
            tasks = getPrivateTasks(someStudentId, STUDENT_TEST);
            for (PrivateTask task : tasks) {
                newTask.setDate(task.getDate())
                        .setTitle(task.getTitle())
                        .setContent(task.getContent());
                mDatabase.insert(
                        Db.PrivateTaskTable.TABLE_TEST,
                        Db.PrivateTaskTable.toContentValues(newTask));
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private String getTableName(short table) {
        switch (table) {
            case STUDENT_TEST:          return Db.PrivateTaskTable.TABLE_TEST;
            case STUDENT_CONTROL_TASK:  return Db.PrivateTaskTable.TABLE_CONTROL_TASK;
            default:
                Timber.e("Wrong table name!");
                return Db.PrivateTaskTable.TABLE_TEST;
        }
    }

}
