package com.nikolaykul.gradebook.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;
import com.squareup.otto.Bus;

import java.util.List;

public class SingleStudentAdapter extends RecyclerView.Adapter<SingleStudentAdapter.ViewHolder>{
    public static class Item {
        public String title;
        public List<Information> infoList;

        public Item(String title, List<Information> infoList) {
            this.title = title;
            this.infoList = infoList;
        }
    }
    private Context mContext;
    private List<Item> mItems;
    private Bus mBus;
    private Database mDatabase;

    public SingleStudentAdapter(Context context, List<Item> items, Bus bus, Database database) {
        mContext = context;
        mItems = items;
        mBus = bus;
        mDatabase = database;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_drawer_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.tvTitle.setText(item.title);
        populateRecycleView(holder.recyclerView, item.infoList);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvTitle;
        public final RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycleView);
        }

    }

    private void populateRecycleView(RecyclerView recyclerView, List<Information> infoList) {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new InformationAdapter(infoList));
    }

    public class InformationAdapter extends
            RecyclerView.Adapter<InformationAdapter.InformationViewHolder> {
        private List<Information> mInfoList;
        private final int mViewWidth;
        private final int mViewHeight;
        private final int mViewTextSize;

        public InformationAdapter(List<Information> infoList) {
            mInfoList = infoList;
            mViewWidth = (int) mContext.getResources().getDimension(R.dimen.table_row_view_width);
            mViewHeight = (int) mContext.getResources().getDimension(R.dimen.table_row_view_height);
            mViewTextSize = (int) mContext.getResources().getDimension(R.dimen.text_tiny_size);
        }

        @Override
        public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(mViewWidth, mViewHeight));
            view.setGravity(Gravity.CENTER);
            view.setSingleLine();
            view.setTextSize(mViewTextSize);
            return new InformationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InformationViewHolder holder, int position) {
            Information info = mInfoList.get(position);
            holder.view.setText(info.getTitle());
            holder.view.setBackgroundResource(info.isPassed() ? R.color.green : R.color.red);
            holder.view.setOnClickListener(view -> Log.d("TAG", "Position: " + position));
        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }

        public class InformationViewHolder extends RecyclerView.ViewHolder {
            public TextView view;

            public InformationViewHolder(View itemView) {
                super(itemView);
                view = (TextView) itemView;
            }

        }
    }

}
