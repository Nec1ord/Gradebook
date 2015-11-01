package com.nikolaykul.gradebook;

import android.app.Application;

import com.nikolaykul.gradebook.di.component.ApplicationComponent;
import com.nikolaykul.gradebook.di.component.DaggerApplicationComponent;
import com.nikolaykul.gradebook.di.module.ApplicationModule;
import com.nikolaykul.gradebook.di.module.DataModule;

import timber.log.Timber;

public class GradeApplication extends Application {
    protected ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initInjection();
        initTimber();
    }

    private void initInjection() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule())
                .build();
        mApplicationComponent.inject(this);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
