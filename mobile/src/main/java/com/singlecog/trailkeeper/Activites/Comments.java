package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import AsyncAdapters.AsyncTrailComments;
import AsyncAdapters.AsyncTrailInfo;
import RecyclerAdapters.RecyclerViewCommentFeedAdapter;
import RecyclerAdapters.RecyclerViewTrailListAdapter;
import models.ModelTrailComments;
import models.ModelTrails;

public class Comments extends BaseActivity {

    private RecyclerView mTrailRecyclerView;
    private RecyclerViewTrailListAdapter mTrailAdapter;

    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewCommentFeedAdapter mTrailCommentAdapter;

    GestureDetectorCompat gestureDetector;

    private List<ModelTrails> trails;
    private List<ModelTrailComments> comments;
    private final Context context = this;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        super.onCreateDrawer();

        // sets the tap event on the recycler views
        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        // calls the AsyncTask for the Trails
        try {
            AsyncTrailInfo ati = new AsyncTrailInfo(this, context);
            trails = new ArrayList<>();
            ati.execute(trails);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // calls the AsyncTask for the comments
        try {
            AsyncTrailComments atc = new AsyncTrailComments(this, context);
            comments = new ArrayList<>();
            atc.execute(comments);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // set up the RecyclerViews
        SetUpTrailListCard();
        SetUpCommentFeedCard();
    }

    // sets up the Trail Status Recycler View
    private void SetUpTrailListCard(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailRecyclerView = (RecyclerView) findViewById(R.id.recycler_choose_trail);
        mTrailRecyclerView.setLayoutManager(layoutManager);
        mTrailRecyclerView.setHasFixedSize(true);
    }

    // sets up the trail comment recycler view
    private void SetUpCommentFeedCard(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailCommentRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_comments_feed);
        mTrailCommentRecyclerView.setLayoutManager(layoutManager);
        mTrailCommentRecyclerView.setHasFixedSize(true);
    }

    public void SetUpTrailRecyclerView() {
        mTrailAdapter = new RecyclerViewTrailListAdapter(trails);
        mTrailRecyclerView.setAdapter(mTrailAdapter);

        // sets the touch listener
        mTrailRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO Set the comment feed to all trails or one trail
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(Comments.this, "Trail Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, TrailScreen.class);
//                    startActivity(intent);

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void SetUpCommentsRecyclerView() {
        mTrailCommentAdapter = new RecyclerViewCommentFeedAdapter(comments);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);

        mTrailCommentRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(Comments.this, "Comment Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }
}
