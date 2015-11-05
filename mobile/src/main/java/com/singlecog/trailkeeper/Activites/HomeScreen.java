package com.singlecog.trailkeeper.Activites;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;
import java.util.List;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import AsyncAdapters.AsyncAdapterLoadAllFromParse.AsyncRefreshCurrentUser;
import Helpers.AlertDialogHelper;
import Helpers.CreateAccountHelper;
import Helpers.GeoLocationHelper;
import RecyclerAdapters.RecyclerViewHomeScreenAdapter;
import models.ModelTrails;

import static android.support.v7.widget.RecyclerView.*;

public class HomeScreen extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = "HomeScreen";
    private SwipeRefreshLayout mSwipeLayout;
    private final Context context = this;
    private RecyclerView mTrailOpenRecyclerView;
    private RecyclerViewHomeScreenAdapter mTrailOpenAdapter;
    private List<ModelTrails> trails;
    // this will let shared preference know if it needs to take longer for the first time load and
    // if we need to ask them to create an account for the first time
    private boolean firstTimeLoad = true;
    //global unit boolean, shared preference imperial or not by Anatoliy
    private boolean globalUnitDefault;

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_home_screen);
        super.onCreateDrawer(mSwipeLayout, this);

        Bundle bundle;
        if (savedInstanceState != null)
            bundle = savedInstanceState;

        loadSavedPreferences();

        // set up the swipe pull to refresh
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.accent,
                R.color.accent, R.color.accent,
                R.color.accent);

        // show the refresh spinner on load
        if (TrailKeeperApplication.home == null) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            mSwipeLayout.setProgressViewOffset(false, -50, height / 8);
            mSwipeLayout.setRefreshing(true);
        }

        // set up the RecyclerViews
        SetUpTrailStatusCard();
        trails = ModelTrails.GetAllTrailInfo();
        SetUpTrailStatusRecyclerView();

        Bundle b = getIntent().getExtras();
        if (b != null){
            View v = mTrailOpenRecyclerView;
            String userName = b.getString("userName");
            if (getIntent().hasExtra("className")) {
                String className = b.getString("className") ;
                if (className != null && className.equals("CreateAccount")) {
                    VerifyEmailDialog();
                } else if(className != null && className.equals("SignIn")) {
                    Snackbar.make(v, "You are signed in " + userName, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(v, "You have been signed out", Snackbar.LENGTH_LONG).show();
                }
            }
        }
        if (ParseUser.getCurrentUser() != null) {
            CreateAccountHelper.CheckUserVerified();
        }
        CheckForGoogleServicesAndGPS();
    }

    private void CheckForGoogleServicesAndGPS() {
        if (!TrailKeeperApplication.GetIsGPSEnabled()) {
            AlertDialogHelper.showCustomAlertDialogForNoGPS(context, "No GPS", "Please Turn On Your GPS To Get Access To All The Features Of TrailKeeper");
        }
        if (!TrailKeeperApplication.GetIsPlayServicesInstalled() || !TrailKeeperApplication.GetIsPlayServicesUpdated()) {
            AlertDialogHelper.showCustomAlertDialogForPlayServices(context, "Update Google Play Service", "Please Update Google Play Services To Have Access To All The Features Of TrailKeeper");
        }
    }

    //region Activity Set up
    private void loadSavedPreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        firstTimeLoad = sp.getBoolean("Firsttimeload", true);
        //by Anatoliy
        globalUnitDefault = sp.getBoolean("Imperial", true);
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Sets up the Trail Status Recycler View
    private void SetUpTrailStatusCard() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailOpenRecyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        mTrailOpenRecyclerView.setLayoutManager(layoutManager);
        mTrailOpenRecyclerView.setHasFixedSize(true);

        // lets make sure the refresh only happens when the top card is fully visible
        mTrailOpenRecyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean enabled = false;
                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    // check if the first card is visible
                    boolean firstCardVisible = mTrailOpenAdapter.getItemViewType(0) == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstVisibleCard = recyclerView.getChildAt(0).getTop() == 0;
                    // enable or disable the refresh layout
                    enabled = firstCardVisible && topOfFirstVisibleCard;
                }
                mSwipeLayout.setEnabled(enabled);
            }
        });
    }

    // Fills the Trail Status Recycler View
    public void SetUpTrailStatusRecyclerView(){
        int runTime;
        if (firstTimeLoad) {
            runTime = 6000;
        } else {
            runTime = 3000;
        }

        // we want to give a few extra seconds for the Google Location API to load and connect
        // but only if the current location is already null
        if (TrailKeeperApplication.home == null) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!trails.isEmpty()) {  //if it's empty we'll do one more fetch
                        SortTrails();
                        ShowTrailCards();
                    } else {
                        trails = ModelTrails.GetAllTrailInfo();
                        SortTrails();
                        ShowTrailCards();
                    }
                }
            }, runTime);
        } else {
            SortTrails();
            ShowTrailCards();
        }
    }

    private void ShowTrailCards(){
        mSwipeLayout.setRefreshing(false);
        //passing globalUnitDefault to constructor by Anatoliy
        mTrailOpenAdapter = new RecyclerViewHomeScreenAdapter(trails, context, globalUnitDefault);
        mTrailOpenRecyclerView.setAdapter(mTrailOpenAdapter);
        mTrailOpenRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // lets show the sign up screen if this is the first time they've been here
        if (firstTimeLoad) {
            showSignUpScreen();
        }

        // show a message in case the load did take too long
        if (trails.isEmpty()) {
            Snackbar.make(mSwipeLayout, "Some Bad Happened, Pull Down To Refresh", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }
    //endregion

    //region Dialogs
    private void VerifyEmailDialog() {
        AlertDialogHelper.showCustomAlertDialog(context, "Thanks for signing up", "Please Check Your Email And Verify Your Account To The Features In TrailKeeper");
    }

    private void showSignUpScreen(){
        final Dialog welcomeDialog = new Dialog(this);
        welcomeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        welcomeDialog.setContentView(R.layout.dialog_first_welcome);
        Button btnNotYet = (Button)welcomeDialog.findViewById(R.id.btn_not_yet);
        Button btnOkay = (Button)welcomeDialog.findViewById(R.id.btn_okay);

        btnNotYet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeDialog.dismiss();
                View view = mTrailOpenRecyclerView;
                Snackbar.make(view, R.string.snackbar_settings_reminder, Snackbar.LENGTH_LONG).show();
            }
        });

        btnOkay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeDialog.dismiss();
                Intent intent = new Intent(context, CreateAccount.class);
                startActivity(intent);
            }
        });

        welcomeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                firstTimeLoad = false;
                savePreferences("Firsttimeload", false);
            }
        });

        welcomeDialog.show();
    }
    //endregion

    //region Private Methods
    private void SortTrails(){
        for (int i = 0; trails.size() > i; i++) {
            // TODO once settings are working add Metric in here (done)
        //by Anatoliy
            if (globalUnitDefault) {
                trails.get(i).distanceAway = (float) Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), TrailKeeperApplication.home) * 100) / 100;
            }
            else {trails.get(i).distanceAway = (float) Math.round(GeoLocationHelper.GetClosestTrails(trails.get(i), TrailKeeperApplication.home)*1.609344 * 100) / 100;}
        }
        GeoLocationHelper.SortTrails(trails);

        //on the Home Screen we only will show the 5 closet trails
        int count = trails.size();
        for (int i = 0; count > i; i++) {
            if (trails.size() > 5) {
                trails.remove(5);
            }
        }
    }
    //endregion

    //region Location API implementation methods
    @Override
    public void onStart() {
        super.onStart();
        TrailKeeperApplication.mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (TrailKeeperApplication.mGoogleApiClient.isConnected()) {
            TrailKeeperApplication.mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TrailKeeperApplication.GetIsGPSEnabled()) {
            TrailKeeperApplication tk = new TrailKeeperApplication(context);
            tk.checkIfGPSIsEnabled();
        }
        if (!TrailKeeperApplication.GetIsPlayServicesUpdated() || !TrailKeeperApplication.GetIsPlayServicesInstalled()) {
            TrailKeeperApplication tk = new TrailKeeperApplication(context);
            tk.checkGooglePlayServices();
        }
        TrailKeeperApplication.mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (TrailKeeperApplication.mGoogleApiClient.isConnected()) {
            TrailKeeperApplication.mGoogleApiClient.disconnect();
        }
    }
    //endregion

    //region Swype To Refresh
    @Override
    public void onRefresh() {

        if (mTrailOpenAdapter != null) {
            trails.clear();
            mTrailOpenAdapter.notifyDataSetChanged();
        }

        // then refresh the Application class Parse Methods
        TrailKeeperApplication.LoadAllTrailsFromParse();
        TrailKeeperApplication.LoadAllCommentsFromParse();
        TrailKeeperApplication.LoadAllAuthorizedCommentorsFromParse();
        TrailKeeperApplication.LoadAllTrailStatusUpdatorsFromParse();
        if (ParseUser.getCurrentUser() != null) {
            AsyncRefreshCurrentUser refreshCurrentUser = new AsyncRefreshCurrentUser();
            Log.i(TAG, "Refreshing user from home screen");
            refreshCurrentUser.execute();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // now set the cards back up and load the recycler view
                trails = ModelTrails.GetAllTrailInfo();
                SetUpTrailStatusCard();
                SetUpTrailStatusRecyclerView();
                mSwipeLayout.setRefreshing(false);
                if (ParseUser.getCurrentUser() != null) {
                    CreateAccountHelper.CheckUserVerified();
                }
            }
        }, 3000);
    }
    //endregion


}
