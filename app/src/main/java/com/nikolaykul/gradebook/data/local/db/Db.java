package com.nikolaykul.gradebook.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.PrivateTask;
import com.nikolaykul.gradebook.data.model.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Db {
    public abstract static class StudentGroupTable {
        public final static String TABLE_NAME = "tblGroup";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_NAME = "name";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_NAME + " text);";

        public static ContentValues toContentValues(Group group) {
            ContentValues cv = new ContentValues(1);
            cv.put(COLUMN_NAME, group.name);
            return cv;
        }

        public static List<Group> parseCursor(Cursor cursor) {
            ArrayList<Group> list = new ArrayList<>();
            if (null == cursor || 0 == cursor.getCount()) return list;

            cursor.moveToFirst();
            do {
                Group group = new Group();
                group.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                group.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                list.add(group);
            } while (cursor.moveToNext());
            cursor.close();

            return list;
        }

    }

    public abstract static class StudentTable {
        public final static String TABLE_NAME = "tblStudent";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_GROUP_ID = "groupId";
        public final static String COLUMN_FULL_NAME = "full_name";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_GROUP_ID + " integer, " +
                        COLUMN_FULL_NAME + " text);";

        public static ContentValues toContentValues(Student student) {
            ContentValues cv = new ContentValues(2);
            cv.put(COLUMN_GROUP_ID, student.groupId);
            cv.put(COLUMN_FULL_NAME, student.fullName);
            return cv;
        }

        public static List<Student> parseCursor(Cursor cursor) {
            if (null == cursor || 0 == cursor.getCount()) return new ArrayList<>();

            ArrayList<Student> studentList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Student student = new Student();
                student.id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                student.groupId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID));
                student.fullName =
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME));
                studentList.add(student);
            } while (cursor.moveToNext());
            cursor.close();

            return studentList;
        }
    }

    public abstract static class StudentInfoTable {
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

        public static ContentValues toContentValues(PrivateTask info) {
            ContentValues cv = new ContentValues(3);
            cv.put(COLUMN_STUDENT_ID, info.studentId);
            cv.put(COLUMN_DATE, info.date.getTime());
            cv.put(COLUMN_WAS_GOOD, info.wasGood ? 1 : 0);
            return cv;
        }

        public static List<PrivateTask> parseCursor(Cursor cursor) {
            if (null == cursor || 0 == cursor.getCount()) return new ArrayList<>();

            ArrayList<PrivateTask> infoList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                PrivateTask info = new PrivateTask();
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
