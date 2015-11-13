package com.nikolaykul.gradebook;

import android.app.Application;
import android.content.Context;

import com.bettervectordrawable.Convention;
import com.bettervectordrawable.VectorDrawableCompat;
import com.nikolaykul.gradebook.di.component.ApplicationComponent;
import com.nikolaykul.gradebook.di.component.DaggerApplicationComponent;
import com.nikolaykul.gradebook.di.module.ApplicationModule;
import com.nikolaykul.gradebook.di.module.DataModule;

import timber.log.Timber;

public class GradeApplication extends Application {
    private ApplicationComponent mApplicationComponent;

    public static ApplicationComponent getAppComponent(Context context) {
        return ((GradeApplication) context.getApplicationContext()).mApplicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initInjection();
        initTimber();
        initVectorDrawables();
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

    private void initVectorDrawables() {
        int[] ids = VectorDrawableCompat.findVectorResourceIdsByConvention(
                getResources(),
                R.drawable.class,
                Convention.RESOURCE_NAME_HAS_VECTOR_PREFIX_OR_SUFFIX);
        VectorDrawableCompat.enableResourceInterceptionFor(getResources(), ids);
    }

}
