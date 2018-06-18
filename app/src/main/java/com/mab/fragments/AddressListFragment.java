package com.mab.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.activities.AddEditAddressActivity;
import com.mab.activities.GroupPlacesListActivity;
import com.mab.activities.MainActivity;
import com.mab.activities.ViewAddressActivity;
import com.mab.adapters.AddressListAdapter;
import com.mab.customviews.VerticalSpaceItemDecoration;
import com.mab.data.tos.AddressData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;


public class AddressListFragment extends Fragment implements AddressListAdapter.OnAddressClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    public static final int REQUEST_CODE_ADD__EDIT_ADDRESS = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public String searchText;

    public int pageIndex = 0;
    View rootView;
    FloatingActionButton btnAddAddress;
    RecyclerView addressListView;
    AddressListAdapter addressListAdapter;
    MABController mabController;
    public AddressListFragmentUIHandler uiHandler;
    public long groupId;
    ArrayList<AddressData> addressDataArrayList;
    String sortOption = Constants.SORTBY_DATE_DESCENDING;
    LinearLayout sortoptions_container;
    LinearLayout no_addresses_layout;
    TextView noSearchPlacesMessageView;
    TextView emptyListMessageView;
    RelativeLayout address_list_layout;
    public boolean mActive = false;

    public AddressListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressListFragment newInstance(String param1, String param2, String param3) {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            searchText = getArguments().getString(ARG_PARAM3);

            groupId = Long.parseLong(mParam1);
            pageIndex = Integer.parseInt(mParam2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_address_list, container, false);
        if (pageIndex == 0) {
            groupId = -1;
        }
        sortOption = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);
        initViews();
        return rootView;
    }

    private void initViews() {
        addressDataArrayList = new ArrayList<>();
        mabController = MABController.getController();
        uiHandler = new AddressListFragmentUIHandler(this);
        sortoptions_container = (LinearLayout) rootView.findViewById(R.id.sortoptions_container);
        addressListView = (RecyclerView) rootView.findViewById(R.id.address_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        addressListAdapter = new AddressListAdapter(this.getActivity(), addressDataArrayList, this);
        addressListView.setLayoutManager(mLayoutManager);
        addressListView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        addressListView.setItemAnimator(new DefaultItemAnimator());
        addressListView.setAdapter(addressListAdapter);
        no_addresses_layout = (LinearLayout) rootView.findViewById(R.id.no_addresses_layout);
        noSearchPlacesMessageView = (TextView) rootView.findViewById(R.id.tv_no_search_places_message);
        emptyListMessageView = (TextView) rootView.findViewById(R.id.tv_empty_list_message);
        address_list_layout = (RelativeLayout) rootView.findViewById(R.id.address_list_layout);

        Typeface cTypeface = Typeface.createFromAsset(getActivity().getAssets(), "dancingscript_regular.ttf");
        emptyListMessageView.setTypeface(cTypeface);
        noSearchPlacesMessageView.setTypeface(cTypeface);

        sortoptions_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog actions;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Sort By");
                String[] options = {"Name - Ascending", "Name - Descending", "Date - Ascending", "Date - Descending"};
                builder.setItems(options, actionListener);
                builder.setNegativeButton("Cancel", null);
                actions = builder.create();
                actions.show();
            }
        });

        btnAddAddress = (FloatingActionButton) rootView.findViewById(R.id.btn_addaddress);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MABApplication.getContext(), AddEditAddressActivity.class);
                if (getActivity() instanceof MainActivity) {
                    intent.putExtra("parentActivity", "MainActivity");
                } else if (getActivity() instanceof GroupPlacesListActivity) {
                    intent.putExtra(Constants.GROUP_ID, groupId);
                    intent.putExtra("parentActivity", "GroupPlacesListActivity");
                }
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
            }
        });

        if (searchText == null || searchText.isEmpty()) {
            btnAddAddress.setVisibility(View.VISIBLE);
        } else {
            btnAddAddress.setVisibility(View.GONE);
        }

        loadSearchResult();

    }


    DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    sortOption = Constants.SORTBY_NAME_ASCENDING;
                    break;
                case 1:
                    sortOption = Constants.SORTBY_NAME_DESCENDING;
                    break;
                case 2:
                    sortOption = Constants.SORTBY_DATE_ASCENDING;
                    break;
                case 3:
                    sortOption = Constants.SORTBY_DATE_DESCENDING;
                    break;
            }

            Util.setStringToSharedPreferences(Constants.PLACE_SORTBY_OPTION, sortOption);

            loadSearchResult();
        }
    };

    private void loadSearchResult() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.GROUP_ID, groupId);
        data.put("pageIndex", pageIndex);
        data.put(Constants.SEARCH_TEXT, searchText);
        mabController.processAction(Actions.GET_ALL_PLACES, uiHandler, data);
    }

    @Override
    public void onAddressCliked(AddressData addressData, View itemView) {
        Intent intent = new Intent(this.getActivity(), ViewAddressActivity.class);
        intent.putExtra(Constants.ADDRESSDATA, addressData);

        if (getActivity() instanceof MainActivity) {
            intent.putExtra("parentActivity", "MainActivity");
        } else if (getActivity() instanceof GroupPlacesListActivity) {
            intent.putExtra(Constants.GROUP_ID, groupId);
            intent.putExtra("parentActivity", "GroupPlacesListActivity");
        }

        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

    }

    class AddressListFragmentUIHandler extends Handler {
        AddressListFragment addressListFragment;

        public AddressListFragmentUIHandler(AddressListFragment addressListFragment) {
            this.addressListFragment = addressListFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (this.addressListFragment != null && !addressListFragment.isRemoving()) {
                switch (msg.what) {
                    case Actions.TECHNICAL_ERROR:
                        Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                        break;

                    case Actions.GET_ALL_PLACES:
                    case Actions.DELETE_PLACE:
                        addressDataArrayList = (ArrayList<AddressData>) msg.obj;
                        addressListAdapter.setAddressDataArrayList(addressDataArrayList);
                        addressListAdapter.notifyDataSetChanged();

                        if (addressDataArrayList.size() > 0) {
//                            sortoptions_container.setVisibility(View.VISIBLE);
                            address_list_layout.setVisibility(View.VISIBLE);
                            no_addresses_layout.setVisibility(View.GONE);
                        } else {
                            sortoptions_container.setVisibility(View.GONE);
                            address_list_layout.setVisibility(View.GONE);
                            no_addresses_layout.setVisibility(View.VISIBLE);
                            if (searchText == null || searchText.isEmpty()) {
                                emptyListMessageView.setVisibility(View.VISIBLE);
                                noSearchPlacesMessageView.setVisibility(View.GONE);
                            } else {
                                emptyListMessageView.setVisibility(View.GONE);
                                noSearchPlacesMessageView.setVisibility(View.VISIBLE);
                            }

                        }

                        break;
                }
            }

        }
    }
}
