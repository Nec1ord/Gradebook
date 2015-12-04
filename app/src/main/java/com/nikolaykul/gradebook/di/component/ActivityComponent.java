package com.nikolaykul.gradebook.di.component;

import android.app.Activity;

import com.nikolaykul.gradebook.activity.StartActivity;
import com.nikolaykul.gradebook.di.module.ActivityModule;
import com.nikolaykul.gradebook.di.scope.PerActivity;
import com.nikolaykul.gradebook.fragment.ListModelFactoryFragment;
import com.nikolaykul.gradebook.fragment.InformationFragment;

import dagger.Component;

@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    void inject(StartActivity startActivity);
    void inject(ListModelFactoryFragment listModelFactoryFragment);
    void inject(InformationFragment informationFragment);

    Activity activity();
}
