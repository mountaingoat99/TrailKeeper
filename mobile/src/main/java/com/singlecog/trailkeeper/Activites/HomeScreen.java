package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.singlecog.trailkeeper.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import AsyncAdapters.AsyncTrailInfo;
import Helpers.GeoLocationHelper;
import RecyclerAdapters.RecyclerViewHomeScreenAdapter;
import models.ModelTrails;

import static android.support.v7.widget.RecyclerView.*;

public class HomeScreen extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeLayout;
    private final Context context = this;
    private RecyclerView mTrailOpenRecyclerView;
    private RecyclerViewHomeScreenAdapter mTrailOpenAdapter;
    private List<ModelTrails> trails;
    private Bundle bundle;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        super.onCreateDrawer();

        if (savedInstanceState != null)
            bundle = savedInstanceState;

        // set up the swipe pull to refresh
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_home_screen);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // show the refresh spinner on load
        if (TrailKeeperApplication.home == null) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            mSwipeLayout.setProgressViewOffset(false, -50, height / 8);
            mSwipeLayout.setRefreshing(true);
        }

        // set up the RecyclerViews
        SetUpTrailStatusCard();

        CallAsyncTrailInfo();
    }

    private void CallAsyncTrailInfo() {
        try {
            AsyncTrailInfo ati = new AsyncTrailInfo(this, context);
            trails = new ArrayList<>();
            ati.execute(trails);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sets up the Trail Status Recycler View
    private void SetUpTrailStatusCard() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailOpenRecyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        mTrailOpenRecyclerView.setLayoutManager(layoutManager);
        mTrailOpenRecyclerView.setHasFixedSize(true);

        // lets make sure the refresh only happens when the top card is fully visible
        mTrailOpenRecyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean enabled = false;
                if (recyclerView != null && recyclerView.getChildCount() > 0){
                    // check if the first card is visible
                    boolean firstCardVisible = mTrailOpenAdapter.getItemViewType(0) == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstVisibleCard = recyclerView.getChildAt(0).getTop() == 0;
                    // enable or disable the refresh layout
                    enabled = firstCardVisible && topOfFirstVisibleCard;
                }
                mSwipeLayout.setEnabled(enabled);
            }
        });

    }

    // Fills the Trail Status Recycler View
    public void SetUpTrailStatusRecyclerView(){
//        for (int i = 1; trails.size() > i; i++){   // TODO this sucks, stop coding with Vodka
//            if (i > 4) {
//                trails.remove(i);
//            }
//        }
        // we want to give a few extra seconds for the Google Location API to load and connect
        // but only if the current location is already null
        if (TrailKeeperApplication.home == null) {
            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    // first we will sort and get the distance correct
                    if (TrailKeeperApplication.home != null) {
                        SortTrails();
                    } else {
                        Toast.makeText(context, "Please Turn On GPS for to get the trails nearest your location", Toast.LENGTH_LONG).show();
                    }
                    ShowTrailCards();
                }
            };

            handler.postDelayed(r, 3000);
        // if not null then we can go ahead and sort and show the cards
        } else {
            SortTrails();
            ShowTrailCards();
        }
    }

    private void SortTrails(){
        for (int i = 0; trails.size() > i; i++) {
            //on the Home Screen we only will show the 5 closet trails
            // TODO once settings are working add Metric in here
            trails.get(i).distance = (float)Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), TrailKeeperApplication.home) * 100) / 100;
        }
        GeoLocationHelper.SortTrails(trails);
    }

    private void ShowTrailCards(){
        mSwipeLayout.setRefreshing(false);
        mTrailOpenAdapter = new RecyclerViewHomeScreenAdapter(trails, context);
        mTrailOpenRecyclerView.setAdapter(mTrailOpenAdapter);
        mTrailOpenRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        TrailKeeperApplication.mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (TrailKeeperApplication.mGoogleApiClient.isConnected()) {
            TrailKeeperApplication.mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrailKeeperApplication.mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (TrailKeeperApplication.mGoogleApiClient.isConnected()) {
            TrailKeeperApplication.mGoogleApiClient.disconnect();
        }
    }

    // swipe to refresh
    @Override
    public void onRefresh() {

        // first we remove all the items
        for (int i = 0; mTrailOpenAdapter.getItemCount() > i; i++) {
            mTrailOpenAdapter.removeData(i);
        }
        for (int i = 0; mTrailOpenAdapter.getItemCount() > i; i++) {
            mTrailOpenAdapter.removeData(i);
        }
        for (int i = 0; mTrailOpenAdapter.getItemCount() > i; i++) {
            mTrailOpenAdapter.removeData(i);
        }
        // then refresh the Application class Parse Methods
        TrailKeeperApplication.LoadAllFromParse();
        TrailKeeperApplication t = new TrailKeeperApplication();
        t.onConnected(bundle);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // now set the cards back up and load the recycler view
                SetUpTrailStatusCard();
                CallAsyncTrailInfo();
                mSwipeLayout.setRefreshing(false);
            }
        }, 3000);
    }
}
