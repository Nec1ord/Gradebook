package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.fragments.StudentInfoFragment;
import com.nikolaykul.gradebook.fragments.StudentListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StudentMainActivity extends BaseActivity {
    private static final byte TABLE_ID = Database.STUDENT_ATTENDANCE;
    private static final long GROUP_ID = 0;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        setStudentListFragment();
    }

    private void setStudentListFragment() {
        StudentListFragment fragmentList = (StudentListFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);

        if (null == fragmentList) {
            fragmentList = StudentListFragment.getInstance(GROUP_ID);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragmentList)
                    .commit();
        }
    }

    private void setStudentInfoFragment() {
        StudentInfoFragment fragmentInfo = (StudentInfoFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);

        if (null == fragmentInfo) {
            fragmentInfo = StudentInfoFragment.getInstance(TABLE_ID, GROUP_ID);
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
