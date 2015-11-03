package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.Student;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StudentMainActivity extends BaseActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Inject Database mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addStudent();
        setContentView(R.layout.activity_student_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_student: addStudent(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    private void addStudent() {
        mDatabase.insertStudent(new Student("one"));
        mDatabase.insertStudent(new Student("two"));
        mDatabase.insertStudent(new Student("three"));

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 9, 1);
        mDatabase.insertStudentInfo(calendar.getTime(), Database.STUDENT_ATTENDANCE);
        calendar.set(2012, 9, 5);
        mDatabase.insertStudentInfo(calendar.getTime(), Database.STUDENT_ATTENDANCE);
        calendar.set(2012, 9, 8);
        mDatabase.insertStudentInfo(calendar.getTime(), Database.STUDENT_ATTENDANCE);
        calendar.set(2012, 9, 10);
        mDatabase.insertStudentInfo(calendar.getTime(), Database.STUDENT_ATTENDANCE);
    }

}
