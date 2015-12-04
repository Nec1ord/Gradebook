package com.nikolaykul.gradebook.di.component;

import android.content.Context;

import com.nikolaykul.gradebook.GradeApplication;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.local.MPreferences;
import com.nikolaykul.gradebook.di.module.ApplicationModule;
import com.nikolaykul.gradebook.di.module.DataModule;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DataModule.class})
public interface ApplicationComponent {

    void inject(GradeApplication application);

    Context context();
    Database database();
    MPreferences preferences();
    Bus bus();
}
