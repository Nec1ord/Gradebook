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
import com.nikolaykul.gradebook.data.local.MPreferences;
import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.fragment.GroupFragment;
import com.nikolaykul.gradebook.fragment.InformationFragment;
import com.nikolaykul.gradebook.fragment.StudentFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("unused")
public class StartActivity extends BaseActivity {
    private static final String BUNDLE_GROUP = "group";
    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout mCollapsingLayout;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewPager) ViewPager mViewPager;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.drawerLayout) DrawerLayout mDrawer;
    @Inject Bus mBus;
    @Inject Database mDatabase;
    @Inject MPreferences mPreferences;
    private Group mSelectedGroup;

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
            case android.R.id.home: mDrawer.openDrawer(GravityCompat.START); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_GROUP, mSelectedGroup);
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
        mSelectedGroup = group;
        mCollapsingLayout.setTitle(group.getTitle());
    }

    private void init(Bundle savedState) {
        if (null != savedState) {
            mSelectedGroup = savedState.getParcelable(BUNDLE_GROUP);
            if (null != mSelectedGroup) mCollapsingLayout.setTitle(mSelectedGroup.getTitle());
        } else {
            // select 1st in the database
            List<Group> allGroups = mDatabase.getGroups();
            if (!allGroups.isEmpty()) {
                mSelectedGroup = allGroups.get(0);
                mPreferences.get(mSelectedGroup).putLastSelectedPosition(0);
                mCollapsingLayout.setTitle(mSelectedGroup.getTitle());
            } else {
                mCollapsingLayout.setTitle(getResources().getString(R.string.app_name));
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
        long selectedGroupId = mSelectedGroup.getId();
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(
                GroupFragment.newInstance(0),
                "Groups");
        adapter.addFragment(
                StudentFragment.newInstance(1, selectedGroupId),
                "Students");
        adapter.addFragment(
                InformationFragment.newInstance(2, Database.TABLE_ATTENDANCE, selectedGroupId),
                "Attendance");
        adapter.addFragment(
                InformationFragment.newInstance(3, Database.TABLE_CONTROL_TASK, selectedGroupId),
                "Private tasks");
        adapter.addFragment(
                InformationFragment.newInstance(4, Database.TABLE_TEST, selectedGroupId),
                "Tests");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {/* empty */}

            @Override
            public void onPageSelected(int position) {
                mFab.show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {/* empty */}
        });
    }

}
