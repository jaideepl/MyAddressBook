package com.mab.data.tos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jaideep.Lakshminaray on 10-04-2017.
 */

public class AddressGroupData implements Serializable {
    private ArrayList<AddressData> addressDataArrayList;
    private ArrayList<GroupData> groupDataArrayList;

    public ArrayList<AddressData> getAddressDataArrayList() {
        return addressDataArrayList;
    }

    public void setAddressDataArrayList(ArrayList<AddressData> addressDataArrayList) {
        this.addressDataArrayList = addressDataArrayList;
    }

    public ArrayList<GroupData> getGroupDataArrayList() {
        return groupDataArrayList;
    }

    public void setGroupDataArrayList(ArrayList<GroupData> groupDataArrayList) {
        this.groupDataArrayList = groupDataArrayList;
    }
}
