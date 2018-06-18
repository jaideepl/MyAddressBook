package com.mab.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Jaideep.Lakshminaray on 21-02-2017.
 */

public class EmailFetcher {
    public static String id = "";
    public static String email = "";
    public static String accountName = "";
    public static String name = "";
    public static String photoUri = "";

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getPhotoUri() {
        photoUri = photoUri.replace("/photo", "");
        return photoUri;
    }

    public EmailFetcher(Context context) {
        final AccountManager manager = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
        final Account[] accounts = manager.getAccountsByType("com.google");
        if (accounts != null && accounts.length > 1 && accounts[0].name != null) {
            accountName = accounts[0].name;

            ContentResolver cr = context.getContentResolver();
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.DATA + " = ?",
                    new String[]{accountName}, null);
            if (emailCur != null) {
                while (emailCur.moveToNext()) {
                    id = emailCur
                            .getString(emailCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
                    email = emailCur
                            .getString(emailCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                    photoUri = emailCur
                            .getString(emailCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.PHOTO_URI));

                    Log.v("Got contacts", "ID " + id + " Email : " + email + " thumbnailUri : " + photoUri);
                }

                emailCur.close();
            } else {
                id = "";
                email = "";
                photoUri = "";
            }


            try {
                Cursor c = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
                int count = c.getCount();
                boolean b = c.moveToFirst();
                int position = c.getPosition();
                if (count == 1 && position == 0) {
                    name = c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
                    Log.v("Contacts", "Contacts Name: " + name);
                }
                c.close();
            } catch (SecurityException se) {
                name = "";
            }


        }
    }
}
