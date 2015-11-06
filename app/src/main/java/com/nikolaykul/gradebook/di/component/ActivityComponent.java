package com.nikolaykul.gradebook.di.component;

import android.app.Activity;

import com.nikolaykul.gradebook.activities.StudentMainActivity;
import com.nikolaykul.gradebook.di.module.ActivityModule;
import com.nikolaykul.gradebook.di.scope.PerActivity;
import com.nikolaykul.gradebook.fragments.StudentInfoFragment;
import com.nikolaykul.gradebook.fragments.StudentFragment;

import dagger.Component;

@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    void inject(StudentMainActivity studentMainActivity);
    void inject(StudentFragment studentFragment);
    void inject(StudentInfoFragment studentInfoFragment);

    Activity activity();
}
