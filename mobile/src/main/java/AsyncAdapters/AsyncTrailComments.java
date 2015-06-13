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

import models.ModelTrailComments;

public class AsyncTrailComments extends AsyncTask<List<ModelTrailComments>, Integer, List<ModelTrailComments>> {

    ProgressDialog dialog;
    private Home homeActivity;
    private Comments commentActivity;
    private Context context;

    public AsyncTrailComments(Home activity, Context context){
        this.homeActivity = activity;
        this.context = context;
    }

    public AsyncTrailComments(Comments activity, Context context) {
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
    protected final List<ModelTrailComments> doInBackground(List<ModelTrailComments>... comments) {
        Parse.initialize(context, "uU8JsEF9eLEYcFzUrwqmrWzblj65IoQ0G6S4DkI8", "4S7u2tedpm9yeE6DR3J6mDyJHHpgmUgktu6Q6QvD");
        final List<ModelTrailComments> passedComments = comments[0];

        ParseQuery<ParseObject> cQuery = ParseQuery.getQuery("comments");
        cQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : list) {
                        ModelTrailComments comment = new ModelTrailComments();
                        comment.TrailName = parseObject.get("TrailName").toString();
                        comment.TrailComments = parseObject.get("comment").toString();

                        passedComments.add(comment);
                    }
                    if (homeActivity != null)
                        homeActivity.SetUpTrailCommentRecyclerView();
                    if (commentActivity != null)
                        commentActivity.SetUpCommentsRecyclerView();
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
