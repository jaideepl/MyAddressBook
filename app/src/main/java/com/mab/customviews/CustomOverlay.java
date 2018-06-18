package com.mab.customviews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.mab.R;


@SuppressLint("InflateParams")
public class CustomOverlay extends Dialog {
    Context context;
    View v = null;
    ProgressBar objProgressBar;
    Dialog d;

    public CustomOverlay(Context context) {
        super(context);
        this.context = context;
    }

    public CustomOverlay(Context context, int transparent) {
        super(context, transparent);
        this.context = context;
    }

    @Override
    public void show() {
        d = new Dialog(context, R.style.Transparent);
        v = getLayoutInflater().inflate(R.layout.overlay_layout, null);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(v);
        d.setCancelable(false);
        d.show();
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        d.dismiss();
    }

    public void update() {
        objProgressBar = (ProgressBar) v.findViewById(R.id.overlayprogressbar);
        if (objProgressBar != null
                && objProgressBar.getVisibility() == View.GONE) {
            objProgressBar.setVisibility(View.VISIBLE);
        }

    }
}
