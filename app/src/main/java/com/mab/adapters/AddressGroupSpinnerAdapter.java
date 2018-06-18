package com.mab.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mab.R;
import com.mab.data.tos.GroupData;

import java.util.ArrayList;

/**
 * Created by Jaideep.Lakshminaray on 07-03-2017.
 */

public class AddressGroupSpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<GroupData> groupDataArrayList;
    LayoutInflater inflater;

    public ArrayList<GroupData> getGroupDataArrayList() {
        return groupDataArrayList;
    }

    public void setGroupDataArrayList(ArrayList<GroupData> groupDataArrayList) {
        this.groupDataArrayList = groupDataArrayList;
    }

    public AddressGroupSpinnerAdapter(Context context, ArrayList<GroupData> groupDataArrayList) {
        this.context = context;
        this.groupDataArrayList = groupDataArrayList;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return groupDataArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return groupDataArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GroupData groupData = groupDataArrayList.get(i);

        view = inflater.inflate(R.layout.contacts_groups_spinner_layout, null);
        View group_indicatorview = view.findViewById(R.id.group_indicatorview);
        TextView tv_groupname = (TextView) view.findViewById(R.id.tv_groupname);
        tv_groupname.setText(groupDataArrayList.get(i).getName());
        GradientDrawable drawable = (GradientDrawable) group_indicatorview.getBackground();
        if (groupData.getColorCode() != 0) {
            drawable.setColor(groupData.getColorCode());
            group_indicatorview.setVisibility(View.VISIBLE);
        } else {
            group_indicatorview.setVisibility(View.GONE);
        }

        return view;
    }
}
