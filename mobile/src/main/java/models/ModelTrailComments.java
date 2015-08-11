package models;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.Activites.TrailScreen;
import com.singlecog.trailkeeper.Activites.AllComments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public AllComments allCommentScreen;

    public ModelTrailComments()
    {

    }

    public ModelTrailComments(Context context, TrailScreen trailScreen) {
        this.context = context;
        this.trailScreen = trailScreen;
    }

    public ModelTrailComments(Context context, AllComments allComments) {
        this.allCommentScreen = allComments;
        this.context = context;
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

    public void GetAllComments() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.fromLocalDatastore();
        query.addDescendingOrder("workingCreatedDate");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<ModelTrailComments> comments = new ArrayList<>();
                if (e == null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
                    for (ParseObject parseObject : list) {
                        ModelTrailComments comment = new ModelTrailComments();
                        comment.TrailName = parseObject.get("trailName").toString();
                        comment.TrailComments = parseObject.get("comment").toString();
                        if(parseObject.getDate("workingCreatedDate") != null)
                            comment.CommentDate = formatter.format(parseObject.getDate("workingCreatedDate"));
                        else
                            comment.CommentDate = "";
                        comment.CommentUserName = parseObject.get("userName").toString();
                        comments.add(comment);
                    }
                    allCommentScreen.ReceiveCommentList(comments);
                    allCommentScreen.SetUpCommentView();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
    //endregion

    public void CreateNewComment(String trailObjectId, final String trailName, String Comment) {

        Calendar c = Calendar.getInstance();

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
