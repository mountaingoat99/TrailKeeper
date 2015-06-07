package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.singlecog.trailkeeper.R;

import java.util.List;

import Adapters.DividerItemDecoration;
import Adapters.RecyclerViewDemoAdapter;
import Adapters.RecyclerViewDemoApp;
import models.DemoModel;

import static Adapters.RecyclerViewDemoApp.*;

import static android.view.GestureDetector.SimpleOnGestureListener;

public class Home extends BaseActivity implements RecyclerView.OnItemTouchListener,
        View.OnClickListener, ActionMode.Callback {

    private RecyclerView mRecyclerView;
    private RecyclerViewDemoAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    int itemCount;
    GestureDetectorCompat gestureDetector;
    ActionMode actionMode;
    Context mContext;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onCreateDrawer();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_trail_updates);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        List<DemoModel> items = getDemoData();
        adapter = new RecyclerViewDemoAdapter(items);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // this is the default; this call is only necessary with custom ItemAnimators
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // onClickDetection is done in this activity's onItemListener
        // with the help od a gesture detector
        mRecyclerView.addOnItemTouchListener(this);
        gestureDetector =
                new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());

        // fab
        //fab.setOnClickListener(this);

    }

    private void addItemToList() {
        DemoModel model = new DemoModel();
        model.label = "New Item " + itemCount;
        itemCount++;
        int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).
                findFirstVisibleItemPosition();
        // needed to be able to show the animation
        // otherwise the view would be inserted before the first
        // visible item; that is outside of the viewable area
        position++;
        RecyclerViewDemoApp.addItemToList(model, position);
        adapter.addData(model, position);
    }

//    private void removeItemFromList() {
//        int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).
//                findFirstCompletelyVisibleItemPosition();
//        RecyclerViewDemoApp.removeItemFromList(position);
//        adapter.removeData(position);
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
//        if (view.getId() == R.id.fab_add) {
//            // fab click
//            addItemToList();
//        } else if (view.getId() == R.id.container_list_item) {
//            // item click
//            int idx = mRecyclerView.getChildPosition(view);
//            if (actionMode != null) {
//                myToggleSelection(idx);
//                return;
//            }
//            DemoModel data = adapter.getItem(idx);
//            View innerContainer = view.findViewById(R.id.container_inner_item);
//            innerContainer.setTransitionName(SyncStateContract.Constants.ACCOUNT_NAME + "_" + data.id);
//            Intent startIntent = new Intent(this, CardViewDemoActivity.class);
//            startIntent.putExtra(SyncStateContract.Constants.KEY_ID, data.id);
//            ActivityOptions options = ActivityOptions
//                    .makeSceneTransitionAnimation(this, innerContainer, SyncStateContract.Constants.NAME_INNER_CONTAINER);
//            this.startActivity(startIntent, options.toBundle());
//        }
    }

//    private void myToggleSelection(int idx) {
//        adapter.toggleSelection(idx);
//        String title = getString(R.string., adapter.getSelectedItemCount());
//        actionMode.setTitle(title);
//    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }



    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

//    @Override
//    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.menu_delete:
//                List<Integer> selectedItemPositions = adapter.getSelectedItems();
//                int currPos;
//                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
//                    currPos = selectedItemPositions.get(i);
//                    RecyclerViewDemoApp.removeItemFromList(currPos);
//                    adapter.removeData(currPos);
//                }
//                actionMode.finish();
//                return true;
//            default:
//                return false;
//        }
//    }

    //    @Override
//    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//        // Inflate a menu resource providing context menu items
//        MenuInflater inflater = actionMode.getMenuInflater();
//        inflater.inflate(R.menu.menu_cab_recyclerviewdemoactivity, menu);
//        //fab.setVisibility(View.GONE);
//        return true;
//    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        adapter.clearSelections();
        //fab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private class RecyclerViewDemoOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
//            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
//            if (actionMode != null) {
//                return;
//            }
//            // Start the CAB using the ActionMode.Callback defined above
//            actionMode = startActionMode(Home.this);
//            int idx = mRecyclerView.getChildPosition(view);
//            myToggleSelection(idx);
//            super.onLongPress(e);
        }
    }
}
