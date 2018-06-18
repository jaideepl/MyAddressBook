package com.mab.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.mab.activities.ViewAddressActivity;
import com.mab.adapters.BookmarksListAdapter;
import com.mab.customviews.ItemOffsetDecoration;
import com.mab.data.tos.AddressData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class BookmarksListFragment extends Fragment implements BookmarksListAdapter.OnBookmarkClickListener {
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

    View rootView;
    RecyclerView bookmarksListView;
    BookmarksListAdapter bookmarksListAdapter;
    MABController mabController;
    public AddressListFragmentUIHandler uiHandler;
    ArrayList<AddressData> addressDataArrayList;
    String sortOption = Constants.SORTBY_DATE_DESCENDING;

    LinearLayout emptyListLayout;
    TextView emptyListMessageView;
    RelativeLayout bookmarks_list_layout;


    public BookmarksListFragment() {
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
    public static BookmarksListFragment newInstance(String param1, String param2, String param3) {
        BookmarksListFragment fragment = new BookmarksListFragment();
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_bookmarks_list, container, false);
        sortOption = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);
        initViews();
        return rootView;
    }

    private void initViews() {
        addressDataArrayList = new ArrayList<>();
        mabController = MABController.getController();
        uiHandler = new AddressListFragmentUIHandler(this);
        bookmarksListView = (RecyclerView) rootView.findViewById(R.id.bookmarks_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MABApplication.getContext(), 2);
        bookmarksListView.setLayoutManager(gridLayoutManager);
        bookmarksListAdapter = new BookmarksListAdapter(this.getActivity(), addressDataArrayList, this);
        bookmarksListView.setAdapter(bookmarksListAdapter);
        bookmarksListView.addItemDecoration(new ItemOffsetDecoration(2));
        bookmarksListView.setHasFixedSize(true);
        emptyListLayout = (LinearLayout) rootView.findViewById(R.id.empty_bookmarks_layout);
        emptyListMessageView = (TextView) rootView.findViewById(R.id.tv_empty_bookmarks_list_message);
        bookmarks_list_layout = (RelativeLayout) rootView.findViewById(R.id.bookmarks_list_layout);

        Typeface cTypeface = Typeface.createFromAsset(getActivity().getAssets(), "dancingscript_regular.ttf");
        emptyListMessageView.setTypeface(cTypeface);

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
        data.put(Constants.SEARCH_TEXT, searchText);
        mabController.processAction(Actions.GET_ALL_BOOKMARKS, uiHandler, data);
    }

    @Override
    public void onBookmarkCliked(AddressData addressData) {
        Intent intent = new Intent(this.getActivity(), ViewAddressActivity.class);
        intent.putExtra(Constants.ADDRESSDATA, addressData);

        intent.putExtra("parentActivity", "MainActivity");

        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    class AddressListFragmentUIHandler extends Handler {
        BookmarksListFragment bookmarksListFragment;

        public AddressListFragmentUIHandler(BookmarksListFragment bookmarksListFragment) {
            this.bookmarksListFragment = bookmarksListFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (this.bookmarksListFragment != null && !bookmarksListFragment.isRemoving()) {
                switch (msg.what) {
                    case Actions.TECHNICAL_ERROR:
                        Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                        break;

                    case Actions.GET_ALL_BOOKMARKS:
                    case Actions.DELETE_BOOKMARK:
                        addressDataArrayList = (ArrayList<AddressData>) msg.obj;
                        bookmarksListAdapter.setAddressDataArrayList(addressDataArrayList);
                        bookmarksListAdapter.notifyDataSetChanged();

                        if (addressDataArrayList.size() > 0) {
//                            sortoptions_container.setVisibility(View.VISIBLE);
                            bookmarks_list_layout.setVisibility(View.VISIBLE);
                            emptyListLayout.setVisibility(View.GONE);
                        } else {
                            bookmarks_list_layout.setVisibility(View.GONE);
                            emptyListLayout.setVisibility(View.VISIBLE);
                        }

                        break;
                }
            }

        }
    }
}
