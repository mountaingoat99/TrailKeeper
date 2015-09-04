package AsyncAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Helpers.ConnectionDetector;
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
                // adding these to shared preferences instead
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("hasNewTrailWaiting", true);
                editor.putBoolean("skillEasy", newTrail.getIsEasy());
                editor.putBoolean("skillMedium", newTrail.getIsMedium());
                editor.putBoolean("skillHard", newTrail.getIsHard());
                editor.putString("trailName", newTrail.getTrailName());
                editor.putString("city", newTrail.getTrailCity());
                editor.putString("state", newTrail.getTrailState());
                editor.putString("country", newTrail.getTrailCountry());
                editor.putFloat("length", (float) newTrail.getLength());
                editor.putFloat("latitude", (float) newTrail.getLocation().latitude);
                editor.putFloat("longitude", (float) newTrail.getLocation().longitude);
                editor.putInt("status", newTrail.getTrailStatus());
                editor.apply();
                Log.i(TAG, "Saving New Trail later");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Saving New Trail Failed");
        }


        return null;
    }
}
