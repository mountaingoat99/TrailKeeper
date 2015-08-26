package com.singlecog.trailkeeper.Activites;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;


import AsyncAdapters.AsyncAdapterLoadAllFromParse.AsyncLoadAllAuthorizedCommentorsFromParse;
import AsyncAdapters.AsyncAdapterLoadAllFromParse.AsyncLoadAllCommentsFromParse;
import AsyncAdapters.AsyncAdapterLoadAllFromParse.AsyncLoadAllTrailStatusFromParse;
import AsyncAdapters.AsyncAdapterLoadAllFromParse.AsyncLoadAllTrailsFromParse;
import Helpers.AlertDialogHelper;
import Helpers.CreateAccountHelper;
import ParseObjects.ParseAuthorizedCommentors;
import ParseObjects.ParseComments;
import ParseObjects.ParseTrailStatus;
import ParseObjects.ParseTrails;

public class TrailKeeperApplication extends Application implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected static final String TAG = "TrailKeeperActivity";
    public static GoogleApiClient mGoogleApiClient;
    //public static LocationRequest mLocationRequest;
    public static Location mLastLocation;
    public static LatLng home;
    public static boolean isEmailVerified = false;
    public static boolean isPlayServicesInstalled;
    public static boolean isPlayServicesUpdated;
    public static boolean isGPSEnabled;
    private Context context = this;

    public TrailKeeperApplication() {}

    public TrailKeeperApplication(Context context) {
        this.context = context;
    }

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static boolean isEmailVerified() {

        return ParseUser.getCurrentUser().getBoolean("emailVerified");
    }

    public static void setIsEmailVerified(boolean isEmailVerified) {
        TrailKeeperApplication.isEmailVerified = isEmailVerified;
    }

    public static boolean GetIsPlayServicesInstalled() {
        return isPlayServicesInstalled;
    }

    public static void setIsPlayServicesInstalled(boolean isPlayServicesInstalled) {
        TrailKeeperApplication.isPlayServicesInstalled = isPlayServicesInstalled;
    }

    public static boolean GetIsPlayServicesUpdated() {
        return isPlayServicesUpdated;
    }

    public static void setIsPlayServicesUpdated(boolean isPlayServicesUpdated) {
        TrailKeeperApplication.isPlayServicesUpdated = isPlayServicesUpdated;
    }

    public static boolean GetIsGPSEnabled() {
        return isGPSEnabled;
    }

    public static void setIsGPSEnabled(boolean isGPSEnabled) {
        TrailKeeperApplication.isGPSEnabled = isGPSEnabled;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        // see if Play Services is installed
        checkGooglePlayServices();

        // see if the GPS is on
        checkIfGPSIsEnabled();

        // get the latest device location
        buildGoogleApiClient();
        //buildLocationRequest();

        // add the Parse Subclasses
        ParseObject.registerSubclass(ParseTrails.class);
        ParseObject.registerSubclass(ParseComments.class);
        ParseObject.registerSubclass(ParseAuthorizedCommentors.class);
        ParseObject.registerSubclass(ParseTrailStatus.class);

        // Enable the Local Datastore
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, getResources().getString(R.string.ApplicationID), getResources().getString(R.string.ClientKEey));
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // enable anon users, they can then be turned into
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().saveInBackground();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        LoadAllTrailsFromParse();
        LoadAllCommentsFromParse();
        LoadAllAuthorizedCommentorsFromParse();
        LoadAllTrailStatusUpdatorsFromParse();

        // Check to see if the Users Email is verified
        CreateAccountHelper.CheckUserVerified();

        savePreferences();
    }

    public void checkIfGPSIsEnabled() {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;
        setIsGPSEnabled(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    private void savePreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasPushWaiting", false);
        editor.apply();
    }

    public void checkGooglePlayServices() {
        Log.i("HomeScreen", "Checking Google Play Services");
        int connectionStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (connectionStatus != ConnectionResult.SUCCESS) {
            Log.i("HomeScreen", "Google Play Services not successfull");
            if (connectionStatus == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                Log.i("HomeScreen", "Google Play Services needs and update");
                setIsPlayServicesUpdated(false);
            } else {
                Log.i("HomeScreen", "Google Play Services not installed");
                setIsPlayServicesInstalled(false);
            }
        } else {
            Log.i("HomeScreen", "Google Play Services is installed and up to date");
            setIsPlayServicesUpdated(true);
            setIsPlayServicesInstalled(true);
        }
    }

    // load all the objects from Parse
    public static void LoadAllTrailsFromParse() {
        try {
            AsyncLoadAllTrailsFromParse load = new AsyncLoadAllTrailsFromParse();
            load.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadAllCommentsFromParse() {
        try {
            AsyncLoadAllCommentsFromParse comments = new AsyncLoadAllCommentsFromParse();
            comments.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadAllAuthorizedCommentorsFromParse() {
        try {
            AsyncLoadAllAuthorizedCommentorsFromParse commentorsFromParse = new AsyncLoadAllAuthorizedCommentorsFromParse();
            commentorsFromParse.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadAllTrailStatusUpdatorsFromParse() {
        try {
            AsyncLoadAllTrailStatusFromParse trailStatusFromParse = new AsyncLoadAllTrailStatusFromParse();
            trailStatusFromParse.execute();
        } catch (Exception e) {
            e.printStackTrace();
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

//    private void buildLocationRequest() {
//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1000); // 1 second, in milliseconds
//    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        home = new LatLng(currentLatitude, currentLongitude);
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
            // calls the AsyncTask for the Trails
        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();  //TODO make a call to send out a global message
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
            Log.i(TAG, "Location services connection failed with code " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void LoadTestCommentsToParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("trails");

        // Retrieve the object by id
        query.getInBackground("eheoC2nCHr", new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.addUnique("Comments", "Too Much Rain");
                    gameScore.saveInBackground();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
