package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.StudentGroup;
import com.nikolaykul.gradebook.fragments.StudentGroupFragment;
import com.nikolaykul.gradebook.fragments.StudentInfoFragment;
import com.nikolaykul.gradebook.fragments.StudentListFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StudentMainActivity extends BaseActivity {
    private static final byte TABLE_ID = Database.STUDENT_ATTENDANCE;
    private static final long GROUP_ID = 0;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Inject Bus mBus;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        setSupportActionBar(mToolbar);
        mFragmentManager = getSupportFragmentManager();

        setStudentGroupFragment();
    }

    @Override
    protected void onResume() {
        mBus.register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mBus.unregister(this);
        super.onPause();
    }

    private void setStudentGroupFragment() {
        StudentGroupFragment fragmentGroup = (StudentGroupFragment)
                mFragmentManager.findFragmentById(R.id.container);

        if (null == fragmentGroup) {
            fragmentGroup = new StudentGroupFragment();
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragmentGroup)
                    .commit();
        }
    }

    private void setStudentListFragment() {
        StudentListFragment fragmentList = (StudentListFragment)
                mFragmentManager.findFragmentById(R.id.container);

        if (null == fragmentList) {
            fragmentList = StudentListFragment.getInstance(GROUP_ID);
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragmentList)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void setStudentInfoFragment() {
        StudentInfoFragment fragmentInfo = (StudentInfoFragment)
                mFragmentManager.findFragmentById(R.id.container);

        if (null == fragmentInfo) {
            fragmentInfo = StudentInfoFragment.getInstance(TABLE_ID, GROUP_ID);
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragmentInfo)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Subscribe public void onReceiveStudentGroup(StudentGroup group) {
        Timber.i("Group -> id: %d, name: %s", group.id, group.name);
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
