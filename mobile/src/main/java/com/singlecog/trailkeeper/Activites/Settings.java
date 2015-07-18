package com.singlecog.trailkeeper.Activites;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.singlecog.trailkeeper.R;

import java.util.ArrayList;

public class Settings extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView settingsList;
    String[] settingArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();

        settingsList = (ListView)findViewById(R.id.listViewSettings);

        populateListView();

        settingsList.setOnItemClickListener(this);
    }

    private void populateListView() {
        settingArray = getResources().getStringArray(R.array.settings_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.activity_setting_listitem, settingArray);
        settingsList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("SettingListView", "User clicked item: " + id + " at position: " + position);
        switch (position){
            case 0:  // Create Account
                // TODO, check if they are signed in, if so remind them they are


                break;
            case 1:  // Sign in

                break;
            case 2:  // Sign out

                break;
            case 3:  // notifications

                break;
            case 4:  // Trail Admin request

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
