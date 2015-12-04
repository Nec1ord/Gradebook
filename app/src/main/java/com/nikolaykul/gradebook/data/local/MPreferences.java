package com.nikolaykul.gradebook.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.Model;

public class MPreferences {
    private static final String MY_PREFERENCES = "shared_preferences";
    private SharedPreferences mPref;

    public MPreferences(Context context) {
        mPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    }

    public ModelPreferencesFactory get(Model model) {
        return new ModelPreferencesFactory(model);
    }

    public class ModelPreferencesFactory {
        private static final String GROUP_PREFERENCES = "group_preferences";
        private static final String STUDENT_PREFERENCES = "student_preferences";
        private String mKey;

        private ModelPreferencesFactory(Model model) {
            mKey = model instanceof Group ? GROUP_PREFERENCES : STUDENT_PREFERENCES;
        }

        public void putLastSelectedPosition(int position) {
            mPref.edit().putInt(mKey, position).apply();
        }

        public int getLastSelectedPosition() {
            return mPref.getInt(mKey, -1);
        }

    }

}
