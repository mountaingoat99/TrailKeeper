package AsyncAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.Activites.MapActivity;

import java.util.List;

import models.ModelTrails;


public class AsyncTrailLocations extends AsyncTask<List<ModelTrails>, Integer, List<ModelTrails>> {

    ProgressDialog dialog;
    private MapActivity mapActivity;
    private Context context;
    private LatLng currentLocation;

    public AsyncTrailLocations(MapActivity mapActivity, Context context, LatLng currentLocation) {
        this.mapActivity = mapActivity;
        this.context = context;
        this.currentLocation = currentLocation;
    }

    @SafeVarargs
    @Override
    protected final List<ModelTrails> doInBackground(List<ModelTrails>... trails) {
        final List<ModelTrails> passedTrails = trails[0];
        ParseQuery<ParseObject> tQuery = ParseQuery.getQuery("Trails");
        tQuery.fromLocalDatastore();
        tQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : list) {
                        ModelTrails trail = new ModelTrails();
                        trail.ObjectID = parseObject.getObjectId();
                        trail.TrailID = parseObject.getInt("trailId");
                        trail.TrailName = parseObject.get("trailName").toString();
                        trail.TrailStatus = Integer.valueOf(parseObject.get("status").toString());
                        trail.GeoLocation = parseObject.getParseGeoPoint("geoLocation");

                        passedTrails.add(trail);
                    }
                } else {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(context, "Please Check your connection and refresh", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return passedTrails;
    }

    @Override
    protected void onPreExecute(){
        dialog = new ProgressDialog(context);
        dialog.setTitle("Updating Trails...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    protected void onPostExecute(List<ModelTrails> passedTrails) {
        dialog.dismiss();
    }
}
