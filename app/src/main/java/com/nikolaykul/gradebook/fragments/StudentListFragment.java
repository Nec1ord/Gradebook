package com.nikolaykul.gradebook.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.adapters.StudentListViewHolder;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.utils.KeyboardUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class StudentListFragment extends BaseFragment {
    private static final String BUNDLE_GROUP = "group";
    @Bind(R.id.student_list) RecyclerView mRecyclerView;
    @Inject Activity mActivity;
    @Inject Database mDatabase;
    private long mGroupId;
    private List<Student> mStudents;
    private AlertDialog mNewStudentDialog;
    private StudentListViewHolder.StudentListener mListener =
            student -> Timber.i("student: id = %d, name = %s", student.id, student.fullName);

    public static StudentListFragment getInstance(long groupId) {
        StudentListFragment fragment = new StudentListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_GROUP, groupId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mGroupId = null != getArguments() ? getArguments().getLong(BUNDLE_GROUP) : 0;
        mStudents = mDatabase.getStudents(mGroupId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        ButterKnife.bind(this, view);
        setSwipeToDelete();
        populateList();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_student_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: showNewStudentDialog(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        if (null != mNewStudentDialog) mNewStudentDialog.dismiss();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void populateList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new EasyRecyclerAdapter<>(
                mActivity,
                StudentListViewHolder.class,
                mStudents,
                mListener));
    }

    private void refreshList() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void setSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int deletedStudentPosition = viewHolder.getAdapterPosition();
                        final Student deletedStudent = mStudents.get(deletedStudentPosition);

                        // delete student from list
                        mStudents.remove(deletedStudentPosition);
                        refreshList();

                        // show Snackbar
                        View iView = mActivity.getCurrentFocus();
                        if (null == iView) iView = new View(mActivity);

                        String message =
                                getResources().getString(R.string.delete_message_successful);
                        message = String.format(message, deletedStudent.fullName);
                        Snackbar.make(iView, message, Snackbar.LENGTH_LONG)
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        // if "undo" wasn't called -> delete student from database
                                        if (deletedStudent.id >= 0 &&
                                                Snackbar.Callback.DISMISS_EVENT_ACTION != event) {
                                            mDatabase.removeStudent(deletedStudent.id);
                                        }
                                    }
                                })
                                .setActionTextColor(
                                        ContextCompat.getColor(mActivity, R.color.purple_light))
                                .setAction(R.string.action_undo, mView -> {
                                    // if "undo" was called -> restore student in list
                                    mStudents.add(deletedStudentPosition, deletedStudent);
                                    deletedStudent.id = -1;
                                    refreshList();
                                })
                                .show();
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showNewStudentDialog() {
        mNewStudentDialog = new AlertDialog.Builder(mActivity)
                .setView(createViewForDialog())
                .create();
        mNewStudentDialog.show();
    }

    private View createViewForDialog() {
        View layout =
                mActivity.getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        EditText etStudentName = (EditText) layout.findViewById(R.id.student_name);
        FloatingActionButton fab =
                (FloatingActionButton) layout.findViewById(R.id.fab);

        etStudentName.postDelayed(() -> KeyboardUtil.showKeyboard(mActivity), 50);
        fab.setOnClickListener(iView -> {
            fab.setEnabled(false);
            String studentName = etStudentName.getText().toString();
            if (!studentName.isEmpty()) {
                // create student
                Student newStudent = new Student(mGroupId, studentName);
                // insert
                newStudent.id = mDatabase.insertStudent(newStudent);
                mStudents.add(newStudent);
                refreshList();
                Toast.makeText(mActivity,
                        R.string.dialog_add_student_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity,
                        R.string.dialog_add_student_error,
                        Toast.LENGTH_SHORT).show();
            }
            KeyboardUtil.hideKeyboard(mActivity);
            if (null != mNewStudentDialog) mNewStudentDialog.dismiss();
            fab.setEnabled(true);
        });

        return layout;
    }

}
