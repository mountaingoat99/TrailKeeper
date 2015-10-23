package com.singlecog.trailkeeper.Activites;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseUser;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.singlecog.trailkeeper.R;

import Helpers.CreateAccountHelper;
import Helpers.PushNotificationHelper;

public class BaseActivity extends AppCompatActivity {

    //Navigation Drawer
    public DrawerLayout mDrawerLayout;
    //Navigation Drawer List of items
    public ListView mDrawerList;
    public ActionBarDrawerToggle mDrawerToggle;
    public String mActivityTitle;
    private View layoutView;
    private Activity activity;

    private boolean isAnonUser;

    protected void onCreateDrawer(View view, Activity activity) {
        //Instantiate Navigation Drawer
        setupNavDrawer();
        setUpDrawer();

        // sets the status bar color on API's between 19 and > Lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setTintColor(getResources().getColor(R.color.primary_dark));
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isAnonUser = CreateAccountHelper.IsAnonUser();
        layoutView = view;
        this.activity = activity;
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
                        getString(R.string.add_trail),
                        //getString(R.string.create_group),
                        getString(R.string.trail_subscriptions),
                        getString(R.string.account_settings),
                        getString(R.string.request_trail_pin),
                        getString(R.string.contact_us)
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
                        String activityName = activity.getLocalClassName();
                        Intent intent;
                        switch (position) {
                            case 0:   // home
                                if (!activityName.equalsIgnoreCase("Activites.HomeScreen")) {
                                    intent = new Intent(BaseActivity.this, HomeScreen.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 1:   // Find Trail
                                if (!activityName.equalsIgnoreCase("Activites.FindTrail")) {
                                    intent = new Intent(BaseActivity.this, FindTrail.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 2:   // Comments
                                if (!activityName.equalsIgnoreCase("Activites.AllComments")) {
                                    intent = new Intent(BaseActivity.this, AllComments.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 3:   // Map
                                if (!activityName.equalsIgnoreCase("Activites.MapActivity")) {
                                    intent = new Intent(BaseActivity.this, MapActivity.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 4:   // Add Trail
                                if (isAnonUser || ParseUser.getCurrentUser() == null) {
                                    Snackbar.make(layoutView, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                                } else {
                                    if (!activityName.equalsIgnoreCase("Activites.AddTrail")) {
                                        intent = new Intent(BaseActivity.this, AddTrail.class);
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    } else {
                                        mDrawerLayout.closeDrawer(GravityCompat.START);
                                    }
                                }
                                break;
                            //case 5:   // Create Group
                                //intent = new Intent(BaseActivity.this, Profile.class);
                                //startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                //break;
                            case 5:   // Subscriptions
                                if (isAnonUser || ParseUser.getCurrentUser() == null) {
                                    Snackbar.make(layoutView, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                                } else {
                                    if (PushNotificationHelper.GetUserSubscriptions() != null && PushNotificationHelper.GetUserSubscriptions().size() > 0) {
                                        if (!activityName.equalsIgnoreCase("Activites.Notifications")) {
                                            intent = new Intent(BaseActivity.this, Notifications.class);
                                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                        } else {
                                            mDrawerLayout.closeDrawer(GravityCompat.START);
                                        }
                                    } else {
                                        Snackbar.make(layoutView, "You Have No Subscriptions", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                break;
                            case 6:   // Account Settings
                                if (!activityName.equalsIgnoreCase("Activites.AccountSettings")) {
                                    intent = new Intent(BaseActivity.this, AccountSettings.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 7:   // Request trail Pin
                                if (!activityName.equalsIgnoreCase("Activites.GetTrailPin")) {
                                    if (isAnonUser || ParseUser.getCurrentUser() == null) {
                                        Snackbar.make(layoutView, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                                    } else {
                                        intent = new Intent(BaseActivity.this, GetTrailPin.class);
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    }
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case 8:   // Contact us
                                if (!activityName.equalsIgnoreCase("Activites.Contact")) {
                                    intent = new Intent(BaseActivity.this, Contact.class);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                } else {
                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                }
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
