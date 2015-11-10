package com.nikolaykul.gradebook.di.module;

import android.content.Context;

import com.nikolaykul.gradebook.GradeApplication;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final GradeApplication mApplication;

    public ApplicationModule(GradeApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }

}
