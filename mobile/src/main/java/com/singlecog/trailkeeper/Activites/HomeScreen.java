package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import AsyncAdapters.AsyncTrailInfo;
import RecyclerAdapters.RecyclerViewHomeScreenAdapter;
import models.ModelTrails;

public class HomeScreen extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    private final Context context = this;
    protected Location mLastLocation;
    private LatLng home;
    protected static final String TAG = "HomeScreenActivity";
    private RecyclerView mTrailOpenRecyclerView;
    private RecyclerViewHomeScreenAdapter mTrailOpenAdapter;
    private List<ModelTrails> trails;
    GestureDetectorCompat gestureDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        super.onCreateDrawer();
        // get the latest device location
        buildGoogleApiClient();

        // sets the tap event on the recycler views
        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        // calls the AsyncTask for the Trails
        try {
            AsyncTrailInfo ati = new AsyncTrailInfo(this, context);
            trails = new ArrayList<>();
            ati.execute(trails);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // set up the RecyclerViews
        SetUpTrailStatusCard();
    }
    // Sets up the Trail Status Recycler View
    private void SetUpTrailStatusCard() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailOpenRecyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        mTrailOpenRecyclerView.setLayoutManager(layoutManager);
        mTrailOpenRecyclerView.setHasFixedSize(true);
    }

    // Fills the Trail Status Recycler View
    public void SetUpTrailStatusRecyclerView(){
        mTrailOpenAdapter = new RecyclerViewHomeScreenAdapter(trails);
        mTrailOpenRecyclerView.setAdapter(mTrailOpenAdapter);

        // sets the touch listener
        mTrailOpenRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    // get a ViewHolder from the recyclerView
                    RecyclerView.ViewHolder v = recyclerView.getChildViewHolder(child);
                    // then call the class and using the ViewHolder and the the trailID
                    int id = mTrailOpenAdapter.getTrailID(v);
                    Intent intent = new Intent(context, TrailScreen.class);
                    intent.putExtra("trailID", id);
                    startActivity(intent);

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            home = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}