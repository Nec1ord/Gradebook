package com.nikolaykul.gradebook.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.model.Group;
import com.squareup.otto.Bus;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private int mLastSelectedItemPosition;
    private List<Group> mGroups;
    private Bus mBus;

    public GroupAdapter(List<Group> groups, Bus bus) {
        mLastSelectedItemPosition = -1;
        mGroups = groups;
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
        holder.tvText.setText(mGroups.get(position).getName());
        holder.tvText.setSelected(false);
        holder.tvText.setOnClickListener(view -> {
            if (mLastSelectedItemPosition != position){
                notifyItemChanged(mLastSelectedItemPosition);
                mLastSelectedItemPosition = position;
            }
            holder.tvText.setSelected(true);
            mBus.post(mGroups.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView;
        }
    }

}
