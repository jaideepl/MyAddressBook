package com.mab.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class MABContentProvider extends ContentProvider {
    private static final String TAG = "MABContentProvider";
    private static HashMap<String, String> sGroupsProjectionMap;
    private static HashMap<String, String> sAddressProjectionMap;

    private static final int GROUPS_LIST = 1;
    private static final int GROUP_ID = 2;
    private static final int ADDRESS_LIST = 3;
    private static final int ADDRESS_ID = 4;

    private static final UriMatcher sUriMatcher;
    private MABDatabaseHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MABDatabaseContract.AUTHORITY, "groups_table", GROUPS_LIST);
        sUriMatcher.addURI(MABDatabaseContract.AUTHORITY, "groups_table/#", GROUP_ID);
        sUriMatcher.addURI(MABDatabaseContract.AUTHORITY, "address_table", ADDRESS_LIST);
        sUriMatcher.addURI(MABDatabaseContract.AUTHORITY, "address_table/#", ADDRESS_ID);

        //Address Book Projection Map
        sGroupsProjectionMap = new HashMap<String, String>();
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider._ID, MABDatabaseContract.GroupsDataProvider._ID);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP);
        sGroupsProjectionMap.put(MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ADDRESS_COUNT, MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ADDRESS_COUNT);

        sAddressProjectionMap = new HashMap<String, String>();
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider._ID, MABDatabaseContract.AddressDataProvider._ID);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_NAME, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_NAME);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LATITUDE, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LATITUDE);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LONGITUDE, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LONGITUDE);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_CONTACTS_LIST, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_CONTACTS_LIST);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NOTES, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NOTES);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LANDMARK, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LANDMARK);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_MANUALADDRESS, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_MANUALADDRESS);
        sAddressProjectionMap.put(MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS, MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MABDatabaseHelper(getContext());
        SQLiteDatabase DB = mOpenHelper.getWritableDatabase();

        return (DB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
                qb.setTables(MABDatabaseContract.GroupsDataProvider.TABLE_NAME);
                qb.setProjectionMap(sGroupsProjectionMap);
                break;
            case GROUP_ID:
                qb.setTables(MABDatabaseContract.GroupsDataProvider.TABLE_NAME);
                qb.setProjectionMap(sGroupsProjectionMap);
                qb.appendWhere(
                        MABDatabaseContract.GroupsDataProvider._ID + "=" + uri.getPathSegments().get(MABDatabaseContract.GroupsDataProvider.GROUPDATA_PROVIDER_ID_PATH_POSITION));
                break;

            case ADDRESS_LIST:
                qb.setTables(MABDatabaseContract.AddressDataProvider.TABLE_NAME);
                qb.setProjectionMap(sAddressProjectionMap);
                break;
            case ADDRESS_ID:
                qb.setTables(MABDatabaseContract.AddressDataProvider.TABLE_NAME);
                qb.setProjectionMap(sAddressProjectionMap);
                qb.appendWhere(
                        MABDatabaseContract.AddressDataProvider._ID + "=" + uri.getPathSegments().get(MABDatabaseContract.AddressDataProvider.ADDRESSDATA_PROVIDER_ID_PATH_POSITION));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = MABDatabaseContract.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }


        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(
                db,            // The database to query
                projection,    // The columns to return from the query
                selection,     // The columns for the where clause
                selectionArgs, // The values for the where clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                orderBy        // The sort order
        );
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
                return MABDatabaseContract.GroupsDataProvider.CONTENT_TYPE;
            case GROUP_ID:
                return MABDatabaseContract.GroupsDataProvider.CONTENT_ITEM_TYPE;
            case ADDRESS_LIST:
                return MABDatabaseContract.AddressDataProvider.CONTENT_TYPE;
            case ADDRESS_ID:
                return MABDatabaseContract.AddressDataProvider.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Uri retURI;
        long rowId;

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            //values = new ContentValues();
            throw new IllegalArgumentException("ContentValue is null");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
            case GROUP_ID:

                rowId =
                        db.insertWithOnConflict(
                                MABDatabaseContract.GroupsDataProvider.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                if (rowId > 0) {
                    retURI = ContentUris.withAppendedId(MABDatabaseContract.GroupsDataProvider.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(retURI, null);
                    return retURI;
                }

                break;

            case ADDRESS_LIST:
            case ADDRESS_ID:

                rowId =
                        db.insert(
                                MABDatabaseContract.AddressDataProvider.TABLE_NAME,
                                null,
                                values);
                if (rowId > 0) {
                    retURI = ContentUris.withAppendedId(MABDatabaseContract.AddressDataProvider.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(retURI, null);
                    return retURI;
                }

                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        String table = "";


        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
                table = MABDatabaseContract.GroupsDataProvider.TABLE_NAME;
                break;

            case ADDRESS_LIST:
                table = MABDatabaseContract.AddressDataProvider.TABLE_NAME;
                break;
        }
        SQLiteDatabase sqlDB = mOpenHelper.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = sqlDB.insertWithOnConflict(
                        table,
                        null,
                        cv,
                        SQLiteDatabase.CONFLICT_REPLACE);

                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }
        return numInserted;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;
        int count;

        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
                count = db.delete(
                        MABDatabaseContract.GroupsDataProvider.TABLE_NAME,  // The database table name
                        where,                     // The incoming where clause column names
                        whereArgs                  // The incoming where clause values
                );
                break;
            case GROUP_ID:
                finalWhere =
                        MABDatabaseContract.GroupsDataProvider._ID +
                                " = " +
                                uri.getPathSegments().
                                        get(MABDatabaseContract.GroupsDataProvider.GROUPDATA_PROVIDER_ID_PATH_POSITION);
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(
                        MABDatabaseContract.GroupsDataProvider.TABLE_NAME,  // The database table name.
                        finalWhere,                // The final WHERE clause
                        whereArgs                  // The incoming where clause values.
                );
                break;

            case ADDRESS_LIST:
                count = db.delete(
                        MABDatabaseContract.AddressDataProvider.TABLE_NAME,  // The database table name
                        where,                     // The incoming where clause column names
                        whereArgs                  // The incoming where clause values
                );
                break;
            case ADDRESS_ID:
                finalWhere =
                        MABDatabaseContract.AddressDataProvider._ID +
                                " = " +
                                uri.getPathSegments().
                                        get(MABDatabaseContract.AddressDataProvider.ADDRESSDATA_PROVIDER_ID_PATH_POSITION);
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(
                        MABDatabaseContract.AddressDataProvider.TABLE_NAME,  // The database table name.
                        finalWhere,                // The final WHERE clause
                        whereArgs                  // The incoming where clause values.
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (sUriMatcher.match(uri)) {
            case GROUPS_LIST:
                count = db.update(MABDatabaseContract.GroupsDataProvider.TABLE_NAME, values, where, whereArgs);
                break;
            case GROUP_ID:
                String groupId = uri.getLastPathSegment();
                finalWhere = MABDatabaseContract.GroupsDataProvider._ID + " = " + groupId;
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(MABDatabaseContract.GroupsDataProvider.TABLE_NAME, values, finalWhere, whereArgs);
                break;

            case ADDRESS_LIST:
                count = db.update(MABDatabaseContract.AddressDataProvider.TABLE_NAME, values, where, whereArgs);
                break;
            case ADDRESS_ID:
                String addressId = uri.getLastPathSegment();
                finalWhere = MABDatabaseContract.AddressDataProvider._ID + " = " + addressId;
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(MABDatabaseContract.AddressDataProvider.TABLE_NAME, values, finalWhere, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
