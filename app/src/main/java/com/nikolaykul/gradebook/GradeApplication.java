package com.nikolaykul.gradebook;

import android.app.Application;

import com.nikolaykul.gradebook.di.component.ApplicationComponent;
import com.nikolaykul.gradebook.di.component.DaggerApplicationComponent;
import com.nikolaykul.gradebook.di.module.ApplicationModule;
import com.nikolaykul.gradebook.di.module.DataModule;

public class GradeApplication extends Application {
    protected ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initInjection();
    }

    private void initInjection() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule())
                .build();
        mApplicationComponent.inject(this);
    }


}
