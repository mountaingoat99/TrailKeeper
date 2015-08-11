package com.singlecog.trailkeeper.Activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.singlecog.trailkeeper.R;

public class BaseActivity extends AppCompatActivity {

    //Navigation Drawer
    public DrawerLayout mDrawerLayout;
    //Navigation Drawer List of items
    public ListView mDrawerList;
    public ActionBarDrawerToggle mDrawerToggle;
    public String mActivityTitle;

    protected void onCreateDrawer() {
        //Instantiate Navigation Drawer
        setupNavDrawer();
        setUpDrawer();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return id == R.id.action_settings ||
                mDrawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // syncs toggle state after onRestoreInstanceState
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Set up Navigation Drawer
    private void setupNavDrawer() {

        //Instantiate Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Instantiate Navigation Drawer List
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        mActivityTitle = getTitle().toString();

        //Populate Navigation Drawer with string.xml values
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_text_items,
                new String[]{
                        getString(R.string.home),
                        getString(R.string.find_trail),
                        getString(R.string.comments),
                        getString(R.string.map),
                        getString(R.string.trail_admin),
                        getString(R.string.profile),
                        getString(R.string.settings)
                }));

        //Manage here what happens after item clicked in the Navigation Drawer
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mDrawerLayout.closeDrawer(GravityCompat.START);

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent;
                        switch (position) {
                            case 0:
                                intent = new Intent(BaseActivity.this, HomeScreen.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 1:
                                intent = new Intent(BaseActivity.this, FindTrail.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 2:
                                intent = new Intent(BaseActivity.this, AllComments.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 3:
                                intent = new Intent(BaseActivity.this, MapActivity.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 4:
                                intent = new Intent(BaseActivity.this, TrailAdmin.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 5:
                                intent = new Intent(BaseActivity.this, Profile.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                            case 6:
                                intent = new Intent(BaseActivity.this, Settings.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                        }
                    }
                }, 200);
            }
        });
    }

    private void setUpDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // here we want to see if this is the root activity or not
        // if root show the drawer icon, if not show the back to home arrow
        //TODO Might want to add all the drawer classes on here
        if (getClass() == HomeScreen.class) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            //mDrawerLayout.setDrawerTitle(GravityCompat.START, "Home");
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }
}
