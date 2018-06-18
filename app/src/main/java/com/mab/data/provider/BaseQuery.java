package com.mab.data.provider;

import android.net.Uri;

public interface BaseQuery {
    String getSelectionQuery();

    String[] getSelectionArgs();

    String[] getProjection();

    String getSortOrder();

    Uri getContentUri();
}
