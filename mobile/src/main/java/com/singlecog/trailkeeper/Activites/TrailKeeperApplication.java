package com.singlecog.trailkeeper.Activites;

import android.app.Application;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;


import AsyncAdapters.AsyncLoadAllFromParse;
import ParseObjects.ParseTrails;

public class TrailKeeperApplication extends Application {

//    public static final String TRAILKEEPER_GROUP_NAME = "ALL_TRAILS";
//    public static final String TAG = "TrailKeeperApplication";
//    private static ParseComments comments;
//    private static ParseTrails trails;

    @Override
    public void onCreate(){
        super.onCreate();

        // add the Parse Subclasses
        ParseObject.registerSubclass(ParseTrails.class);
        //ParseObject.registerSubclass(ParseComments.class);

        // Enable the Local Datastore
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, getResources().getString(R.string.ApplicationID), getResources().getString(R.string.ClientKEey));
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        //LoadTestCommentsToParse();

        LoadAllFromParse();
    }
    private void LoadAllFromParse() {
        try {
            AsyncLoadAllFromParse load = new AsyncLoadAllFromParse();
            load.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadTestCommentsToParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("trails");

        // Retrieve the object by id
        query.getInBackground("eheoC2nCHr", new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.addUnique("Comments", "Too Much Rain");
                    gameScore.saveInBackground();
                }
            }
        });
    }
}
