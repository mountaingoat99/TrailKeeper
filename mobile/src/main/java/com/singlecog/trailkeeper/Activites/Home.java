package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.singlecog.trailkeeper.R;

import java.util.List;

import AsyncAdapters.RecyclerViewAsyncTrailComments;
import RecyclerAdapters.DividerItemDecoration;
import AsyncAdapters.RecyclerViewAsyncTrailInfo;
import RecyclerAdapters.RecyclerViewTrailCommentsAdapter;
import RecyclerAdapters.RecyclerViewTrailOpenClosedAdapter;
import models.ModelOpenClosedTrails;
import models.ModelTrailComments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import static AsyncAdapters.RecyclerViewAsyncTrailComments.getTrailCommentData;
import static AsyncAdapters.RecyclerViewAsyncTrailInfo.*;

public class Home extends BaseActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener{

    protected static final String TAG = "homeActivity";
    private final Context context = this;

    private RecyclerView mTrailOpenRecyclerView;
    private RecyclerViewTrailOpenClosedAdapter mTrailOpenAdapter;

    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewTrailCommentsAdapter mTrailCommentAdapter;

    GestureDetectorCompat gestureDetector;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private LatLng home;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_home);
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

        // set up the RecyclerViews
        SetUpTrailStatusRecyclerView();
        SetUpTrailCommentRecyclerView();
    }

    // sets up the Trail Status Recycler View
    private void SetUpTrailStatusRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailOpenRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_updates);
        mTrailOpenRecyclerView.setLayoutManager(layoutManager);
        mTrailOpenRecyclerView.setHasFixedSize(true);

        // call the async class to load the trail info data
        RecyclerViewAsyncTrailInfo trailInfo = new RecyclerViewAsyncTrailInfo();
        trailInfo.onCreate();
        List<ModelOpenClosedTrails> items = getTrailOpenData();
        mTrailOpenAdapter = new RecyclerViewTrailOpenClosedAdapter(items);
        mTrailOpenRecyclerView.setAdapter(mTrailOpenAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mTrailOpenRecyclerView.addItemDecoration(itemDecoration);

        mTrailOpenRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // sets the touch listener
        mTrailOpenRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    //Toast.makeText(Home.this, "Trail Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, TrailScreen.class);
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

    // sets up the trail comment recycler view
    private void SetUpTrailCommentRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailCommentRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_comments);
        mTrailCommentRecyclerView.setLayoutManager(layoutManager);
        mTrailCommentRecyclerView.setHasFixedSize(true);

        // call the async class to load the trail info data
        RecyclerViewAsyncTrailComments trailInfo = new RecyclerViewAsyncTrailComments();
        trailInfo.onCreate();
        List<ModelTrailComments> items = getTrailCommentData();
        mTrailCommentAdapter = new RecyclerViewTrailCommentsAdapter(items);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mTrailCommentRecyclerView.addItemDecoration(itemDecoration);

        mTrailCommentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTrailCommentRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(Home.this, "Comment Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

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
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    // this gets called on the getMapAsync method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            home = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 9));
            googleMap.addMarker(new MarkerOptions()
                    .title("Home")
                    .position(home));
        } else {
            Toast.makeText(this,"Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            home = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            Toast.makeText(this,"Please Turn On GPS", Toast.LENGTH_LONG).show();
        }

        // even if the connection is not successful we still want to call and show the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
