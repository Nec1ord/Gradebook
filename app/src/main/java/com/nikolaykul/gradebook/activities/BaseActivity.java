package com.nikolaykul.gradebook.activities;

import android.support.v7.app.AppCompatActivity;

import com.nikolaykul.gradebook.GradeApplication;
import com.nikolaykul.gradebook.di.component.ActivityComponent;
import com.nikolaykul.gradebook.di.component.DaggerActivityComponent;
import com.nikolaykul.gradebook.di.module.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {
    protected ActivityComponent getComponent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(GradeApplication.getAppComponent(this))
                .activityModule(new ActivityModule(this))
                .build();
    }
}
