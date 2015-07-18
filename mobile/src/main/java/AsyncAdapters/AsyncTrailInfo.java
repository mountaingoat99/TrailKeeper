package AsyncAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.AvoidXfermode;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.Activites.Comments;
import com.singlecog.trailkeeper.Activites.HomeScreen;

import java.util.Iterator;
import java.util.List;

import Helpers.GeoLocationHelper;
import ParseObjects.ParseTrails;
import models.ModelTrails;

public class AsyncTrailInfo extends AsyncTask<List<ModelTrails>, Integer, List<ModelTrails>> {

    ProgressDialog dialog;
    private Comments commentActivity;
    private HomeScreen homeScreenActivity;
    private Context context;

    public AsyncTrailInfo(Comments activity, Context context){
        this.commentActivity = activity;
        this.context = context;
    }

    public AsyncTrailInfo(HomeScreen activity, Context context){
        this.homeScreenActivity = activity;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
//        dialog = new ProgressDialog(context);
//        dialog.setTitle("Updating Trails...");
//        dialog.setMessage("Please wait...");
//        dialog.setIndeterminate(true);
//        dialog.show();
    }

    @SafeVarargs
    @Override
    protected final List<ModelTrails> doInBackground(List<ModelTrails>... trails) {
        final List<ModelTrails> passedTrails = trails[0];

        //Parse
        ParseQuery<ParseObject> tQuery = ParseQuery.getQuery("trails");
        tQuery.fromLocalDatastore();
        tQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : list) {
                        ModelTrails trail = new ModelTrails();
                        trail.ObjectID = parseObject.getObjectId();
                        trail.TrailID = parseObject.getInt("TrailID");
                        trail.TrailName = parseObject.get("TrailName").toString();
                        trail.TrailStatus = Integer.valueOf(parseObject.get("Status").toString());
                        trail.TrailState = parseObject.get("State").toString();
                        trail.TrailCity = parseObject.get("City").toString();
                        trail.GeoLocation = parseObject.getParseGeoPoint("GeoLocation");

                        passedTrails.add(trail);
                    }
                    if (homeScreenActivity != null)
                        homeScreenActivity.SetUpTrailStatusRecyclerView();
                    if (commentActivity != null)
                        commentActivity.SetUpTrailRecyclerView();
                } else {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(context, "Please Check your connection and refresh", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return passedTrails;
    }

    protected void onPostExecute(List<ModelTrails> passedTrails) {
        //dialog.dismiss();
    }
}
