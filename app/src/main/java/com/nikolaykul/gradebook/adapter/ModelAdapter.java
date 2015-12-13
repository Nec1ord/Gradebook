package com.nikolaykul.gradebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.local.MPreferences;
import com.nikolaykul.gradebook.data.local.MPreferences.ModelPreferencesFactory;
import com.nikolaykul.gradebook.data.model.Model;
import com.squareup.otto.Bus;

import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {
    private ModelPreferencesFactory mModelPreferencesFactory; // to get & store selected position
    private int mLastSelectedItemPosition;
    private Context mContext;
    private List<Model> mModels;
    private Bus mBus;
    private Database mDatabase;

    public ModelAdapter(Context context, List<Model> models, Bus bus, Database database) {
        if (!models.isEmpty()) {
            mModelPreferencesFactory = new MPreferences(context).with(models.get(0));
            mLastSelectedItemPosition = mModelPreferencesFactory.getLastSelectedPosition();
        }
        mModels = models;
        mBus = bus;
        mDatabase = database;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.etTitle.setText(mModels.get(position).getTitle());
        holder.etTitle.setSelected(mLastSelectedItemPosition == position);
        // set callbacks
        final GestureDetector detector = new GestureDetector(mContext,
                new GestureListener(holder.etTitle, position));
        holder.etTitle.setOnTouchListener((view, event) -> detector.onTouchEvent(event));
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final EditText etTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            etTitle = (EditText) itemView;
            etTitle.setInputType(InputType.TYPE_NULL);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final EditText mView;
        private final int mPosition;
        private final Model mItem;

        public GestureListener(EditText view, int position) {
            mView = view;
            mPosition = position;
            mItem = mModels.get(position);
        }

        /**
         *  Remove previous selection {@see #notifyItemChanged},
         * store current {@link #mPosition} in {@link #mLastSelectedItemPosition},
         * set {@link #mView} selected and notify observers {@see #mItem.notifyClicked}.
         * */
        @Override public boolean onSingleTapConfirmed(MotionEvent e) {
            //  Prevent the selection of the last item in the adapter
            // right after some item was just deleted from the list.
            // Other way it may throw IndexOutOfBoundsException.
            if (mPosition >= getItemCount()) return true;

            if (mLastSelectedItemPosition != mPosition){
                notifyItemChanged(mLastSelectedItemPosition);   // remove prev selection
                mLastSelectedItemPosition = mPosition;
                mView.setSelected(true);
                if (null != mModelPreferencesFactory) {
                    mModelPreferencesFactory.putLastSelectedPosition(mPosition); // store selection
                }
            }
            mItem.notifyClicked(mBus);
            return true;
        }

        /**
         *  Set {@link #mView} editable. When focus has changed set {@link #mView} NOT editable and
         *  remove the focus listener. If {@see #mView.getText} is not empty ->
         *  set new title {@see #mItem.setTitle}, update the database {@see #mItem.updateInDatabase}
         *  and notify observers {@see #mItem.notifyUpdated}.
         * */
        @Override public boolean onDoubleTap(MotionEvent e) {
            mView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            mView.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    mView.setInputType(InputType.TYPE_NULL);
                    mView.setOnFocusChangeListener(null);
                    String newTitle = mView.getText().toString();
                    if (!newTitle.isEmpty()) {
                        mItem.setTitle(newTitle);
                        mItem.updateInDatabase(mDatabase);
                        mItem.notifyUpdated(mBus);
                        notifyItemChanged(mPosition);
                    }
                }
            });
            return true;
        }
    }

}
