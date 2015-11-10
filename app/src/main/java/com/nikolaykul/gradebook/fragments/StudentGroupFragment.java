package com.nikolaykul.gradebook.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.models.StudentGroup;
import com.nikolaykul.gradebook.utils.KeyboardUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StudentGroupFragment extends BaseFragment {
    @Bind(R.id.gridLayout) GridLayout mGridLayout;
    @Inject Activity mActivity;
    @Inject Database mDatabase;
    private float mGroupsTextSize;
    private List<StudentGroup> mGroups;
    private AlertDialog mDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mGroups = mDatabase.getStudentGroups();
        mGroupsTextSize = getResources().getDimension(R.dimen.student_group_text_size);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_group, container, false);
        ButterKnife.bind(this, view);
        refreshContainer();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_student_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: showNewGroupDialog(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        if (null != mDialog) mDialog.dismiss();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void refreshContainer() {
        mGridLayout.removeAllViews();
        for (StudentGroup group : mGroups) {
            mGridLayout.addView(createViewContent(group));
        }
    }

    private TextView createViewContent(StudentGroup group) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                0,
                (int) getResources().getDimension(R.dimen.student_group_layout_height));
        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(linearParams);
        gridParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);

        TextView tv = new TextView(mActivity);
        tv.setLayoutParams(gridParams);
        tv.setSingleLine(true);
        tv.setGravity(Gravity.CENTER);
        tv.setText(group.name);
        tv.setTextSize(mGroupsTextSize);
        tv.setTextColor(ContextCompat.getColor(mActivity, android.R.color.black));

        // setBackground
        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.circle_gradient);
        if (Build.VERSION.SDK_INT < 16) {
            tv.setBackgroundDrawable(drawable);
        } else {
            tv.setBackground(drawable);
        }

        tv.setTag(group);
        tv.setOnClickListener(iView -> {
            StudentGroup currentGroup = (StudentGroup) tv.getTag();
            // TODO:
            Timber.i("OnClick -> Group: id = %d, name = %s", currentGroup.id, currentGroup.name);
        });
        tv.setOnLongClickListener(iView -> {
            StudentGroup currentGroup = (StudentGroup) tv.getTag();
            showDeleteGroupDialog(currentGroup);
            return true;
        });

        return tv;
    }

    private void showNewGroupDialog() {
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(createViewForDialog())
                .create();
        mDialog.show();
    }

    private void showDeleteGroupDialog(StudentGroup group) {
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(createViewForDialogDelete(group))
                .create();
        mDialog.show();
    }

    private View createViewForDialog() {
        View layout =
                mActivity.getLayoutInflater().inflate(R.layout.dialog_add_student_group, null);
        EditText etGroupName = (EditText) layout.findViewById(R.id.group_name);
        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);

        etGroupName.postDelayed(() -> KeyboardUtil.showKeyboard(mActivity), 50);
        fab.setOnClickListener(iView -> {
            String groupName = etGroupName.getText().toString();
            if (!groupName.isEmpty()) {
                // create group
                StudentGroup newGroup = new StudentGroup(groupName);
                // insert
                newGroup.id = mDatabase.insertStudentGroup(newGroup);
                mGroups.add(newGroup);
                refreshContainer();
                Toast.makeText(mActivity,
                        R.string.dialog_add_student_group_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity,
                        R.string.dialog_add_student_group_error,
                        Toast.LENGTH_SHORT).show();
            }
            KeyboardUtil.hideKeyboard(mActivity);
            if (null != mDialog) mDialog.dismiss();
            fab.setEnabled(true);
        });

        return layout;
    }

    private View createViewForDialogDelete(StudentGroup group) {
        View layout =
                mActivity.getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.message);
        ImageButton btnOk = (ImageButton) layout.findViewById(R.id.ok);
        ImageButton btnNo = (ImageButton) layout.findViewById(R.id.no);

        String message = getResources().getString(R.string.dialog_delete_student_group_message);
        tvMessage.setText(String.format(message, group.name));
        btnOk.setOnClickListener(iView -> {
            mDatabase.removeStudentGroup(group.id);
            mGroups.remove(group);
            refreshContainer();
            if (null != mDialog) mDialog.dismiss();
        });
        btnNo.setOnClickListener(iView -> {
            if (null != mDialog) mDialog.dismiss();
        });

        return layout;
    }

}
