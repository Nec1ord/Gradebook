package com.nikolaykul.gradebook.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentInfo;

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

    public abstract static class StudentInformation {
        public final static String TABLE_ATTENDANCE = "tblStudentAttendance";
        public final static String TABLE_PRIVATE_TASKS = "tblStudentPrivateTasks";
        public final static String TABLE_TESTS = "tblStudentTests";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_STUDENT_ID = "student_id";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_WAS_GOOD = "was_good";

        public static String createInfoTable(String tableName) {
            return "create table " + tableName + " ( " +
                            COLUMN_ID + " integer primary key autoincrement, " +
                            COLUMN_STUDENT_ID + " integer, " +
                            COLUMN_DATE + " integer, " +
                            COLUMN_WAS_GOOD + " integer);";
        }

        public static ContentValues toContentValues(StudentInfo info) {
            ContentValues cv = new ContentValues(4);
            cv.put(COLUMN_ID, info.id);
            cv.put(COLUMN_STUDENT_ID, info.studentId);
            cv.put(COLUMN_DATE, info.date.getTime());
            cv.put(COLUMN_WAS_GOOD, info.wasGood ? 1 : 0);
            return cv;
        }

        public static List<StudentInfo> parseCursor(Cursor cursor) {
            if (null == cursor || 0 == cursor.getCount()) return null;

            ArrayList<StudentInfo> infoList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                StudentInfo info = new StudentInfo();
                info.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                info.studentId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));
                info.date =
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                info.wasGood =
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAS_GOOD)) > 0;
                infoList.add(info);
            } while (cursor.moveToNext());
            cursor.close();

            return infoList;
        }
    }

}
