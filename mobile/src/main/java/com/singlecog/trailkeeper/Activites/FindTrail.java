package com.singlecog.trailkeeper.Activites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    private List<String> trailNames;
    private Set<String> states;
    private View view;
    private Dialog searchDialog;
    //global unit boolean, shared preference imperial or not by Anatoliy
    private boolean globalUnitDefault;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trail);
        view = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(view, this);

        // Google AdMob
        adView = (AdView) findViewById(R.id.adView);
        // comment out for testing
        AdRequest adRequest = new AdRequest.Builder().build();
        // Comment out for Production
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("TEST_DEVICE_ID")
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                //.addTestDevice("9C75E9349CF38EF5EB2C6C6100E96A7E") // nexus 7
//                .build();
        // always call this
        adView.loadAd(adRequest);

        btnSearch = (FloatingActionButton)findViewById(R.id.search_fab);

        trailNames = ModelTrails.GetTrailNames();

        setUpdateRecyclerView();
        states = ModelTrails.GetTrailStates();
        SetUpStateRecyclerView();
        SetUpOnClickForFab();
        //by Anatoliy
        loadSavedPreferences();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_find_trail, menu);
        return true;
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
                    ModelTrails trails = ModelTrails.GetTrailObjectIDs(searchForTrailEditText.getText().toString().trim());
                    SendToTrailScreen(trails);
                } else {
                    Snackbar.make(v, "Please enter a Trail Name", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        searchDialog.show();
    }

    private void SendToTrailScreen(ModelTrails trails) {
        if (!trails.getObjectID().isEmpty()) {
            searchDialog.dismiss();
            Intent intent = new Intent(context, TrailScreen.class);
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

    public void SetUpStateRecyclerView() {
        List<String> trailStates = new ArrayList<>();
        // convert the set to a List for the Recycler Adapter
        trailStates.addAll(states);
        RecyclerViewFindTrailByState mFindTrailByStateAdapter = new RecyclerViewFindTrailByState(trailStates, context, globalUnitDefault);
        mFindTrailByStateRecyclerView.setAdapter(mFindTrailByStateAdapter);
        mFindTrailByStateRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //by Anatoliy
    private void loadSavedPreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //by Anatoliy
        globalUnitDefault = sp.getBoolean("Imperial", true);
    }
}
