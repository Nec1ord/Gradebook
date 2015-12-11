package com.nikolaykul.gradebook.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.other.InfoView;
import com.squareup.otto.Bus;

import java.util.List;

public class SingleStudentAdapter extends RecyclerView.Adapter<SingleStudentAdapter.ViewHolder>{
    public static class Item {
        @Database.InformationTable public final int TABLE;
        public final String TITLE;
        public List<Information> infoList;

        public Item(@Database.InformationTable final int table,
                    final String title,
                    List<Information> infoList) {
            this.TABLE = table;
            this.TITLE = title;
            this.infoList = infoList;
        }
    }
    private final Context mContext;
    private final Bus mBus;
    private final Database mDatabase;
    private List<Item> mItems;

    public SingleStudentAdapter(Context context, Bus bus, Database database, List<Item> items) {
        mContext = context;
        mBus = bus;
        mDatabase = database;
        mItems = items;
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
        holder.tvTitle.setText(item.TITLE);

        // populate nested RV
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.nestedRecyclerView.setLayoutManager(manager);
        holder.nestedRecyclerView.setHasFixedSize(true);
        holder.nestedRecyclerView.setAdapter(new InformationAdapter(item));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvTitle;
        public final RecyclerView nestedRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            nestedRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycleView);
        }

    }

    public class InformationAdapter extends
            RecyclerView.Adapter<InformationAdapter.InformationViewHolder> {
        private Item mItem;
        private final int mViewWidth;
        private final int mViewHeight;
        private final int mViewTextSize;

        public InformationAdapter(Item item) {
            mItem = item;
            mViewWidth = (int) mContext.getResources().getDimension(R.dimen.table_row_view_width);
            mViewHeight = (int) mContext.getResources().getDimension(R.dimen.table_row_view_height);
            mViewTextSize = (int) mContext.getResources().getDimension(R.dimen.text_tiny_size);
        }

        @Override
        public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            InfoView view = new InfoView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(mViewWidth, mViewHeight));
            view.setGravity(Gravity.CENTER);
            view.setSingleLine();
            view.setTextSize(mViewTextSize);
            return new InformationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InformationViewHolder holder, int position) {
            Information info = mItem.infoList.get(position);
            holder.view.setText(info.getTitle());
            holder.view.init(info, mItem.TABLE, mDatabase, mBus);
        }

        @Override
        public int getItemCount() {
            return mItem.infoList.size();
        }

        public class InformationViewHolder extends RecyclerView.ViewHolder {
            public InfoView view;

            public InformationViewHolder(View itemView) {
                super(itemView);
                view = (InfoView) itemView;
            }

        }
    }

}
