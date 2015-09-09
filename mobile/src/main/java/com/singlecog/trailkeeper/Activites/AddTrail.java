package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import AsyncAdapters.AsyncTrailLocations;
import Helpers.AlertDialogHelper;
import Helpers.GeoLocationHelper;
import Helpers.StateListHelper;
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
    private EditText editTextTrailName, editTextCity, editLength;
    private AutoCompleteTextView editTextState, editTextCountry;
    private Switch switchCurrentLocation;
    //private Switch switchPrivateTrail;
    private CheckBox chkEasy;
    private CheckBox chkMedium;
    private CheckBox chkHard;

    private int mainLayoutHeight = 0;
    private int layout1Height = 0;

    GoogleMap googleMap;
    Marker newLocationMarkerOptions;
    protected Location mLastLocation;
    private LatLng trailLocation;
    private String trailName, objectID;
    private int trailStatus;
    private boolean isMapUp = true, doUseCurrentLocation = false, isPrivateTrail = false;
    private boolean isEasy = false, isMedium = false, isHard = false;
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
        setSwitchListeners();
        setSkillLevelListeners();
        getCountryList();
        getStateList();
        //setCreateMarkerListener();
    }

    //region setUp Activity
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
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
    }

    private void setUpView() {
        mlayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        layout1 = findViewById(R.id.layout1);
        up_view = (ImageView)findViewById(R.id.up_image);
        editTextTrailName = (EditText)findViewById(R.id.edittext_trail_name);
        editTextCity = (EditText)findViewById(R.id.edittext_city);
        editLength = (EditText)findViewById(R.id.edittext_distance);
        editTextState = (AutoCompleteTextView)findViewById(R.id.edittext_state);
        editTextCountry = (AutoCompleteTextView)findViewById(R.id.edittext_country);
        switchCurrentLocation = (Switch)findViewById(R.id.switch_current_location);
        //switchPrivateTrail = (Switch)findViewById(R.id.switch_private_trail);
        chkEasy = (CheckBox)findViewById(R.id.check_box_easy);
        chkMedium = (CheckBox)findViewById(R.id.check_box_medium);
        chkHard = (CheckBox)findViewById(R.id.check_box_hard);
        mlayout.setDragView(up_view);
    }

    // TODO may not use this unless I add a reset button on the screen
    private void resetEverything() {
        editLength.setText("");
        editTextState.setText("");
        editTextTrailName.setText("");
        editTextCountry.setText("");
        editTextCity.setText("");
        switchCurrentLocation.setChecked(false);
        //switchPrivateTrail.setChecked(false);
        chkEasy.setChecked(false);
        chkMedium.setChecked(false);
        chkHard.setChecked(false);
    }

    private void setSwitchListeners() {
        switchCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doUseCurrentLocation = isChecked;
            }
        });

//        switchPrivateTrail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                isPrivateTrail = isChecked;
//            }
//        });
    }

    private void setSkillLevelListeners() {
        chkEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEasy = isChecked;
            }
        });

        chkMedium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMedium = isChecked;
            }
        });

        chkHard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHard = isChecked;
            }
        });
    }

    public void btn_create_click(View view) {
        if (editTextTrailName.getText().length() > 3) {
            if (editTextCity.getText().length() > 1) {
                if (editTextState.getText().length() > 1) {
                    if (editTextCountry.getText().length() > 1) {
                        if (doUseCurrentLocation) {
                            trailLocation = home;
                            SaveTrail();
                        } else {
                            if (newLocationMarkerOptions != null) {
                                trailLocation = newLocationMarkerOptions.getPosition();
                                SaveTrail();
                            } else {
                                Snackbar.make(view, "Please Choose A Location", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Snackbar.make(view, "Country Is Not Valid", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(view, "State Name Is Not Valid", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(view, "City Is Not Valid", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(view, "Trail Name Is Not Valid", Snackbar.LENGTH_LONG).show();
        }
    }

    private void getStateList() {
        Map<String, String> stateList = StateListHelper.getStates();
        List<String> list = new ArrayList<>(stateList.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        editTextState.setAdapter(adapter);
        editTextState.setThreshold(1);

        editTextState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = editTextState.getText().toString();
                    ListAdapter listAdapter = editTextState.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++){
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    editTextState.setText("");
                }
            }
        });
    }

    private void getCountryList() {
        Locale[] locales = Locale.getAvailableLocales();
        List<String> countries = new ArrayList<>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        editTextCountry.setAdapter(adapter);
        editTextCountry.setThreshold(1);

        editTextCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = editTextCountry.getText().toString();
                    ListAdapter listAdapter = editTextCountry.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++){
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    editTextCountry.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_trail, menu);
        return true;
    }
    //endregion

    //region Save Trail

    private void SaveTrail() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ModelTrails newTrail = new ModelTrails();
        newTrail.setTrailName(editTextTrailName.getText().toString().trim());
        newTrail.setTrailCity(editTextCity.getText().toString().trim());
        newTrail.setTrailState(editTextState.getText().toString().trim());
        newTrail.setTrailCountry(editTextCountry.getText().toString().trim());
        if (editLength.getText().length() > 0) {
            newTrail.setLength(Double.valueOf(editLength.getText().toString().trim()));
        }
        newTrail.setIsEasy(isEasy);
        newTrail.setIsMedium(isMedium);
        newTrail.setIsHard(isHard);
        newTrail.setLocation(trailLocation);
        newTrail.setIsPrivate(isPrivateTrail);
        newTrail.setTrailStatus(2);

        // call the method to do the update to Parse
        ModelTrails modelTrails = new ModelTrails();
        modelTrails.CreateNewTrail(context, newTrail);
        AlertDialogHelper.showCustomAlertDialog(context, "New Trail!", newTrail.getTrailName() + " Has Been Created, But It May Not Be Available For A Few Minutes Until The Cloud Has Done It's Magic.");
        resetEverything();
    }

    //endregion

    //region Sliding Panel
    private void setImageClickListener(){
        up_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "UpDownImage Was Clicked");
                if (mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Log.i(TAG, "Panel State is Expanded and will be collapsed");
                    RotateImageViewDown();
                    mlayout.setEnabled(true);
                    mlayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                if (mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    Log.i(TAG, "Panel State is Collapsed and will be Expanded");
                    RotateImageViewUp();
                    mlayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    isMapUp = true;
                }
            }
        });
    }

    private void RotateImageViewUp(){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center_up);

        up_view.startAnimation(animation);
    }

    private void RotateImageViewDown() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center_down);
        up_view.startAnimation(animation);
    }

    private void setPanelListener() {
        mlayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                Log.i(TAG, "Panel State is Sliding");
                mlayout.setEnabled(true);
                if (!isMapUp) {
                    RotateImageViewUp();
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
                Log.i(TAG, "Panel State is Collapsed");
                mlayout.setEnabled(true);
                isMapUp = false;
            }

            @Override
            public void onPanelExpanded(View view) {
                Log.i(TAG, "Panel State is Expanding");
                mlayout.setEnabled(false);
                isMapUp = true;
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.i(TAG, "Panel State is Anchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.i(TAG, "Panel State is Hidden");
            }
        });
    }
    //endregion

    //region Google MapActivity Api Methods
    //private void setCreateMarkerListener() {

//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//            }
//        });
//    }

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
            for (int i = 0; trails.size() > i; i++) {
                trails.get(i).distanceAway = (float) Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), home) * 100) / 100;
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
                            .snippet(trails.get(i).distanceAway + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();

                } else if (trails.get(i).getTrailStatus() == 2) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).getTrailName())
                            .snippet(trails.get(i).distanceAway + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();
                } else {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .title(trails.get(i).getTrailName())
                            .snippet(trails.get(i).distanceAway + " miles away")
                            .position(trailHomeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    SetMultilineInfoAdapter(googleMap);
                    marker.showInfoWindow();
                }
            }
        }
        // zoom to current location always as long as we have it
        if (mLastLocation != null) {
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
        // clear the previous marker if not null
        if (newLocationMarkerOptions != null) {
            newLocationMarkerOptions.remove();
        }
        newLocationMarkerOptions = googleMap.addMarker(new MarkerOptions()
                .title("New Trail Location")
                .snippet(latLng.latitude + " : " + latLng.longitude)
                .position(latLng));
        SetMultilineInfoAdapter(googleMap);
        newLocationMarkerOptions.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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

    //region Location API
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
    //endregion
}
