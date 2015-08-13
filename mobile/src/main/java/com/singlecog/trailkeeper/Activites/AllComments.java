package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

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
    private int fabSearchbaseline;
    private int fabSearchbottom;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);
        super.onCreateDrawer();
        fabSearch = (FloatingActionButton)findViewById(R.id.search_fab);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // get the height of the main layout once it is drawn
        fabSearch = (FloatingActionButton)findViewById(R.id.search_fab);
        ViewTreeObserver treeObserver = fabSearch.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;

                fabSearch.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //fabSearchHeight = fabSearch.getMeasuredHeight();
                fabSearchbottom= fabSearch.getBottom();
                fabSearchbaseline = fabSearch.getBaseline();



                //SetUpFabs();

                //recyclerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //mainLayoutHeight = recyclerLayout.getMeasuredHeight();
            }
        });


    }

    private void SetUpFabs() {

        fabUser = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent_one))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 80)
                //.withButtonSize(60)
                .create();

        fabUser.setClickable(true);
        fabUser.hideFloatingActionButton();
        fabTrails = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent_two))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 160)
                //.withButtonSize(60)
                .create();
        fabTrails.setClickable(true);
        fabTrails.hideFloatingActionButton();
        fabAll = new MyFloatingActionButton.Builder(AllComments.this)
                .withDrawable(getResources().getDrawable(R.mipmap.ic_search_small))
                .withButtonColor(getResources().getColor(R.color.accent_three))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 240)
                //.withButtonSize(60)
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
