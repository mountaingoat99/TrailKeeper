package models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.AllComments;
import com.singlecog.trailkeeper.Activites.FindTrail;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Helpers.PushNotificationHelper;
import RecyclerAdapters.RecyclerViewNotifications;

public class ModelTrails {

    private static final String LOG = "ModelTrails";

    public String ObjectID;
    public int TrailID;
    public String TrailName;
    public int TrailStatus;
    public String TrailCity;
    public String TrailState;
    public ParseGeoPoint GeoLocation;
    public float distance;
    private Context context;
    private TrailScreen trailScreen;
    private RecyclerViewNotifications notificationsScreen;
    private FindTrail findTrailScreen;
    private AllComments allCommentsScreen;

    public ModelTrails()
    {

    }

    public ModelTrails(Context context, TrailScreen trailScreen) {
        this.context = context;
        this.trailScreen = trailScreen;
    }

    public ModelTrails (Context context, RecyclerViewNotifications notifications) {
        this.context = context;
        this.notificationsScreen = notifications;
    }

    public ModelTrails (Context context, FindTrail findTrail) {
        this.context = context;
        this.findTrailScreen = findTrail;
    }

    public ModelTrails (Context context, AllComments allComments) {
        this.context = context;
        this.allCommentsScreen = allComments;
    }

    public int getTrailID() {
        return TrailID;
    }

    public void setTrailID(int trailID) {
        TrailID = trailID;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    //Region Static Methods

    public static String ConvertTrailStatus(ModelTrails model){
        String statusName = "";

        switch (model.TrailStatus){
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

    //endregion

    //Region Public Methods
    // gets the trail names for the updateTrailStatus field in user
    // this field decides if a user can update a status, if a
    // user gets trail admin status we remove it from all other users
    public static List<String> GetTrailNamesForStatusChangeAuthorized() {
        List<String> trailNames = new ArrayList<>();
        List<ParseObject> trails = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");

        query.fromLocalDatastore();
        try {
            trails = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; trails.size() > i; i++) {
            String trailname = trails.get(i).get("trailName").toString();
            trailNames.add(trailname);
        }
        return trailNames;
    }

    // gets the trail names
    public void GetTrailNames() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<String> trails = new ArrayList<>();
                if (e == null) {
                    for (ParseObject parseObject : list) {
                        String trailName = parseObject.get("trailName").toString();
                        trails.add(trailName);
                    }
                    if (findTrailScreen != null) {
                        findTrailScreen.RecieveTrailNames(trails);
                    } else {
                        allCommentsScreen.RecieveTrailNames(trails);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // gets the states for each trail we have
    public void GetTrailStates(final RecyclerView RecyclerView) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Set<String> uniqueStates = new HashSet<>();
                    for (ParseObject parseObject : list) {
                        String trailState;
                        trailState = parseObject.get("state").toString();
                        // Set will only add unique States, and will sort Alphabetically
                        uniqueStates.add(trailState);
                    }
                    FindTrail findTrail = new FindTrail();
                    findTrail.SetUpStateRecyclerView(uniqueStates, context, RecyclerView);
                } else {
                    e.printStackTrace();
                }
            }
        });
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

    // query the local datastore to get the trailID and Object from a trail name
    public void GetTrailIDs(final String name, final Context context) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.whereEqualTo("trailName", name);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ModelTrails trails = new ModelTrails();
                    Log.d(name, "Retrieved");
                    for (ParseObject trail : list) {
                        trails.setObjectID(trail.getObjectId());
                        trails.setTrailID(trail.getInt("trailId"));
                    }
                    // lets decide which screen to send back the info
                    if (notificationsScreen != null) {
                        RecyclerViewNotifications notifications = new RecyclerViewNotifications();
                        notifications.SendToTrailScreen(trails, context);
                    } else if (findTrailScreen != null) {
                        findTrailScreen.SendToTrailScreen(trails, context);
                    } else if (allCommentsScreen != null) {
                        allCommentsScreen.SendToTrailScreen(trails);
                    }
                } else {
                    Log.d(name, "Not Retrieved");
                }
            }
        });
    }

    public void UpdateTrailStatus(String objectId, final int choice) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");

        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    parseObject.put("status", choice);
                    parseObject.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // call class methods to send info back to activity
                                TrailStatusUpdateSuccessful(true, null);
                            } else {
                                TrailStatusUpdateSuccessful(false, e.getMessage());
                            }
                        }
                    });
                } else {
                    TrailStatusUpdateSuccessful(false, e.getMessage());
                }
            }
        });
    }

    public void SubscribeToChannel(String trailName, int choice, final String whichActivity){    // 0 means Yes, 1 means No
        //When a user indicates they want trail Updates we subscribe them to them
        final String trailNameChannel = PushNotificationHelper.CreateChannelName(trailName);
        if (choice == 0) {
            ParsePush.subscribeInBackground(trailNameChannel, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(LOG, "successfully subscribed to the " + trailNameChannel + " broadcast channel.");
                        SubscribeWasSuccessful(true, null, whichActivity);
                    } else {
                        SubscribeWasSuccessful(false, e.getMessage(), whichActivity);
                        Log.e(LOG, "failed to subscribe for push for " + trailNameChannel, e);
                    }
                }
            });
        } else {
            ParsePush.unsubscribeInBackground(trailNameChannel, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(LOG, "successfully un-subscribed to the " + trailNameChannel + " broadcast channel.");
                        SubscribeWasSuccessful(true, null, whichActivity);
                    } else {
                        SubscribeWasSuccessful(false, e.getMessage(), whichActivity);
                        Log.e(LOG, "failed to un-subscribe for push " + trailNameChannel, e);
                    }
                }
            });
        }
    }

    public float getDistance() {
        return distance;
    }

    //endregion

    // Region Private Methods
    private void TrailStatusUpdateSuccessful(boolean valid, String message) {
        if (valid) {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, null);
        } else {
            trailScreen.TrailStatusUpdateWasSuccessful(valid, message);
        }
    }

    // here we will call the TrailScreen class and let them know it was valid
    private void SubscribeWasSuccessful(boolean valid, String message, String whichActivity) {
        if (valid) {
            if (whichActivity.isEmpty()) {
                trailScreen.UpdateSubscriptionWasSuccessful(valid, null);
            } else {
                notificationsScreen.UpdateSubscriptionWasSuccessful(true, null);
            }
            Log.d(LOG, "successfully changed subscriptions.");
        } else {
            if (whichActivity.isEmpty()) {
                trailScreen.UpdateSubscriptionWasSuccessful(valid, message);
            } else {
                notificationsScreen.UpdateSubscriptionWasSuccessful(false, message);
            }
            Log.e(LOG, "Unsuccessfully changed subscription");
        }
    }
    //endregion
}