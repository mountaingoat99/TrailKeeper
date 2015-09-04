package models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import AsyncAdapters.AsyncSaveOfflineTrail;
import Helpers.ConnectionDetector;
import Helpers.PushNotificationHelper;
import ParseObjects.ParseTrailStatus;
import ParseObjects.ParseTrails;

public class ModelTrails {

    //region properties
    public static final String LOG = "ModelTrails";
    private String ObjectID;
    private String TrailName;
    private int TrailStatus;
    private String TrailCity;
    private String TrailState;
    private String TrailCountry;
    private double Length;
    private LatLng Location;
    private boolean isEasy;
    private boolean isMedium;
    private boolean isHard;
    private boolean IsPrivate;

    public ParseGeoPoint GeoLocation;
    public float distanceAway;
    private Context context;
    private TrailScreen trailScreen;
    //endregion

    //region contruct
    public ModelTrails()
    {

    }

    public ModelTrails(Context context, TrailScreen trailScreen) {
        this.context = context;
        this.trailScreen = trailScreen;
    }
    //endregion

    //region gets and sets
    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public float getDistanceAway() {
        return distanceAway;
    }

    public int getTrailStatus() {
        return TrailStatus;
    }

    public void setTrailStatus(int trailStatus) {
        TrailStatus = trailStatus;
    }

    public String getTrailName() {
        return TrailName;
    }

    public void setTrailName(String trailName) {
        TrailName = trailName;
    }

    public String getTrailCity() {
        return TrailCity;
    }

    public void setTrailCity(String trailCity) {
        TrailCity = trailCity;
    }

    public String getTrailState() {
        return TrailState;
    }

    public void setTrailState(String trailState) {
        TrailState = trailState;
    }

    public String getTrailCountry() {
        return TrailCountry;
    }

    public void setTrailCountry(String trailCountry) {
        TrailCountry = trailCountry;
    }

    public double getLength() {
        return Length;
    }

    public void setLength(double length) {
        Length = length;
    }

    public LatLng getLocation() {
        return Location;
    }

    public void setLocation(LatLng location) {
        Location = location;
    }

    public boolean getIsEasy() {
        return isEasy;
    }

    public void setIsEasy(boolean isEasy) {
        this.isEasy = isEasy;
    }

    public boolean getIsMedium() {
        return isMedium;
    }

    public void setIsMedium(boolean isMedium) {
        this.isMedium = isMedium;
    }

    public boolean getIsHard() {
        return isHard;
    }

    public void setIsHard(boolean isHard) {
        this.isHard = isHard;
    }

    public boolean getIsPrivate() {
        return IsPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        IsPrivate = isPrivate;
    }
    //endregion

    //region Static Methods
    public static String ConvertTrailStatus(int status){
        String statusName = "";

        switch (status){
            case 1:
                statusName = "Closed";
                break;
            case 2:
                statusName = "Open";
                break;
            case 3:
                statusName = "Unknown";
                break;
        }
        return statusName;
    }

    public static List<ModelTrails> GetAllTrailInfo() {
        List<ModelTrails> passedTrails = new ArrayList<>();

        //Parse
        ParseQuery<ParseObject> tQuery = ParseQuery.getQuery("Trails");
        tQuery.fromLocalDatastore();
        try {
            List<ParseObject> list = tQuery.find();
            for (ParseObject parseObject : list) {
                ModelTrails trail = new ModelTrails();
                trail.ObjectID = parseObject.getObjectId();
                trail.TrailName = parseObject.get("trailName").toString();
                trail.TrailStatus = Integer.valueOf(parseObject.get("status").toString());
                trail.TrailState = parseObject.get("state").toString();
                trail.TrailCity = parseObject.get("city").toString();
                trail.GeoLocation = parseObject.getParseGeoPoint("geoLocation");

                passedTrails.add(trail);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return passedTrails;
    }

    // gets the trail names
    public static List<String> GetTrailNames() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        List<String> trails = new ArrayList<>();
        query.fromLocalDatastore();
        try {
            List<ParseObject> list = query.find();
            for (ParseObject parseObject : list) {
                String trailName = parseObject.get("trailName").toString();
                trails.add(trailName);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return trails;
    }

    // get the trail Pin based on ObjectID
    public static int GetTrailPin(String trailName) {
        List<ParseObject> trailPinList = new ArrayList<>();
        int trailPin = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrailStatus");
        query.whereEqualTo("trailName", trailName);
        query.fromLocalDatastore();

        try {
            trailPinList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (trailPinList.size() > 0) {
            for (ParseObject object : trailPinList) {
                trailPin = Integer.valueOf(object.get("updateStatusPin").toString());
            }
        }
        return trailPin;
    }

    // gets the states for each trail we have
    public static Set<String> GetTrailStates() {
        Set<String> uniqueStates = new HashSet<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.fromLocalDatastore();
        try {
            List<ParseObject> list = query.find();
            for (ParseObject parseObject : list) {
                String trailState;
                trailState = parseObject.get("state").toString();
                // Set will only add unique States, and will sort Alphabetically
                uniqueStates.add(trailState);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return uniqueStates;
    }

    // get the trails for any given state, pass in the state abbreviation
    public static List<ParseObject> GetTrailsByState(String state) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.whereEqualTo("state", state);
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // query the local datastore to get the ObjectID from a trail name
    public static ModelTrails GetTrailObjectIDs(final String name) {
        ModelTrails trails = new ModelTrails();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.whereEqualTo("trailName", name);
        query.fromLocalDatastore();
        try {
            List<ParseObject> list = query.find();
            Log.d(name, "Retrieved");
            for (ParseObject trail : list) {
                trails.setObjectID(trail.getObjectId());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return trails;
    }
    //endregion

    //region Public Methods
    public void CreateNewTrail(Context context, ModelTrails newTrail) {
        AsyncSaveOfflineTrail offlineTrail = new AsyncSaveOfflineTrail(context, newTrail);
        offlineTrail.execute();
    }

    public void SaveNewTrail(ModelTrails modelTrails) {
        ParseTrails parseTrails = new ParseTrails();

        parseTrails.put("skillEasy", modelTrails.getIsEasy());
        parseTrails.put("skillMedium", modelTrails.getIsMedium());
        parseTrails.put("skillHard", modelTrails.getIsHard());
        parseTrails.put("trailName", modelTrails.getTrailName());
        parseTrails.put("city", modelTrails.getTrailCity());
        parseTrails.put("state", modelTrails.getTrailState());
        parseTrails.put("country", modelTrails.getTrailCountry());
        parseTrails.put("distance", modelTrails.getLength());
        parseTrails.put("private", modelTrails.getIsPrivate());
        ParseGeoPoint point = new ParseGeoPoint(modelTrails.getLocation().latitude, modelTrails.getLocation().longitude);
        parseTrails.put("geoLocation", point);
        parseTrails.put("status", modelTrails.getTrailStatus());
        parseTrails.put("createdBy", ParseUser.getCurrentUser().getUsername());
        try {
            parseTrails.saveInBackground();
            Log.i(LOG, "Saving New Trail");
            parseTrails.pinInBackground();
            CreateNewTrailStatus(modelTrails.getTrailName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG, "Saving New Trail Failed");
        }
    }

    private void CreateNewTrailStatus(String trailName) {
        // create a random number to use as a Pin
        Random rand = new Random();
        int min = 1000;
        int max = 9999;
        int trailPin = rand.nextInt((max - min) + 1) + min;

        List<String> userList = new ArrayList<>();
        userList.add(ParseUser.getCurrentUser().getUsername());
        ParseTrailStatus trailStatus = new ParseTrailStatus();
        try {
            trailStatus.put("trailName", trailName);
            trailStatus.put("updateStatusPin", trailPin);
            trailStatus.put("authorizedUserName", ParseUser.getCurrentUser().getUsername());
            trailStatus.addAllUnique("authorizedUserNames", userList);
            trailStatus.saveInBackground();
            Log.i(LOG, " New TrailStatus Creation Succeeded");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG, "Saving the New TrailStatus Failed");
        }
    }

    public void UpdateTrailStatus(Context context, String objectId, final int choice) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.fromLocalDatastore();
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.put("status", choice);
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                parseObject.saveInBackground();
            } else {
                parseObject.saveEventually();
            }
            parseObject.pinInBackground();
            TrailStatusUpdateSuccessful(true, null);
        } catch (ParseException e) {
            e.printStackTrace();
            TrailStatusUpdateSuccessful(false, e.getMessage());
        }
    }

    public static void UpdateTrailStatusUser(Context context, String trailName) {
        List<String> userList = new ArrayList<>();
        userList.add(ParseUser.getCurrentUser().getUsername());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrailStatus");
        query.whereEqualTo("trailName", trailName);
        query.fromLocalDatastore();
        try {
            ParseObject parseObject = query.getFirst();
            parseObject.addAllUnique("authorizedUserNames", userList);
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                parseObject.saveInBackground();
            } else {
                parseObject.saveEventually();
            }
            Log.i(LOG, "TrailStatus User Update Succeeded");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(LOG, "TrailStatus User Update Failed");
        }
    }

    public void SubscribeToChannel(String trailName, int choice){    // 0 means Yes, 1 means No
        //When a user indicates they want trail Updates we subscribe them to them
        final String trailNameChannel = PushNotificationHelper.CreateChannelName(trailName);
        if (choice == 0) {
            ParsePush.subscribeInBackground(trailNameChannel);
        } else {
            ParsePush.unsubscribeInBackground(trailNameChannel);
        }
    }

    //endregion

    //region private methods
    private void TrailStatusUpdateSuccessful(boolean valid, String message) {
        if (valid) {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, null);
        } else {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, message);
        }
    }
    //endregion
}