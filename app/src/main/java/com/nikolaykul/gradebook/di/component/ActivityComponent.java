package com.nikolaykul.gradebook.di.component;

import android.app.Activity;

import com.nikolaykul.gradebook.activities.MainActivity;
import com.nikolaykul.gradebook.di.module.ActivityModule;
import com.nikolaykul.gradebook.di.module.ApplicationModule;
import com.nikolaykul.gradebook.di.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    Activity activity();
}
