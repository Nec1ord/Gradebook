package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.fragments.StudentInfoFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StudentMainActivity extends BaseActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Inject Database mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        StudentInfoFragment fragmentInfo = (StudentInfoFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);

        if (null == fragmentInfo) {
            fragmentInfo = StudentInfoFragment.getInstance(Database.STUDENT_ATTENDANCE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragmentInfo)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

}
