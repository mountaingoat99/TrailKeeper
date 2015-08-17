package com.singlecog.trailkeeper.Activites;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import ParseObjects.ParseAuthorizedCommentors;
import RecyclerAdapters.RecyclerViewAllComments;
import models.ModelTrailComments;
import models.ModelTrails;

public class AllComments extends BaseActivity {

    final static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    final static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    boolean mHidden = true;

    private String LOG = "AllComments";
    private final Context context = this;
    private RecyclerView mAllCommentsRecyclerView;
    private RecyclerViewAllComments mRecyclerViewAllComments;
    private List<String> trailNames;
    private List<String> userNames;
    private List<ModelTrailComments> comments;
    private ModelTrailComments modelTrailComments;
    private ModelTrails modelTrails;
    private Boolean isFromTrailScreen = false;
    private String trailObjectID;
    private Dialog searchDialog;
    private FloatingActionButton fabUser;
    private FloatingActionButton fabTrails;
    private FloatingActionButton fabAll;
    private FloatingActionButton fabSearch;
    private View snackbarView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);
        snackbarView = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(snackbarView, this);
        SetUpFabs();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            trailObjectID = b.getString("trailObjectId");
            isFromTrailScreen = b.getBoolean("fromTrailScreen");
        }

        GetAllUsersWhoComment();

        // call to get all the comments first
        modelTrailComments = new ModelTrailComments(context, this);

        // call to get the trail names first
        modelTrails = new ModelTrails(context, this);
        modelTrails.GetTrailNames();

        if (!isFromTrailScreen) {
            modelTrailComments.GetAllComments();
        } else {
            modelTrailComments.GetCommentsByTrail(trailObjectID);
        }

        setCommentRecyclerView();
        SetUpOnClickForFab();

    }

    //region Activity Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_all_comments, menu);
        return true;
    }

    private void setCommentRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mAllCommentsRecyclerView = (RecyclerView)findViewById(R.id.all_comment_recycler_view);
        mAllCommentsRecyclerView.setLayoutManager(layoutManager);
        mAllCommentsRecyclerView.setHasFixedSize(true);
    }

    public void SetUpCommentView() {
        mRecyclerViewAllComments = new RecyclerViewAllComments(comments, context);
        mAllCommentsRecyclerView.setAdapter(mRecyclerViewAllComments);
        mAllCommentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void SetUpOnClickForFab() {
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO set up new fabs for other search
                if (isHidden()) {
                    showFloatingActionButton(fabUser);
                    showFloatingActionButton(fabTrails);
                    showFloatingActionButton(fabAll);
                    mHidden = false;
                } else {
                    hideFloatingActionButton(fabUser);
                    hideFloatingActionButton(fabTrails);
                    hideFloatingActionButton(fabAll);
                    mHidden = true;
                }
            }
        });

        fabUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchByUserDialog();
            }
        });

        fabTrails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchByTrailDialog();
            }
        });

        fabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchAllTrails();
            }
        });
    }

    private void SetUpFabs() {
        fabSearch = (FloatingActionButton)findViewById(R.id.search_fab);
        fabUser = (FloatingActionButton)findViewById(R.id.search_fab_by_user);
        fabTrails = (FloatingActionButton)findViewById(R.id.search_fab_by_trail);
        fabAll = (FloatingActionButton)findViewById(R.id.search_fab_by_all);
        hideFloatingActionButton(fabUser);
        hideFloatingActionButton(fabTrails);
        hideFloatingActionButton(fabAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }
    //endregion

    public void RecieveTrailNames(List<String> trails) {
        trailNames = trails;
    }

    public void ReceiveCommentList(List<ModelTrailComments> comment) {
        // check here to see if the list has anything, if we are checking by user or trail
        // the may not have had anything, and if so we just leave the fill list
        if (comment.size() > 0) {
            comments = comment;
        } else {
            Snackbar.make(snackbarView, "No Comments Have Been Left", Snackbar.LENGTH_LONG).show();
        }
    }

    public void hideFloatingActionButton(FloatingActionButton button) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1, 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1, 0);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(accelerateInterpolator);
            animSetXY.setDuration(100);
            animSetXY.start();
    }

    public void showFloatingActionButton(FloatingActionButton button) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(overshootInterpolator);
            animSetXY.setDuration(200);
            animSetXY.start();
    }

    public boolean isHidden() {
        return mHidden;
    }

    public void SendToTrailScreen(ModelTrails trails) {
        if (trails.TrailID > 0) {
            searchDialog.dismiss();
            modelTrailComments.GetCommentsByTrail(trails.getObjectID());
            setCommentRecyclerView();
        } else {
            Snackbar.make(snackbarView, "Trail does not exist", Snackbar.LENGTH_LONG).show();
        }
    }

    public void GetAllUsersWhoComment() {
        userNames = new ArrayList<>();
        ParseQuery<ParseAuthorizedCommentors> query  =  ParseAuthorizedCommentors.getQuery();
        query.fromLocalDatastore();
        try {
            List<ParseAuthorizedCommentors> list = query.find();
            for (ParseAuthorizedCommentors commentors : list) {
                String username = commentors.getUserName();
                userNames.add(username);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void GetAllUserObjectIds(String userName, final View v) {
        ParseQuery<ParseAuthorizedCommentors> query  =  ParseAuthorizedCommentors.getQuery();
        query.whereEqualTo("userName", userName);
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseAuthorizedCommentors>() {
            @Override
            public void done(ParseAuthorizedCommentors parseAuthorizedCommentors, ParseException e) {
                if (e == null) {
                    modelTrailComments.GetCommentsByUser(parseAuthorizedCommentors.getUserObjectID());
                    searchDialog.dismiss();
                } else {
                    Snackbar.make(v, "User Has No Comments", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SearchByUserDialog() {
        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.dialog_search_comment_by_user);
        final AutoCompleteTextView searchForTrailEditText = (AutoCompleteTextView)searchDialog.findViewById(R.id.edittext_search);
        Button btnGo = (Button)searchDialog.findViewById(R.id.btn_load_comment_by_user);

        // set the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        searchForTrailEditText.setAdapter(adapter);
        searchForTrailEditText.setThreshold(1);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchForTrailEditText.getText().length() > 0) {
                    GetAllUserObjectIds(searchForTrailEditText.getText().toString().trim(), v);
                } else {
                    Snackbar.make(v, "Please enter a Username", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        searchDialog.show();
    }

    private void SearchByTrailDialog() {
        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.dialog_search_comment_by_trail);
        final AutoCompleteTextView searchForTrailEditText = (AutoCompleteTextView)searchDialog.findViewById(R.id.edittext_search);
        Button btnGo = (Button)searchDialog.findViewById(R.id.btn_load_comment_by_trail);

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

    private void SearchAllTrails() {
        modelTrailComments.GetAllComments();
        setCommentRecyclerView();
    }
}
