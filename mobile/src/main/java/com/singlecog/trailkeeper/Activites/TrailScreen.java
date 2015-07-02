package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import AsyncAdapters.AsyncOneTrailComments;
import Helpers.TrailStatusHelper;
import RecyclerAdapters.RecyclerViewOneTrailCommentAdapter;
import models.ModelTrailComments;
import models.ModelTrails;

public class TrailScreen extends BaseActivity implements OnMapReadyCallback
        , GoogleMap.OnMapClickListener
        , GoogleMap.OnMapLongClickListener{

    protected static final String TAG = "trailScreenActivity";

    private int trailId;

    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewOneTrailCommentAdapter mTrailCommentAdapter;
    private TextView trailName, trailCity, trailState;
    private ImageView trailStatus;

    GestureDetectorCompat gestureDetector;
    GoogleMap googleMap;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private LatLng trailLocation;
    private String trailNameString;

    private List<ModelTrailComments> comments;
    private final Context context = this;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_screen);
        super.onCreateDrawer();

        SetUpViews();

        // get the trailID from the previous view
        Intent intent = getIntent();
        trailId = intent.getIntExtra("trailID", 0);

        // call method to get items from Local DataStore to fill the Views
        GetTrailData();

        // sets the tap event on the recycler views
        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        // Call the Async method
        try {
            AsyncOneTrailComments atc = new AsyncOneTrailComments(this, context, trailId);
            comments = new ArrayList<>();
            atc.execute(comments);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // set up the Recycler View
        SetupCommentCard();
        ShowGoogleMap();
    }

    private void ShowGoogleMap() {
        // even if the connection is not successful we still want to call and show the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
    }

    private void SetUpViews() {
        trailName = (TextView)findViewById(R.id.txtTrail_name);
        trailStatus = (ImageView)findViewById(R.id.txtTrail_status);
        trailCity = (TextView)findViewById(R.id.txtTrail_city);
        trailState = (TextView)findViewById(R.id.txtTrail_state);
    }

    private void GetTrailData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("trails");
        query.fromLocalDatastore();
        query.whereEqualTo("TrailID", trailId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    for (ParseObject object : list) {
                        trailNameString = object.get("TrailName").toString();
                        trailName.setText(trailNameString);
                        int status = object.getInt("Status");
                        trailCity.setText(object.get("City").toString());
                        trailState.setText(object.get("State").toString());
                        trailLocation = new LatLng(object.getParseGeoPoint("GeoLocation").getLatitude(), object.getParseGeoPoint("GeoLocation").getLongitude());

                        if (status == 1) {
                            trailStatus.setImageResource(R.mipmap.red_closed);
                        } else if (status == 2) {
                            trailStatus.setImageResource(R.mipmap.green_open);
                        } else {
                            trailStatus.setImageResource(R.mipmap.yellow_unknown);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // sets up the trail comment recycler view
    public void SetUpTrailCommentRecyclerView() {
        mTrailCommentAdapter = new RecyclerViewOneTrailCommentAdapter(comments);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);

        mTrailCommentRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(TrailScreen.this, "Comment Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

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

    private void SetupCommentCard() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailCommentRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_comments);
        mTrailCommentRecyclerView.setLayoutManager(layoutManager);
        mTrailCommentRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_trail_screen, menu);
        return true;
    }

    // this gets called on the getMapAsync method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        if (trailLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trailLocation, 13));
            googleMap.addMarker(new MarkerOptions()
                    .title(trailNameString)
                    .position(trailLocation)).showInfoWindow();

        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this,"You tapped the map yo!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent(context, TrailMap.class);
        startActivity(intent);
    }
}
