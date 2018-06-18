package com.mab.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mab.MABController;
import com.mab.R;
import com.mab.data.tos.GroupData;
import com.mab.fragments.GroupsListFragment;
import com.mab.utils.Actions;
import com.mab.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Jaideep.Lakshminaray on 27-02-2017.
 */

public class GroupsListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<GroupData> groupDataArrayList;
    private OnGroupClickListener onGroupClickListener;

    public ArrayList<GroupData> getGroupDataArrayList() {
        return groupDataArrayList;
    }

    public void setGroupDataArrayList(ArrayList<GroupData> groupDataArrayList) {
        this.groupDataArrayList = groupDataArrayList;
    }

    public GroupsListAdapter(Context mContext, ArrayList<GroupData> groupDataArrayList, OnGroupClickListener onGroupClickListener) {
        this.mContext = mContext;
        this.groupDataArrayList = groupDataArrayList;
        this.onGroupClickListener = onGroupClickListener;
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
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blank_footer, parent, false);

        return new GroupsListFooterViewHolder(itemView);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_list_row, parent, false);

        return new MyViewHolder(itemView, onGroupClickListener);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        final GroupData groupData = groupDataArrayList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.bind(groupData, onGroupClickListener);

        myViewHolder.setGroupData(groupData);

        if (groupData.isDefault()) {
            myViewHolder.groupVisibilityIndicatorView.setVisibility(View.INVISIBLE);
            myViewHolder.deleteView.setVisibility(View.INVISIBLE);
        } else {
            myViewHolder.groupVisibilityIndicatorView.setVisibility(View.VISIBLE);
            myViewHolder.deleteView.setVisibility(View.VISIBLE);
        }
        myViewHolder.name.setText(groupData.getName());
        myViewHolder.groupVisibilityIndicatorView.setChecked(groupData.isVisible());
        GradientDrawable drawable = (GradientDrawable) myViewHolder.colorIndicatorView.getBackground();
        drawable.setColor(groupData.getColorCode());

        myViewHolder.groupVisibilityIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                groupData.setVisible(b);
                Calendar calendar = Calendar.getInstance();
                groupData.setModifiedDateMillis(calendar.getTimeInMillis());

                HashMap<String, Object> data = new HashMap<>();
                data.put(Constants.GROUP_DATA, groupData);
                data.put("isNewGroup", false);
                MABController mabController = MABController.getController();
                mabController.processAction(Actions.ADD_EDIT_GROUP, ((GroupsListFragment) onGroupClickListener).groupsListFragmentUIHandler, data);
            }
        });

        myViewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.delete_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(dialogView);

                TextView tv_mainText = (TextView) dialogView.findViewById(R.id.tv_mainText);
                TextView tv_subText = (TextView) dialogView.findViewById(R.id.tv_subText);

                tv_mainText.setText("Are you sure you want to delete this Group?");
                tv_subText.setText("(All the places associated with this Group will be removed)");

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MABController mabController = MABController.getController();
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put(Constants.GROUP_ID, groupData.getId());
                        mabController.processAction(Actions.DELETE_GROUP, ((GroupsListFragment) onGroupClickListener).groupsListFragmentUIHandler, data);
                    }
                });

                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View colorIndicatorView;
        public ImageView deleteView;
        public CheckBox groupVisibilityIndicatorView;
        OnGroupClickListener onGroupClickListener;
        GroupData groupData;
        public RelativeLayout group_list_row_layout;

        public MyViewHolder(View view, OnGroupClickListener onGroupClickListener) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_group_name);
            colorIndicatorView = view.findViewById(R.id.group_color_code_view);
            groupVisibilityIndicatorView = (CheckBox) view.findViewById(R.id.cb_group_visible_hide);
            deleteView = (ImageView) view.findViewById(R.id.iv_group_delete);
            group_list_row_layout = (RelativeLayout) view.findViewById(R.id.group_list_row_layout);
            this.onGroupClickListener = onGroupClickListener;
        }

        public GroupData getGroupData() {
            return groupData;
        }

        public void setGroupData(GroupData groupData) {
            this.groupData = groupData;
        }

        public void bind(final GroupData item, final OnGroupClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGroupClicked(item);
                }
            });
        }
    }

    public class GroupsListFooterViewHolder extends RecyclerView.ViewHolder {

        public GroupsListFooterViewHolder(View view) {
            super(view);
        }
    }

    public interface OnGroupClickListener {
        void onGroupClicked(GroupData groupData);
    }
}
