package com.nikolaykul.gradebook.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.Model;
import com.nikolaykul.gradebook.data.model.Student;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.event.GroupDeletedEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StudentFragment extends ListModelFactoryFragment {
    private static final String BUNDLE_TAB_NUM = "tubNum";
    private static final String BUNDLE_GROUP_ID = "groupId";
    private int mTabNum;
    private long mGroupId;

    public static StudentFragment newInstance(int tabNum, long groupId) {
        StudentFragment fragment = new StudentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAB_NUM, tabNum);
        bundle.putLong(BUNDLE_GROUP_ID, groupId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mTabNum = getArguments().getInt(BUNDLE_TAB_NUM, -1);
            mGroupId = getArguments().getLong(BUNDLE_GROUP_ID, -1);
        } else {
            mTabNum = -1;
            mGroupId = -1;
        }
        super.onCreate(savedInstanceState);
    }

    @Subscribe public void onGroupSelected(final Group group) {
        mGroupId = group.getId();
        super.refreshList();
    }

    @Subscribe public void onGroupDeleted(final GroupDeletedEvent event) {
        if (mGroupId == event.group.getId()) {
            mGroupId = -1;
            super.refreshList();
        }
    }

    @Subscribe public void onFabClicked(FloatingActionButtonEvent event) {
        if (event.currentTabNum != mTabNum) return;
        if (-1 == mGroupId) {
            View focusedView = mActivity.getCurrentFocus();
            if (null != focusedView) {
                Snackbar.make(focusedView,
                        R.string.error_missing_group,
                        Snackbar.LENGTH_SHORT).show();
            }
            return;
        }
        super.showDialogToAddNewModel(event);
    }

    @Override protected Model createNewModel(String name) {
        return new Student(mGroupId, name);
    }

    @Override protected List<Model> getModels(Database database) {
        ArrayList<Model> models = new ArrayList<>();
        models.addAll(database.getStudents(mGroupId));
        return models;
    }

}
