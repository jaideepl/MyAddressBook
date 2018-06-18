package com.mab.customviews;

import android.app.Activity;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mab.R;


public class OverlayHandler {

    CustomOverlay objDialog;

    RelativeLayout objOverlayLayout;

    private static OverlayHandler objOverlayHandler;

    public static synchronized OverlayHandler getOverlayHandler() {
        if (objOverlayHandler == null) {
            objOverlayHandler = new OverlayHandler();
        } else {
            objOverlayHandler.hideOverlay();
        }
        return objOverlayHandler;
    }

    public void displayOverlay(Activity objCurrentActivity) {

        objOverlayLayout = (RelativeLayout) objCurrentActivity
                .getLayoutInflater().inflate(R.layout.overlay_layout, null);

        if (objDialog != null && !objDialog.isShowing()) {
            objDialog.show();
        } else if (objDialog == null) {
            objDialog = new CustomOverlay(objCurrentActivity,
                    R.style.Transparent);
            objDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            objDialog.setContentView(R.layout.overlay_layout);
            objDialog.setCancelable(false);
            objDialog.show();
        }

    }

    public void hideOverlay() {

        if (objOverlayHandler != null && objOverlayHandler.objDialog != null) {
            objOverlayHandler.objDialog.dismiss();
            objOverlayHandler.objDialog = null;
        }

    }

}
