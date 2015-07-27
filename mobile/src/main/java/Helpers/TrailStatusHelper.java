package Helpers;

import android.content.Context;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import models.ModelTrails;

public class TrailStatusHelper {

    private static final String LOG = "TrailStatusHelper";
    private TrailScreen trailScreenActivity;
    private Context context;

    public TrailStatusHelper (Context context, TrailScreen trailScreenActivity) {
        this.trailScreenActivity = trailScreenActivity;
        this.context = context;
    }

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

    private void TrailStatusUpdateSuccessful(boolean valid, String message) {
        if (valid) {
            trailScreenActivity.TrailStatusUpdateWasSuccessful(valid, null);
        } else {
            trailScreenActivity.TrailStatusUpdateWasSuccessful(valid, message);
        }
    }

}
