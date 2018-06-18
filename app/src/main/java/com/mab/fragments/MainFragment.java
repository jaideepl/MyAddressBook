package com.mab.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.mab.MABController;
import com.mab.R;
import com.mab.adapters.AddressListPagerAdapter;
import com.mab.data.tos.GroupData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    ViewPager viewPager;
    PagerSlidingTabStrip tabsStrip;
    AddressListMainFragmentUIHandler uiHandler;
    private ArrayList<GroupData> groupDataArrayList;
    MABController mabController;
    AddressListPagerAdapter addressListPagerAdapter;
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_address_list_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mabController = MABController.getController();
        uiHandler = new AddressListMainFragmentUIHandler(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.FILTER_VISIBLE_GROUPS, true);
        mabController.processAction(Actions.GET_ALL_GROUPS, uiHandler, data);
        initViews();
    }

    private void initViews() {
        groupDataArrayList = new ArrayList<>();
        viewPager = (ViewPager) rootView.findViewById(R.id.vp_address_list);
        viewPager.setOffscreenPageLimit(0);
        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView) rootView.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_all_addresses:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_groups:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_favourites:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((AddressListPagerAdapter) viewPager.getAdapter()).getFragment(position);

                if (fragment != null) {
                    if (fragment instanceof AddressGroupsListFragment) {
                        fragment.onResume();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void displayGroups() {


        if (addressListPagerAdapter == null) {
            addressListPagerAdapter = new AddressListPagerAdapter(getFragmentManager(), groupDataArrayList);
            viewPager.setAdapter(addressListPagerAdapter);
        } else {
            addressListPagerAdapter.notifyDataSetChanged();
        }


        viewPager.setOffscreenPageLimit(0);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((AddressListPagerAdapter) viewPager.getAdapter()).getFragment(position);

                if (fragment != null) {
//                    if (fragment instanceof AddressGroupsListFragment) {
//                        fragment.onResume();
//                    }
                }

                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class AddressListMainFragmentUIHandler extends Handler {
        MainFragment mainFragment;

        public AddressListMainFragmentUIHandler(MainFragment mainFragment) {
            this.mainFragment = mainFragment;
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
                        groupDataArrayList = new ArrayList<>();
                        groupDataArrayList = (ArrayList<GroupData>) msg.obj;
                        displayGroups();
                        break;
                }
            }

        }
    }


}
