package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import AsyncAdapters.AsyncOneTrailComments;
import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;
import ParseObjects.ParseAuthorizedCommentors;
import RecyclerAdapters.RecyclerViewOneTrailCommentAdapter;
import models.ModelTrailComments;
import models.ModelTrails;

public class TrailScreen extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnMapReadyCallback
        , GoogleMap.OnMapClickListener
        , GoogleMap.OnMapLongClickListener{

    //region Properties
    protected static final String LOG = "trailScreenActivity";
    private SwipeRefreshLayout mSwipeLayout;
    private int trailId, status;
    private String objectID, trailSubscriptionStatus;
    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewOneTrailCommentAdapter mTrailCommentAdapter;
    private TextView trailName, trailCity, trailState;
    private ImageView trailStatus;
    private Button btnComment, btnTrailStatus, btnSubscribe;
    private AlertDialog statusDialog;
    private ProgressDialog dialog;
    private ConnectionDetector connectionDetector;
    GestureDetectorCompat gestureDetector;
    GoogleMap googleMap;
    private boolean isAnonUser;
    private boolean isEmailVerified;
    private boolean isValidCommentor = false;
    private View v;

    /**
     * Represents a geographical location.
     */
    private LatLng trailLocation;
    private String trailNameString;
    private List<ModelTrailComments> comments;
    private ModelTrails modelTrails;
    private final Context context = this;
    //endregion

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_screen);
        super.onCreateDrawer();

        // set up the swipe pull to refresh
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_trail_screen);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.accent,
                R.color.accent, R.color.accent,
                R.color.accent);

        connectionDetector = new ConnectionDetector(context);
        isEmailVerified = TrailKeeperApplication.isEmailVerified();
        CanComment();

        modelTrails = new ModelTrails(context, this);
        isAnonUser = CreateAccountHelper.IsAnonUser();
        SetUpViews();

        // get the trailID from the previous view
        Intent intent = getIntent();
        trailId = intent.getIntExtra("trailID", 0);
        objectID = intent.getStringExtra("objectID");

        // call method to get items from Local DataStore to fill the Views
        GetTrailData();


        // sets the tap event on the recycler views
        gestureDetector =
                new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        // Call the Async method
        try {
            AsyncOneTrailComments atc = new AsyncOneTrailComments(this, context, trailId);
            comments = new ArrayList<>();
            atc.execute(comments);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // set up the Recycler View
        SetupCommentCard();
        ShowGoogleMap();
        SetUpBtnStatusClick();
        SetUpBtnSubscribeClick();
        SetUpCommentButtonClick();
        mSwipeLayout.setEnabled(true);
    }

    //region Activity Methods

    private void ChangeStatusButtonText() {
        if(status == 1) {
            btnTrailStatus.setText("Open Trail");
        } else if (status == 2 ) {
            btnTrailStatus.setText("Close Trail");
        } else {
            btnTrailStatus.setText("Trail Status");
        }
    }

    public void CanComment() {
        ParseQuery<ParseAuthorizedCommentors> query  =  ParseAuthorizedCommentors.getQuery();
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseAuthorizedCommentors>() {
            @Override
            public void done(ParseAuthorizedCommentors parseAuthorizedCommentors, ParseException e) {
                if (e == null) {
                    isValidCommentor = (Boolean)parseAuthorizedCommentors.get("canComment");
                } else {
                    isValidCommentor = false;
                }
            }
        });
    }


    private void SetUpBtnSubscribeClick() {
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnonUser) {
                    if (isEmailVerified) {
                        OpenSubscribeDialog();
                    } else {
                        AlertDialogHelper.showCustomAlertDialog(context, "Verify Email!", "Please Verify Your Email Before Subscribing To Notifications, Or Refresh The Screen If You've Already Done So");
                    }
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No User Account!", "Please Create an Account in Settings Before Subscribing to Notifications.");
                }
            }
        });
    }

    private void SetUpCommentButtonClick() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnonUser) {
                    if (isEmailVerified) {
                        if (isValidCommentor) {
                            Snackbar.make(v, "Test Can Leave A Comment", Snackbar.LENGTH_LONG).show();
                        } else {
                            AlertDialogHelper.showCustomAlertDialog(context, "Not Authorized", "You Have Been Banned From Leaving Comments. Please Contact Us If You Think This Is A Mistake.");
                        }
                    } else {
                        AlertDialogHelper.showCustomAlertDialog(context, "Verify Email!", "Please Verify Your Email Before Commenting, Or Refresh The Screen If You've Already Done So");
                    }

                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No User Account!", "Please Create an Account in Settings Before Leaving Comments.");
                }
            }
        });
    }

//    private void IsValidCommentor(String trailName) {
//        isUpdateStatusVerified = false;
//        JSONArray trailnames = ParseInstallation.getCurrentInstallation().getJSONArray("updateTrailStatus");
//        for (int i = 0; trailnames.length() > i; i++){
//            String name = null;
//            try {
//                name = trailnames.getString(i);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (name != null && name.equals(trailName)) {
//                isUpdateStatusVerified = true;
//                break;
//            }
//        }
//    }

    private void SetUpBtnStatusClick() {
        btnTrailStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnonUser) {
                    if (isEmailVerified) {
                        OpenTrailStatusDialog();
                    } else {
                        AlertDialogHelper.showCustomAlertDialog(context, "Verify Email!", "Please Verify Your Email in Settings Before Updating Trails, Or Refresh The Screen If You've Already Done So");
                    }
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No User Account!", "Please Create an Account in Settings Before Updating Trails");
                }
            }
        });
    }

    private void SetUpViews() {
        trailName = (TextView)findViewById(R.id.txtTrail_name);
        trailStatus = (ImageView)findViewById(R.id.txtTrail_status);
        trailCity = (TextView)findViewById(R.id.txtTrail_city);
        trailState = (TextView)findViewById(R.id.txtTrail_state);
        btnComment = (Button)findViewById(R.id.btn_leave_comment);
        btnTrailStatus = (Button)findViewById(R.id.btn_set_trail_status);
        btnSubscribe = (Button)findViewById(R.id.btn_subscribe);
        v = findViewById(R.id.linearlayout_root_main);
    }

    private void GetTrailData() {
        if (connectionDetector.isConnectingToInternet()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
            query.fromLocalDatastore();
            query.whereEqualTo("trailId", trailId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : list) {
                            trailNameString = object.get("trailName").toString();
                            trailName.setText(trailNameString);
                            status = object.getInt("status");
                            trailCity.setText(object.get("city").toString());
                            trailState.setText(object.get("state").toString());
                            trailLocation = new LatLng(object.getParseGeoPoint("geoLocation").getLatitude(), object.getParseGeoPoint("geoLocation").getLongitude());

                            UpdateStatusIcon();
                        }
                    } else {
                        AlertDialogHelper.showCustomAlertDialog(context, "Oops", "Something went wrong, and we don't know what. \nGo back to the home screen and try again");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
        }
    }

    private void UpdateStatusIcon() {
        if (status == 1) {
            trailStatus.setImageResource(R.mipmap.red_closed);
        } else if (status == 2) {
            trailStatus.setImageResource(R.mipmap.green_open);
        } else {
            trailStatus.setImageResource(R.mipmap.yellow_unknown);
        }
        ChangeStatusButtonText();
    }

    // sets up the trail comment recycler view
    public void SetUpTrailCommentRecyclerView() {
        mTrailCommentAdapter = new RecyclerViewOneTrailCommentAdapter(comments);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);

        mTrailCommentRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //TODO call the new activity here instead of the Toast
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(TrailScreen.this, "Comment Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

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

    private void SetupCommentCard() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mTrailCommentRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_comments);
        mTrailCommentRecyclerView.setLayoutManager(layoutManager);
        mTrailCommentRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_trail_screen, menu);
        return true;
    }
    //endregion

    //region Subscribe to Notifications
    //TODO write method to check subsciptions and change button text to 'Subscribe' or 'Unsubscribe'

    private void OpenSubscribeDialog() {
        final CharSequence[] choiceList = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Receive Notifications For This Trail?");
        builder.setSingleChoiceItems(choiceList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:  // Yes
                        trailSubscriptionStatus = "You have been subscribed to " + trailNameString;
                        UpdateSubscribeStatus(which);
                        break;
                    case 1:  // No
                        trailSubscriptionStatus = "You have been un-subscribed to " + trailNameString;
                        UpdateSubscribeStatus(which);
                        break;
                }
                dialog.dismiss();
            }
        });
        statusDialog = builder.create();
        statusDialog.show();
    }

    private void UpdateSubscribeStatus(int subscribe) {
        if (connectionDetector.isConnectingToInternet()) {
            dialog = ProgressDialogHelper.ShowProgressDialog(context, "Updating Subscriptions");
            modelTrails.SubscribeToChannel(trailNameString, subscribe, "");
        } else {
            AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
        }
    }

    public void UpdateSubscriptionWasSuccessful(boolean valid, String message) {
        dialog.dismiss();
        if (valid) {
            Snackbar.make(v, trailSubscriptionStatus, Snackbar.LENGTH_LONG).show();
            //AlertDialogHelper.showAlertDialog(context, trailNameString, trailSubscriptionStatus);
            Log.i(LOG, "Subscription change for " + trailNameString + " was updated");
        } else {
            Snackbar.make(v, trailSubscriptionStatus, Snackbar.LENGTH_LONG).show();
            //AlertDialogHelper.showAlertDialog(context, "Try Again", "Something went wrong: " + message);
            Log.i(LOG, "Subscription change for " + trailNameString + " was not updated");
        }
    }

    //endregion

    //region Trail Status Updates
    private void OpenTrailStatusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Trail Status");
        builder.setSingleChoiceItems(ModelTrails.getTrailStatusNames(), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:                   // Open Trail
                        status = 2;
                        ChangeTrailStatus(status);
                        break;
                    case 1:                   // Close Trail
                        status = 1;
                        ChangeTrailStatus(status);
                        break;
                    case 2:                   // Unknown
                        status = 3;
                        ChangeTrailStatus(status);
                        break;
                }
                dialog.dismiss();
            }
        });

        statusDialog = builder.create();
        statusDialog.show();
    }

    private void ChangeTrailStatus(final int choice){
        if (connectionDetector.isConnectingToInternet()) {
            CallChangeTrailStatusClass(choice);
        } else {
            AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
        }
    }

    private void CallChangeTrailStatusClass(int choice) {
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Updating Trail Status");
        modelTrails.UpdateTrailStatus(objectID, choice);
    }

    public void TrailStatusUpdateWasSuccessful(boolean valid, String message) {
        dialog.dismiss();
        if (valid) {
            Snackbar.make(v, "The Trail has been changed to " + ModelTrails.ConvertTrailStatus(status), Snackbar.LENGTH_LONG).show();
            UpdateStatusIcon();
            ModelTrails.SendOutAPushNotifications(trailNameString, status);
            Log.i(LOG, "Trail Status was changed");
        } else {
            Snackbar.make(v, "Something went wrong: " + message, Snackbar.LENGTH_LONG).show();
            Log.i(LOG, "Trail Status was not changed");
        }
    }
    //endregion

    //region Google MapActivity Api Methods
    private void ShowGoogleMap() {
        // even if the connection is not successful we still want to call and show the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
    }

    // this gets called on the getMapAsync method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        if (trailLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trailLocation, 13));
            googleMap.addMarker(new MarkerOptions()
                    .title(trailNameString)
                    .position(trailLocation)).showInfoWindow();

        } else {
            Toast.makeText(this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this,"You tapped the map yo!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent(context, TrailMap.class);
        intent.putExtra("trailID", trailId);
        intent.putExtra("objectID", objectID);
        startActivity(intent);
    }
    //endregion

    //Region Swipe To Refresh
    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);

        TrailKeeperApplication.LoadAllTrailsFromParse();
        TrailKeeperApplication.LoadAllCommentsFromParse();
        TrailKeeperApplication.LoadAllTrailStatusUpdatorsFromParse();
        TrailKeeperApplication.LoadAllAuthorizedCommentorsFromParse();
        CreateAccountHelper.CheckUserVerified();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //IsAuthorizedToUpdateTrailStatus(trailNameString);
                isEmailVerified = TrailKeeperApplication.isEmailVerified();
                isAnonUser = CreateAccountHelper.IsAnonUser();
                CanComment();
                mSwipeLayout.setRefreshing(false);
            }
        }, 3000);
    }

    //endregion
}
