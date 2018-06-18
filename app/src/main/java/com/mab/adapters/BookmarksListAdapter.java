package com.mab.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.data.tos.AddressData;
import com.mab.fragments.BookmarksListFragment;
import com.mab.utils.Actions;
import com.mab.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaideep.Lakshminaray on 13-03-2017.
 */

public class BookmarksListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder> {
    ArrayList<AddressData> addressDataArrayList;
    Context context;
    private OnBookmarkClickListener bookmarkClickListener;
    private int lastPosition = -1;

    public BookmarksListAdapter(Context context, ArrayList<AddressData> addressDatas, OnBookmarkClickListener bookmarkClickListener) {
        this.addressDataArrayList = addressDatas;
        this.context = context;
        this.bookmarkClickListener = bookmarkClickListener;
    }

    public ArrayList<AddressData> getAddressDataArrayList() {
        return addressDataArrayList;
    }

    public void setAddressDataArrayList(ArrayList<AddressData> addressDataArrayList) {
        this.addressDataArrayList = addressDataArrayList;
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blank_footer, parent, false);

        return new AddressListFooterViewHolder(itemView);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmarks_list_row, parent, false);

        return new AddressViewHolder(itemView, bookmarkClickListener);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        final AddressData addressData = addressDataArrayList.get(position);

        AddressViewHolder viewHolder = (AddressViewHolder) holder;

        viewHolder.bind(addressData, bookmarkClickListener);
        viewHolder.setAddressData(addressData);

        viewHolder.placeName.setText(addressData.getName());
        viewHolder.placeAddress.setText(addressData.getAddress());

//        if (addressData.getLongitude() != 0 && addressData.getLatitude() != 0) {
//            viewHolder.navigationView.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.navigationView.setVisibility(View.INVISIBLE);
//        }

        viewHolder.navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?f=d&hl=en&daddr=";

                if (addressData.getLongitude() != 0 && addressData.getLatitude() != 0) {
                    uri += addressData.getLatitude() + "," + addressData.getLongitude();
                } else {
                    uri += addressData.getAddress();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);

                TextView tv_mainText = (TextView) dialogView.findViewById(R.id.tv_mainText);
                TextView tv_subText = (TextView) dialogView.findViewById(R.id.tv_subText);

                tv_mainText.setText("Are you sure you want to delete this Bookmark?");
                tv_subText.setVisibility(View.GONE);

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MABController mabController = MABController.getController();
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        addressData.setBookmarked(false);
                        data.put(Constants.ADDRESSDATA, addressData);
                        mabController.processAction(Actions.DELETE_BOOKMARK, ((BookmarksListFragment) bookmarkClickListener).uiHandler, data);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(MABApplication.getContext(),
                (position > lastPosition) ? R.anim.fadein
                        : R.anim.fadeout);
        viewHolder.itemView.startAnimation(animation);

        lastPosition = position;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    @Override
    public int getBasicItemCount() {
        return addressDataArrayList.size();
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }


    public class AddressViewHolder extends RecyclerView.ViewHolder {
        public TextView placeName;
        public TextView placeAddress;
        public ImageButton deleteView;
        public ImageButton navigationView;
        public CardView bookmark_list_row_layout;
        OnBookmarkClickListener onBookmarkClickListener;
        AddressData addressData;

        public AddressViewHolder(View view, OnBookmarkClickListener onBookmarkClickListener) {
            super(view);
            placeName = (TextView) view.findViewById(R.id.tv_place_name);
            placeAddress = (TextView) view.findViewById(R.id.tv_place_address);
            deleteView = (ImageButton) view.findViewById(R.id.ib_delete_bookmark);
            navigationView = (ImageButton) view.findViewById(R.id.ib_navigate_bookmark);
            bookmark_list_row_layout = (CardView) view.findViewById(R.id.bookmark_list_row_layout);
            this.onBookmarkClickListener = onBookmarkClickListener;
        }

        public AddressData getAddressData() {
            return addressData;
        }

        public void setAddressData(AddressData addressData) {
            this.addressData = addressData;
        }

        public void bind(final AddressData item, final OnBookmarkClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBookmarkCliked(item);
                }
            });
        }
    }


    public class AddressListFooterViewHolder extends RecyclerView.ViewHolder {

        public AddressListFooterViewHolder(View view) {
            super(view);
        }
    }


    public interface OnBookmarkClickListener {
        void onBookmarkCliked(AddressData addressData);
    }
}
