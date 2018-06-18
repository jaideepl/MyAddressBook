package com.mab.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mab.data.tos.GroupData;
import com.mab.fragments.AddressGroupsListFragment;
import com.mab.fragments.AddressListFragment;
import com.mab.fragments.BookmarksListFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaideep.Lakshminaray on 03-03-2017.
 */

public class AddressListPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<GroupData> groupDataArrayList;
    String[] titles = {"All Places", "Groups", "Favourites"};
    HashMap<Integer, Object> tags = new HashMap<>();
    FragmentManager mFragmentManager;

    public ArrayList<GroupData> getGroupDataArrayList() {
        return groupDataArrayList;
    }

    public void setGroupDataArrayList(ArrayList<GroupData> groupDataArrayList) {
        this.groupDataArrayList = groupDataArrayList;
    }

    public AddressListPagerAdapter(FragmentManager fm, ArrayList<GroupData> groupDataArrayList) {
        super(fm);
        this.groupDataArrayList = groupDataArrayList;
        mFragmentManager = fm;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);

        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            tags.put(position, f);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        return (Fragment) tags.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = AddressListFragment.newInstance("" + groupDataArrayList.get(position).getId(), "" + position, "");
                break;

            case 1:
                fragment = AddressGroupsListFragment.newInstance("", "");
                break;

            case 2:
                fragment = BookmarksListFragment.newInstance("", "", "");
                break;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
