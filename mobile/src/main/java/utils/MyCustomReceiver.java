package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Helpers.PushNotificationHelper;
import ParseObjects.ParseTrails;
import database.NewTrailDatabase;
import database.TrailCommentDatabase;
import database.TrailStatusDatabase;
import models.ModelTrailComments;
import models.ModelTrailStatus;
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

            //TODO check if phone or table getting null on mobile connect

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

        // new Comment saved offline
        TrailCommentDatabase cdb = new TrailCommentDatabase(context);
        int cCount  = cdb.GetDBRowCount();
        if (cCount > 0) {
            Log.i(TAG, "New Trail Comment is waiting to be updated");
            Log.i(TAG, "Offline Comment count: " + cCount);
            for (int i = 0; cCount > i; i++) {
                Log.i(TAG, "Looping through Offline trail Comments count: " + i);
                ModelTrailComments trailComments = cdb.GetOffLineTrailComment();
                ModelTrailComments saveComment = new ModelTrailComments();
                saveComment.CreateNewComment(trailComments.getObjectID(), trailComments.getTrailName(), trailComments.getTrailComments());
                // TODO might need to send out a push here
            }
        }

        // new trail status saved offline
        TrailStatusDatabase sdb = new TrailStatusDatabase(context);
        int sCount = sdb.GetDBRowCount();
        if (sCount > 0) {
            Log.i(TAG, "New Trail Status is waiting to be updated");
            Log.i(TAG, "Offline status count: " + sCount);
            for (int i = 0; sCount > i; i++) {
                Log.i(TAG, "Looping through Offline trail Status count: " + i);
                ModelTrailStatus trailStatus = sdb.GetOffLineTrailStatus();
                ModelTrails saveStatus = new ModelTrails();
                saveStatus.UpdateTrailStatus(trailStatus.getObjectID(), trailStatus.getChoice(), trailStatus.getTrailName());
                //PushNotificationHelper.SendOutAPushNotificationsForStatusUpdate(trailStatus.getTrailName(), trailStatus.getChoice(), trailStatus.getObjectID());
                //Log.i(TAG, "Notification was sent");
            }
        }

        // New Trails saved offline
        NewTrailDatabase db = new NewTrailDatabase(context);
        int count = db.GetDBRowCount();
        if (count > 0) {
            Log.i(TAG, "New Trail is waiting to be updated");
            Log.i(TAG, "Offline trail count: " + count);
            for (int i = 0; count > i; i++) {
                Log.i(TAG, "Looping through Offline trail count: " + i);
                ModelTrails trail = db.GetOffLineTrail();
                ModelTrails saveTrail = new ModelTrails();
                saveTrail.SaveNewTrail(trail);
            }
        }
    }

    private void generateCommentNotification(Context context, JSONObject json) {
        String ObjectID = null;
        String Comment = null;
        String trailName = null;
        String userObjectId = null;

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            Comment = json != null ? json.getString("comment") : "";
            trailName = json != null ? json.getString("trailName") : "";
            userObjectId = json != null ? json.getString("userId") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!ParseUser.getCurrentUser().getObjectId().equals(userObjectId)) {

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
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setContentTitle(_Title)
                            .setContentText(Comment)
                            .setNumber(++numMessages);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);

            mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void generateStatusNotification(Context context, JSONObject json) {
        String ObjectID = null;
        String StatusUpdate = null;
        String userObjectId = null;

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            StatusUpdate = json != null ? json.getString("statusUpdate") : "";
            userObjectId = json != null ? json.getString("userId") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!ParseUser.getCurrentUser().getObjectId().equals(userObjectId)) {

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
                            .setSmallIcon(R.drawable.ic_stat_notification)
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
