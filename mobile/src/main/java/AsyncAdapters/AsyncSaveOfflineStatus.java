package AsyncAdapters;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import Helpers.ConnectionDetector;
import database.TrailStatusDatabase;
import models.ModelTrailStatus;
import models.ModelTrails;

public class AsyncSaveOfflineStatus extends AsyncTask<String, Void, String> {

    private final String TAG = "AsyncSaveOfflineStatus";
    private Context context;
    private String objectID;
    private int choice;
    private String trailName;

    public AsyncSaveOfflineStatus(Context context, String objectId, int choice, String trailName) {
        this.context = context;
        this.objectID = objectId;
        this.choice = choice;
        this.trailName = trailName;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                // save the status right now
                Log.i(TAG, "Saving new Status Right Away");
                ModelTrails modelTrails = new ModelTrails();
                modelTrails.UpdateTrailStatus(objectID, choice, trailName);
            } else {
                // save the status to the database until we get a connection
                Log.i(TAG, "Saving new Status to Database, Currently Offline");
                TrailStatusDatabase db = new TrailStatusDatabase(context);
                ModelTrailStatus modelTrailStatus = new ModelTrailStatus();
                modelTrailStatus.setObjectID(objectID);
                modelTrailStatus.setChoice(choice);
                modelTrailStatus.setTrailName(trailName);
                db.AddNewStatus(modelTrailStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Saving new Status failed");
        }
        return null;
    }
}
