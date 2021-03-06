package com.nikolaykul.gradebook.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.event.InformationAddedEvent;
import com.nikolaykul.gradebook.event.InformationDeletedEvent;
import com.nikolaykul.gradebook.event.InformationUpdatedEvent;
import com.nikolaykul.gradebook.other.InformationViewFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
    private final long mStudentId;
    private List<Item> mItems;

    public SingleStudentAdapter(Context context, Bus bus, Database database,
                                long studentId, List<Item> items) {
        mContext = context;
        mBus = bus;
        mDatabase = database;
        mStudentId = studentId;
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
        private final InformationViewFactory mViewFactory;
        private Item mItem;

        public InformationAdapter(Item item) {
            mViewFactory = new InformationViewFactory(mContext);
            mItem = item;
            mBus.register(new Object() {
                @Subscribe public void onInformationAdded(final InformationAddedEvent event) {
                    if (mItem.TABLE == event.TABLE) {
                        mItem.infoList.clear();
                        mItem.infoList.addAll(mDatabase.getInformation(mStudentId, mItem.TABLE));
                        notifyDataSetChanged();
                    }
                }

                @Subscribe public void onInformationDeleted(final InformationDeletedEvent event) {
                    if (mItem.TABLE == event.TABLE) {
                        mItem.infoList.clear();
                        mItem.infoList.addAll(mDatabase.getInformation(mStudentId, mItem.TABLE));
                        notifyDataSetChanged();
                    }
                }

                @Subscribe public void onInformationUpdated(final InformationUpdatedEvent event) {
                    if (mItem.TABLE == event.TABLE && mStudentId == event.info.getStudentId()) {
                        // find the information that has changed
                        List<Information> mInfoList = mItem.infoList;
                        for (int i = 0; i < mInfoList.size(); i++) {
                            Information info = mInfoList.get(i);
                            if (info.equals(event.info)) {
                                info.update(event.info);
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }
                }
            });
        }

        @Override
        public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InformationViewHolder(mViewFactory.createHeaderView());
        }

        @Override
        public void onBindViewHolder(InformationViewHolder holder, int position) {
            Information info = mItem.infoList.get(position);
            holder.view.setBackgroundResource(info.isPassed() ? R.color.green : R.color.red);
            holder.view.setText(info.getTitle());
            holder.view.setTag(info);
            holder.view.setOnClickListener(view -> {
                Information vInfo = (Information) view.getTag();
                vInfo.setPassed(!vInfo.isPassed());
                mDatabase.updateInformation(vInfo, mItem.TABLE);
                mBus.post(new InformationUpdatedEvent(mItem.TABLE, vInfo));
            });
        }

        @Override
        public int getItemCount() {
            return mItem.infoList.size();
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
