package com.nikolaykul.gradebook.fragment;

import android.app.Activity;
import android.content.Context;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Model;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;

public abstract class SimpleListFragment extends BaseFragment {
    @Bind(R.id.recycleView) RecyclerView mRecyclerView;
    @Inject Bus mBus;
    @Inject Database mDatabase;
    @Inject Activity mActivity;
    private List<Model> mModels;

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.register(this);
        mModels = getModels(mDatabase);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateList();
        setSwipeToDelete();
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    /** Called from children to add new model. */
    public void showDialogToAddNewModel(FloatingActionButtonEvent event) {
        MaterialDialog.SingleButtonCallback positiveCallback =
                (materialDialog, dialogAction) -> {
                    materialDialog.dismiss();
                    if (null != materialDialog.getInputEditText()) {
                        String name = materialDialog.getInputEditText().getText().toString();
                        if (!name.isEmpty()) {
                            // create, insert & notify
                            Model newModel = createNewModel(name);
                            newModel.insertInDatabase(mDatabase);
                            newModel.notifyInserted(mBus);

                            addModelToList(mModels.size(), newModel);
                            Toast.makeText(mActivity,
                                    R.string.message_add_model_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_add_model_title)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_add)
                .input(R.string.dialog_add_model_hint, 0, (materialDialog, charSequence) -> {/* no filter */})
                .onPositive(positiveCallback)
                .build()
                .show();
    }

    private void populateList() {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.Adapter adapter = getAdapter(mActivity, mModels);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mRecyclerView.setAdapter(new SlideInRightAnimationAdapter(adapter));
    }

    /** Called from children to refresh {@link #mModels} and {@link #mRecyclerView}. */
    protected void refreshList() {
        mModels.clear();
        mModels.addAll(getModels(mDatabase));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void addModelToList(int position, Model model) {
        mModels.add(position, model);
        mRecyclerView.getAdapter().notifyItemInserted(position);
    }

    private void deleteModelFromList(int position) {
        mModels.remove(position);
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
                        final Model deletedModel = mModels.get(deletedPosition);

                        deleteModelFromList(deletedPosition);

                        // show Snackbar
                        View focusedView = mActivity.getCurrentFocus();
                        if (null == focusedView) focusedView = mRecyclerView;

                        String message =
                                getResources().getString(R.string.message_delete_model_success);
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
                                                deletedModel.removeFromDatabase(mDatabase);
                                                deletedModel.notifyRemoved(mBus);
                                        }
                                    }
                                })
                                .setActionTextColor(
                                        ContextCompat.getColor(mActivity, R.color.purple_light))
                                .setAction(R.string.action_undo, iView -> {
                                    // if "undo" was called -> restore model in list
                                    addModelToList(deletedPosition, deletedModel);
                                })
                                .show();

                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    protected abstract Model createNewModel(String name);

    protected abstract List<Model> getModels(Database database);

    protected abstract RecyclerView.Adapter getAdapter(Context context, List<Model> models);

}
