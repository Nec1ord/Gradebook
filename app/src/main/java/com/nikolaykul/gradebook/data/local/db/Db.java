package com.nikolaykul.gradebook.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentAttendance;
import com.nikolaykul.gradebook.data.models.StudentPrivateTask;
import com.nikolaykul.gradebook.data.models.StudentTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Db {
    public abstract static class StudentTable {
        public final static String TABLE_NAME = "tblStudent";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_FULL_NAME = "full_name";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_FULL_NAME + " text);";

        public static ContentValues toContentValues(Student student) {
            ContentValues cv = new ContentValues(2);
            cv.put(COLUMN_ID, student.id);
            cv.put(COLUMN_FULL_NAME, student.fullName);
            return cv;
        }

        public static List<Student> parseCursor(Cursor cursor) {
            ArrayList<Student> studentList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Student student = new Student();
                student.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                student.fullName =
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME));
                studentList.add(student);
            } while (cursor.moveToNext());
            cursor.close();

            return studentList;
        }
    }

    public abstract static class StudentAttendanceTable {
        public final static String TABLE_NAME = "tblStudentAttendance";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_STUDENT_ID = "student_id";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_WAS_ABSENT = "was_absent";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_STUDENT_ID + " integer, " +
                        COLUMN_DATE + " integer, " +
                        COLUMN_WAS_ABSENT + " integer);";

        public static ContentValues toContentValues(StudentAttendance attendance) {
            ContentValues cv = new ContentValues(4);
            cv.put(COLUMN_ID, attendance.id);
            cv.put(COLUMN_STUDENT_ID, attendance.studentId);
            cv.put(COLUMN_DATE, attendance.date.getTime());
            cv.put(COLUMN_WAS_ABSENT, attendance.wasAbsent ? 1 : 0);
            return cv;
        }

        public static List<StudentAttendance> parseCursor(Cursor cursor) {
            ArrayList<StudentAttendance> attendanceList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                StudentAttendance attendance = new StudentAttendance();
                attendance.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                attendance.studentId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));
                attendance.date =
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                attendance.wasAbsent =
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAS_ABSENT)) > 0;
                attendanceList.add(attendance);
            } while (cursor.moveToNext());
            cursor.close();

            return attendanceList;
        }
    }

    public abstract static class StudentPrivateTaskTable {
        public final static String TABLE_NAME = "tblStudentPrivateTask";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_STUDENT_ID = "student_id";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_HAS_PASSED = "has_passed";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_STUDENT_ID + " integer, " +
                        COLUMN_DATE + " integer, " +
                        COLUMN_HAS_PASSED + " integer);";

        public static ContentValues toContentValues(StudentPrivateTask tasks) {
            ContentValues cv = new ContentValues(4);
            cv.put(COLUMN_ID, tasks.id);
            cv.put(COLUMN_STUDENT_ID, tasks.studentId);
            cv.put(COLUMN_DATE, tasks.date.getTime());
            cv.put(COLUMN_HAS_PASSED, tasks.hasPassed ? 1 : 0);
            return cv;
        }

        public static List<StudentPrivateTask> parseCursor(Cursor cursor) {
            ArrayList<StudentPrivateTask> taskList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                StudentPrivateTask task = new StudentPrivateTask();
                task.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                task.studentId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));
                task.date =
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                task.hasPassed =
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HAS_PASSED)) > 0;
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();

            return taskList;
        }
    }

    public abstract static class StudentTestTable {
        public final static String TABLE_NAME = "tblStudentTest";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_STUDENT_ID = "student_id";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_HAS_PASSED = "has_passed";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_STUDENT_ID + " integer, " +
                        COLUMN_DATE + " integer, " +
                        COLUMN_HAS_PASSED + " integer);";

        public static ContentValues toContentValues(StudentTest test) {
            ContentValues cv = new ContentValues(4);
            cv.put(COLUMN_ID, test.id);
            cv.put(COLUMN_STUDENT_ID, test.studentId);
            cv.put(COLUMN_DATE, test.date.getTime());
            cv.put(COLUMN_HAS_PASSED, test.hasPassed ? 1 : 0);
            return cv;
        }

        public static List<StudentTest> parseCursor(Cursor cursor) {
            ArrayList<StudentTest> testList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                StudentTest test = new StudentTest();
                test.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                test.studentId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));
                test.date =
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                test.hasPassed =
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HAS_PASSED)) > 0;
                testList.add(test);
            } while (cursor.moveToNext());
            cursor.close();

            return testList;
        }
    }
}
