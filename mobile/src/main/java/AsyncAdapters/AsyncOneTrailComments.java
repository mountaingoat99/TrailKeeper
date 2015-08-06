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

import java.util.List;
import java.util.Objects;

import models.ModelTrailComments;

public class AsyncOneTrailComments extends AsyncTask<List<ModelTrailComments>, Integer, List<ModelTrailComments>> {

    ProgressDialog dialog;
    private TrailScreen activity;
    private Context context;
    private int mTrailID;

    public AsyncOneTrailComments(TrailScreen activity, Context context, int mTrailID) {
        this.activity = activity;
        this.context = context;
        this.mTrailID = mTrailID;
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

        ParseQuery<ParseObject> cQuery = ParseQuery.getQuery("Trails");
        cQuery.fromLocalDatastore();
        cQuery.whereEqualTo("trailId", mTrailID);
        cQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null) {
                        for (ParseObject parseObject : list) {
                            // get the commentArray from the class
                            List<Object> commentList = parseObject.getList("comments");
                            List<Object> commentDateList = parseObject.getList("commentDate");
                            int count = 0;
                            for (Object cList : commentList){
                                ModelTrailComments comment = new ModelTrailComments();
                                comment.TrailComments = cList.toString();
                                comment.CommentDate = commentDateList.get(count).toString();
                                // add it to the list
                                passedComments.add(comment);
                                count++;
                            }
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
