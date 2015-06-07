package com.singlecog.trailkeeper.Activites;

import android.os.Bundle;
import android.view.Menu;

import com.singlecog.trailkeeper.R;

public class TrailAdmin extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_admin);
        super.onCreateDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_trail_admin, menu);
        return true;
    }
}
