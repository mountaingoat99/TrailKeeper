package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.singlecog.trailkeeper.Activites.BaseActivity;
import com.singlecog.trailkeeper.R;

import java.util.List;

import RecyclerAdapters.RecyclerViewAllComments;
import models.ModelTrailComments;

public class AllComments extends BaseActivity {

    private String LOG = "AllComments";
    private final Context context = this;
    private FloatingActionButton btnSearch;
    private RecyclerView mAllCommentsRecyclerView;
    private RecyclerViewAllComments mRecyclerViewAllComments;
    private List<ModelTrailComments> comments;
    private ModelTrailComments modelTrailComments;
    private Boolean isFromTrailScreen = false;
    private String trailObjectID;
    private Dialog searchDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);
        super.onCreateDrawer();
        btnSearch = (FloatingActionButton)findViewById(R.id.search_fab);

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

    public void ReceiveCommentList(List<ModelTrailComments> comment) {
        comments = comment;
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
