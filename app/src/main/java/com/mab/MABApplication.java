package com.mab;

import android.app.Application;
import android.content.Context;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.maps.MapView;
import com.mab.utils.Util;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Jaideep.Lakshminaray on 31-07-2017.
 */

public class MABApplication extends Application implements BillingProcessor.IBillingHandler {
    private static Context appContext = null;
    public static boolean isContactPermissionGranted = false;
    public static BillingProcessor bp;
    static final String MERCHANT_ID = "03036764948190851471";

    public static final String WEEKLY_SUBSCRIPTION_ID = "weekly_subscription";

    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqLrFgHmeJkTbvrSc9oHMlu1uYMlYhKd6mlu9I7jdwqaLP+eCcMf5AXfKRodk6BOyCkDCWo/FWFPWKkRLeiDGDOVxwRZ8q7+vn3eGfyS2KRRj/2WENe9kvBXkoorCPUWiOvxrjXHsUENx5ozHU+tVyfJMzUWzT0ahjQMjX0E/JZjYVJF8LSAC31QJlrJHsUL/CGe8hWnuuFaAABrmOBcdfbACKzTgQvOZ9yAeog7ipqNo5wLZduKk7eI7wr+DLZ86BhciS7Ahi5n4JjLAmHUB+NPb3I2TjJzOtVTirZPQ7X8ZpdVR9WsXg8POEELhs9LY5IPS1V2nqv24zk7zkjxwAwIDAQAB";

    public static Context getContext() {
        return appContext;
    }

    public static void setAppContext(final Context context) {
        MABApplication.appContext = context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MABApplication.appContext = this;
        initImageLoader(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                } catch (Exception ignored) {

                }
            }
        }).start();

        bp = new BillingProcessor(this, base64EncodedPublicKey, MERCHANT_ID, this);
        bp.initialize();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (bp != null)
            bp.release();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(WEEKLY_SUBSCRIPTION_ID);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Util.displayMessage("Unable to initiate Billing. Please try again after sometime.");
    }

    @Override
    public void onBillingInitialized() {

    }

}
