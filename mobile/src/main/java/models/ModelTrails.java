package models;

import android.content.Context;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Helpers.ConnectionDetector;
import Helpers.PushNotificationHelper;
import ParseObjects.ParseTrailStatus;
import ParseObjects.ParseTrails;

public class ModelTrails {

    public static final String LOG = "ModelTrails";
    public String ObjectID;
    public String TrailName;
    public int TrailStatus;
    public String TrailCity;
    public String TrailState;
    public ParseGeoPoint GeoLocation;
    public float distance;
    private Context context;
    private TrailScreen trailScreen;

    public ModelTrails()
    {

    }

    public ModelTrails(Context context, TrailScreen trailScreen) {
        this.context = context;
        this.trailScreen = trailScreen;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    //Region Static Methods
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

    //Region Public Methods
    public boolean CreateNewTrail(Context context, String trailName, String city, String state, String country,
                               String length, List<String> skillLevelList, LatLng location, boolean isPrivate) {
        boolean valid = true;
        ParseTrails parseTrails = new ParseTrails();
        try {
            // convert the list, if anything in it, to Json Array
            if (skillLevelList.size() > 0) {
                //String json = new Gson().toJson(skillLevelList);
                parseTrails.addAllUnique("skillLevels", skillLevelList);
            }
            // save the GeoPoint first
            ParseGeoPoint point = new ParseGeoPoint(location.latitude, location.longitude);
            // the update the rest if the values
            parseTrails.put("trailName", trailName);
            parseTrails.put("city", city);
            parseTrails.put("state", state);
            parseTrails.put("country", country);
            parseTrails.put("distance", Double.valueOf(length));
            parseTrails.put("private", isPrivate);
            parseTrails.put("geoLocation", point);
            parseTrails.put("status", 2);
            parseTrails.put("createdBy", ParseUser.getCurrentUser().getUsername());
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                parseTrails.saveInBackground();
            } else {
                parseTrails.saveEventually();
            }
            Log.i(LOG, "Saving New Trail");
            parseTrails.pinInBackground();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG, "Saving New Trail Failed");
            valid = false;
        }
        CreateNewTrailStatus(context, trailName);
        return valid;
    }

    private void CreateNewTrailStatus(Context context, String trailName) {
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
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                trailStatus.saveInBackground();
            } else {
                trailStatus.saveEventually();
            }
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

    public float getDistance() {
        return distance;
    }
    //endregion

    private void TrailStatusUpdateSuccessful(boolean valid, String message) {
        if (valid) {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, null);
        } else {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, message);
        }
    }
}