package com.mab.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class MABDatabaseContract {

    public static final String AUTHORITY = "com.mab.data.provider.MABDatabaseContract";
    private static final String SCHEME = "content://";
    public static final String DEFAULT_SORT_ORDER = "name ASC";

    private MABDatabaseContract() {
    }

    public static final class GroupsDataProvider implements BaseColumns {
        private GroupsDataProvider() {
        }

        public static final String TABLE_NAME = "groups_table";
        private static final String PATH_GROUPSDATA_PROVIDER = "/groups_table";
        private static final String PATH_GROUPSDATA_PROVIDER_ID = "/groups_table/";
        public static final int GROUPDATA_PROVIDER_ID_PATH_POSITION = 1;

        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_GROUPSDATA_PROVIDER);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_GROUPSDATA_PROVIDER_ID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_GROUPSDATA_PROVIDER_ID + "/#");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.mab.data.provider.groupsdata";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.mab.data.provider.groupsdata";


        public static final String COLUMN_NAME_GROUP_ID = "groupid";
        public static final String COLUMN_NAME_GROUP_NAME = "name";
        public static final String COLUMN_NAME_GROUP_COLORCODE = "colorcode";
        public static final String COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS = "modifieddatemillis";
        public static final String COLUMN_NAME_ISGROUP_VISIBLE = "isvisible";
        public static final String COLUMN_NAME_ADDRESS_COUNT = "addressescount";
        public static final String COLUMN_NAME_ISDEFAULT_GROUP = "isdefault";
    }

    public static final class AddressDataProvider implements BaseColumns {
        private AddressDataProvider() {
        }

        public static final String TABLE_NAME = "address_table";
        private static final String PATH_ADDRESSDATA_PROVIDER = "/address_table";
        private static final String PATH_ADDRESSDATA_PROVIDER_ID = "/address_table/";
        public static final int ADDRESSDATA_PROVIDER_ID_PATH_POSITION = 1;

        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_ADDRESSDATA_PROVIDER);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_ADDRESSDATA_PROVIDER_ID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_ADDRESSDATA_PROVIDER_ID + "/#");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.mab.data.provider.addressdata";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.mab.data.provider.addressdata";


        public static final String COLUMN_NAME_ADDRESS_ID = "addressid";
        public static final String COLUMN_NAME_ADDRESS_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS_GROUP_CODE = "groupcode";
        public static final String COLUMN_NAME_ADDRESS_GROUP_NAME = "groupname";
        public static final String COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE = "groupcolorcode";
        public static final String COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS = "modifieddatemillis";
        public static final String COLUMN_NAME_ADDRESS_VALUE = "addressval";
        public static final String COLUMN_NAME_ADDRESS_LATITUDE = "latitude";
        public static final String COLUMN_NAME_ADDRESS_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ADDRESS_CONTACTS_LIST = "contacts";
        public static final String COLUMN_NAME_ADDRESS_NOTES = "notes";
        public static final String COLUMN_NAME_ADDRESS_LANDMARK = "landmark";
        public static final String COLUMN_NAME_IS_MANUALADDRESS = "ismanualaddress";
        public static final String COLUMN_NAME_IS_BOOKMARKEDADDRESS = "isbookmarkedaddress";
    }
}
