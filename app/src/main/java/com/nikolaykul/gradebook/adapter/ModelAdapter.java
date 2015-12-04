package com.nikolaykul.gradebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.MPreferences;
import com.nikolaykul.gradebook.data.local.MPreferences.ModelPreferencesFactory;
import com.nikolaykul.gradebook.data.model.Model;
import com.squareup.otto.Bus;

import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {
    private ModelPreferencesFactory mModelPreferencesFactory; // to get & store selected position
    private int mLastSelectedItemPosition;
    private List<Model> mModels;
    private Bus mBus;

    public ModelAdapter(Context context, List<Model> models, Bus bus) {
        if (!models.isEmpty()) {
            mModelPreferencesFactory = new MPreferences(context).with(models.get(0));
            mLastSelectedItemPosition = mModelPreferencesFactory.getLastSelectedPosition();
        }
        mModels = models;
        mBus = bus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvText.setText(mModels.get(position).getTitle());
        holder.tvText.setSelected(mLastSelectedItemPosition == position);
        holder.tvText.setOnClickListener(view -> {
            if (mLastSelectedItemPosition != position){
                notifyItemChanged(mLastSelectedItemPosition);               // remove prev selection
                mLastSelectedItemPosition = position;
                mModelPreferencesFactory.putLastSelectedPosition(position); // store selection
                holder.tvText.setSelected(true);
            }
            mModels.get(position).notifyClicked(mBus);
        });
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView;
        }
    }

}
