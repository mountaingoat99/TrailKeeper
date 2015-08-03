package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

import AsyncAdapters.AsyncTrailInfo;
import RecyclerAdapters.RecyclerViewFindTrailByState;
import RecyclerAdapters.RecylerViewFindTrailInState;
import models.ModelTrails;

public class FindTrail extends BaseActivity {

    private final Context context = this;
    private RecyclerView mFindTrailByStateRecyclerView;
    private RecyclerViewFindTrailByState mFindTrailByStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trail);
        super.onCreateDrawer();

        setUpdateRecyclerView();
        ModelTrails modelTrails = new ModelTrails(context, this);
        modelTrails.GetTrailStates(mFindTrailByStateRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_find_trail, menu);
        return true;
    }

    private void setUpdateRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mFindTrailByStateRecyclerView = (RecyclerView)findViewById(R.id.find_trail_by_state_recycler_view);
        mFindTrailByStateRecyclerView.setLayoutManager(layoutManager);
        mFindTrailByStateRecyclerView.setHasFixedSize(true);
    }

    public void SetUpStateRecyclerView(Set<String> states, Context passedContext, RecyclerView RecyclerView) {
        List<String> trailStates = new ArrayList<>();
        // convert the set to a List for the Recycler Adapter
        trailStates.addAll(states);
        mFindTrailByStateAdapter = new RecyclerViewFindTrailByState(trailStates, passedContext);
        RecyclerView.setAdapter(mFindTrailByStateAdapter);
        RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
