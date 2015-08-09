package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MyCustomReceiver extends BroadcastReceiver {

    private static final String TAG = "MyCustomReciever";
    public static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.Parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.i(TAG, " got action " + action + " on channel " + channel);

            if (action.equalsIgnoreCase("com.singlecog.trailkeeper.NEW_NOTIF")) {
                String title = "TrailKeeper";
                generateNotification(context, title, json);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateNotification(Context context, String title, JSONObject json) {
        String ObjectID = null;
        Integer TrailID = 0;
        String Comment = null;
        String trailName = null;

        try {
            ObjectID = json != null ? json.getString("trailObjectId") : "";
            TrailID = json != null ? json.getInt("trailId") : 0;
            Comment = json != null ? json.getString("comment") : "";
            trailName = json != null ? json.getString("trailName") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(context, TrailScreen.class);
        Bundle b = new Bundle();
        b.putInt("trailID", TrailID);
        b.putString("objectID", ObjectID);
        intent.putExtras(b);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        numMessages = 0;
        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(trailName + " has a new comment:" + "\n" + Comment)
                        .setNumber(++numMessages);

        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
