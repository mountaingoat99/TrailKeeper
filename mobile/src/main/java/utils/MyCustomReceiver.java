package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseInstallation;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MyCustomReceiver extends BroadcastReceiver {

    private static final String TAG = "MyCustomReciever";
    private static final String _Title = "TrailKeeper";
    public static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.Parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.i(TAG, " got action " + action + " on channel " + channel);

            if (action.equalsIgnoreCase("com.singlecog.trailkeeper.NEW_COMMENT_NOTIF")) {
                generateCommentNotification(context, json);
            }

            if (action.equalsIgnoreCase("com.singlecog.trailkeeper.NEW_STATUS_NOTIF")) {
                generateStatusNotification(context, json);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateCommentNotification(Context context, JSONObject json) {
        String ObjectID = null;
        Integer TrailID = 0;
        String Comment = null;
        String trailName = null;
        String InstallObjectID = null;
        String ThisInstallObjectID = ParseInstallation.getCurrentInstallation().getObjectId();

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            TrailID = json != null ? json.getInt("trailId") : 0;
            Comment = json != null ? json.getString("comment") : "";
            trailName = json != null ? json.getString("trailName") : "";
            InstallObjectID = json != null ? json.getString("InstallationObjectId") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!ThisInstallObjectID.equals(InstallObjectID)) {

            TrailKeeperApplication.LoadAllCommentsFromParse();

            Intent intent = new Intent(context, TrailScreen.class);
            Bundle b = new Bundle();
            b.putInt("trailID", TrailID);
            b.putString("objectID", ObjectID);
            b.putBoolean("fromNotification", true);
            intent.putExtras(b);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            numMessages = 0;
            NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(_Title)
                            .setContentText(trailName + " has a new comment:" + "\n" + Comment)
                            .setNumber(++numMessages);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);

            mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void generateStatusNotification(Context context, JSONObject json) {
        String ObjectID = null;
        Integer TrailID = 0;
        String StatusUpdate = null;
        String InstallObjectID = null;
        String ThisInstallObjectID = ParseInstallation.getCurrentInstallation().getObjectId();

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            TrailID = json != null ? json.getInt("trailId") : 0;
            StatusUpdate = json != null ? json.getString("statusUpdate") : "";
            InstallObjectID = json != null ? json.getString("InstallationObjectId") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!ThisInstallObjectID.equals(InstallObjectID)) {

            TrailKeeperApplication.LoadAllTrailsFromParse();

            Intent intent = new Intent(context, TrailScreen.class);
            Bundle b = new Bundle();
            b.putInt("trailID", TrailID);
            b.putString("objectID", ObjectID);
            b.putBoolean("fromNotification", true);
            intent.putExtras(b);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            numMessages = 0;
            NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(_Title)
                            .setContentText(StatusUpdate)
                            .setNumber(++numMessages);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);

            mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
