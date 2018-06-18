package com.mab.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.data.tos.AddressData;
import com.mab.fragments.AddressListFragment;
import com.mab.utils.Actions;
import com.mab.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaideep.Lakshminaray on 13-03-2017.
 */

public class AddressListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder> {
    ArrayList<AddressData> addressDataArrayList;
    Context context;
    private OnAddressClickListener addressClickListener;
    private int lastPosition = -1;

    public AddressListAdapter(Context context, ArrayList<AddressData> addressDatas, OnAddressClickListener addressClickListener) {
        this.addressDataArrayList = addressDatas;
        this.context = context;
        this.addressClickListener = addressClickListener;
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
        return true;
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
                .inflate(R.layout.address_list_row, parent, false);

        return new AddressViewHolder(itemView, addressClickListener);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        final AddressData addressData = addressDataArrayList.get(position);

        AddressViewHolder viewHolder = (AddressViewHolder) holder;
        viewHolder.bind(addressData, addressClickListener);
        viewHolder.setAddressData(addressData);

        viewHolder.placeName.setText(addressData.getName());
        viewHolder.address.setText(addressData.getAddress());
        GradientDrawable drawable = (GradientDrawable) viewHolder.colorIndicatorView.getBackground();
        drawable.setColor(addressData.getGroupcolorcode());

//        if (addressData.getLongitude() != 0 && addressData.getLatitude() != 0) {
//            viewHolder.navigation_layout.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.navigation_layout.setVisibility(View.INVISIBLE);
//        }

        viewHolder.navigation_layout.setOnClickListener(new View.OnClickListener() {
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

        viewHolder.deleteaddress_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);

                TextView tv_mainText = (TextView) dialogView.findViewById(R.id.tv_mainText);
                TextView tv_subText = (TextView) dialogView.findViewById(R.id.tv_subText);

                tv_mainText.setText("Are you sure you want to delete this Place?");
                tv_subText.setVisibility(View.GONE);

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MABController mabController = MABController.getController();
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put(Constants.ADDRESS_ID, addressData.getId());
                        data.put(Constants.GROUP_ID, ((AddressListFragment) addressClickListener).groupId);
                        data.put(Constants.SEARCH_TEXT, ((AddressListFragment) addressClickListener).searchText);
                        mabController.processAction(Actions.DELETE_PLACE, ((AddressListFragment) addressClickListener).uiHandler, data);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        viewHolder.bookmarkSelectionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                addressData.setBookmarked(isChecked);
                MABController mabController = MABController.getController();
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put(Constants.ADDRESSDATA, addressData);
                mabController.processAction(Actions.BOOKMARK_PLACE, ((AddressListFragment) addressClickListener).uiHandler, data);
            }
        });

        viewHolder.bookmarkSelectionView.setChecked(addressData.isBookmarked());

        Animation animation = AnimationUtils.loadAnimation(MABApplication.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
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
        public TextView address;
        public View colorIndicatorView;
        public ImageView deleteView;
        public ImageView navigationView;
        public CheckBox bookmarkSelectionView;
        public RelativeLayout navigation_layout;
        public RelativeLayout deleteaddress_layout;
        public LinearLayout address_list_row_action_layout;

        OnAddressClickListener onAddressClickListener;
        AddressData addressData;

        public AddressViewHolder(View view, OnAddressClickListener onAddressClickListener) {
            super(view);
            placeName = (TextView) view.findViewById(R.id.tv_placename);
            address = (TextView) view.findViewById(R.id.tv_placeaddress);
            colorIndicatorView = view.findViewById(R.id.iv_addressgroupindicator);
            deleteView = (ImageView) view.findViewById(R.id.iv_deleteaddress);
            navigationView = (ImageView) view.findViewById(R.id.iv_navigate);
            bookmarkSelectionView = (CheckBox) view.findViewById(R.id.cb_bookmark_selector);
            navigation_layout = (RelativeLayout) view.findViewById(R.id.navigation_layout);
            deleteaddress_layout = (RelativeLayout) view.findViewById(R.id.deleteaddress_layout);
            address_list_row_action_layout = (LinearLayout) view.findViewById(R.id.address_list_row_action_layout);
            this.onAddressClickListener = onAddressClickListener;
        }

        public AddressData getAddressData() {
            return addressData;
        }

        public void setAddressData(AddressData addressData) {
            this.addressData = addressData;
        }

        public void bind(final AddressData item, final OnAddressClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddressCliked(item, itemView);
                }
            });
        }
    }


    public class AddressListFooterViewHolder extends RecyclerView.ViewHolder {

        public AddressListFooterViewHolder(View view) {
            super(view);
        }
    }


    public interface OnAddressClickListener {
        void onAddressCliked(AddressData addressData, View itemView);
    }
}
