package com.mab.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.activities.GroupPlacesListActivity;
import com.mab.adapters.AddressGroupsListAdapter;
import com.mab.customviews.AutoFitGridLayoutManager;
import com.mab.customviews.ItemOffsetDecoration;
import com.mab.data.tos.GroupData;
import com.mab.data.tos.UpdateAddressGroupList;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AddressGroupsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressGroupsListFragment extends Fragment implements AddressGroupsListAdapter.OnAddressGroupClickListener, UpdateAddressGroupList {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;
    private RecyclerView addressGroupsListView;
    ArrayList<GroupData> groupDataArrayList;
    MABController mabController;
    AddressGroupsListFragmentUIHandler addressGroupsListFragmentUIHandler;
    AddressGroupsListAdapter adapter;

    public AddressGroupsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressGroupsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressGroupsListFragment newInstance(String param1, String param2) {
        AddressGroupsListFragment fragment = new AddressGroupsListFragment();
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
    public void onResume() {
        super.onResume();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.FILTER_VISIBLE_GROUPS, true);
        if (mabController == null) {
            mabController = MABController.getController();
        }
        mabController.processAction(Actions.GET_ALL_GROUPS, addressGroupsListFragmentUIHandler, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_address_groups_list, container, false);
        mabController = MABController.getController();
        addressGroupsListFragmentUIHandler = new AddressGroupsListFragmentUIHandler(this);

        initViews();

        return rootView;
    }

    private void initViews() {
        addressGroupsListView = (RecyclerView) rootView.findViewById(R.id.address_groups_list);
        adapter = new AddressGroupsListAdapter(getActivity(), groupDataArrayList, this);
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(MABApplication.getContext(), 300);
        addressGroupsListView.setLayoutManager(layoutManager);
        addressGroupsListView.setHasFixedSize(true);
        addressGroupsListView.addItemDecoration(new ItemOffsetDecoration(10));
        addressGroupsListView.setItemAnimator(new DefaultItemAnimator());
        addressGroupsListView.setAdapter(adapter);

        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.FILTER_VISIBLE_GROUPS, true);
        mabController.processAction(Actions.GET_ALL_GROUPS, addressGroupsListFragmentUIHandler, data);
    }

    @Override
    public void onAddressGroupClicked(GroupData groupData) {
        Intent intent = new Intent(getActivity(), GroupPlacesListActivity.class);
        intent.putExtra(Constants.GROUP_ID, groupData.getId());
        getActivity().startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public void update() {
        mabController = MABController.getController();
        if (addressGroupsListFragmentUIHandler == null) {
            addressGroupsListFragmentUIHandler = new AddressGroupsListFragmentUIHandler(this);
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.FILTER_VISIBLE_GROUPS, true);
        mabController.processAction(Actions.GET_ALL_GROUPS, addressGroupsListFragmentUIHandler, data);
    }


    class AddressGroupsListFragmentUIHandler extends Handler {
        AddressGroupsListFragment addressGroupsListFragment;

        public AddressGroupsListFragmentUIHandler(AddressGroupsListFragment addressGroupsListFragment) {
            this.addressGroupsListFragment = addressGroupsListFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Actions.TECHNICAL_ERROR:
                    Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                    break;

                case Actions.GET_ALL_GROUPS:
                    groupDataArrayList = new ArrayList<>();
                    groupDataArrayList = (ArrayList<GroupData>) msg.obj;
                    if (groupDataArrayList != null && groupDataArrayList.size() > 0) {
                        adapter.setGroupDataArrayList(groupDataArrayList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }

        }
    }

}


