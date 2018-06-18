package com.mab.data.provider;

import android.net.Uri;


public class AddressLocalSearchQuery implements BaseQuery {
    private String mSelection;
    private String[] mSelectionArgs;
    private String[] mProjection;
    private String sortOrder;
    private Uri mUri;


    public AddressLocalSearchQuery(String selection,
                                   String[] selectionArgs,
                                   String[] projection, String sortOrder,
                                   Uri uri) {
        mSelection = selection;
        mProjection = projection;
        mSelectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
        mUri = uri;
    }

    @Override
    public String getSelectionQuery() {
        return mSelection;
    }

    @Override
    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    @Override
    public String[] getProjection() {
        return mProjection;
    }

    @Override
    public Uri getContentUri() {
        return mUri;
    }

    public void setSelectionQuery(String selection) {
        mSelection = selection;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String getSortOrder() {
        String sortColumn = "";
//        if (sortOrder.equals(Constants.SORTBY_NAME_ASCENDING)) {
//            sortColumn = MABDatabaseContract.AABDataProvider.COLUMN_NAME_NOTE_NAME + " COLLATE NOCASE ASC";
//        } else if (sortOrder.equals(Constants.SORTBY_NAME_DESCENDING)) {
//            sortColumn = MABDatabaseContract.AABDataProvider.COLUMN_NAME_NOTE_NAME + " COLLATE NOCASE DESC";
//        } else if (sortOrder.equals(Constants.SORTBY_DATE_ASCENDING)) {
//            sortColumn = MABDatabaseContract.AABDataProvider.COLUMN_NAME_NOTE_MODIFIED_DATEMILLIS + " ASC";
//        } else if (sortOrder.equals(Constants.SORTBY_DATE_DESCENDING)) {
//            sortColumn = MABDatabaseContract.AABDataProvider.COLUMN_NAME_NOTE_MODIFIED_DATEMILLIS + " DESC";
//        }
        return sortColumn;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
