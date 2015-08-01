package models;

import android.content.Context;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.Notifications;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import java.util.ArrayList;
import java.util.List;

import RecyclerAdapters.RecyclerViewNotifications;

public class ModelTrails {

    private static final String LOG = "ModelTrails";
    private static int nextId = 0;
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

    public static void SendOutAPushNotifications(String trailNameString, int status) {
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

    //endregion

    //Region Public Methods

    // Subscriptions are also channels on Parse
    public static List<String> GetUserSubscriptions() {
         return ParseInstallation.getCurrentInstallation().getList("channels");
    }

    public void UpdateTrailStatus(String objectId, final int choice) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("trails");

        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    parseObject.put("Status", choice);
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