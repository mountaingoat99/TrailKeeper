package AsyncAdapters;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import Helpers.ConnectionDetector;
import models.ModelTrails;

public class AsyncSaveOfflineStatus extends AsyncTask<String, Void, String> {

    private final String TAG = "AsyncSaveOfflineStatus";
    private ModelTrails newStatus;
    private Context context;
    private String objectID;
    private int choice;

    public AsyncSaveOfflineStatus(Context context, ModelTrails newStatus, String objectId, int choice) {
        this.newStatus = newStatus;
        this.context = context;
        this.objectID = objectId;
        this.choice = choice;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                // save the status right now
                ModelTrails modelTrails = new ModelTrails();
                modelTrails.UpdateTrailStatus(context, objectID, choice);
            } else {
                // save the status to the database until we get a connection
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Saving new Status failed");
        }
        return null;
    }
}
