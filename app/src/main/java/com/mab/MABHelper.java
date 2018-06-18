package com.mab;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mab.data.provider.MABDatabaseContract;
import com.mab.data.tos.AddressData;
import com.mab.data.tos.AddressGroupData;
import com.mab.data.tos.ContactData;
import com.mab.data.tos.GroupData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MABHelper {

    static final String TAG = "MABHelper";


    public void handleGetAllGroups(int what, Handler handler, Object data) {
        new Thread(new GetAllGroupsRunnable(what, handler, data)).start();
    }

    private class GetAllGroupsRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public GetAllGroupsRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            ArrayList<GroupData> groupDataArrayList = new ArrayList<GroupData>();

            try {
                HashMap<String, Object> map = new HashMap<>();
                if (data != null) {
                    map = (HashMap<String, Object>) data;
                }
                boolean isVisibleGroupsFilter = false;
                if (map.containsKey(Constants.FILTER_VISIBLE_GROUPS)) {
                    isVisibleGroupsFilter = (boolean) map.get(Constants.FILTER_VISIBLE_GROUPS);
                }

                getAllGroupsDataFromDb(groupDataArrayList, isVisibleGroupsFilter);
                if (groupDataArrayList == null || groupDataArrayList.size() == 0) {
                    //create default group "Default"
                    addDefaultGroupInDb(groupDataArrayList);
                }
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = groupDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }

    private void getAllGroupsDataFromDb(ArrayList<GroupData> groupDataArrayList, boolean isVisibleGroupsFilter) {
        String selection = null;
        if (isVisibleGroupsFilter) {
            selection = MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE + "=1";
        }
        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, null, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                GroupData groupData = new GroupData();
                groupDataArrayList.add(groupData);

                String addressCount = cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ADDRESS_COUNT));
                if (addressCount != null && !addressCount.isEmpty()) {
                    long addressVal = Long.valueOf(addressCount);
                    groupData.setAddressesCount(addressVal);
                }
                groupData.setId(Long.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID))));
                groupData.setName(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME)));
                groupData.setColorCode(Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE))));
                groupData.setModifiedDateMillis(Long.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS))));

                int isVisible = Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE)));
                groupData.setVisible(isVisible == 1 ? true : false);

                int isDefault = Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP)));
                groupData.setDefault(isDefault == 1 ? true : false);

            } while (cursor.moveToNext());
        }
    }


    public void handleGetGroupData(int what, Handler handler, Object data) {
        new Thread(new GetGroupDataRunnable(what, handler, data)).start();
    }

    private class GetGroupDataRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public GetGroupDataRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            GroupData groupData = new GroupData();

            try {

                HashMap<String, Object> map = (HashMap<String, Object>) data;

                getGroupDataFromDb(groupData, (Long) map.get(Constants.GROUP_ID));
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = groupData;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }

    private void getGroupDataFromDb(GroupData groupData, long groupId) {
        String selection = null;
        selection = MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + "=" + groupId;
        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, null, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupData.setId(Long.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID))));
                groupData.setName(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME)));
                groupData.setColorCode(Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE))));
                groupData.setModifiedDateMillis(Long.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS))));

                int isVisible = Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE)));
                groupData.setVisible(isVisible == 1 ? true : false);

                int isDefault = Integer.valueOf(cursor.getString(
                        cursor.getColumnIndex(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP)));
                groupData.setDefault(isDefault == 1 ? true : false);

            } while (cursor.moveToNext());
        }
    }

    private boolean hasGroupInDb(long groupId) {
        boolean retVal;
        String selection = null;
        selection = MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + "=" + groupId;
        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, null, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            retVal = true;
        } else {
            retVal = false;
        }
        return retVal;
    }

    private boolean hasAddressInDb(long addressId) {
        boolean retVal;
        String selection = null;
        selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + "=" + addressId;
        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.AddressDataProvider.CONTENT_URI, null, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            retVal = true;
        } else {
            retVal = false;
        }
        return retVal;
    }


    public void handleAddEditGroups(int what, Handler handler, Object data) {
        new Thread(new AddEditGroupsRunnable(what, handler, data)).start();
    }

    private class AddEditGroupsRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public AddEditGroupsRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            HashMap<String, Object> map = (HashMap<String, Object>) data;
            GroupData groupData = (GroupData) map.get(Constants.GROUP_DATA);

            boolean isNewGroup = false;
            if (map.containsKey("isNewGroup")) {
                isNewGroup = (boolean) map.get("isNewGroup");
            }

            ContentValues contentValues = getGroupDataContentValuesForSave(groupData);
            ContentResolver resolver = MABApplication.getContext().getContentResolver();

            if (!isNewGroup) {
                String selection = MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + " =" + groupData.getId();
                resolver.update(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, contentValues, selection, null);
            } else {
                resolver.insert(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, contentValues);
            }

            ArrayList<GroupData> groupDataArrayList = new ArrayList<GroupData>();

            try {
                getAllGroupsDataFromDb(groupDataArrayList, false);

                if (groupDataArrayList == null || groupDataArrayList.size() == 0) {
                    //create default group "Default"
                    addDefaultGroupInDb(groupDataArrayList);
                }

                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();
                ArrayList<AddressData> addressDataArrayList = new ArrayList<>();
                getAllPlacesDataFromDb(addressDataArrayList, groupData.getId(), "", "");
                if (addressDataArrayList != null && addressDataArrayList.size() > 0) {
                    for (int i = 0; i < addressDataArrayList.size(); i++) {
                        addressDataArrayList.get(i).setGroupcolorcode(groupData.getColorCode());

                        String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressDataArrayList.get(i).getId();
                        ops.add(
                                ContentProviderOperation.newUpdate(MABDatabaseContract.AddressDataProvider.CONTENT_URI)
                                        .withValue(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE, groupData.getColorCode())
                                        .withSelection(selection, null)
                                        .withYieldAllowed(true)
                                        .build());
                    }
                }

                try {
                    MABApplication.getContext().getContentResolver().
                            applyBatch(MABDatabaseContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }


                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = groupDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleDeleteGroup(int what, Handler handler, Object data) {
        new Thread(new DeleteGroupRunnable(what, handler, data)).start();
    }

    private class DeleteGroupRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public DeleteGroupRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            HashMap<String, Object> map = (HashMap<String, Object>) data;
            long groupId = (long) map.get(Constants.GROUP_ID);

            String selection = MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + " =" + groupId;
            ContentResolver resolver = MABApplication.getContext().getContentResolver();
            resolver.delete(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, selection, null);

            ArrayList<GroupData> groupDataArrayList = new ArrayList<GroupData>();

            try {
                getAllGroupsDataFromDb(groupDataArrayList, false);

                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = groupDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }

    private void addDefaultGroupInDb(ArrayList<GroupData> groupDataArrayList) {
        GroupData groupData = new GroupData();
        groupDataArrayList.add(groupData);

        long timeMillis = Calendar.getInstance().getTimeInMillis();
        groupData.setDefault(true);
        groupData.setId(0);
        groupData.setName("Default");
        groupData.setVisible(true);
        groupData.setColorCode(MABApplication.getContext().getResources().getColor(R.color.colorAccent));
        groupData.setModifiedDateMillis(timeMillis);

        ContentValues newcontentValues = new ContentValues();

        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID, groupData.getId());
        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP, groupData.isDefault() ? 1 : 0);
        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME, groupData.getName());
        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE, groupData.getColorCode());
        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS, groupData.getModifiedDateMillis());
        newcontentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE, groupData.isVisible() ? 1 : 0);
        ContentResolver newresolver = MABApplication.getContext().getContentResolver();
        try {
            newresolver.insert(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, newcontentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private ContentValues getGroupDataContentValuesForSave(GroupData groupData) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID, groupData.getId());
        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME, groupData.getName());
        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE, groupData.getColorCode());
        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS, groupData.getModifiedDateMillis());
        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE, groupData.isVisible() ? 1 : 0);
        contentValues.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP, groupData.isDefault() ? 1 : 0);

        return contentValues;
    }


    public void handleAddEditAddress(int what, Handler handler, Object data) {
        new Thread(new AddEditAddressRunnable(what, handler, data)).start();
    }

    private class AddEditAddressRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public AddEditAddressRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            try {
                HashMap<String, Object> map = (HashMap<String, Object>) data;
                AddressData addressData = (AddressData) map.get(Constants.ADDRESSDATA);

                boolean isNewAddress = false;
                if (map.containsKey("isNewAddress")) {
                    isNewAddress = (boolean) map.get("isNewAddress");
                }

                ContentValues contentValues = getAddressDataContentValuesForSave(addressData);
                ContentResolver resolver = MABApplication.getContext().getContentResolver();

                if (!isNewAddress) {
                    //Edit Mode
                    String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressData.getId();
                    resolver.update(MABDatabaseContract.AddressDataProvider.CONTENT_URI, contentValues, selection, null);
                } else {
                    resolver.insert(MABDatabaseContract.AddressDataProvider.CONTENT_URI, contentValues);
                }

                Message msg = handler.obtainMessage();
                msg.what = Actions.ADD_EDIT_PLACE;
                msg.obj = null;
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleBookmarkAddress(int what, Handler handler, Object data) {
        new Thread(new BookmarkAddressRunnable(what, handler, data)).start();
    }

    private class BookmarkAddressRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public BookmarkAddressRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            try {
                HashMap<String, Object> map = (HashMap<String, Object>) data;
                AddressData addressData = (AddressData) map.get(Constants.ADDRESSDATA);

                ContentValues contentValues = getAddressDataContentValuesForSave(addressData);
                ContentResolver resolver = MABApplication.getContext().getContentResolver();

                String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressData.getId();
                resolver.update(MABDatabaseContract.AddressDataProvider.CONTENT_URI, contentValues, selection, null);

                Message msg = handler.obtainMessage();
                msg.what = Actions.BOOKMARK_PLACE;
                msg.obj = null;
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleDeleteBookmark(int what, Handler handler, Object data) {
        new Thread(new DeleteBookmarkRunnable(what, handler, data)).start();
    }

    private class DeleteBookmarkRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public DeleteBookmarkRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            try {
                HashMap<String, Object> map = (HashMap<String, Object>) data;
                AddressData addressData = (AddressData) map.get(Constants.ADDRESSDATA);

                ContentValues contentValues = getAddressDataContentValuesForSave(addressData);
                ContentResolver resolver = MABApplication.getContext().getContentResolver();

                String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressData.getId();
                resolver.update(MABDatabaseContract.AddressDataProvider.CONTENT_URI, contentValues, selection, null);

                ArrayList<AddressData> addressDataArrayList = new ArrayList<>();
                String sortby = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);
                getAllBookmarksDataFromDb(addressDataArrayList, sortby, "");

                Message msg = handler.obtainMessage();
                msg.what = Actions.DELETE_BOOKMARK;
                msg.obj = addressDataArrayList;
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }

    private ContentValues getAddressDataContentValuesForSave(AddressData addressData) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID, addressData.getId());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME, addressData.getName());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_NAME, addressData.getGroupName());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE, addressData.getGroupid());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE, addressData.getGroupcolorcode());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS, addressData.getModifiedDateMillis());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE, addressData.getAddress());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NOTES, addressData.getNotes());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LANDMARK, addressData.getLandmark());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LATITUDE, addressData.getLatitude());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LONGITUDE, addressData.getLongitude());
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_MANUALADDRESS, addressData.isManualAddress() ? 1 : 0);
        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS, addressData.isBookmarked() ? 1 : 0);

        String contactlistJson = "";
        if (addressData.getAddressContacts() != null && addressData.getAddressContacts().size() > 0) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ContactData>>() {
            }.getType();
            contactlistJson = gson.toJson(addressData.getAddressContacts(), type);
        } else {
            contactlistJson = "";
        }

        contentValues.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_CONTACTS_LIST, contactlistJson);

        return contentValues;
    }


    public void handleGetAllPlaces(int what, Handler handler, Object data) {
        new Thread(new GetAllPlacesRunnable(what, handler, data)).start();
    }

    private class GetAllPlacesRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public GetAllPlacesRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            ArrayList<AddressData> addressDataArrayList = new ArrayList<AddressData>();

            try {
                HashMap<String, Object> map = new HashMap<>();
                if (data != null) {
                    map = (HashMap<String, Object>) data;
                }
                long groupId;
                groupId = Long.valueOf(map.get(Constants.GROUP_ID).toString());

                String searchText = "";
                if (map.containsKey(Constants.SEARCH_TEXT)) {
                    searchText = (String) map.get(Constants.SEARCH_TEXT);
                }

                int pageIndex = 0;
                if (map.containsKey("pageIndex")) {
                    pageIndex = (int) map.get("pageIndex");
                }

                if (pageIndex == 0) {
                    groupId = -1;
                }

                String sortby = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);

                getAllPlacesDataFromDb(addressDataArrayList, groupId, sortby, searchText);

                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = addressDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleGetAllBookmarks(int what, Handler handler, Object data) {
        new Thread(new GetAllBookmarksRunnable(what, handler, data)).start();
    }

    private class GetAllBookmarksRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public GetAllBookmarksRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            ArrayList<AddressData> addressDataArrayList = new ArrayList<AddressData>();

            try {
                HashMap<String, Object> map = new HashMap<>();
                if (data != null) {
                    map = (HashMap<String, Object>) data;
                }

                String searchText = "";
                if (map.containsKey(Constants.SEARCH_TEXT)) {
                    searchText = (String) map.get(Constants.SEARCH_TEXT);
                }

                String sortby = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);

                getAllBookmarksDataFromDb(addressDataArrayList, sortby, searchText);

                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = addressDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    private void getAllPlacesDataFromDb(ArrayList<AddressData> addressDataArrayList, long groupId, String sortby, String searchText) {
        String selection;

        if (groupId == -1) {
            selection = null;
        } else {
            selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE + "=" + groupId;
        }

        if (searchText != null && !searchText.isEmpty()) {
            if (selection != null) {
                selection += " AND (" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " LIKE '%" + searchText + "%' OR "
                        + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE + " LIKE '%" + searchText + "%')";
            } else {
                selection = "(" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " LIKE '%" + searchText + "%' OR "
                        + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE + " LIKE '%" + searchText + "%')";
            }
        }


        String sortbyoption = getSortbyOptions(sortby);

        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.AddressDataProvider.CONTENT_URI, null, selection, null, sortbyoption);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                populateAddressData(addressDataArrayList, cursor);

                //temp
//                for (int i = 0; i < 20; i++) {
//                    populateAddressData(addressDataArrayList, cursor);
//                }
                //temp


            } while (cursor.moveToNext());
        }
    }

    private void getAllBookmarksDataFromDb(ArrayList<AddressData> addressDataArrayList, String sortby, String searchText) {
        String selection;

        selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS + "= '1'";

        if (searchText != null && !searchText.isEmpty()) {
            if (selection != null) {
                selection += " AND (" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " LIKE '%" + searchText + "%' OR "
                        + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE + " LIKE '%" + searchText + "%')";
            } else {
                selection = "(" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " LIKE '%" + searchText + "%' OR "
                        + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE + " LIKE '%" + searchText + "%')";
            }
        }


        String sortbyoption = getSortbyOptions(sortby);

        Cursor cursor = MABApplication.getContext().getContentResolver().query(MABDatabaseContract.AddressDataProvider.CONTENT_URI, null, selection, null, sortbyoption);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                populateAddressData(addressDataArrayList, cursor);
            } while (cursor.moveToNext());
        }
    }

    private void populateAddressData(ArrayList<AddressData> addressDataArrayList, Cursor cursor) {
        AddressData addressData = new AddressData();
        addressDataArrayList.add(addressData);

        addressData.setId(Long.valueOf(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID))));
        addressData.setName(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME)));
        addressData.setGroupid(Long.valueOf(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE))));
        addressData.setGroupName(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_NAME)));
        ;
        addressData.setGroupcolorcode(Integer.valueOf(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE))));
        addressData.setAddress(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE)));
        addressData.setModifiedDateMillis(Long.parseLong(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS))));
        addressData.setLandmark(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LANDMARK)));
        addressData.setLatitude(Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LATITUDE))));
        addressData.setLongitude(Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LONGITUDE))));
        addressData.setNotes(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NOTES)));

        String contactsData = cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_CONTACTS_LIST));

        if (contactsData != null && !contactsData.isEmpty() && !contactsData.equalsIgnoreCase("null")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ContactData>>() {
            }.getType();
            ArrayList<ContactData> contactDataArrayList = gson.fromJson(contactsData, type);
            addressData.setAddressContacts(contactDataArrayList);
        }

        int manualAddress = Integer.valueOf(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_MANUALADDRESS)));

        addressData.setManualAddress((manualAddress == 1) ? true : false);

        int bookmarkedAddress = Integer.valueOf(cursor.getString(
                cursor.getColumnIndex(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS)));

        addressData.setBookmarked((bookmarkedAddress == 1) ? true : false);
    }

    private String getSortbyOptions(String sortby) {
        String sortbyoption = "";
        if (sortby.isEmpty()) {
            sortbyoption = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS + " DESC";
        } else if (sortby.equalsIgnoreCase(Constants.SORTBY_DATE_ASCENDING)) {
            sortbyoption = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS + " ASC";
        } else if (sortby.equalsIgnoreCase(Constants.SORTBY_DATE_DESCENDING)) {
            sortbyoption = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS + " DESC";
        } else if (sortby.equalsIgnoreCase(Constants.SORTBY_NAME_ASCENDING)) {
            sortbyoption = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " ASC";
        } else if (sortby.equalsIgnoreCase(Constants.SORTBY_NAME_DESCENDING)) {
            sortbyoption = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " DESC";
        }

        return sortbyoption;
    }


    public void handleDeletePlace(int what, Handler handler, Object data) {
        new Thread(new DeletePlaceRunnable(what, handler, data)).start();
    }

    private class DeletePlaceRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public DeletePlaceRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            HashMap<String, Object> map = (HashMap<String, Object>) data;
            long addressId = (long) map.get(Constants.ADDRESS_ID);
            long groupId = (long) map.get(Constants.GROUP_ID);
            String sortby = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);

            String searchText = "";
            if (map.containsKey(Constants.SEARCH_TEXT)) {
                searchText = (String) map.get(Constants.SEARCH_TEXT);
            }

            String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressId;
            ContentResolver resolver = MABApplication.getContext().getContentResolver();
            resolver.delete(MABDatabaseContract.AddressDataProvider.CONTENT_URI, selection, null);

            ArrayList<AddressData> addressDataArrayList = new ArrayList<AddressData>();

            try {
                getAllPlacesDataFromDb(addressDataArrayList, groupId, sortby, searchText);

                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = addressDataArrayList;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleGetAllPlacesAndGroups(int what, Handler handler, Object data) {
        new Thread(new GetAllPlacesAndGroupsRunnable(what, handler, data)).start();
    }

    private class GetAllPlacesAndGroupsRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public GetAllPlacesAndGroupsRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            ArrayList<AddressData> addressDataArrayList = new ArrayList<AddressData>();
            ArrayList<GroupData> groupDatas = new ArrayList<GroupData>();
            AddressGroupData addressGroupData = new AddressGroupData();

            try {
                HashMap<String, Object> map = new HashMap<>();
                if (data != null) {
                    map = (HashMap<String, Object>) data;
                }

                String sortby = Util.getStringFromSharedPreferences(Constants.PLACE_SORTBY_OPTION, Constants.SORTBY_DATE_DESCENDING);

                getAllPlacesDataFromDb(addressDataArrayList, -1, sortby, "");
                getAllGroupsDataFromDb(groupDatas, false);

                addressGroupData.setAddressDataArrayList(addressDataArrayList);
                addressGroupData.setGroupDataArrayList(groupDatas);

                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = addressGroupData;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }


    public void handleSaveAllPlacesAndGroups(int what, Handler handler, Object data) {
        new Thread(new SaveAllPlacesAndGroupsRunnable(what, handler, data)).start();
    }

    private class SaveAllPlacesAndGroupsRunnable implements Runnable {
        private Object data;
        private int what;
        private Handler handler;

        public SaveAllPlacesAndGroupsRunnable(int what, Handler handler, Object data) {
            this.data = data;
            this.handler = handler;
            this.what = what;
        }

        public void run() {
            AddressGroupData addressGroupData;

            try {
                HashMap<String, Object> map = new HashMap<>();
                if (data != null) {
                    map = (HashMap<String, Object>) data;
                }

                addressGroupData = (AddressGroupData) map.get(Constants.ADDRESS_GROUPS_DATA);

                if (addressGroupData.getGroupDataArrayList() != null && addressGroupData.getGroupDataArrayList().size() > 0) {

                    //Check if Group already exists in db. If it exists, do not replace it.
                    ArrayList<GroupData> filteredGroupDataList = new ArrayList<>();
                    for (int i = 0; i < addressGroupData.getGroupDataArrayList().size(); i++) {
                        boolean isGroupPresentInDb = hasGroupInDb(addressGroupData.getGroupDataArrayList().get(i).getId());
                        if (!isGroupPresentInDb) {
                            filteredGroupDataList.add(addressGroupData.getGroupDataArrayList().get(i));
                        }
                    }

                    long insertCount = 0;
                    try {
                        // insert new entries

                        // ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
                        ArrayList<ContentValues> valueList = new ArrayList<>();
                        for (int i = 0; i < filteredGroupDataList.size(); i++) {
                            ContentValues values = getGroupDataContentValuesForSave(filteredGroupDataList.get(i));
                            valueList.add(values);
                        }

                        ContentValues[] values = new ContentValues[valueList.size()];
                        values = valueList.toArray(values);
                        // returns ID
                        insertCount = MABApplication.getContext().getContentResolver().bulkInsert(MABDatabaseContract.GroupsDataProvider.CONTENT_URI, values);

                    } catch (Exception e) {
                        // Your error handling
                        Message msg = handler.obtainMessage();
                        msg.what = Actions.TECHNICAL_ERROR;
                        msg.obj = e;
                        handler.sendMessage(msg);
                    }
                    Log.d("MABHelper", "" + insertCount);
                }

                if (addressGroupData.getAddressDataArrayList() != null && addressGroupData.getAddressDataArrayList().size() > 0) {

                    //Check if Address already exists in db. If it exists, delete it.
                    for (int i = 0; i < addressGroupData.getAddressDataArrayList().size(); i++) {
                        boolean isAddressPresentInDb = hasAddressInDb(addressGroupData.getAddressDataArrayList().get(i).getId());
                        if (isAddressPresentInDb) {
                            String selection = MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " =" + addressGroupData.getAddressDataArrayList().get(i).getId();
                            ContentResolver resolver = MABApplication.getContext().getContentResolver();
                            resolver.delete(MABDatabaseContract.AddressDataProvider.CONTENT_URI, selection, null);
                        }
                    }

                    long insertCount = 0;
                    try {
                        // insert new entries

                        // ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
                        ArrayList<ContentValues> valueList = new ArrayList<>();
                        for (int i = 0; i < addressGroupData.getAddressDataArrayList().size(); i++) {
                            ContentValues values = getAddressDataContentValuesForSave(addressGroupData.getAddressDataArrayList().get(i));
                            valueList.add(values);
                        }

                        ContentValues[] values = new ContentValues[valueList.size()];
                        values = valueList.toArray(values);
                        // returns ID
                        insertCount = MABApplication.getContext().getContentResolver().bulkInsert(MABDatabaseContract.AddressDataProvider.CONTENT_URI, values);

                    } catch (Exception e) {
                        // Your error handling
                        Message msg = handler.obtainMessage();
                        msg.what = Actions.TECHNICAL_ERROR;
                        msg.obj = e;
                        handler.sendMessage(msg);
                    }
                    Log.d("MABHelper", "" + insertCount);
                }


                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = addressGroupData;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = Actions.TECHNICAL_ERROR;
                msg.obj = e;
                handler.sendMessage(msg);
            }
        }
    }
}
