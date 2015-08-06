package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.singlecog.trailkeeper.R;

import Helpers.CreateAccountHelper;
import models.ModelTrails;

public class Settings extends BaseActivity implements AdapterView.OnItemClickListener {

    private final String LOG = "Settings";
    private ListView settingsList;
    String[] settingArray;
    private final Context context = this;
    private boolean isAnonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();
        isAnonUser = CreateAccountHelper.IsAnonUser();
        settingsList = (ListView)findViewById(R.id.listViewSettings);
        populateListView();
        settingsList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //region ListItem Methods
    private void populateListView() {
        settingArray = getResources().getStringArray(R.array.settings_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.activity_setting_listitem, settingArray);
        settingsList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(LOG, "User clicked item: " + id + " at position: " + position);
        switch (position){
            case 0:  // Account Settings
                Intent intent = new Intent(context, AccountSettings.class);
                startActivity(intent);
                break;
            case 1:  // Notifications
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    if (ModelTrails.GetUserSubscriptions() != null && ModelTrails.GetUserSubscriptions().size() > 0) {
                        Intent intent1 = new Intent(context, Notifications.class);
                        startActivity(intent1);
                    } else {
                        Snackbar.make(view, "You Have No Subscriptions", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case 2: // TrailOwnerAdmin request
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    //TODO go to TrailOwnerAdmin screen
                }
                break;
            case 3: // Contact
                // TODO contact dialog
                break;
        }
    }
    //endregion


}
