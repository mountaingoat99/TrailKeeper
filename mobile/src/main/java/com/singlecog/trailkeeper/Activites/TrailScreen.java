package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import AsyncAdapters.AsyncOneTrailComments;
import AsyncAdapters.AsyncSaveOfflineComments;
import AsyncAdapters.AsyncSaveOfflineStatus;
import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import ParseObjects.ParseAuthorizedCommentors;
import RecyclerAdapters.RecyclerViewOneTrailCommentAdapter;
import models.ModelTrailComments;
import models.ModelTrails;

public class TrailScreen extends BaseActivity {

    //region Properties
    protected static final String TAG = "trailScreenActivity";
    private int status, trailStatusPin;
    private String objectID, trailSubscriptionStatus;
    private RecyclerView mTrailCommentRecyclerView;
    private RecyclerViewOneTrailCommentAdapter mTrailCommentAdapter;
    private TextView trailName, trailCity, trailState;
    private ImageView trailStatus, imageEasy, imageMedium, imageHard;
    private Button btnComment, btnTrailStatus, btnSubscribe, btnAllCommments;
    private AlertDialog statusDialog;
    private Dialog trailStatusDialog;
    private boolean isAnonUser;
    private boolean isEmailVerified;
    private boolean isValidCommentor = false;
    private View v;
    private LinearLayout layoutDifficulty;
    private LatLng trailLocation;
    private String trailNameString, newTrailCommentString;
    private boolean isEasy = false, isMedium = false, isHard = false;
    private List<ModelTrailComments> comments;
    private ModelTrails modelTrails;
    private final Context context = this;
    private RadioButton rdoOpen;
    private RadioButton rdoClosed;
    private RadioButton rdoUnknown;

    //endregion

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_screen);
        Log.i(TAG, "Starting OnCreate Trail Screen");
        v = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(v, this);

        isEmailVerified = TrailKeeperApplication.isEmailVerified();
        CanComment();

        modelTrails = new ModelTrails(context, this);
        isAnonUser = CreateAccountHelper.IsAnonUser();
        SetUpViews();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            objectID = b.getString("objectID");
        }


        CallTrailCommentsAsync();
        GetTrailData();

        // set up the Recycler View
        // need to check version here. Ice Cream Sandwich
        // loads the Async task really slow
        SetupCommentCard();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SetUpTrailCommentRecyclerView();
                }
            },1000 );
        } else {
            SetUpTrailCommentRecyclerView();
        }
        SetUpBtnStatusClick();
        SetUpBtnSubscribeClick();
        SetUpCommentButtonClick();
        SetUpAllCommentButtonClick();
        showTrailDifficulty();
    }

    private void CallTrailCommentsAsync() {
        Log.i(TAG, "Calling The Trail Comments Async");
        // Call the Async method
        try {
            AsyncOneTrailComments atc = new AsyncOneTrailComments(objectID);
            comments = new ArrayList<>();
            atc.execute(comments);
        }catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            List<ParseAuthorizedCommentors>  parseAuthorizedCommentors = query.find();
            if (parseAuthorizedCommentors.size() > 0 ) {
                isValidCommentor = (Boolean) parseAuthorizedCommentors.get(0).get("canComment");
            } else isValidCommentor = false;

        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                            ShowLeaveCommentDialog();
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

    private void SetUpAllCommentButtonClick() {
        btnAllCommments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllComments.class);
                Bundle b = new Bundle();
                b.putString("trailObjectId", objectID);
                b.putBoolean("fromTrailScreen", true);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void showTrailDifficulty() {

        if (isEasy) {
            layoutDifficulty.setVisibility(View.VISIBLE);
            imageEasy.setVisibility(View.VISIBLE);
        }
        if (isMedium) {
            layoutDifficulty.setVisibility(View.VISIBLE);
            imageMedium.setVisibility(View.VISIBLE);
        }
        if (isHard) {
            layoutDifficulty.setVisibility(View.VISIBLE);
            imageHard.setVisibility(View.VISIBLE);
        }
    }

    private void SetUpViews() {
        trailName = (TextView)findViewById(R.id.txtTrail_name);
        trailStatus = (ImageView)findViewById(R.id.txtTrail_status);
        trailCity = (TextView)findViewById(R.id.txtTrail_city);
        trailState = (TextView)findViewById(R.id.txtTrail_state);
        btnComment = (Button)findViewById(R.id.btn_leave_comment);
        btnTrailStatus = (Button)findViewById(R.id.btn_set_trail_status);
        btnSubscribe = (Button)findViewById(R.id.btn_subscribe);
        btnAllCommments = (Button)findViewById(R.id.btn_all_comments);
        layoutDifficulty = (LinearLayout)findViewById(R.id.layout_skill_level);
        imageEasy = (ImageView)findViewById(R.id.image_easy);
        imageMedium = (ImageView)findViewById(R.id.image_medium);
        imageHard = (ImageView)findViewById(R.id.image_hard);
    }

    private void GetTrailData() {
        Log.i(TAG, "Getting the Trail DATA");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trails");
        query.fromLocalDatastore();
        query.whereEqualTo("objectId", objectID);
        try {
            List<ParseObject> list = query.find();
            if (list.size() > 0) {
                for (ParseObject object : list) {
                    trailNameString = object.get("trailName").toString();
                    trailName.setText(trailNameString);
                    status = object.getInt("status");
                    trailCity.setText(object.get("city").toString());
                    trailState.setText(object.get("state").toString());
                    trailLocation = new LatLng(object.getParseGeoPoint("geoLocation").getLatitude(), object.getParseGeoPoint("geoLocation").getLongitude());
                    isEasy = object.getBoolean("skillEasy");
                    isMedium = object.getBoolean("skillMedium");
                    isHard = object.getBoolean("skillHard");

                    UpdateStatusIcon();
                }
            } else {
                AlertDialogHelper.showCustomAlertDialog(context, "Oops", "Something went wrong, and we don't know what. \nGo back to the home screen and try again");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        trailStatusPin = ModelTrails.GetTrailPin(trailNameString);
    }

    private void UpdateStatusIcon() {
        if (status == 1) {
            trailStatus.setImageResource(R.drawable.status_closed);
        } else if (status == 2) {
            trailStatus.setImageResource(R.drawable.status_open);
        } else {
            trailStatus.setImageResource(R.drawable.status_caution);
        }
        ChangeStatusButtonText();
    }

    // sets up the trail comment recycler view
    public void SetUpTrailCommentRecyclerView() {
        Log.i(TAG, "Setting up the trail comment card Recyclcer view");
        mTrailCommentAdapter = new RecyclerViewOneTrailCommentAdapter(comments);
        mTrailCommentRecyclerView.setAdapter(mTrailCommentAdapter);
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
        getMenuInflater().inflate(R.menu.menu_trail_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                // we want to reload the home screen in case there was an update
                Intent intentHome = new Intent(context, HomeScreen.class);
                startActivity(intentHome);
            break;
            case R.id.action_map_click:
                Intent intent = new Intent(context, TrailMap.class);
                Bundle args = new Bundle();
                args.putParcelable("geoPoint", trailLocation);
                args.putString("objectID", objectID);
                args.putString("trailName", trailNameString);
                intent.putExtra("bundle", args);
                startActivity(intent);
                break;
        }
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
        modelTrails.SubscribeToChannel(trailNameString, subscribe);
        Snackbar.make(v, trailSubscriptionStatus, Snackbar.LENGTH_LONG).show();
    }
    //endregion

    // region Comments
    private void ShowLeaveCommentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_comment);
        final EditText txtComment = (EditText)dialog.findViewById(R.id.edittext_comment);
        Button btnCancel = (Button)dialog.findViewById(R.id.btn_cancel);
        Button btnSave = (Button)dialog.findViewById(R.id.btn_create_comment);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtComment.getText().length() > 0) {
                    newTrailCommentString = txtComment.getText().toString().trim();
                    SaveComment(txtComment.getText().toString().trim(), dialog);
                }
            }
        });

        dialog.show();
    }

    private void SaveComment(String comment, Dialog dialog) {
        dialog.dismiss();
        AsyncSaveOfflineComments offlineComments = new AsyncSaveOfflineComments(context, objectID, trailNameString, comment);
        offlineComments.execute();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
        ModelTrailComments modelTrailComments = new ModelTrailComments();
        modelTrailComments.CommentUserName = ParseUser.getCurrentUser().getUsername();
        modelTrailComments.TrailComments = comment;
        modelTrailComments.CommentDate = formatter.format(c.getTime());
        comments.add(0, modelTrailComments);
        mTrailCommentAdapter.notifyDataSetChanged();
    }

    //endregion

    //region Trail Status Updates
    private void OpenTrailStatusDialog() {
        trailStatusDialog = new Dialog(this);
        trailStatusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        trailStatusDialog.setContentView(R.layout.dialog_update_trail_status);
        final EditText pinText = (EditText)trailStatusDialog.findViewById(R.id.edit_text_pin);
        rdoOpen = (RadioButton)trailStatusDialog.findViewById(R.id.status_open);
        rdoClosed = (RadioButton)trailStatusDialog.findViewById(R.id.status_closed);
        rdoUnknown = (RadioButton)trailStatusDialog.findViewById(R.id.status_unknown);

        DoATextWatcher(pinText);

        rdoOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status = 2;
                ChangeTrailStatus(status);
                trailStatusDialog.dismiss();
            }
        });

        rdoClosed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status = 1;
                ChangeTrailStatus(status);
                trailStatusDialog.dismiss();
            }
        });

        rdoUnknown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status = 3;
                ChangeTrailStatus(status);
                trailStatusDialog.dismiss();
            }
        });

        trailStatusDialog.show();
    }

    private void DoATextWatcher(EditText pinText) {
        pinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (trailStatusPin == Integer.valueOf(s.toString())) {
                        rdoOpen.setEnabled(true);
                        rdoClosed.setEnabled(true);
                        rdoUnknown.setEnabled(true);
                    } else {
                        rdoOpen.setEnabled(false);
                        rdoClosed.setEnabled(false);
                        rdoUnknown.setEnabled(false);
                    }
                } else {
                    rdoOpen.setEnabled(false);
                    rdoClosed.setEnabled(false);
                    rdoUnknown.setEnabled(false);
                }
            }
        });
    }

    // TODO until the javascript to send push is working we need a data connection on status change
    private void ChangeTrailStatus(final int choice){
        CallChangeTrailStatusClass(choice);
    }

    private void CallChangeTrailStatusClass(int choice) {
        AsyncSaveOfflineStatus offlineStatus = new AsyncSaveOfflineStatus(context, objectID, choice, trailNameString);
        offlineStatus.execute();
        UpdateStatusIcon();
        Snackbar.make(v, "The Trail has been changed to " + ModelTrails.ConvertTrailStatus(status), Snackbar.LENGTH_LONG).show();
    }

    //endregion
}
