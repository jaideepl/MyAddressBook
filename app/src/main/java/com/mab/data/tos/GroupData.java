package com.mab.data.tos;

import java.io.Serializable;

/**
 * Created by Jaideep.Lakshminaray on 27-02-2017.
 */

public class GroupData implements Serializable {
    private long id;
    private String name;
    private int colorCode;
    private boolean isVisible;
    private long modifiedDateMillis;
    private boolean isDefault;
    private long addressesCount;

    public long getAddressesCount() {
        return addressesCount;
    }

    public void setAddressesCount(long addressesCount) {
        this.addressesCount = addressesCount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public long getModifiedDateMillis() {
        return modifiedDateMillis;
    }

    public void setModifiedDateMillis(long modifiedDateMillis) {
        this.modifiedDateMillis = modifiedDateMillis;
    }
}
