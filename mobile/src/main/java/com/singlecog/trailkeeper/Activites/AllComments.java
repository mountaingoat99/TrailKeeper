package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.singlecog.trailkeeper.R;

import java.util.List;

import RecyclerAdapters.RecyclerViewAllComments;
import models.ModelTrailComments;
import utils.MyFloatingActionButton;

public class AllComments extends BaseActivity {

    private String LOG = "AllComments";
    private final Context context = this;
    private RecyclerView mAllCommentsRecyclerView;
    private RecyclerViewAllComments mRecyclerViewAllComments;
    private List<ModelTrailComments> comments;
    private ModelTrailComments modelTrailComments;
    private Boolean isFromTrailScreen = false;
    private String trailObjectID;
    private Dialog searchDialog;
    private MyFloatingActionButton fabUser;
    private MyFloatingActionButton fabTrails;
    private MyFloatingActionButton fabAll;

    private FloatingActionButton fabSearch;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);
        super.onCreateDrawer();

        SetUpFabs();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            trailObjectID = b.getString("trailObjectId");
            isFromTrailScreen = b.getBoolean("fromTrailScreen");
        }

        // call to get all the comments first
        modelTrailComments = new ModelTrailComments(context, this);

        if (!isFromTrailScreen) {
            modelTrailComments.GetAllComments();
        } else {
            modelTrailComments.GetCommentsByTrail(trailObjectID);
        }

        setCommentRecyclerView();
        SetUpOnClickForFab();
    }

    private void SetUpFabs() {
        fabSearch = (FloatingActionButton)findViewById(R.id.search_fab);
        fabUser = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 80)
                //.withButtonSize(40)
                .create();
        fabUser.setClickable(true);
        fabUser.hideFloatingActionButton();
        fabTrails = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 160)
                //.withButtonSize(40)
                .create();
        fabTrails.setClickable(true);
        fabTrails.hideFloatingActionButton();
        fabAll = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 240)
                //.withButtonSize(40)
                .create();
        fabAll.setClickable(true);
        fabAll.hideFloatingActionButton();
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

    public void ReceiveCommentList(List<ModelTrailComments> comment) {
        comments = comment;
    }

    private void SetUpOnClickForFab() {
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO set up new fabs for other search
                if (fabUser.isHidden()) {
                    fabUser.showFloatingActionButton();
                    fabTrails.showFloatingActionButton();
                    fabAll.showFloatingActionButton();
                } else {
                    fabUser.hideFloatingActionButton();
                    fabTrails.hideFloatingActionButton();
                    fabAll.hideFloatingActionButton();
                }

                //SearchDialog();
            }
        });
    }

    private void SearchDialog() {

    }

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

}
