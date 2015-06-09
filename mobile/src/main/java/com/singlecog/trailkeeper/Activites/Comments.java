package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.singlecog.trailkeeper.R;

import java.util.List;

import AsyncAdapters.RecyclerViewAsyncCommentFeed;
import AsyncAdapters.RecyclerViewAsyncTrailComments;
import AsyncAdapters.RecyclerViewAsyncTrailInfo;
import RecyclerAdapters.DividerItemDecoration;
import RecyclerAdapters.RecyclerViewCommentFeedAdapter;
import RecyclerAdapters.RecyclerViewTrailListAdapter;
import models.ModelTrailComments;
import models.ModelTrails;

import static AsyncAdapters.RecyclerViewAsyncTrailComments.getTrailCommentData;
import static AsyncAdapters.RecyclerViewAsyncTrailInfo.getTrailOpenData;

public class Comments extends BaseActivity {

    private RecyclerView mTrailRecyclerView;
    private RecyclerViewTrailListAdapter mTrailAdapter;

    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewCommentFeedAdapter mTrailCommentAdapter;

    GestureDetectorCompat gestureDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_comments);
        super.onCreateDrawer();

        // sets the tap event on the recycler views
        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        // set up the RecyclerViews
        SetUpTrailListRecyclerView();
        SetUpCommentFeedRecyclerView();
    }

    // sets up the Trail Status Recycler View
    private void SetUpTrailListRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailRecyclerView = (RecyclerView) findViewById(R.id.recycler_choose_trail);
        mTrailRecyclerView.setLayoutManager(layoutManager);
        mTrailRecyclerView.setHasFixedSize(true);

        // call the async class to load the trail info data
        RecyclerViewAsyncTrailInfo trailInfo = new RecyclerViewAsyncTrailInfo();
        trailInfo.onCreate();
        List<ModelTrails> items = getTrailOpenData();
        mTrailAdapter = new RecyclerViewTrailListAdapter(items);
        mTrailRecyclerView.setAdapter(mTrailAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mTrailRecyclerView.addItemDecoration(itemDecoration);

        mTrailRecyclerView.setItemAnimator(new DefaultItemAnimator());

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

    // sets up the trail comment recycler view
    private void SetUpCommentFeedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailCommentRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_comments_feed);
        mTrailCommentRecyclerView.setLayoutManager(layoutManager);
        mTrailCommentRecyclerView.setHasFixedSize(true);

        // call the async class to load the trail info data
        RecyclerViewAsyncCommentFeed trailInfo = new RecyclerViewAsyncCommentFeed();
        trailInfo.onCreate();
        List<ModelTrailComments> items = getTrailCommentData();
        mTrailCommentAdapter = new RecyclerViewCommentFeedAdapter(items);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mTrailCommentRecyclerView.addItemDecoration(itemDecoration);

        mTrailCommentRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
