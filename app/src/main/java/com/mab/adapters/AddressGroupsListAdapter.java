package com.mab.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mab.MABApplication;
import com.mab.R;
import com.mab.data.provider.MABDatabaseContract;
import com.mab.data.tos.GroupData;

import java.util.ArrayList;

/**
 * Created by Jaideep.Lakshminaray on 21-03-2017.
 */

public class AddressGroupsListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<GroupData> groupDataArrayList;
    private OnAddressGroupClickListener onAddressGroupClickListener;

    public ArrayList<GroupData> getGroupDataArrayList() {
        return groupDataArrayList;
    }

    public void setGroupDataArrayList(ArrayList<GroupData> groupDataArrayList) {
        this.groupDataArrayList = groupDataArrayList;
    }

    public AddressGroupsListAdapter(Context mContext, ArrayList<GroupData> groupDataArrayList, OnAddressGroupClickListener onAddressGroupClickListener) {
        this.mContext = mContext;
        this.groupDataArrayList = groupDataArrayList;
        this.onAddressGroupClickListener = onAddressGroupClickListener;
    }

    @Override
    public int getItemCount() {
        int itemCount = getBasicItemCount();
        if (useHeader()) {
            itemCount += 1;
        }
        if (useFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }

    @Override
    public boolean useHeader() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean useFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_groups_layout, parent, false);

        return new AddressGroupsViewHolder(itemView, onAddressGroupClickListener);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        final GroupData groupData = groupDataArrayList.get(position);
        AddressGroupsViewHolder addressGroupsViewHolder = (AddressGroupsViewHolder) holder;
        addressGroupsViewHolder.bind(groupData, onAddressGroupClickListener);
        addressGroupsViewHolder.setGroupData(groupData);

        addressGroupsViewHolder.groupName.setText(groupData.getName());
        new GroupAddressCountAsyncTask(groupData, addressGroupsViewHolder.placeCount).execute();
        GradientDrawable bgShape = (GradientDrawable) addressGroupsViewHolder.groupName.getBackground();
        bgShape.setColor(groupData.getColorCode());
    }

    @Override
    public int getBasicItemCount() {
        if (groupDataArrayList != null) {
            return groupDataArrayList.size();
        }
        return 0;
    }

    @Override
    public int getBasicItemType(int position) {
        return position;
    }

    public class AddressGroupsViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView placeCount;
        RelativeLayout address_groups_container;
        OnAddressGroupClickListener onAddressGroupClickListener;
        GroupData groupData;

        public AddressGroupsViewHolder(View view, OnAddressGroupClickListener onAddressGroupClickListener) {
            super(view);
            groupName = (TextView) view.findViewById(R.id.tv_group_name);
            placeCount = (TextView) view.findViewById(R.id.tv_places_count);
            address_groups_container = (RelativeLayout) view.findViewById(R.id.address_groups_container);
            this.onAddressGroupClickListener = onAddressGroupClickListener;
        }

        public GroupData getGroupData() {
            return groupData;
        }

        public void setGroupData(GroupData groupData) {
            this.groupData = groupData;
        }

        public void bind(final GroupData item, final OnAddressGroupClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddressGroupClicked(item);
                }
            });
        }
    }

    public interface OnAddressGroupClickListener {
        void onAddressGroupClicked(GroupData groupData);
    }


    class GroupAddressCountAsyncTask extends AsyncTask<Void, Void, String> {
        GroupData groupData;
        TextView placeCountView;

        public GroupAddressCountAsyncTask(GroupData groupData, TextView placeCountView) {
            this.groupData = groupData;
            this.placeCountView = placeCountView;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String retVal = "";

            ContentResolver resolver = MABApplication.getContext().getContentResolver();

            String selection = null;
            selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE + "=" + groupData.getId();
            Cursor cursor = resolver.query(MABDatabaseContract.AddressDataProvider.CONTENT_URI, null, selection, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.getCount() > 999) {
                    retVal = "999+ Place(s)";
                } else {
                    retVal = cursor.getCount() + " Place(s)";
                }

            } else {
                retVal = "No Places";
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            placeCountView.setText(s);
        }
    }
}
