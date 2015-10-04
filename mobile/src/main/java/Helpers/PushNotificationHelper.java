package Helpers;

import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PushNotificationHelper {

    private static final String LOG = "PushNotificationHelper";

    public static void SendOutAPushNotificationForNewComment(String trailNameString, String Comment, String ObjectID) {
        ParsePush push = new ParsePush();
        String trailChannel = CreateChannelName(trailNameString);
        push.setChannel(trailChannel);
        Log.i(LOG, trailNameString + " has a new comment: " + Comment);
        JSONObject json = new JSONObject();
        try {
            json.put("action", "com.singlecog.trailkeeper.NEW_COMMENT_NOTIF");
            json.put("com.Parse.Channel", trailChannel);
            json.put("trailObjectId", ObjectID);
            json.put("trailName", trailNameString);
            json.put("comment", Comment);
            json.put("InstallationObjectId", ParseInstallation.getCurrentInstallation().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        push.setData(json);
        push.sendInBackground();
    }

    public static void SendOutAPushNotificationsForStatusUpdate(String trailNameString, int status, String ObjectID) {
        ParsePush push = new ParsePush();
        String statusUpdate;
        String trailChannel = CreateChannelName(trailNameString);
        push.setChannel(trailChannel);
        Log.i(LOG, trailNameString + " has an updated status ");
        JSONObject json = new JSONObject();

        if (status == 1)
            statusUpdate = trailNameString + " trails are closed!";
        else if (status == 2)
            statusUpdate =  trailNameString + " trails are open!";
        else
            statusUpdate = "We don't know if " + trailNameString + " trails are open or closed";

        try {
            json.put("action", "com.singlecog.trailkeeper.NEW_STATUS_NOTIF");
            json.put("com.Parse.Channel", trailChannel);
            json.put("trailObjectId", ObjectID);
            json.put("statusUpdate", statusUpdate);
            json.put("InstallationObjectId", ParseInstallation.getCurrentInstallation().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        push.setData(json);
        push.sendInBackground();     // TODO until the javascript to send push is working we need a data connection on status change
    }

    public static CharSequence[] getTrailStatusNames() {
        return new CharSequence[]{"Open", "Closed", "Unknown"};
    }

    public  static String FormatChannelName(String trail) {
        // strip out the word Channel
        trail = trail.replace("Channel", "");
        // add a space before each capital letter
        trail = trail.replaceAll("([A-Z])", " $1").trim();
        return trail;
    }

    public static String CreateChannelName(String trail) {
        return trail.replace(" ", "") + "Channel";
    }

    // Subscriptions are also channels on Parse
    public static List<String> GetUserSubscriptions() {
         return ParseInstallation.getCurrentInstallation().getList("channels");
    }
}
