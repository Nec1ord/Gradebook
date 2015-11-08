package com.nikolaykul.gradebook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    @Bind(R.id.student_list) RecyclerView mRecyclerView;
    @Inject Context mContext;
    @Inject Database mDatabase;
    private List<Student> mStudents;
    private AlertDialog mNewStudentDialog;
    private StudentListViewHolder.StudentListener mListener =
            student -> Timber.i("student: id = %d, name = %s", student.id, student.fullName);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mStudents = mDatabase.getStudents();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        ButterKnife.bind(this, view);
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
        // TODO search & add & delete.
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new EasyRecyclerAdapter<>(
                mContext,
                StudentListViewHolder.class,
                mStudents,
                mListener));
    }

    private void refreshList() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void showNewStudentDialog() {
        mNewStudentDialog = new AlertDialog.Builder(getActivity())
                .setView(createViewForDialog())
                .create();
        mNewStudentDialog.show();
    }

    private View createViewForDialog() {
        View layout =
                getActivity().getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        EditText etStudentName = (EditText) layout.findViewById(R.id.student_name);
        FloatingActionButton fab =
                (FloatingActionButton) layout.findViewById(R.id.fab);

        fab.setOnClickListener(iView -> {
            fab.setEnabled(false);
            String studentName = etStudentName.getText().toString();
            if (!studentName.isEmpty()) {
                Student newStudent = new Student(studentName);
                newStudent.id = mDatabase.insertStudent(newStudent);
                mStudents.add(newStudent);
                refreshList();
                Toast.makeText(mContext,
                        R.string.dialog_add_student_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,
                        R.string.dialog_add_student_error,
                        Toast.LENGTH_SHORT).show();
            }
            KeyboardUtil.hideKeyboard(getActivity());
            if (null != mNewStudentDialog) mNewStudentDialog.dismiss();
            fab.setEnabled(true);
        });

        return layout;
    }

}
