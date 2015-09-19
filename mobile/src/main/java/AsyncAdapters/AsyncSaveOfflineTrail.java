package AsyncAdapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import Helpers.ConnectionDetector;
import database.NewTrailDatabase;
import models.ModelTrails;

public class AsyncSaveOfflineTrail extends AsyncTask<String, Void, String> {

    private final String TAG = "AsyncSaveOfflineTrail";
    private ModelTrails newTrail;
    private Context context;

    public AsyncSaveOfflineTrail(Context context, ModelTrails newTrail) {
        this.newTrail = newTrail;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // lets check if we have a current connection
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                // if we have it, we will save it now
                ModelTrails modelTrails = new ModelTrails();
                modelTrails.SaveNewTrail(newTrail);
                Log.i(TAG, "Saving New Trail immediately");
            } else {
                NewTrailDatabase db = new NewTrailDatabase(context);
                db.AddNewTrail(newTrail);
                Log.i(TAG, "Saving New Trail later");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Saving New Trail Failed");
        }
        return null;
    }
}
