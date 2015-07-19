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

import ParseObjects.ParseTrailUser;

public class Settings extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView settingsList;
    String[] settingArray;
    private final Context context = this;
    private boolean isAnonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();

        isAnonUser = ParseTrailUser.IsAnonUser();

        settingsList = (ListView)findViewById(R.id.listViewSettings);
        populateListView();
        settingsList.setOnItemClickListener(this);
    }

    private void populateListView() {
        settingArray = getResources().getStringArray(R.array.settings_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.activity_setting_listitem, settingArray);
        settingsList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("SettingListView", "User clicked item: " + id + " at position: " + position);
        switch (position){
            case 0:  // Create Account
                if (isAnonUser) {
                    Intent intent = new Intent(context, CreateAccount.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_create_account, Snackbar.LENGTH_LONG).show();
                }
                break;
            case 1:  // Sign in
                if(!isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_login, Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO go to login screen
                }
                break;
            case 2:  // Sign out
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_logout, Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO go to logout screen
                }
                break;
            case 3:  // Delete Account
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_delete_account, Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO go to delete account screen
                }
                break;
            case 4:  // Notifications
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO go to Notifications screen
                }
                break;
            case 5: // TrailOwnerAdmin request
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    //TODO go to TrailOwnerAdmin screen
                }
                break;
            case 6: // Contact
                // TODO contact dialog
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}
