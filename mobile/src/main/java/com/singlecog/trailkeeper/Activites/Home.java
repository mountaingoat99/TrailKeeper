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

import com.singlecog.trailkeeper.R;

import java.util.List;

import RecyclerAdapters.DividerItemDecoration;
import RecyclerAdapters.RecyclerViewTrailOpenClosedAdapter;
import models.ModelOpenClosedTrails;

import static RecyclerAdapters.RecyclerViewAsyncTrailInfo.*;

public class Home extends BaseActivity {

    private RecyclerView mTrailOpenRecyclerView;
    private RecyclerViewTrailOpenClosedAdapter mTrailOpenAdapter;

    GestureDetectorCompat gestureDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onCreateDrawer();

        mTrailOpenRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_updates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mTrailOpenRecyclerView.setLayoutManager(layoutManager);
        mTrailOpenRecyclerView.setHasFixedSize(true);

        List<ModelOpenClosedTrails> items = getDemoData();
        mTrailOpenAdapter = new RecyclerViewTrailOpenClosedAdapter(items);
        mTrailOpenRecyclerView.setAdapter(mTrailOpenAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mTrailOpenRecyclerView.addItemDecoration(itemDecoration);

        mTrailOpenRecyclerView.setItemAnimator(new DefaultItemAnimator());

        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        mTrailOpenRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(Home.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

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
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}
