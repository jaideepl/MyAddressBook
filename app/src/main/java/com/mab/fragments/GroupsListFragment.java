package com.mab.fragments;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.enrico.colorpicker.colorDialog;
import com.mab.MABController;
import com.mab.R;
import com.mab.activities.MainActivity;
import com.mab.adapters.GroupsListAdapter;
import com.mab.customviews.VerticalSpaceItemDecoration;
import com.mab.data.tos.GroupData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupsListFragment extends Fragment implements GroupsListAdapter.OnGroupClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView;
    RecyclerView groupsListView;
    GroupsListAdapter groupsListAdapter;
    ArrayList<GroupData> groupDataArrayList;
    FloatingActionButton btnAddGroup;
    MABController mabController;
    public GroupsListFragmentUIHandler groupsListFragmentUIHandler;

    public GroupsListFragment() {
        // Required empty public constructor
    }

    public static GroupsListFragment newInstance(String param1, String param2) {
        GroupsListFragment fragment = new GroupsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);
        mabController = MABController.getController();
        groupsListFragmentUIHandler = new GroupsListFragmentUIHandler(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        btnAddGroup = (FloatingActionButton) rootView.findViewById(R.id.btn_addgroup);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupData groupData = new GroupData();
                Calendar calendar = Calendar.getInstance();
                long timeMillis = calendar.getTimeInMillis();
                groupData.setVisible(true);
                groupData.setId(timeMillis);
                displayGroupInfoDialog(groupData, true);
            }
        });
        groupDataArrayList = new ArrayList<>();

        groupsListView = (RecyclerView) rootView.findViewById(R.id.groups_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        groupsListAdapter = new GroupsListAdapter(getActivity(), groupDataArrayList, this);
        groupsListView.setLayoutManager(mLayoutManager);
        groupsListView.addItemDecoration(new VerticalSpaceItemDecoration(50));
        groupsListView.setItemAnimator(new DefaultItemAnimator());
        groupsListView.setAdapter(groupsListAdapter);

        mabController.processAction(Actions.GET_ALL_GROUPS, groupsListFragmentUIHandler, null);
    }

    private void displayGroupInfoDialog(final GroupData groupData, final boolean isNewGroup) {

        View groupInfoDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.group_info_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(groupInfoDialogView);


        ((MainActivity) getActivity()).colorIndicatorView = groupInfoDialogView.findViewById(R.id.group_color_indicator);
        final EditText groupNameView = (EditText) groupInfoDialogView.findViewById(R.id.et_groupname);

        String addeditbuttonText = "";

        int color = getResources().getColor(R.color.colorAccent);
        if (!isNewGroup) {
            color = groupData.getColorCode();
            if (groupData.isDefault()) {
                groupNameView.setEnabled(false);
            } else {
                groupNameView.setEnabled(true);
            }
            groupNameView.setText(groupData.getName());
            addeditbuttonText = "Update";
        } else {
            groupNameView.setText("");
            addeditbuttonText = "Add";
        }

        GradientDrawable drawable = (GradientDrawable) ((MainActivity) getActivity()).colorIndicatorView.getBackground();
        drawable.setColor(color);
        ((MainActivity) getActivity()).groupColor = color;

        builder.setPositiveButton(addeditbuttonText, null);
        builder.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String groupName = groupNameView.getText().toString().trim();

                        if (groupName == null || groupName.isEmpty()) {
                            Util.displayMessage("Please enter Group name");
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            long timeMillis = calendar.getTimeInMillis();

                            groupData.setName(groupName);
                            groupData.setColorCode(((MainActivity) getActivity()).groupColor);
                            groupData.setModifiedDateMillis(timeMillis);

                            HashMap<String, Object> data = new HashMap<>();
                            data.put(Constants.GROUP_DATA, groupData);
                            data.put("isNewGroup", isNewGroup);
                            mabController.processAction(Actions.ADD_EDIT_GROUP, groupsListFragmentUIHandler, data);
                            mAlertDialog.dismiss();
                        }
                    }
                });
            }
        });
        mAlertDialog.show();

        ((MainActivity) getActivity()).colorIndicatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorDialog.showColorPicker((MainActivity) getActivity(), -1);
                int color = getResources().getColor(R.color.colorAccent);
                colorDialog.setPickerColor((MainActivity) getActivity(), -1, color);
            }
        });

    }

    @Override
    public void onGroupClicked(GroupData groupData) {
        displayGroupInfoDialog(groupData, false);
    }

    class GroupsListFragmentUIHandler extends Handler {
        GroupsListFragment groupsListFragment;

        public GroupsListFragmentUIHandler(GroupsListFragment groupsListFragment) {
            this.groupsListFragment = groupsListFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (getActivity() != null && !getActivity().isFinishing()) {
                switch (msg.what) {
                    case Actions.TECHNICAL_ERROR:
                        Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                        break;

                    case Actions.GET_ALL_GROUPS:
                    case Actions.ADD_EDIT_GROUP:
                    case Actions.DELETE_GROUP:
                        groupDataArrayList = new ArrayList<>();
                        groupDataArrayList = (ArrayList<GroupData>) msg.obj;
                        if (groupDataArrayList != null && groupDataArrayList.size() > 0) {
                            groupsListAdapter.setGroupDataArrayList(groupDataArrayList);
                            groupsListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }

        }
    }

}
