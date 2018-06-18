package com.mab.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mab.MABApplication;
import com.mab.R;
import com.mab.data.tos.AddressData;
import com.mab.data.tos.ContactData;
import com.mab.utils.Constants;
import com.mab.utils.Util;

public class ViewAddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Toolbar toolbar;
    GoogleMap addressMap;
    AddressData addressData;
    TextView tv_savedaddress;
    LinearLayout savedcontacts_container;
    TextView tv_savedaddress_notes;
    TextView tv_savedaddress_landmark;
    TextView savedcontacts_contacts_empty_message;
    TextView savedaddress_notes_empty_message;
    TextView savedaddress_landmark_empty_message;
    LinearLayout googleMap_container;
    SupportMapFragment supportMapFragment;
    String parentActivity = "";
    long groupId;

    GoogleApiClient googleApiClient;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public void settingsrequest() {
        if (googleApiClient.isConnected()) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); //this is the key ingredient

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            setupLocationOnMap();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(ViewAddressActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setupLocationOnMap();
                        break;
                    case Activity.RESULT_CANCELED:
//                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLocationOnMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MABApplication.getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
        }

        addressData = (AddressData) getIntent().getSerializableExtra(Constants.ADDRESSDATA);

        if (getIntent().hasExtra("parentActivity")) {
            parentActivity = getIntent().getStringExtra("parentActivity");
        }

        if (getIntent().hasExtra(Constants.GROUP_ID)) {
            groupId = getIntent().getLongExtra(Constants.GROUP_ID, 0);
        }

        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }

    private void navigateBack() {
        Intent intent = new Intent();

        if (getIntent().hasExtra("parentActivity")) {

            if (parentActivity.equalsIgnoreCase("MainActivity")) {
                intent = new Intent(ViewAddressActivity.this, MainActivity.class);
            } else if (parentActivity.equalsIgnoreCase("GroupPlacesListActivity")) {
                intent = new Intent(ViewAddressActivity.this, GroupPlacesListActivity.class);
                intent.putExtra(Constants.GROUP_ID, groupId);
            }
        } else {
            intent = new Intent(ViewAddressActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_address_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                navigateBack();
                break;

            case R.id.action_edit:
                Intent intent = new Intent(ViewAddressActivity.this, AddEditAddressActivity.class);
                intent.putExtra("parentActivity", parentActivity);
                intent.putExtra(Constants.GROUP_ID, groupId);
                intent.putExtra(Constants.ADDRESSDATA, addressData);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
                break;

            case R.id.action_maps:
                String uri = "";
                if (addressData.getLatitude() != 0 && addressData.getLongitude() != 0) {
                    uri = "http://maps.google.com/maps?q=loc:" + addressData.getLatitude() + "," + addressData.getLongitude() + "(" + addressData.getName() + ")";
                } else if (addressData.isManualAddress()) {
                    uri = "http://maps.google.co.in/maps?q=" + addressData.getAddress();
                }
                Intent mapsintent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(mapsintent);
                break;

            case R.id.action_share:
                String mapsLocation = "";
                String shareBody = "Name: " + addressData.getName();
                shareBody += "\nAddress: " + addressData.getAddress();
                if (!addressData.isManualAddress()) {
                    mapsLocation = "http://maps.google.com/maps?q=loc:" + addressData.getLatitude() + "," + addressData.getLongitude() + "(" + addressData.getName() + ")";
                    shareBody += "\nMaps Location: " + mapsLocation;
                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Location Details of " + addressData.getName());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_share_message)));
                break;

            case R.id.action_copyaddress:
                String copyText = "Name: " + addressData.getName();
                copyText += "\nAddress: " + addressData.getAddress();
                if (!addressData.isManualAddress()) {
                    mapsLocation = "http://maps.google.com/maps?q=loc:" + addressData.getLatitude() + "," + addressData.getLongitude() + "(" + addressData.getName() + ")";
                    copyText += "\n\nMaps Location: " + mapsLocation;
                }
                ClipboardManager clipboard = (ClipboardManager) MABApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Address", copyText);
                clipboard.setPrimaryClip(clip);
                String message = "Address of " + addressData.getName() + " copied";
                Util.displayMessage(message);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (addressMap != null) {
            addressMap.clear();
        }

        if (supportMapFragment != null) {
            supportMapFragment.onStop();
        }

    }

    private void initViews() {
        googleMap_container = (LinearLayout) findViewById(R.id.googleMap_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(addressData.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (addressData.getLatitude() != 0 && addressData.getLongitude() != 0) {
            googleMap_container.setVisibility(View.VISIBLE);
        } else {
            googleMap_container.setVisibility(View.GONE);
        }
        setupLocationOnMap();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addressData != null && !addressData.isManualAddress()) {
//                    settingsrequest();
                }
            }
        }, 500);

        tv_savedaddress_landmark = (TextView) findViewById(R.id.tv_savedaddress_landmark);
        tv_savedaddress_notes = (TextView) findViewById(R.id.tv_savedaddress_notes);
        tv_savedaddress = (TextView) findViewById(R.id.tv_savedaddress);
        savedcontacts_contacts_empty_message = (TextView) findViewById(R.id.savedcontacts_contacts_empty_message);
        savedcontacts_container = (LinearLayout) findViewById(R.id.savedcontacts_container);
        savedaddress_notes_empty_message = (TextView) findViewById(R.id.savedaddress_notes_empty_message);
        savedaddress_landmark_empty_message = (TextView) findViewById(R.id.savedaddress_landmark_empty_message);

        tv_savedaddress.setText(addressData.getAddress());


        if (addressData.getAddressContacts() != null && addressData.getAddressContacts().size() > 0) {
            savedcontacts_contacts_empty_message.setVisibility(View.GONE);
            savedcontacts_container.setVisibility(View.VISIBLE);
            savedcontacts_container.removeAllViews();
            for (int i = 0; i < addressData.getAddressContacts().size(); i++) {
                final ContactData contactData = addressData.getAddressContacts().get(i);
                View savedAddressContactsView = LayoutInflater.from(MABApplication.getContext()).inflate(R.layout.saved_contact_layout, null);
                savedcontacts_container.addView(savedAddressContactsView);

                TextView tv_savedaddress_contact_name = (TextView) savedAddressContactsView.findViewById(R.id.tv_savedaddress_contact_name);
                TextView tv_savedaddress_contact_number = (TextView) savedAddressContactsView.findViewById(R.id.tv_savedaddress_contact_number);
                TextView tv_savedaddress_contact_emailid = (TextView) savedAddressContactsView.findViewById(R.id.tv_savedaddress_contact_emailid);
                ImageView iv_contact_call = (ImageView) savedAddressContactsView.findViewById(R.id.iv_contact_call);
                ImageView iv_contact_message = (ImageView) savedAddressContactsView.findViewById(R.id.iv_contact_message);
                ImageView iv_contact_email = (ImageView) savedAddressContactsView.findViewById(R.id.iv_contact_email);
                ImageView contactPhotoView = (ImageView) savedAddressContactsView.findViewById(R.id.iv_contact_photo);

                if (contactData.getPhotoUri() != null && !contactData.getPhotoUri().isEmpty()) {
                    contactPhotoView.setImageURI(Uri.parse(contactData.getPhotoUri()));
                }

                if (contactPhotoView.getDrawable() == null) {
                    contactPhotoView.setImageResource(R.drawable.ic_name);
                }

                tv_savedaddress_contact_name.setText(contactData.getName());
                if (contactData.getNumber() != null && !contactData.getNumber().isEmpty()) {
                    tv_savedaddress_contact_number.setVisibility(View.VISIBLE);
                    tv_savedaddress_contact_number.setText(contactData.getNumber());
                    iv_contact_call.setVisibility(View.VISIBLE);
                    iv_contact_message.setVisibility(View.VISIBLE);
                } else {
                    tv_savedaddress_contact_number.setVisibility(View.GONE);
                    iv_contact_call.setVisibility(View.GONE);
                    iv_contact_message.setVisibility(View.GONE);
                }

                if (contactData.getEmailid() != null && !contactData.getEmailid().isEmpty()) {
                    tv_savedaddress_contact_emailid.setVisibility(View.VISIBLE);
                    tv_savedaddress_contact_emailid.setText(contactData.getEmailid());
                    iv_contact_email.setVisibility(View.VISIBLE);
                } else {
                    tv_savedaddress_contact_emailid.setVisibility(View.GONE);
                    iv_contact_email.setVisibility(View.GONE);
                }

                iv_contact_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "tel:" + contactData.getNumber().trim();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                iv_contact_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentsms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
                        intentsms.putExtra("address", contactData.getNumber().trim());
                        startActivity(intentsms);
                    }
                });

                iv_contact_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", contactData.getEmailid().trim(), null));
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
            }

        } else {
            savedcontacts_container.setVisibility(View.GONE);
            savedcontacts_contacts_empty_message.setVisibility(View.VISIBLE);
        }

        if (addressData.getNotes() != null && !addressData.getNotes().isEmpty()) {
            savedaddress_notes_empty_message.setVisibility(View.GONE);
            tv_savedaddress_notes.setVisibility(View.VISIBLE);
            tv_savedaddress_notes.setText(addressData.getNotes());
        } else {
            tv_savedaddress_notes.setVisibility(View.GONE);
            savedaddress_notes_empty_message.setVisibility(View.VISIBLE);
        }

        if (addressData.getLandmark() != null && !addressData.getLandmark().isEmpty()) {
            savedaddress_landmark_empty_message.setVisibility(View.GONE);
            tv_savedaddress_landmark.setVisibility(View.VISIBLE);
            tv_savedaddress_landmark.setText(addressData.getLandmark());
        } else {
            savedaddress_landmark_empty_message.setVisibility(View.VISIBLE);
            tv_savedaddress_landmark.setVisibility(View.GONE);
        }

    }

    private void setupLocationOnMap() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    supportMapFragment = SupportMapFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.googleMap_container, supportMapFragment)
                            .commit();
                    if (addressData.getLatitude() != 0 && addressData.getLongitude() != 0) {
                        // Getting a reference to the map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                addressMap = googleMap;
                                LatLng latLng = new LatLng(addressData.getLatitude(), addressData.getLongitude());
                                MarkerOptions options = new MarkerOptions()
                                        .position(latLng)
                                        .title("I am here!");
                                addressMap.addMarker(options);
                                addressMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                            }
                        });
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Util.displayMessage("Unable to get the Location. Please try again later.");
    }
}
