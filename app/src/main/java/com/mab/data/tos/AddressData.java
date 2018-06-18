package com.mab.data.tos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jaideep.Lakshminaray on 09-03-2017.
 */

public class AddressData implements Serializable {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private long groupid;
    private int groupcolorcode;
    private String groupName;
    private ArrayList<ContactData> addressContacts;
    private String notes;
    private String landmark;
    private long modifiedDateMillis;
    private boolean isManualAddress;
    private boolean isBookmarked;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean isManualAddress() {
        return isManualAddress;
    }

    public void setManualAddress(boolean manualAddress) {
        isManualAddress = manualAddress;
    }

    public int getGroupcolorcode() {
        return groupcolorcode;
    }

    public void setGroupcolorcode(int groupcolorcode) {
        this.groupcolorcode = groupcolorcode;
    }

    public long getModifiedDateMillis() {
        return modifiedDateMillis;
    }

    public void setModifiedDateMillis(long modifiedDateMillis) {
        this.modifiedDateMillis = modifiedDateMillis;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public ArrayList<ContactData> getAddressContacts() {
        return addressContacts;
    }

    public void setAddressContacts(ArrayList<ContactData> addressContacts) {
        this.addressContacts = addressContacts;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
