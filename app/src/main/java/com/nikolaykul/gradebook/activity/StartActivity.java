package com.nikolaykul.gradebook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.adapter.SimplePagerAdapter;
import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.fragment.GroupFragment;
import com.nikolaykul.gradebook.fragment.StudentFragment;
import com.nikolaykul.gradebook.fragment.StudentInfoFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("unused")
public class StartActivity extends BaseActivity {
    private static final String BUNDLE_GROUP_ID = "group_id";
    private static final String BUNDLE_GROUP_NAME = "group_name";
    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout mCollapsingLayout;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewPager) ViewPager mViewPager;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.drawerLayout) DrawerLayout mDrawer;
    @Inject Bus mBus;
    @Inject Database mDatabase;
    private long mSelectedGroupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        init(savedInstanceState);
        setToolbar(mToolbar);
        setViewPager(mViewPager);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        mBus.unregister(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        CharSequence title = mCollapsingLayout.getTitle();
        if (null != title) {
            outState.putString(BUNDLE_GROUP_NAME, title.toString());
        }
        outState.putLong(BUNDLE_GROUP_ID, mSelectedGroupId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fab) public void postFloatingActionButtonEvent() {
        int currentTabNum = mViewPager.getCurrentItem();
        mBus.post(new FloatingActionButtonEvent(currentTabNum));
    }

    @Subscribe public void OnGroupSelected(Group group) {
        mSelectedGroupId = group.getId();
        mCollapsingLayout.setTitle(group.getName());
    }

    private void init(Bundle savedState) {
        if (null != savedState) {
            String title = savedState.getString(BUNDLE_GROUP_NAME, "");
            if (!title.isEmpty()) {
                mCollapsingLayout.setTitle(title);
            }
            mSelectedGroupId = savedState.getLong(BUNDLE_GROUP_ID, -1);
        } else {
            // get 1st in the database or '-1' if there are no groups
            List<Group> allGroups = mDatabase.getGroups();
            if (!allGroups.isEmpty()) {
                Group group = allGroups.get(0);
                mCollapsingLayout.setTitle(group.getName());
                mSelectedGroupId = group.getId();
            } else {
                mCollapsingLayout.setTitle(getResources().getString(R.string.app_name));
                mSelectedGroupId = -1;
            }
        }
    }

    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_vector);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setViewPager(ViewPager viewPager) {
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(
                GroupFragment.newInstance(0),
                "Groups");
        adapter.addFragment(
                StudentFragment.newInstance(1, mSelectedGroupId),
                "Students");
        adapter.addFragment(
                StudentInfoFragment.newInstance(2, Database.STUDENT_ATTENDANCE, mSelectedGroupId),
                "Attendance");
        adapter.addFragment(
                StudentInfoFragment.newInstance(3, Database.STUDENT_CONTROL_TASK, mSelectedGroupId),
                "Private tasks");
        adapter.addFragment(
                StudentInfoFragment.newInstance(4, Database.STUDENT_TEST, mSelectedGroupId),
                "Tests");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mFab.show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
