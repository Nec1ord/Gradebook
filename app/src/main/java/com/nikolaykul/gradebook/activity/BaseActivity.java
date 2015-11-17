package com.nikolaykul.gradebook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nikolaykul.gradebook.GradeApplication;
import com.nikolaykul.gradebook.di.HasComponent;
import com.nikolaykul.gradebook.di.component.ActivityComponent;
import com.nikolaykul.gradebook.di.component.DaggerActivityComponent;
import com.nikolaykul.gradebook.di.module.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity implements HasComponent {
    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityComponent();
    }

    protected abstract void setActivityComponent();

    protected ActivityComponent getActivityComponent() {
        if (null == mActivityComponent) {
            mActivityComponent = createActivityComponent();
        }
        return mActivityComponent;
    }

    /**
     * Uses in fragments in order to inject them.
     * @return {@link #mActivityComponent}
     */
    @Override
    public ActivityComponent getComponent() {
        return getActivityComponent();
    }

    private ActivityComponent createActivityComponent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(GradeApplication.getAppComponent(this))
                .activityModule(new ActivityModule(this))
                .build();
    }

}
