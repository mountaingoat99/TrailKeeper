package models;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.TrailScreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Helpers.PushNotificationHelper;
import ParseObjects.ParseComments;

public class ModelTrailComments {

    private static final String LOG = "ModelTrailComment";
    private String objectID;
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

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getTrailName() {
        return TrailName;
    }

    public void setTrailName(String trailName) {
        TrailName = trailName;
    }

    public String getTrailComments() {
        return TrailComments;
    }

    public void setTrailComments(String trailComments) {
        TrailComments = trailComments;
    }

    public String getCommentDate() {
        return CommentDate;
    }

    public void setCommentDate(String commentDate) {
        CommentDate = commentDate;
    }

    public String getCommentUserName() {
        return CommentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        CommentUserName = commentUserName;
    }

    //Region Static Methods
    public static List<ModelTrailComments> GetCommentsByUser(String userObjectID) {
        List<ModelTrailComments> comments = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("userObjectId", userObjectID);
        query.addDescendingOrder("workingCreatedDate");
        query.fromLocalDatastore();
        try {
            List<ParseObject> list = query.find();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
            for (ParseObject parseObject : list) {
                ModelTrailComments comment = new ModelTrailComments();
                comment.TrailName = parseObject.get("trailName").toString();
                comment.TrailComments = parseObject.get("comment").toString();
                if (parseObject.getDate("workingCreatedDate") != null)
                    comment.CommentDate = formatter.format(parseObject.getDate("workingCreatedDate"));
                else
                    comment.CommentDate = "";
                comment.CommentUserName = parseObject.get("userName").toString();
                comments.add(comment);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static List<ModelTrailComments> GetCommentsByTrail(String trailObjectID) {
        List<ModelTrailComments> comments = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("trailObjectId", trailObjectID);
        query.fromLocalDatastore();
        query.addDescendingOrder("workingCreatedDate");
        try {
            List<ParseObject> list = query.find();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
            for (ParseObject parseObject : list) {
                ModelTrailComments comment = new ModelTrailComments();
                comment.TrailName = parseObject.get("trailName").toString();
                comment.TrailComments = parseObject.get("comment").toString();
                if (parseObject.getDate("workingCreatedDate") != null)
                    comment.CommentDate = formatter.format(parseObject.getDate("workingCreatedDate"));
                else
                    comment.CommentDate = "";
                comment.CommentUserName = parseObject.get("userName").toString();
                comments.add(comment);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static List<ModelTrailComments> GetAllComments() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        List<ModelTrailComments> comments = new ArrayList<>();
        query.fromLocalDatastore();
        query.addDescendingOrder("workingCreatedDate");
        try {
            List<ParseObject> list = query.find();
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comments;
    }
    //endregion

    public void CreateNewComment(String trailObjectId, final String trailName, String Comment) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
        try {
            ModelTrailComments modelTrailComments = new ModelTrailComments(); // need to send this object back to update the recycler view

            ParseComments parseComments = new ParseComments();
            parseComments.put("trailObjectId", trailObjectId);
            parseComments.put("trailName", trailName);
            parseComments.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
            parseComments.put("userName", ParseUser.getCurrentUser().getUsername());
            modelTrailComments.CommentUserName = ParseUser.getCurrentUser().getUsername();  //Recycler View Update
            parseComments.put("comment", Comment);
            modelTrailComments.TrailComments = Comment;                                     //Recycler View Update
            parseComments.put("workingCreatedDate", c.getTime());
            modelTrailComments.CommentDate = formatter.format(c.getTime());
            parseComments.saveInBackground();
            Log.i(LOG, "Save Comment Completed");
            parseComments.pinInBackground();
            //PushNotificationHelper.SendOutAPushNotificationForNewComment(trailName, Comment, trailObjectId);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG, "Save Comment Failed" + e.getMessage());
        }
    }
}
