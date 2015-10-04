package AsyncAdapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import Helpers.ConnectionDetector;
import database.TrailCommentDatabase;
import models.ModelTrailComments;

public class AsyncSaveOfflineComments extends AsyncTask<String, Void, String> {

    private Context context;
    private String objectID;
    private String trailName;
    private String comment;

    public AsyncSaveOfflineComments(Context context, String objectID, String trailName, String comment) {
        this.context = context;
        this.objectID = objectID;
        this.trailName = trailName;
        this.comment = comment;
    }

    @Override
    protected String doInBackground(String... params) {
        String TAG = "AsyncSaveOLComments";
        try {
            ConnectionDetector cd = new ConnectionDetector(context);
            if (cd.isConnectingToInternet()) {
                // save the comment right now
                Log.i(TAG, "Saving new Comment Right Away");
                ModelTrailComments modelTrailComments = new ModelTrailComments();
                modelTrailComments.CreateNewComment(objectID, trailName, comment);
            } else {
                // save the comment to the database until we get a connection
                Log.i(TAG, "Saving new Comment To Database, Currently Offline");
                TrailCommentDatabase db = new TrailCommentDatabase(context);
                ModelTrailComments trailComments = new ModelTrailComments();
                trailComments.setObjectID(objectID);
                trailComments.setTrailName(trailName);
                trailComments.setTrailComments(comment);
                db.AddNewComment(trailComments);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Saving new Comment failed");
        }
        return null;
    }
}
