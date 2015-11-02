package com.nikolaykul.gradebook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentInfo;
import com.nikolaykul.gradebook.utils.MViewUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StudentDetailsFragment extends BaseFragment {
    @Bind(R.id.details_table) TableLayout mTable;
    @Inject Context mContext;
    @Inject Database mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivityComponent().inject(this);
        populateTable();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void populateTable() {
        List<Student> studentList = mDatabase.getStudents();
        for (Student student : studentList) {
            mTable.addView(getRow(student.id));
        }
    }

    private TableRow getRow(long studentId) {
        TableRow row = new TableRow(mContext);
        row.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        List<StudentInfo> infoList = mDatabase.getInfo(studentId, Database.STUDENT_ATTENDANCE);
        for (StudentInfo info : infoList) {
            row.addView(MViewUtils.getDetailRowView(mContext, info.wasGood));
        }

        return row;
    }

}
