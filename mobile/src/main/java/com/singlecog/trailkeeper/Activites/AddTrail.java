package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import AsyncAdapters.AsyncTrailLocations;
import Helpers.GeoLocationHelper;
import models.ModelTrails;

public class AddTrail extends BaseActivity implements
        OnMapReadyCallback
       , GoogleMap.OnMapClickListener
        , GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    protected static final String TAG = "AddTrailActivity";
    private final Context context = this;
    private View view;
    private View layout1;
    private SlidingUpPanelLayout mlayout;
    private ImageView up_view;
    private EditText editTextTrailName, editTextCity;
    private AutoCompleteTextView editTextState, editTextCountry;
    private Switch aSwitchCurrentLocation;

    private int mainLayoutHeight = 0;
    private int layout1Height = 0;

    GoogleMap googleMap;
    protected Location mLastLocation;
    private LatLng trailLocation;
    private String trailName, objectID;
    private int trailStatus;
    private List<ModelTrails> trails;
    private LatLng home;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trail);
        view = findViewById(R.id.main_layout);
        super.onCreateDrawer(view, this);
        setUpView();
        // get the latest device location
        buildGoogleApiClient();

        // calls the AsyncTask for the distance
        try {
            AsyncTrailLocations ati = new AsyncTrailLocations(context, home);
            trails = new ArrayList<>();
            ati.execute(trails);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPanelListener();
        setImageClickListener();

        //setCreateMarkerListener();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ViewTreeObserver treeObserver = view.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mainLayoutHeight = view.getMeasuredHeight();
            }
        });

        ViewTreeObserver treeObserver1 = layout1.getViewTreeObserver();
        treeObserver1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                layout1Height = layout1.getMeasuredHeight();

                mlayout.setPanelHeight(mainLayoutHeight - layout1Height);
            }
        });


    }


    //private void setCreateMarkerListener() {

//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//            }
//        });
//    }

    private void setUpView() {
        mlayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        layout1 = findViewById(R.id.layout1);
        up_view = (ImageView)findViewById(R.id.up_image);
        editTextTrailName = (EditText)findViewById(R.id.edittext_trail_name);
        editTextCity = (EditText)findViewById(R.id.edittext_city);
        editTextState = (AutoCompleteTextView)findViewById(R.id.edittext_state);
        editTextCountry = (AutoCompleteTextView)findViewById(R.id.edittext_country);
        aSwitchCurrentLocation = (Switch)findViewById(R.id.switch_current_location);
        mlayout.setDragView(up_view);
    }

    private void setImageClickListener(){
        up_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mlayout.setEnabled(true);
                    mlayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                if (mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mlayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });
    }

    private void setPanelListener() {
        mlayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                mlayout.setEnabled(true);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center_point);
                up_view.startAnimation(animation);
            }

            @Override
            public void onPanelCollapsed(View view) {
                up_view.setImageResource(R.mipmap.ic_up);
                mlayout.setEnabled(true);
            }

            @Override
            public void onPanelExpanded(View view) {
                //up_view.setImageResource(R.mipmap.ic_down);
                mlayout.setEnabled(false);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_trail, menu);
        return true;
    }

    //region Google MapActivity Api Methods
    private void ShowGoogleMap() {
        // even if the connection is not successful we still want to call and show the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
    }

    // this gets called on the getMapAsync method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // get the distance from Current Location to Trails first
        // this only runs on every call to this activity
        // because we still want to show all the other trails when they move around
        if(trails != null) {
            for (int i = 0; trails.size() > i; i++){
                trails.get(i).distance = (float)Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), home) * 100) / 100;
            }

            // then sort them
            GeoLocationHelper.SortTrails(trails);

            // for now we will show all the trail, TODO we may cut that list down as it grows
            for (int i = 0; i < trails.size(); i++) {
                LatLng trailHomeLocation = new LatLng(trails.get(i).GeoLocation.getLatitude(), trails.get(i).GeoLocation.getLongitude());

                // 1 closed, 2 open, 3 unknown
                if (trails.get(i).TrailStatus == 1) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).TrailName + "   " + trails.get(i).distance + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    marker.showInfoWindow();

                } else if (trails.get(i).TrailStatus == 2) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).TrailName + "   " + trails.get(i).distance + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    marker.showInfoWindow();
                } else {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).TrailName + "   " + trails.get(i).distance + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    marker.showInfoWindow();
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
            } else if (trailStatus == 2) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
            } else {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(trailName)
                        .position(trailLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                marker.showInfoWindow();
                ModelTrails trail = new ModelTrails();
                trail.setObjectID(objectID);
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
        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

        // Clears the previously touched position
        googleMap.clear();

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions);

        //Toast.makeText(this,"You tapped the map yo!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//         Intent intent = new Intent(context, TrailMap.class);
//         Bundle args = new Bundle();
//         args.putParcelable("geoPoint", trailLocation);
//         args.putInt("trailID", trailId);
//         args.putString("objectID", objectID);
//         args.putString("trailName", trailNameString);
//         intent.putExtra("bundle", args);
//         startActivity(intent);
    }
    //endregion

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
        ShowGoogleMap();
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
