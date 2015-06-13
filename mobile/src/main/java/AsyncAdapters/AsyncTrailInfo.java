package AsyncAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.Activites.Comments;
import com.singlecog.trailkeeper.Activites.Home;

import java.util.List;

import models.ModelTrails;

public class AsyncTrailInfo extends AsyncTask<List<ModelTrails>, Integer, List<ModelTrails>> {

    ProgressDialog dialog;
    private Home homeActivity;
    private Comments commentActivity;
    private Context context;

    public AsyncTrailInfo(Home activity, Context context){
        this.homeActivity = activity;
        this.context = context;
    }

    public AsyncTrailInfo(Comments activity, Context context){
        this.commentActivity = activity;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        dialog = new ProgressDialog(context);
        dialog.setTitle("Updating Trails...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @SafeVarargs
    @Override
    protected final List<ModelTrails> doInBackground(List<ModelTrails>... trails) {
        Parse.initialize(context, "uU8JsEF9eLEYcFzUrwqmrWzblj65IoQ0G6S4DkI8", "4S7u2tedpm9yeE6DR3J6mDyJHHpgmUgktu6Q6QvD");
        final List<ModelTrails> passedTrails = trails[0];

        //Parse
        ParseQuery<ParseObject> tQuery = ParseQuery.getQuery("trails");
        tQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : list) {
                        ModelTrails trail = new ModelTrails();
                        trail.TrailName = parseObject.get("TrailName").toString();
                        trail.TrailStatus = Integer.valueOf(parseObject.get("Status").toString());
                        trail.TrailState = parseObject.get("State").toString();

                        passedTrails.add(trail);
                    }
                    if (homeActivity != null)
                        homeActivity.SetUpTrailStatusRecyclerView();
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
        dialog.dismiss();
    }
}
