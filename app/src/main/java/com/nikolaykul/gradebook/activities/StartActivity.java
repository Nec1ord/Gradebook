package com.nikolaykul.gradebook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.events.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.data.models.StudentGroup;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class StartActivity extends BaseActivity {
    @Inject Bus mBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @OnClick(R.id.fab) public void postFloatActionButtonEvent() {
        mBus.post(new FloatingActionButtonEvent());
    }

    @Subscribe public void OnGroupClicked(StudentGroup group) {
        Timber.i("group: id = %d, name = %s", group.id, group.name);
    }

}
