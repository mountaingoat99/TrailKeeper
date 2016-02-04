package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AsyncAdapters.AsyncTrailLocations;
import Helpers.GeoLocationHelper;
import models.ModelTrails;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "homeActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private LatLng home;
    private LatLng trailLocation;
    private String trailName, objectID;
    private int trailStatus;
    private final Context context = this;
    private List<ModelTrails> trails;
    private Map<Marker, ModelTrails> markers;
    private boolean globalUnitDefault;
    private String milesOrKs;
    private AdView adView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Google AdMob
        adView = (AdView) findViewById(R.id.adView);
        // comment out for testing
        //AdRequest adRequest = new AdRequest.Builder().build();
        // Comment out for Production
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("TEST_DEVICE_ID")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9C75E9349CF38EF5EB2C6C6100E96A7E") // nexus 7
                .build();
        // always call this
        adView.loadAd(adRequest);

        View view = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(view, this);
        markers = new HashMap<>();

        // get the trail ObjectID from the previous view
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        if (bundle != null) {
            trailLocation = bundle.getParcelable("geoPoint");
            trailName = bundle.getString("trailName");
            trailStatus = bundle.getInt("trailStatus");
            objectID = bundle.getString("objectID");
        }

        loadSavedPreferences();

        // get the latest device location
        buildGoogleApiClient();

        // calls the AsyncTask for the distance
        try {
            AsyncTrailLocations ati = new AsyncTrailLocations(this, context, home);
            trails = new ArrayList<>();
            ati.execute(trails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    private void loadSavedPreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //by Anatoliy
        globalUnitDefault = sp.getBoolean("Imperial", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    private void SetMarkerOnClickListener(GoogleMap googleMap){
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (Map.Entry<Marker, ModelTrails> entry : markers.entrySet()) {
                    if (entry.getKey().getId().equals(marker.getId())) {
                        Intent intent = new Intent(context, TrailScreen.class);
                        intent.putExtra("objectID", entry.getValue().getObjectID());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // this gets called on the getMapAsync method
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
//
//            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
//                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
//        }

        googleMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // get the distance from Current Location to Trails first
        // this only runs on every call to this activity
        // because we still want to show all the other trails when they move around
        if(trails != null) {
            for (int i = 0; trails.size() > i; i++) {
                if (globalUnitDefault) {
                    milesOrKs = " miles away";
                    trails.get(i).distanceAway = (float) Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), home) * 100) / 100;
                } else {
                    milesOrKs = " kilometers away";
                    trails.get(i).distanceAway = (float) Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), TrailKeeperApplication.home)*1.609344 * 100) / 100;
                }
            }
            // then sort them
            GeoLocationHelper.SortTrails(trails);

            // for now we will show all the trail, TODO we may cut that list down as it grows
            for (int i = 0; i < trails.size(); i++) {
                LatLng trailHomeLocation = new LatLng(trails.get(i).GeoLocation.getLatitude(), trails.get(i).GeoLocation.getLongitude());

                // 1 closed, 2 open, 3 unknown
                if (trails.get(i).getTrailStatus() == 1) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).getTrailName())
                            .snippet(trails.get(i).distanceAway + milesOrKs)
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();
                    AddToMarkerList(marker, trails.get(i));

                } else if (trails.get(i).getTrailStatus() == 2) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).getTrailName())
                            .snippet(trails.get(i).distanceAway + milesOrKs)
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();
                    AddToMarkerList(marker, trails.get(i));
                } else {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).getTrailName())
                            .snippet(trails.get(i).distanceAway + milesOrKs)
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();
                    AddToMarkerList(marker, trails.get(i));
                }
            }
        }
        // if coming from the Map Button in the Main Trail Screen
        // Should have a trailLocation and if device location is not null
        if (trailLocation != null && mLastLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trailLocation, 13));

            // 1 closed, 2 open, 3 unknown
            if (trailStatus == 1) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            } else if (trailStatus == 2) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            } else {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            }

            googleMap.addMarker(new MarkerOptions()
                    .title("Current Location")
                    .position(home)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
        }
        // if coming from the Menu Map Button (trailLocation should be null)
        // and Device location is not null
        else if (mLastLocation != null && trailLocation == null) {
            home = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 9));
            googleMap.addMarker(new MarkerOptions()
                    .title("Current Location")
                    .position(home)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
        }
        // if coming from the Main Trail Screen (trailLocation will not be null)
        // but the device location is null
        else if (trailLocation != null && mLastLocation == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trailLocation, 13));

            // 1 closed, 2 open, 3 unknown
            if (trailStatus == 1) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            } else if (trailStatus == 2) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            } else {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
                AddToMarkerList(marker, trail);
            }
        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
        // call the method to set the onclick listener
        SetMarkerOnClickListener(googleMap);
    }

    private void AddToMarkerList(Marker marker, ModelTrails trail) {
        ModelTrails markerTrail = new ModelTrails();
        markerTrail.setObjectID(trail.getObjectID());
        markers.put(marker, markerTrail);
    }

    private void SetMultilineInfoAdapter(GoogleMap googleMap) {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
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
