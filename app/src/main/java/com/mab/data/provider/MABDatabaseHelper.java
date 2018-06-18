package com.mab.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MABDatabaseHelper extends SQLiteOpenHelper {

    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "addressbook.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 1;


    MABDatabaseHelper(Context context) {

        // calls the super constructor, requesting the default cursor factory.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

    }

    /**
     * Creates the underlying database with table name and column names taken from the
     * NotePad class.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + MABDatabaseContract.GroupsDataProvider.TABLE_NAME + " ("
                + MABDatabaseContract.GroupsDataProvider._ID + " INTEGER,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + " INTEGER PRIMARY KEY,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_NAME + " TEXT,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_COLORCODE + " INTEGER,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_MODIFIED_DATEMILLIS + " INTEGER,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISGROUP_VISIBLE + " INTEGER,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ADDRESS_COUNT + " INTEGER,"
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_ISDEFAULT_GROUP + " INTEGER, UNIQUE ("
                + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + ") ON CONFLICT REPLACE"
                + ");");


        db.execSQL("CREATE TABLE " + MABDatabaseContract.AddressDataProvider.TABLE_NAME + " ("
                + MABDatabaseContract.AddressDataProvider._ID + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + " INTEGER PRIMARY KEY,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NAME + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_NAME + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_COLOR_CODE + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_MODIFIED_DATEMILLIS + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_VALUE + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_CONTACTS_LIST + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_NOTES + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LANDMARK + " TEXT,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LATITUDE + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_LONGITUDE + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_MANUALADDRESS + " INTEGER,"
                + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_IS_BOOKMARKEDADDRESS + " INTEGER,"
                + "FOREIGN KEY (" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_GROUP_CODE + ") REFERENCES " +
                MABDatabaseContract.GroupsDataProvider.TABLE_NAME + " (" + MABDatabaseContract.GroupsDataProvider.COLUMN_NAME_GROUP_ID + ") ON DELETE CASCADE," +
                " UNIQUE (" + MABDatabaseContract.AddressDataProvider.COLUMN_NAME_ADDRESS_ID + ") ON CONFLICT REPLACE"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + MABDatabaseContract.GroupsDataProvider.TABLE_NAME);

        // Recreates the database with a new version
        onCreate(db);
    }

}
