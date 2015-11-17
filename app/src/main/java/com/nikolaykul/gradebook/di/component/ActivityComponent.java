package com.nikolaykul.gradebook.di.component;

import android.app.Activity;

import com.nikolaykul.gradebook.activity.StartActivity;
import com.nikolaykul.gradebook.di.module.ActivityModule;
import com.nikolaykul.gradebook.di.scope.PerActivity;
import com.nikolaykul.gradebook.fragment.StudentGroupListFragment;
import com.nikolaykul.gradebook.fragment.StudentInfoFragment;
import com.nikolaykul.gradebook.fragment.StudentListFragment;

import dagger.Component;

@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    void inject(StartActivity startActivity);
    void inject(StudentListFragment studentListFragment);
    void inject(StudentInfoFragment studentInfoFragment);
    void inject(StudentGroupListFragment studentGroupListFragment);

    Activity activity();
}
