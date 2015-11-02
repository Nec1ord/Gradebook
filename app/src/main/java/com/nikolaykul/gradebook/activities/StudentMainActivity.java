package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.di.HasComponent;
import com.nikolaykul.gradebook.di.component.ActivityComponent;

import java.util.List;

import javax.inject.Inject;

public class StudentMainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

}
