package com.mab.activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mab.MABApplication;
import com.mab.MABController;
import com.mab.R;
import com.mab.adapters.AddressGroupSpinnerAdapter;
import com.mab.data.tos.AddressData;
import com.mab.data.tos.ContactData;
import com.mab.data.tos.GroupData;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.mab.MABApplication.isContactPermissionGranted;


public class AddEditAddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Toolbar toolbar;
    Spinner addressGroupSpinner;
    ArrayList<GroupData> groupDataArrayList;
    AddressGroupSpinnerAdapter addressGroupSpinnerAdapter;
    MABController mabController;
    AddEditAddressActivityUIHandler addEditAddressActivityUIHandler;
    Button btnAddContact;
    EditText addContactNameView;
    EditText addContactNumberView;
    EditText addContactEmailIdView;
    AddressData addressData;
    EditText placeNameView;
    EditText manualAddressView;
    EditText notesView;
    EditText landmarkView;
    LinearLayout contactListContainer;
    LinearLayout contact_details_layout;
    RadioButton selectLocationOptionView;
    RadioButton manualLocationOptionView;
    LinearLayout pickupaddress_container;
    LinearLayout selectedaddress_container;
    TextView selectedAddressView;
    TextInputLayout tv_manual_address;
    boolean isEditMode;
    long groupId;
    String parentActivity = "";
    ImageView pickContactView;
    TextView tv_orlabel;
    GoogleApiClient googleApiClient;

    private static final int PICK_CONTACT = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3;
    private static final int REQUEST_CHECK_SETTINGS = 4;


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
                            displayPlacePicker();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(AddEditAddressActivity.this, REQUEST_CHECK_SETTINGS);
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
    protected void onStart() {
        super.onStart();
    }


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

                displayContactPopup(null);
                return;
            }
        }
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
                intent = new Intent(AddEditAddressActivity.this, MainActivity.class);
            } else if (parentActivity.equalsIgnoreCase("GroupPlacesListActivity")) {
                intent = new Intent(AddEditAddressActivity.this, GroupPlacesListActivity.class);
                intent.putExtra(Constants.GROUP_ID, groupId);
            }
        } else {
            intent = new Intent(AddEditAddressActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            addContactEmailIdView.setText("");
            addContactNumberView.setText("");
            addContactNameView.setText("");

            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {

                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    String cNumber = phones.getString(phones.getColumnIndex("data1"));
                    System.out.println("number is:" + cNumber);

                    if (cNumber != null && !cNumber.isEmpty()) {
                        if (cNumber.startsWith("+")) {
                            cNumber = cNumber.replace("+", "");
                        }
                        cNumber = cNumber.replace(" ", "");
                        addContactNumberView.setText(cNumber);
                    }

                }

                Cursor cur1 = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                String email = "";


                while (cur1.moveToNext()) {
                    email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    addContactEmailIdView.setText(email);
                }

                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                System.out.println("name is:" + name);
                addContactNameView.setText(name);

                Uri photoUri = getPhotoUri(Long.parseLong(id));
                addContactNameView.setTag(photoUri);
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                String address = "";
                if (selectedPlace.getName() != null && !selectedPlace.getName().toString().isEmpty()) {
                    address += selectedPlace.getName() + "\n";
                }
                address += selectedPlace.getAddress();
                if (selectedPlace.getPhoneNumber() != null && !selectedPlace.getPhoneNumber().toString().isEmpty()) {
                    address += "\nContact: " + selectedPlace.getPhoneNumber();
                }
                selectedAddressView.setText(address);
                selectedaddress_container.setVisibility(View.VISIBLE);
                addressData.setAddress(address);
                addressData.setLatitude(selectedPlace.getLatLng().latitude);
                addressData.setLongitude(selectedPlace.getLatLng().longitude);
                addressData.setManualAddress(false);
//                selectLocationOptionView.setChecked(true);
//                manualLocationOptionView.setChecked(false);
            } else {
//                selectLocationOptionView.setChecked(false);
//                manualLocationOptionView.setChecked(false);
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            displayPlacePicker();
        }
    }


    public Uri getPhotoUri(long contactId) {
        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photo = Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        Cursor cur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID
                        + "="
                        + contactId
                        + " AND "
                        + ContactsContract.Data.MIMETYPE
                        + "='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                        + "'", null, null);
        if (cur != null) {
            if (!cur.moveToFirst()) {
                return null; // no photo
            }
        } else {
            return null; // error in cursor process
        }
        return photo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_address_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                navigateBack();
                break;
            case R.id.action_save:
                if (addressData.getName() == null || addressData.getName().isEmpty()) {
                    Util.displayMessage("Please enter the Place Name");
                } else if (addressData.getAddress() == null || addressData.getAddress().isEmpty()) {
                    Util.displayMessage("Please enter the Place Address");
                } else {
                    long timeMillis = Calendar.getInstance().getTimeInMillis();
                    addressData.setModifiedDateMillis(timeMillis);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(Constants.ADDRESSDATA, addressData);
                    if (!isEditMode) {
                        addressData.setId(timeMillis);
                        data.put("isNewAddress", true);
                    } else {
                        data.put("isNewAddress", false);
                    }
                    mabController.processAction(Actions.ADD_EDIT_PLACE, addEditAddressActivityUIHandler, data);
                }

                break;

            case R.id.action_addcontact:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {
                        isContactPermissionGranted = true;
                        displayContactPopup(null);
                    }
                } else {
                    isContactPermissionGranted = true;
                    displayContactPopup(null);
                }
                break;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MABApplication.getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
        }

        if (!Util.isGooglePlayServicesAvailable(this)) {
            Util.displayMessage("Please enable Location services");
        }

        if (getIntent().hasExtra(Constants.GROUP_ID)) {
            groupId = getIntent().getLongExtra(Constants.GROUP_ID, 0);
        }

        if (getIntent().hasExtra("parentActivity")) {
            parentActivity = getIntent().getStringExtra("parentActivity");
        }

        if (getIntent().hasExtra(Constants.ADDRESSDATA)) {
            addressData = (AddressData) getIntent().getSerializableExtra(Constants.ADDRESSDATA);
            isEditMode = true;
        } else {
            addressData = new AddressData();
            isEditMode = false;
        }

        groupDataArrayList = new ArrayList<>();
        mabController = MABController.getController();
        addEditAddressActivityUIHandler = new AddEditAddressActivityUIHandler();

        initViews();

        mabController.processAction(Actions.GET_ALL_GROUPS, addEditAddressActivityUIHandler, null);

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Util.setToolbarProperties(toolbar);

        placeNameView = (EditText) findViewById(R.id.et_placename);
        manualAddressView = (EditText) findViewById(R.id.et_address);
        notesView = (EditText) findViewById(R.id.et_notes);
        landmarkView = (EditText) findViewById(R.id.et_landmark);

        pickupaddress_container = (LinearLayout) findViewById(R.id.pickupaddress_container);
        placeNameView.addTextChangedListener(new EditTextFieldsTextWatcher(placeNameView));
        manualAddressView.addTextChangedListener(new EditTextFieldsTextWatcher(manualAddressView));
        notesView.addTextChangedListener(new EditTextFieldsTextWatcher(notesView));
        landmarkView.addTextChangedListener(new EditTextFieldsTextWatcher(landmarkView));

        tv_manual_address = (TextInputLayout) findViewById(R.id.tv_manual_address);
        selectedaddress_container = (LinearLayout) findViewById(R.id.selectedaddress_container);
        selectedAddressView = (TextView) findViewById(R.id.tv_selectedaddress);
        selectLocationOptionView = (RadioButton) findViewById(R.id.rb_selectlocation);
        manualLocationOptionView = (RadioButton) findViewById(R.id.rb_manuallocation);

        selectLocationOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocationOptionView.setChecked(true);
                manualLocationOptionView.setChecked(false);
                tv_manual_address.setVisibility(View.GONE);
                settingsrequest();
            }
        });

        manualLocationOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocationOptionView.setChecked(false);
                manualLocationOptionView.setChecked(true);
                selectedaddress_container.setVisibility(View.GONE);
                tv_manual_address.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manualAddressView.requestFocus();
                        Util.showInputMethod(manualAddressView);
                    }
                }, 100);
                addressData.setManualAddress(true);
                addressData.setLatitude(0);
                addressData.setLongitude(0);
                manualAddressView.setText("");
                addressData.setAddress("");
            }
        });

        contactListContainer = (LinearLayout) findViewById(R.id.contacts_list_container);
        contact_details_layout = (LinearLayout) findViewById(R.id.contact_details_layout);
        addressGroupSpinner = (Spinner) findViewById(R.id.address_group_spinner);
        addressGroupSpinnerAdapter = new AddressGroupSpinnerAdapter(getApplicationContext(), groupDataArrayList);
        addressGroupSpinner.setAdapter(addressGroupSpinnerAdapter);


        contact_details_layout.setVisibility(View.GONE);
        if (isEditMode) {
            getSupportActionBar().setTitle("Edit Place");
            placeNameView.setText(addressData.getName());
            if (addressData.getNotes() != null && !addressData.getNotes().isEmpty()) {
                notesView.setText(addressData.getNotes());
            }

            if (addressData.getLandmark() != null && !addressData.getLandmark().isEmpty()) {
                landmarkView.setText(addressData.getLandmark());
            }

            if (addressData.getAddressContacts() != null && addressData.getAddressContacts().size() > 0) {
                updateAddressContactList();
            }

            if (!addressData.isManualAddress()) {
                selectLocationOptionView.setChecked(true);
                manualLocationOptionView.setChecked(false);
                tv_manual_address.setVisibility(View.GONE);
                selectedAddressView.setText(addressData.getAddress());
                selectedaddress_container.setVisibility(View.VISIBLE);
            } else {
                manualLocationOptionView.setChecked(true);
                selectedaddress_container.setVisibility(View.GONE);
                tv_manual_address.setVisibility(View.VISIBLE);
                manualAddressView.setText(addressData.getAddress());
            }
        }
    }

    private void displayPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(AddEditAddressActivity.this), PLACE_PICKER_REQUEST);
            overridePendingTransition(R.anim.grow_from_bottomright_to_topleft, R.anim.shrink_from_topleft_to_bottomright);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void updateAddressContactList() {
        contactListContainer.removeAllViews();
        if (addressData.getAddressContacts().size() > 0) {
            contact_details_layout.setVisibility(View.VISIBLE);
        } else {
            contact_details_layout.setVisibility(View.GONE);
        }
        for (int i = 0; i < addressData.getAddressContacts().size(); i++) {
            final ContactData contactData = addressData.getAddressContacts().get(i);
            View contactView = LayoutInflater.from(MABApplication.getContext()).inflate(R.layout.contact_email_name_layout, null);
            TextView contactNameView = (TextView) contactView.findViewById(R.id.tv_contact_name);
            TextView contactNumberView = (TextView) contactView.findViewById(R.id.tv_contact_number);
            TextView contactEmailIdView = (TextView) contactView.findViewById(R.id.tv_contact_emailid);
            ImageView deleteContactView = (ImageView) contactView.findViewById(R.id.iv_deletecontact);
            ImageView contactPhotoView = (ImageView) contactView.findViewById(R.id.iv_contact_photo);

            contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayContactPopup(contactData);
                }
            });

            if (contactData.getPhotoUri() != null && !contactData.getPhotoUri().isEmpty()) {
                contactPhotoView.setImageURI(Uri.parse(contactData.getPhotoUri()));
            }

            if (contactPhotoView.getDrawable() == null) {
                contactPhotoView.setImageResource(R.drawable.ic_name);
            }

            deleteContactView.setTag(contactData);
            contactNameView.setText(contactData.getName());

            if (contactData.getNumber() == null || contactData.getNumber().isEmpty()) {
                contactNumberView.setVisibility(View.GONE);
            } else {
                contactNumberView.setVisibility(View.VISIBLE);
                contactNumberView.setText(contactData.getNumber());
            }

            if (contactData.getEmailid() == null || contactData.getEmailid().isEmpty()) {
                contactEmailIdView.setVisibility(View.GONE);
            } else {
                contactEmailIdView.setVisibility(View.VISIBLE);
                contactEmailIdView.setText(contactData.getEmailid());
            }

            deleteContactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView deleteContactView = (ImageView) view;
                    ContactData selectedContactData = (ContactData) deleteContactView.getTag();
                    for (int i = 0; i < addressData.getAddressContacts().size(); i++) {
                        ContactData contactData = addressData.getAddressContacts().get(i);

                        if (contactData.getId() == selectedContactData.getId()) {
                            addressData.getAddressContacts().remove(contactData);
                        }
                    }
                    updateAddressContactList();
                }
            });

            contactListContainer.addView(contactView);
        }
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


    class AddEditAddressActivityUIHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Actions.TECHNICAL_ERROR:
                    Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                    break;

                case Actions.GET_ALL_GROUPS:
                    groupDataArrayList = new ArrayList<>();
                    groupDataArrayList = (ArrayList<GroupData>) msg.obj;
                    addressGroupSpinnerAdapter.setGroupDataArrayList(groupDataArrayList);
                    addressGroupSpinnerAdapter.notifyDataSetChanged();


                    long id = 0;
                    if (parentActivity != null && !parentActivity.isEmpty() && parentActivity.equalsIgnoreCase("GroupPlacesListActivity")) {
                        //Displayed this screen from Groups Section on Main Screen. So, select the group sent in intent and disable the spinner.
                        id = groupId;
                    } else {
                        id = addressData.getGroupid();
                    }

                    for (int i = 0; i < groupDataArrayList.size(); i++) {
                        if (groupDataArrayList.get(i).getId() == id) {
                            addressGroupSpinner.setSelection(i);
                            break;
                        }
                    }

                    if (!isEditMode && parentActivity != null && !parentActivity.isEmpty() && parentActivity.equalsIgnoreCase("GroupPlacesListActivity")) {
                        addressGroupSpinner.setEnabled(false);
                    } else {
                        addressGroupSpinner.setEnabled(true);
                    }

                    addressGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            GroupData groupData = groupDataArrayList.get(i);
                            addressData.setGroupName(groupData.getName());
                            addressData.setGroupid(groupData.getId());
                            addressData.setGroupcolorcode(groupDataArrayList.get(i).getColorCode());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    break;

                case Actions.ADD_EDIT_PLACE:
                    Util.displayMessage("Place saved successfully");
                    navigateBack();
                    break;
            }

        }
    }

    class AddContactOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {


        }
    }

    private void displayContactPopup(final ContactData contactData) {
        View dialogView = LayoutInflater.from(AddEditAddressActivity.this).inflate(R.layout.addcontact_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditAddressActivity.this);
        builder.setView(dialogView);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel_addcontact);
        Button btnContinue = (Button) dialogView.findViewById(R.id.btn_continue_addcontact);
        tv_orlabel = (TextView) dialogView.findViewById(R.id.tv_orlabel);
        pickContactView = (ImageView) dialogView.findViewById(R.id.iv_pickcontact);
        addContactNameView = (EditText) dialogView.findViewById(R.id.et_addcontact_name);
        addContactNumberView = (EditText) dialogView.findViewById(R.id.et_addcontact_number);
        addContactEmailIdView = (EditText) dialogView.findViewById(R.id.et_addcontact_emailid);

        if (isContactPermissionGranted) {
            pickContactView.setVisibility(View.VISIBLE);
            tv_orlabel.setVisibility(View.VISIBLE);
        } else {
            pickContactView.setVisibility(View.GONE);
            tv_orlabel.setVisibility(View.GONE);
        }

        if (contactData != null) {
            addContactNameView.setText(contactData.getName().trim());
            addContactNameView.setTag(contactData.getPhotoUri());
            if (contactData.getNumber() != null && !contactData.getNumber().isEmpty()) {
                addContactNumberView.setText(contactData.getNumber().trim());
            }

            if (contactData.getEmailid() != null && !contactData.getEmailid().isEmpty()) {
                addContactNumberView.setText(contactData.getEmailid().trim());
            }
        }

        builder.setPositiveButton("Continue", null);
        builder.setNegativeButton("Cancel", null);

        final AlertDialog mDialog = builder.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (addContactNameView.getText().toString().isEmpty()) {
                            if ((addContactNumberView.getText().toString().isEmpty() || addContactEmailIdView.getText().toString().isEmpty())) {
                                Util.displayMessage("Please enter Contact Name and either Phone Number / Email Id");
                            } else {
                                Util.displayMessage("Please enter Contact Name");
                            }
                        } else {
                            ArrayList<ContactData> contactDataArrayList = addressData.getAddressContacts();
                            if (contactDataArrayList == null) {
                                contactDataArrayList = new ArrayList<ContactData>();
                                addressData.setAddressContacts(contactDataArrayList);
                            }
                            ContactData newExistingContactData = contactData;
                            if (newExistingContactData == null) {
                                newExistingContactData = new ContactData();
                                contactDataArrayList.add(newExistingContactData);
                            }

                            if (addContactNameView.getTag() != null) {
                                newExistingContactData.setPhotoUri(addContactNameView.getTag().toString());
                            } else {
                                newExistingContactData.setPhotoUri("");
                            }

                            newExistingContactData.setId(Calendar.getInstance().getTimeInMillis());
                            newExistingContactData.setName(addContactNameView.getText().toString().trim());
                            newExistingContactData.setNumber(addContactNumberView.getText().toString().trim());
                            newExistingContactData.setEmailid(addContactEmailIdView.getText().toString().trim());

                            updateAddressContactList();

                            mDialog.dismiss();
                        }
                    }
                });
            }
        });

        mDialog.show();

        pickContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }


    class EditTextFieldsTextWatcher implements TextWatcher {
        EditText currentField;

        public EditTextFieldsTextWatcher(EditText editText) {
            this.currentField = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String val = editable.toString();

            switch (this.currentField.getId()) {
                case R.id.et_placename:
                    addressData.setName(val);
                    break;

                case R.id.et_address:
                    addressData.setAddress(val);
                    addressData.setLongitude(0);
                    addressData.setLatitude(0);
                    break;

                case R.id.et_notes:
                    addressData.setNotes(val);
                    break;

                case R.id.et_landmark:
                    addressData.setLandmark(val);
                    break;
            }
        }
    }
}
