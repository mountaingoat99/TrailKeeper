package AsyncAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import models.ModelTrailComments;

public class AsyncOneTrailComments extends AsyncTask<List<ModelTrailComments>, Integer, List<ModelTrailComments>> {

    ProgressDialog dialog;
    private TrailScreen activity;
    private Context context;
    private String trailObectID;

    public AsyncOneTrailComments(TrailScreen activity, Context context, String trailObectID) {
        this.activity = activity;
        this.context = context;
        this.trailObectID = trailObectID;
    }

    @Override
    protected void onPreExecute(){
        dialog = new ProgressDialog(context);
        dialog.setTitle("Updating Trail info...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @SafeVarargs
    @Override
    protected final List<ModelTrailComments> doInBackground(List<ModelTrailComments>... comments) {
        final List<ModelTrailComments> passedComments = comments[0];

        ParseQuery<ParseObject> cQuery = ParseQuery.getQuery("Comments");
        cQuery.whereEqualTo("trailObjectId", trailObectID);
        cQuery.addDescendingOrder("workingCreatedDate");
        cQuery.fromLocalDatastore();
        cQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
                        for (ParseObject parseObject : list) {
                            // get the commentArray from the class
                            ModelTrailComments comment = new ModelTrailComments();
                            comment.TrailComments = parseObject.get("comment").toString();
                            comment.CommentDate = formatter.format(parseObject.getDate("workingCreatedDate"));
                            passedComments.add(comment);
                        }
                        if (activity != null)
                            activity.SetUpTrailCommentRecyclerView();
                    }
                } else {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(context, "Please Check your connection and refresh", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return passedComments;
    }


    protected void onPostExecute(List<ModelTrailComments> passedComments) {
        dialog.dismiss();
    }
}
