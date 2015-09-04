package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Helpers.PushNotificationHelper;
import ParseObjects.ParseTrails;
import models.ModelTrails;

public class MyCustomReceiver extends BroadcastReceiver {

    private static final String TAG = "MyCustomReciever";
    private static final String _Title = "TrailKeeper";
    public static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            // checking for an internet connection and seeing if we need to send off a push
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isConnected() || mobile.isConnected()) {
                UpdateOfflineContent(context);
                Log.i(TAG, "Network is reconnected");
            }

            String action = intent.getAction();

            if (action.equalsIgnoreCase("com.singlecog.trailkeeper.NEW_COMMENT_NOTIF")) {
                String channel = intent.getExtras().getString("com.Parse.Channel");
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.i(TAG, " got action " + action + " on channel " + channel);
                generateCommentNotification(context, json);
            }

            if (action.equalsIgnoreCase("com.singlecog.trailkeeper.NEW_STATUS_NOTIF")) {
                String channel = intent.getExtras().getString("com.Parse.Channel");
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.i(TAG, " got action " + action + " on channel " + channel);
                generateStatusNotification(context, json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UpdateOfflineContent(Context context) {
        Log.i(TAG, "Attemping to update offline content");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean hasPushWaiting = sp.getBoolean("hasPushWaiting", false);
        boolean hasNewTrailWaiting = sp.getBoolean("hasNewTrailWaiting", false);
        if (hasPushWaiting) {
            Log.i(TAG, "Notification Push is waiting");
            savePreferences(context, "hasPushWaiting", false);
            String trailNameString = sp.getString("trailNameString", "");
            int status = sp.getInt("status", 0);
            String objectID = sp.getString("objectId", "");
            PushNotificationHelper.SendOutAPushNotificationsForStatusUpdate(trailNameString, status, objectID);
            Log.i(TAG, "Notification was sent");
        }
        if (hasNewTrailWaiting) {
            Log.i(TAG, "New Trail is waiting to be updated");
            savePreferences(context, "hasNewTrailWaiting", false);
            ModelTrails trail = new ModelTrails();
            trail.setIsEasy(sp.getBoolean("skillEasy", false));
            trail.setIsMedium(sp.getBoolean("skillMedium", false));
            trail.setIsHard(sp.getBoolean("skillHard", false));
            trail.setTrailName(sp.getString("trailName", ""));
            trail.setTrailCity(sp.getString("city", ""));
            trail.setTrailState(sp.getString("state", ""));
            trail.setTrailCountry(sp.getString("country", ""));
            trail.setLength((double) sp.getFloat("length", 0.0f));
            LatLng location = new LatLng((double) sp.getFloat("latitude", 0.0f), (double) sp.getFloat("longitude", 0.0f));
            trail.setLocation(location);
            trail.setTrailStatus(sp.getInt("status", 0));
            ModelTrails saveTrail = new ModelTrails();
            saveTrail.SaveNewTrail(trail);

            Log.i(TAG, "New Trail was sent to Parse");
        }
    }

    private void savePreferences(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void generateCommentNotification(Context context, JSONObject json) {
        String ObjectID = null;
        String Comment = null;
        String trailName = null;
        String InstallObjectID = null;
        String ThisInstallObjectID = ParseInstallation.getCurrentInstallation().getObjectId();

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
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
            b.putString("objectID", ObjectID);
            intent.putExtras(b);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            numMessages = 0;
            NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_up)
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
        String StatusUpdate = null;
        String InstallObjectID = null;
        String ThisInstallObjectID = ParseInstallation.getCurrentInstallation().getObjectId();

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            StatusUpdate = json != null ? json.getString("statusUpdate") : "";
            InstallObjectID = json != null ? json.getString("InstallationObjectId") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!ThisInstallObjectID.equals(InstallObjectID)) {

            // need to unpin the trails first, then load the new trails
            ParseQuery<ParseTrails> query = new ParseQuery<>("Trails");
            query.fromLocalDatastore();
            try {
                List<ParseTrails> parseTrails = query.find();
                for (ParseTrails trails : parseTrails) {
                    if (trails.getObjectId().equals(ObjectID)) {
                        trails.unpin();
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TrailKeeperApplication.LoadAllTrailsFromParse();

            Intent intent = new Intent(context, TrailScreen.class);
            Bundle b = new Bundle();
            b.putString("objectID", ObjectID);
            intent.putExtras(b);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            numMessages = 0;
            final NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            final android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_up)
                            .setContentTitle(_Title)
                            .setContentText(StatusUpdate)
                            .setNumber(++numMessages);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }, 5000);


        }
    }
}
