package models;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ParseObjects.ParseComments;

public class ModelTrailComments {

    private static final String LOG = "ModelTrailComment";
    public String TrailName;
    public String TrailComments;
    public String CommentDate;
    public String CommentUserName;
    public Context context;
    public TrailScreen trailScreen;

    public ModelTrailComments()
    {

    }

    public ModelTrailComments(Context context, TrailScreen trailScreen) {
        this.context = context;
        this.trailScreen = trailScreen;
    }

    //Region Static Methods
    private static List<ParseObject> GetCommentsByUser(String userObjectID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("userObjectId", userObjectID);
        query.addDescendingOrder("workingCreatedDate");
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<ParseObject> GetCommentsByTrail(String trailObjectID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("trailObjectId", trailObjectID);
        query.addDescendingOrder("workingCreatedDate");
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    public void CreateNewComment(String trailObjectId, final String trailName, String Comment) {

        Calendar c = Calendar.getInstance();
        //Date df = new Date(String.valueOf(c.getTime()));
        //String formattedDate = df.format(c.getTime());

        final ParseComments parseComments = new ParseComments();
        parseComments.put("trailObjectId", trailObjectId);
        parseComments.put("trailName", trailName);
        parseComments.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
        parseComments.put("userName", ParseUser.getCurrentUser().getUsername());
        parseComments.put("comment", Comment);
        parseComments.put("workingCreatedDate", c.getTime());
        parseComments.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "Save Comment Completed");
                    parseComments.pinInBackground();
                    trailScreen.SaveCommentWasSuccessful(true);
                } else {
                    Log.i(LOG, "Save Comment Failed" + e.getMessage());
                    trailScreen.SaveCommentWasSuccessful(false);
                }
            }
        });
    }
}
