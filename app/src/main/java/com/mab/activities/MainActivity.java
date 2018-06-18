package com.mab.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.enrico.colorpicker.colorDialog;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.customviews.OverlayHandler;
import com.mab.data.tos.AddressGroupData;
import com.mab.fragments.GroupsListFragment;
import com.mab.fragments.MainFragment;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.EmailFetcher;
import com.mab.utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;

import static com.mab.MABApplication.getContext;
import static com.mab.MABApplication.isContactPermissionGranted;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, colorDialog.ColorSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    DrawerLayout drawer;
    private ImageLoaderConfiguration ilConfig;
    private DisplayImageOptions options;
    public View colorIndicatorView;
    public int groupColor;
    private AppBarLayout mAppBarLayout;
    ImageLoader imageLoader;
    NavigationView navigationView;

    GoogleApiClient mGoogleApiClient;
    private DriveId mFileId;
    public DriveFile file;

    MABController mabController;
    MainActivityUIHandler uiHandler;

    String addressList = "";
    boolean fileOperation;

    Toolbar mToolbar;
    MenuItem searchMenuItem;

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int REQ_ACCPICK = 3;
    private static final int REQUEST_CODE_OPENER = 4;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isContactPermissionGranted = true;
                } else {
                    isContactPermissionGranted = false;
                }


                displayUserDetails();

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        OverlayHandler.getOverlayHandler().hideOverlay();

        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;

            case REQ_ACCPICK:
                if (resultCode == RESULT_OK) {
                    OverlayHandler.getOverlayHandler().displayOverlay(MainActivity.this);

                    String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addConnectionCallbacks(MainActivity.this)
                            .addOnConnectionFailedListener(MainActivity.this)
                            .setAccountName(email)
                            .build();

                    mGoogleApiClient.connect();

                    // create new contents resource
                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(driveContentsCallback);
                }

                break;

            case REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

                    OverlayHandler.getOverlayHandler().displayOverlay(this);
                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.e("file id", mFileId.getResourceId() + "");

                    DriveFile file = mFileId.asDriveFile();
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                            .setResultCallback(contentsOpenedCallback);

                }

                break;
        }
    }

    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    String contents = null;
                    if (!result.getStatus().isSuccess()) {
                        // display an error saying file can't be opened
                        Util.displayMessage("Unable to retrieve the contents");
                        return;
                    }
                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents driveContents = result.getDriveContents();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(driveContents.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        contents = builder.toString();
                    } catch (IOException e) {
                    }

                    driveContents.discard(mGoogleApiClient);

                    if (contents != null && !contents.isEmpty()) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<AddressGroupData>() {
                        }.getType();
                        AddressGroupData addressGroupData = gson.fromJson(contents, type);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(Constants.ADDRESS_GROUPS_DATA, addressGroupData);
                        mabController.processAction(Actions.STORE_ALL_PLACES_AND_GROUPS, uiHandler, data);
                    }

                }
            };

    protected void onResume() {
        super.onResume();
    }

    /**
     * This is Result result handler of Drive contents.
     * this callback method call CreateFileOnGoogleDrive() method
     * and also call OpenFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
     */
    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {

                    if (result.getStatus().isSuccess()) {
                        if (fileOperation == true) {
                            createFileOnGoogleDrive(result);
                        } else {
                            openFileFromGoogleDrive();
                        }

                    } else {
                        Util.displayMessage("Failed to Backup");
                    }
                }
            };

    /**
     * Open list of folder and file of the Google Drive
     */
    public void openFileFromGoogleDrive() {

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

        } catch (IntentSender.SendIntentException e) {

        }

    }

    private void getAllAddresses() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.GROUP_ID, -1);
        data.put("pageIndex", -1);
        mabController.processAction(Actions.GET_ALL_PLACES_AND_GROUPS, uiHandler, data);
    }

    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
    public void createFileOnGoogleDrive(DriveApi.DriveContentsResult result) {

        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                //Check if Drive has the backup file already.
                String id = Util.getStringFromSharedPreferences("DriveId", "");
                if (id != null && !id.isEmpty()) {
                    DriveId driveid = DriveId.decodeFromString(id);
                    DriveFile existingfile = driveid.asDriveFile();
                    if (existingfile != null) {
                        existingfile.delete(mGoogleApiClient);
                    }
                }

                // write content to DriveContents
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try {
                    writer.write(addressList);
                    writer.close();
                } catch (IOException e) {
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("My Address Book Backup")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    OverlayHandler.getOverlayHandler().hideOverlay();
                    if (result.getStatus().isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Backup Successfull!!", Toast.LENGTH_LONG).show();
                        Util.setStringToSharedPreferences("DriveId", result.getDriveFile().getDriveId().toString());
                    }

                    return;

                }
            };


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            // disconnect Google Android Drive API connection.
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Util.setToolbarProperties(mToolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        mabController = MABController.getController();
        uiHandler = new MainActivityUIHandler();

        initNavigationView();

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(5)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse(
                                "mailto:jaideepl1984@gmail.com?" + "subject=" + "Feedback on " + Util.getAppName() + " App - v " + Util
                                        .getVersionName()));
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                feedback);

                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }
                }).build();

        ratingDialog.show();
    }


    private void initNavigationView() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 175, 175, false);
                    }
                })
                .build();

        ilConfig =
                new ImageLoaderConfiguration.Builder(getApplicationContext()).build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ilConfig);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.addresses);
        navigationView.getMenu().performIdentifierAction(R.id.addresses, 0);

        MenuItem nav_camara = navigationView.getMenu().findItem(R.id.versionname);
        nav_camara.setTitle("App Version - " + Util.getVersionName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                return;
            } else {
                isContactPermissionGranted = true;
                displayUserDetails();
            }
        } else {
            isContactPermissionGranted = true;
            displayUserDetails();
        }

    }

    private void displayUserDetails() {
        new EmailFetcher(getContext());
        RelativeLayout headerLayout = (RelativeLayout) navigationView.getHeaderView(0);
        ImageView userThumbnail = (ImageView) headerLayout.findViewById(R.id.iv_userImage);

        if (isContactPermissionGranted) {
            imageLoader.displayImage(EmailFetcher.getPhotoUri(), userThumbnail, options, null);
        } else {
            imageLoader.displayImage("", userThumbnail, options, null);
        }

        TextView userName = (TextView) headerLayout.findViewById(R.id.tv_userName);
        TextView userEmailId = (TextView) headerLayout.findViewById(R.id.tv_userEmailId);
        if (isContactPermissionGranted) {
            if (EmailFetcher.getName() != null && !EmailFetcher.getName().isEmpty()) {
                userName.setText(EmailFetcher.getName());
            } else {
                userName.setText(Util.getAppName());
            }

            if (EmailFetcher.getEmail() != null && !EmailFetcher.getEmail().isEmpty()) {
                userEmailId.setText(EmailFetcher.getEmail());
                userEmailId.setVisibility(View.VISIBLE);
            } else {
                userEmailId.setVisibility(View.GONE);
            }
        } else {
            userName.setText(Util.getAppName());
            userEmailId.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (searchMenuItem != null) {
            searchMenuItem.setVisible(false);
        }

        switch (id) {
            case R.id.addresses:

                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(true);
                }
                getSupportActionBar().setTitle("Places");
                fragment = MainFragment.newInstance("", "");
                break;

            case R.id.groups:
                getSupportActionBar().setTitle("Groups");
                fragment = GroupsListFragment.newInstance("", "");
                break;

            case R.id.backup:
                fileOperation = true;
                getAllAddresses();
                break;

            case R.id.restore:
                fileOperation = false;

                startActivityForResult(AccountPicker.newChooseAccountIntent(null,
                        null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null),
                        REQ_ACCPICK);
                OverlayHandler.getOverlayHandler().displayOverlay(this);
                break;

            case R.id.ratetheapp:
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Util.displayMessage("Couldn't launch the market");
                }
                break;

            case R.id.feedback:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(
                        "mailto:jaideepl1984@gmail.com?" + "subject=" + "Feedback on " + Util.getAppName() + " App - v " + Util
                                .getVersionName()));
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;

            case R.id.promoteapp:
                String shareBody = "Checkout New Android App to Manage Addresses of your favourite Contacts\n" +
                        "https://play.google.com/store/apps/details?id=" + getContext().getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Checkout new Android App : " + Util.getAppName() + "!!!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_share_message)));
                break;

            case R.id.privacypolicy:
                Uri privacypolicyuri = Uri.parse("https://saiblessapps.wordpress.com/2017/02/16/privacy-policy/");
                Intent privacypolicyIntent = new Intent(Intent.ACTION_VIEW, privacypolicyuri);
                try {
                    startActivity(privacypolicyIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MABApplication.getContext(), "Could not display Privacy Policy", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.moreapps:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:saibless")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=saibless")));
                }
                break;
        }

        displayView(fragment);
        return true;
    }

    private void displayView(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.content_main, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {
        GradientDrawable drawable = (GradientDrawable) colorIndicatorView.getBackground();
        drawable.setColor(selectedColor);
        groupColor = selectedColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchMenuItem = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, PlaceSearchListActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        OverlayHandler.getOverlayHandler().hideOverlay();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        OverlayHandler.getOverlayHandler().hideOverlay();
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }


    class MainActivityUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            OverlayHandler.getOverlayHandler().hideOverlay();

            switch (msg.what) {
                case Actions.TECHNICAL_ERROR:
                    Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                    break;

                case Actions.GET_ALL_PLACES_AND_GROUPS:
                    AddressGroupData addressGroupData = (AddressGroupData) msg.obj;
                    Gson gson = new Gson();
                    addressList = gson.toJson(addressGroupData);

                    startActivityForResult(AccountPicker.newChooseAccountIntent(null,
                            null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null),
                            REQ_ACCPICK);

                    OverlayHandler.getOverlayHandler().displayOverlay(MainActivity.this);

                    break;

                case Actions.STORE_ALL_PLACES_AND_GROUPS:
                    Util.displayMessage("Data Restored Successfully!!");
                    getSupportActionBar().setTitle("Places");
                    Fragment fragment = MainFragment.newInstance("", "");
                    displayView(fragment);
                    break;
            }
        }
    }
}
