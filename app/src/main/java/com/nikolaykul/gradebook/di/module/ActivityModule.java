package com.nikolaykul.gradebook.di.module;

import android.app.Activity;

import com.nikolaykul.gradebook.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    Activity provideActivity() {
        return mActivity;
    }

}
