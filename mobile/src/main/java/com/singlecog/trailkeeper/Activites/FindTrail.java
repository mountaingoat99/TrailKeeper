package com.singlecog.trailkeeper.Activites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.singlecog.trailkeeper.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import RecyclerAdapters.RecyclerViewFindTrailByState;
import models.ModelTrails;

public class FindTrail extends BaseActivity {

    private final String LOG = "FindTrail";
    private final Context context = this;
    private FloatingActionButton btnSearch;
    private RecyclerView mFindTrailByStateRecyclerView;
    private RecyclerViewFindTrailByState mFindTrailByStateAdapter;
    private List<String> trailNames;
    private ModelTrails modelTrails;
    private View view;
    private Dialog searchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trail);
        view = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(view);
        btnSearch = (FloatingActionButton)findViewById(R.id.search_fab);

        // call to get the trail names first
        modelTrails = new ModelTrails(context, this);
        modelTrails.GetTrailNames();

        setUpdateRecyclerView();
        modelTrails.GetTrailStates(mFindTrailByStateRecyclerView);
        SetUpOnClickForFab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_find_trail, menu);
        return true;
    }

    public void RecieveTrailNames(List<String> trails) {
        trailNames = trails;
    }

    private void SetUpOnClickForFab() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog();
            }
        });
    }

    private void SearchDialog() {
        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.dialog_search_for_trail);
        final AutoCompleteTextView searchForTrailEditText = (AutoCompleteTextView)searchDialog.findViewById(R.id.edittext_search);
        Button btnGo = (Button)searchDialog.findViewById(R.id.btn_go_trail_home);

        // set the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trailNames);
        searchForTrailEditText.setAdapter(adapter);
        searchForTrailEditText.setThreshold(1);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchForTrailEditText.getText().length() > 0) {
                    modelTrails.GetTrailIDs(searchForTrailEditText.getText().toString().trim(), context);
                } else {
                    Snackbar.make(v, "Please enter a Trail Name", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        searchDialog.show();
    }

    public void SendToTrailScreen(ModelTrails trails, Context context) {
        if (trails.TrailID > 0) {
            searchDialog.dismiss();
            Intent intent = new Intent(context, TrailScreen.class);
            intent.putExtra("trailID", trails.getTrailID());
            intent.putExtra("objectID", trails.getObjectID());
            context.startActivity(intent);
        } else {
            Snackbar.make(view, "Trail does not exist", Snackbar.LENGTH_LONG).show();
        }
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
