package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.PushNotificationHelper;
import RecyclerAdapters.RecyclerViewNotifications;

public class Notifications extends BaseActivity {

    private final Context context = this;
    private RecyclerView mSubsciptionsRecyclerView;
    private RecyclerViewNotifications mSubsciptionsAdapter;
    private List<String>  formattedSubscriptions;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        v = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(v, this);
        getUserSubscriptions();
        SetUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    private void SetUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mSubsciptionsRecyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);
        mSubsciptionsRecyclerView.setLayoutManager(layoutManager);
        mSubsciptionsRecyclerView.setHasFixedSize(true);
        mSubsciptionsAdapter = new RecyclerViewNotifications(formattedSubscriptions, context, v, this);
        mSubsciptionsRecyclerView.setAdapter(mSubsciptionsAdapter);
    }

    private void getUserSubscriptions() {
        formattedSubscriptions = new ArrayList<>();
        List<String> subscriptionsList = PushNotificationHelper.GetUserSubscriptions();

        for (String string : subscriptionsList) {
            string = PushNotificationHelper.FormatChannelName(string);
            formattedSubscriptions.add(string);
        }
    }


}
