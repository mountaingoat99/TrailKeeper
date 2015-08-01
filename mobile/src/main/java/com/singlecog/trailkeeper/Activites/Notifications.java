package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.singlecog.trailkeeper.Activites.BaseActivity;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import Helpers.ConnectionDetector;
import models.ModelTrails;

public class Notifications extends BaseActivity implements AdapterView.OnItemClickListener {

    private final String LOG = "Notifications";
    private ListView listView;
    private final Context context = this;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        super.onCreateDrawer();
        listView = (ListView)findViewById(R.id.listSubscriptions);
        connectionDetector = new ConnectionDetector(context);
        populateListView();
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    //region ListItem Methods

    private List<String> getUserSubscriptions() {
        ArrayList<String> noSubscriptions = new ArrayList<>();
        noSubscriptions.add(0, "Looks like you haven't subscribed to any trails yet.");
        List<String> subscriptions;
        ModelTrails trails = new ModelTrails();
        subscriptions = trails.GetUserSubscriptions();

        if (subscriptions == null) {
            return noSubscriptions;
        }
        return subscriptions;
    }

    private void populateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.activity_setting_listitem, getUserSubscriptions());
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(LOG, "User clicked item: " + id + " at position: " + position);

    }
    //endregion
}
