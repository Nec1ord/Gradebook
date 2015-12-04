package com.nikolaykul.gradebook.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.nikolaykul.gradebook.adapter.GroupAdapter;
import com.nikolaykul.gradebook.adapter.OldGroupViewHolder;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.Model;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class GroupFragment extends SimpleListFragment {
    private static final String BUNDLE_TAB_NUM = "tubNum";
    private int mTabNum;

    public static GroupFragment newInstance(int tabNum) {
        GroupFragment fragment = new GroupFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAB_NUM, tabNum);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabNum = null != getArguments() ? getArguments().getInt(BUNDLE_TAB_NUM, -1) : -1;
    }

    @Subscribe public void onFabClick(FloatingActionButtonEvent event) {
        if (event.currentTabNum != mTabNum) return;
        super.showDialogToAddNewModel(event);
    }

    @Override protected Model createNewModel(String name) {
        return new Group(name);
    }

    @Override protected List<Model> getModels(Database database) {
        ArrayList<Model> models = new ArrayList<>();
        models.addAll(database.getGroups());
        return models;
    }

    @Override protected RecyclerView.Adapter getAdapter(Context context, List<Model> models) {
        return new GroupAdapter(mDatabase.getGroups(), mBus);
//        return new EasyRecyclerAdapter<>(context,
//                OldGroupViewHolder.class,
//                models,
//                (OldGroupViewHolder.StudentGroupListener) mBus::post); // using Bus to notify onClick
    }

}
