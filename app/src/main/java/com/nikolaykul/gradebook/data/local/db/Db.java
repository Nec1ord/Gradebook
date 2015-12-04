package com.nikolaykul.gradebook.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.data.model.Student;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Db {
    public abstract static class GroupTable {
        public final static String TABLE_NAME = "tblGroup";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_NAME = "name";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_NAME + " text);";

        public static ContentValues toContentValues(Group group) {
            ContentValues cv = new ContentValues(1);
            cv.put(COLUMN_NAME, group.getName());
            return cv;
        }

        public static List<Group> parseCursor(Cursor cursor) {
            ArrayList<Group> list = new ArrayList<>();
            if (null == cursor || 0 == cursor.getCount()) return list;

            cursor.moveToFirst();
            do {
                Group group = new Group()
                        .setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
                        .setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                list.add(group);
            } while (cursor.moveToNext());
            cursor.close();

            return list;
        }

    }

    public abstract static class StudentTable {
        public final static String TABLE_NAME = "tblStudent";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_GROUP_ID = "group_id";
        public final static String COLUMN_FULL_NAME = "full_name";

        public static final String CREATE =
                "create table " + TABLE_NAME + " ( " +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_GROUP_ID + " integer, " +
                        COLUMN_FULL_NAME + " text);";

        public static ContentValues toContentValues(Student student) {
            ContentValues cv = new ContentValues(2);
            cv.put(COLUMN_GROUP_ID, student.getGroupId());
            cv.put(COLUMN_FULL_NAME, student.getFullName());
            return cv;
        }

        public static List<Student> parseCursor(Cursor cursor) {
            ArrayList<Student> list = new ArrayList<>();
            if (null == cursor || 0 == cursor.getCount()) return list;

            cursor.moveToFirst();
            do {
                Student student = new Student()
                        .setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
                        .setGroupId(cursor.getLong(cursor.getColumnIndex(COLUMN_GROUP_ID)))
                        .setFullName(cursor.getString(cursor.getColumnIndex(COLUMN_FULL_NAME)));
                list.add(student);
            } while (cursor.moveToNext());
            cursor.close();

            return list;
        }
    }

    public abstract static class InformationTable {
        public final static String TABLE_ATTENDANCE = "tblAttendance";
        public final static String TABLE_CONTROL_TASK = "tblControlTask";
        public final static String TABLE_TEST = "tblTest";
        public final static String COLUMN_ID = "id";
        public final static String COLUMN_STUDENT_ID = "student_id";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_CONTENT = "content";
        public final static String COLUMN_PASSED = "passed";

        public static String createTable(String tableName) {
            return "create table " + tableName + " ( " +
                            COLUMN_ID + " integer primary key autoincrement, " +
                            COLUMN_STUDENT_ID + " integer, " +
                            COLUMN_TITLE + " text, " +
                            COLUMN_CONTENT + " text, " +
                            COLUMN_DATE + " integer, " +
                    COLUMN_PASSED + " integer);";
        }

        public static ContentValues toContentValues(Information info) {
            ContentValues cv = new ContentValues(5);
            cv.put(COLUMN_STUDENT_ID, info.getStudentId());
            cv.put(COLUMN_DATE, info.getDate().getMillis());
            cv.put(COLUMN_TITLE, info.getTitle());
            cv.put(COLUMN_CONTENT, info.getContent());
            cv.put(COLUMN_PASSED, info.isPassed() ? 1 : 0);
            return cv;
        }

        public static List<Information> parseCursor(Cursor cursor) {
            ArrayList<Information> list = new ArrayList<>();
            if (null == cursor || 0 == cursor.getCount()) return list;

            cursor.moveToFirst();
            do {
                Information info = new Information()
                        .setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
                        .setStudentId(cursor.getLong(cursor.getColumnIndex(COLUMN_STUDENT_ID)))
                        .setDate(new DateTime(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))))
                        .setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)))
                        .setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)))
                        .setPassed(cursor.getInt(cursor.getColumnIndex(COLUMN_PASSED)) > 0);
                list.add(info);
            } while (cursor.moveToNext());
            cursor.close();

            return list;
        }
    }

}
