package com.mab.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mab.MABApplication;
import com.mab.R;
import com.mab.fragments.AddressListFragment;
import com.mab.utils.Util;


public class PlaceSearchListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    Toolbar toolbar;
    FrameLayout search_places_list_container;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Util.setToolbarProperties(toolbar);

        search_places_list_container = (FrameLayout) findViewById(R.id.search_places_list_container);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.placesearch_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MABApplication.getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setQuery("", false);

        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.color_666666));
        ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        MenuItemCompat.expandActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.requestFocus();
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.BLACK);
        searchEditText.setTextSize(14.0f);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setPadding(0, 2, 0, 2);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(MABApplication.getContext().getResources().getColor(R.color.white));

        searchView.post(new Runnable() {
            @Override
            public void run() {
                Util.showInputMethod(searchView.findFocus());
            }
        });

//        MenuItem.setOnActionExpandListener(searchItem,
//                new MenuItemCompat.OnActionExpandListener() {
//                    @Override
//                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                        searchView.setQuery("", false);
//                        Util.hideSoftinput(PlaceSearchListActivity.this);
//                        finish();
//                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
//                        return true;
//                    }
//                });

        return true;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            search_places_list_container.removeAllViews();
            AddressListFragment addressListFragment = AddressListFragment.newInstance("-1", "-1", query);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_places_list_container, addressListFragment).commit();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String strQuery) {
        if (TextUtils.isEmpty(strQuery)) {
            search_places_list_container.removeAllViews();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            search_places_list_container.removeAllViews();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Util.hideSoftinput(this);
                navigateBack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateBack() {
        Intent intent = new Intent(PlaceSearchListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        searchView.setQuery("", false);
        Util.hideSoftinput(PlaceSearchListActivity.this);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        return true;
    }
}
