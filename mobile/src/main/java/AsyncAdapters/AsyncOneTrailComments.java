package AsyncAdapters;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import models.ModelTrailComments;

public class AsyncOneTrailComments extends AsyncTask<List<ModelTrailComments>, Integer, List<ModelTrailComments>> {

    private final String TAG = "AsyncOneTrailComment";
    private String trailObectID;

    public AsyncOneTrailComments(String trailObectID) {
        this.trailObectID = trailObectID;
    }

    @Override
    protected void onPreExecute(){

    }

    @SafeVarargs
    @Override
    protected final List<ModelTrailComments> doInBackground(List<ModelTrailComments>... comments) {
        Log.i(TAG, "Doing the Async Fetch of the Trail Comments");
        final List<ModelTrailComments> passedComments = comments[0];
        ParseQuery<ParseObject> cQuery = ParseQuery.getQuery("Comments");
        cQuery.whereEqualTo("trailObjectId", trailObectID);
        cQuery.addDescendingOrder("workingCreatedDate");
        cQuery.fromLocalDatastore();
        try {
            List<ParseObject> list = cQuery.find();
            if (list != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
                for (ParseObject parseObject : list) {
                    // get the commentArray from the class
                    ModelTrailComments comment = new ModelTrailComments();
                    comment.TrailComments = parseObject.get("comment").toString();
                    comment.CommentUserName = parseObject.get("userName").toString();
                    if (parseObject.getDate("workingCreatedDate") != null)
                        comment.CommentDate = formatter.format(parseObject.getDate("workingCreatedDate"));
                    else
                        comment.CommentDate = "";
                    passedComments.add(comment);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return passedComments;
        }

    protected void onPostExecute(List<ModelTrailComments> passedComments) {
    }
}
