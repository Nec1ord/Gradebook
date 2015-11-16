package com.nikolaykul.gradebook.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.adapters.StudentViewHolder;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentGroup;
import com.nikolaykul.gradebook.events.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.events.StudentAddedEvent;
import com.nikolaykul.gradebook.utils.DialogFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class StudentListFragment extends BaseFragment {
    private static final String BUNDLE_TAB_NUM = "tabNum";
    private static final String BUNDLE_GROUP = "group";
    @Bind(R.id.recycleView) RecyclerView mRecyclerView;
    @Inject Activity mActivity;
    @Inject Database mDatabase;
    @Inject Bus mBus;
    private List<Student> mStudents;
    private int mTabNum;
    private long mGroupId;

    public static StudentListFragment newInstance(int tabNum, long groupId) {
        StudentListFragment fragment = new StudentListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAB_NUM, tabNum);
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
        mBus.register(this);

        Bundle args = getArguments();
        if (null != args) {
            mTabNum = args.getInt(BUNDLE_TAB_NUM);
            mGroupId = args.getLong(BUNDLE_GROUP);
        } else {
            mTabNum = 0;
            mGroupId = -1;
        }
        mStudents = mDatabase.getStudents(mGroupId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeToDelete();
        populateList();
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Subscribe public void showNewStudentDialog(FloatingActionButtonEvent event) {
        if (mTabNum != event.currentTabNum) return;

        DialogFactory.getMaterialAddDialog(mActivity, Student.class,
                (materialDialog, dialogAction) -> {
                    materialDialog.dismiss();
                    if (null != materialDialog.getInputEditText()) {
                        String name = materialDialog.getInputEditText().getText().toString();
                        if (!name.isEmpty()) {
                            // create student
                            Student newStudent = new Student(mGroupId, name);
                            // insert
                            newStudent.id = mDatabase.insertStudent(newStudent);
                            addStudent(newStudent, mStudents.size());
                            Toast.makeText(mActivity,
                                    R.string.dialog_add_student_success,
                                    Toast.LENGTH_SHORT).show();
                            mBus.post(new StudentAddedEvent());
                        }
                    }
                })
                .show();
    }

    @Subscribe public void onGroupSelected(StudentGroup group) {
        mGroupId = group.id;
        mStudents.clear();
        mStudents.addAll(mDatabase.getStudents(mGroupId));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void populateList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        EasyRecyclerAdapter adapter = new EasyRecyclerAdapter<>(
                mActivity,
                StudentViewHolder.class,
                mStudents,
                (StudentViewHolder.StudentListener) mBus::post);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mRecyclerView.setAdapter(new SlideInRightAnimationAdapter(adapter));
    }

    private void addStudent(Student student, int position) {
        mStudents.add(position, student);
        mRecyclerView.getAdapter().notifyItemInserted(position);
    }

    private void removeStudent(int position) {
        mStudents.remove(position);
        mRecyclerView.getAdapter().notifyItemRemoved(position);
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
                        final int deletedPosition = viewHolder.getAdapterPosition();
                        final Student deletedStudent = mStudents.get(deletedPosition);

                        // delete student from list
                        removeStudent(deletedPosition);

                        // show Snackbar
                        View focusedView = mActivity.getCurrentFocus();
                        if (null == focusedView) focusedView = mRecyclerView;

                        String message =
                                getResources().getString(R.string.delete_message_successful);
                        message = String.format(message, deletedStudent.fullName);
                        Snackbar.make(focusedView, message, Snackbar.LENGTH_LONG)
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        // if "undo" wasn't called -> delete from database
                                        switch (event) {
                                            case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                                            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                                mDatabase.removeStudent(deletedStudent.id);
                                        }
                                    }
                                })
                                .setActionTextColor(
                                        ContextCompat.getColor(mActivity, R.color.purple_light))
                                .setAction(R.string.action_undo, mView -> {
                                    // if "undo" was called -> restore student in list
                                    addStudent(deletedStudent, deletedPosition);
                                })
                                .show();
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
