package com.mab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mab.MABController;
import com.mab.R;
import com.mab.data.tos.GroupData;
import com.mab.fragments.AddressListFragment;
import com.mab.utils.Actions;
import com.mab.utils.Constants;
import com.mab.utils.Util;

import java.util.HashMap;

public class GroupPlacesListActivity extends AppCompatActivity {
    GroupData groupData;
    long groupId;
    FrameLayout container;
    Toolbar toolbar;
    MABController mabController;
    GroupPlacesListActivityUIHandler uiHandler;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_places_list);

        mabController = MABController.getController();
        uiHandler = new GroupPlacesListActivityUIHandler();

        groupId = getIntent().getLongExtra(Constants.GROUP_ID, 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Util.setToolbarProperties(toolbar);

        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.GROUP_ID, groupId);
        mabController.processAction(Actions.GET_GROUP_DATA, uiHandler, data);

        container = (FrameLayout) findViewById(R.id.group_places_list_container);
    }

    private void navigateBack() {
        Intent intent = new Intent(GroupPlacesListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                navigateBack();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class GroupPlacesListActivityUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Actions.TECHNICAL_ERROR:
                    Util.displayMessage(getResources().getString(R.string.technicalerror_message));
                    break;

                case Actions.GET_GROUP_DATA:
                    groupData = (GroupData) msg.obj;
                    getSupportActionBar().setTitle(groupData.getName());
                    AddressListFragment addressListFragment = AddressListFragment.newInstance("" + groupData.getId(), "-1", "");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.group_places_list_container, addressListFragment).commit();
                    break;
            }
        }
    }
}
