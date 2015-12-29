package com.nikolaykul.gradebook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.adapter.SingleStudentAdapter;
import com.nikolaykul.gradebook.adapter.SingleStudentAdapter.Item;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Student;
import com.nikolaykul.gradebook.event.GroupDeletedEvent;
import com.nikolaykul.gradebook.event.StudentDeletedEvent;
import com.nikolaykul.gradebook.event.StudentUpdatedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DrawerFragment extends BaseFragment {
    @Bind(R.id.title) TextView tvTitle;
    @Bind(R.id.recycleView) RecyclerView mRecyclerView;
    @Inject Bus mBus;
    @Inject Database mDatabase;
    @Inject Activity mActivity;
    private Student mStudent;

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateList();
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Subscribe public void onGroupDeleted(final GroupDeletedEvent event) {
        if (mStudent != null && mStudent.getGroupId() == event.group.getId()) {
            mStudent = null;
            populateList();
        }
    }

    @Subscribe public void onStudentDeleted(final StudentDeletedEvent event) {
        if (mStudent != null && mStudent.equals(event.student)) {
            mStudent = null;
            populateList();
        }
    }

    @Subscribe public void onStudentUpdated(final StudentUpdatedEvent event) {
        if (mStudent != null && mStudent.equals(event.student)) {
            mStudent = event.student;
            tvTitle.setText(mStudent.getTitle()); // we need to change only title here
        }
    }

    @Subscribe public void onStudentClicked(final Student student) {
        mStudent = student;
        populateList();
    }

    private void populateList() {
        if (null == mStudent) {
            tvTitle.setText(R.string.app_name);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }

        tvTitle.setText(mStudent.getTitle());
        mRecyclerView.setVisibility(View.VISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.Adapter adapter = new SingleStudentAdapter(
                mActivity, mBus, mDatabase, mStudent.getId(), getListForAdapter());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
    }

    private List<Item> getListForAdapter() {
        ArrayList<Item> items = new ArrayList<>(3);

        int infoTable = Database.TABLE_ATTENDANCE;
        items.add(new Item(infoTable,
                getResources().getString(R.string.title_attendance),
                mDatabase.getInformation(mStudent.getId(), infoTable)));

        infoTable = Database.TABLE_CONTROL_TASK;
        items.add(new SingleStudentAdapter.Item(infoTable,
                getResources().getString(R.string.title_control_tasks),
                mDatabase.getInformation(mStudent.getId(), infoTable)));

        infoTable = Database.TABLE_TEST;
        items.add(new Item(infoTable,
                getResources().getString(R.string.title_tests),
                mDatabase.getInformation(mStudent.getId(), infoTable)));
        return items;
    }

}
