package models;

import android.content.Context;
import android.graphics.AvoidXfermode;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.singlecog.trailkeeper.Activites.FindTrail;
import com.singlecog.trailkeeper.Activites.Notifications;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import RecyclerAdapters.RecyclerViewNotifications;

public class ModelTrails {

    private static final String LOG = "ModelTrails";
    private static CharSequence[] trailStatusNames;
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

    public static String CreateChannelName(String trail) {
        return trail.replace(" ", "") + "Channel";
    }

    public  static String FormatChannelName(String trail) {
        // strip out the word Channel
        trail = trail.replace("Channel", "");
        // add a space before each capital letter
        trail = trail.replaceAll("([A-Z])", " $1").trim();
        return trail;
    }

    public static CharSequence[] getTrailStatusNames() {
        return trailStatusNames = new CharSequence[]{"Open", "Closed", "Unknown"};
    }

    public static void SendOutAPushNotificationsForStatusUpdate(String trailNameString, int status) {
        ParsePush push = new ParsePush();
        String trailChannel = CreateChannelName(trailNameString);
        push.setChannel(trailChannel);
        if (status == 1)
            push.setMessage(trailNameString + " trails are closed!");
        else if (status == 2)
            push.setMessage(trailNameString + " trails are open!");
        else
            push.setMessage("We don't know if " + trailNameString + " trails are open or closed");
        push.sendInBackground();
    }

    public static void SendOutAPushNotificationForNewComment(String trailNameString, String Comment, int TrailID, String ObjectID) {
        ParsePush push = new ParsePush();
        String trailChannel = CreateChannelName(trailNameString);
        push.setChannel(trailChannel);
        Log.i(LOG, trailNameString + " has a new comment: " + Comment);
        //String trailIdString = String.valueOf(TrailID);
        JSONObject json = new JSONObject();
        try {
            json.put("action", "com.singlecog.trailkeeper.NEW_NOTIF");
            json.put("com.Parse.Channel", trailChannel);
            json.put("trailId", TrailID);
            json.put("trailObjectId", ObjectID);
            json.put("trailName", trailNameString);
            json.put("comment", Comment);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setData(json);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(LOG, "Parse Push Failed " + e.getMessage());
                }
            }
        });
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
                    findTrailScreen.RecieveTrailNames(trails);
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
                    }
                } else {
                    Log.d(name, "Not Retrieved");
                }
            }
        });
    }

    // Subscriptions are also channels on Parse
    public static List<String> GetUserSubscriptions() {
         return ParseInstallation.getCurrentInstallation().getList("channels");
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
        final String trailNameChannel = CreateChannelName(trailName);
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