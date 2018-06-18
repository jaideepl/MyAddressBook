package com.mab;

import android.os.Handler;

import com.mab.utils.Actions;

import java.util.Map;

/**
 * Created by Jaideep.Lakshminaray on 20-10-2015.
 */
public class MABController {
    private static MABController objMABController;

    private MABController() {

    }

    public static synchronized MABController getController() {
        if (objMABController == null) {
            objMABController = new MABController();
        }
        return objMABController;
    }

    public Object processAction(int what, Handler handler,
                                Map<String, Object> map) {

        MABHelper MABHelper = new MABHelper();
//        OverlayHandler.getOverlayHandler().hideOverlay();
        switch (what) {

            case Actions.GET_ALL_GROUPS:
                MABHelper.handleGetAllGroups(what, handler, map);
                break;

            case Actions.ADD_EDIT_GROUP:
                MABHelper.handleAddEditGroups(what, handler, map);
                break;

            case Actions.DELETE_GROUP:
                MABHelper.handleDeleteGroup(what, handler, map);
                break;

            case Actions.GET_GROUP_DATA:
                MABHelper.handleGetGroupData(what, handler, map);
                break;

            case Actions.ADD_EDIT_PLACE:
                MABHelper.handleAddEditAddress(what, handler, map);
                break;

            case Actions.BOOKMARK_PLACE:
                MABHelper.handleBookmarkAddress(what, handler, map);
                break;

            case Actions.DELETE_BOOKMARK:
                MABHelper.handleDeleteBookmark(what, handler, map);
                break;
            case Actions.GET_ALL_PLACES:
                MABHelper.handleGetAllPlaces(what, handler, map);
                break;

            case Actions.GET_ALL_BOOKMARKS:
                MABHelper.handleGetAllBookmarks(what, handler, map);
                break;

            case Actions.DELETE_PLACE:
                MABHelper.handleDeletePlace(what, handler, map);
                break;

            case Actions.GET_ALL_PLACES_AND_GROUPS:
                MABHelper.handleGetAllPlacesAndGroups(what, handler, map);
                break;

            case Actions.STORE_ALL_PLACES_AND_GROUPS:
                MABHelper.handleSaveAllPlacesAndGroups(what, handler, map);
                break;
        }

        return null;
    }
}
