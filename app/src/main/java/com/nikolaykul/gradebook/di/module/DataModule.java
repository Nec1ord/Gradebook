package com.nikolaykul.gradebook.di.module;

import android.content.Context;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.local.MPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    Database provideDatabase(Context context) {
        return new Database(context);
    }

    @Provides
    @Singleton
    MPreferences providePreferences(Context context) {
        return new MPreferences(context);
    }

}
