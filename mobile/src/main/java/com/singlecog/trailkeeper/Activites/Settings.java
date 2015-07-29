package com.singlecog.trailkeeper.Activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;
import com.singlecog.trailkeeper.UpdateAccount;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;

public class Settings extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView settingsList;
    String[] settingArray;
    private final Context context = this;
    private boolean isAnonUser;
    private AlertDialog signOutDialog, deleteDialog;
    private ProgressDialog dialog;
    private View v;
    private CreateAccountHelper createAccountHelper;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();
        connectionDetector = new ConnectionDetector(context);
        isAnonUser = CreateAccountHelper.IsAnonUser();
        settingsList = (ListView)findViewById(R.id.listViewSettings);
        populateListView();
        settingsList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //region ListItem Methods
    private void populateListView() {
        settingArray = getResources().getStringArray(R.array.settings_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.activity_setting_listitem, settingArray);
        settingsList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("SettingListView", "User clicked item: " + id + " at position: " + position);
        switch (position){
            case 0:  // Create Account
                if (isAnonUser) {
                    Intent intent = new Intent(context, CreateAccount.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_create_account, Snackbar.LENGTH_LONG).show();
                }
                break;
            case 1:  // update account
                if (!isAnonUser) {
                    Intent intent = new Intent(context, UpdateAccount.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_logout, Snackbar.LENGTH_LONG).show();
                }
            case 2:  // Sign in
                if(!isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_login, Snackbar.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, SignIn.class);
                    startActivity(intent);
                }
                break;
            case 3:  // Sign out
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_logout, Snackbar.LENGTH_LONG).show();
                } else {
                    SignOut();
                }
                break;
            case 4:  // Delete Account
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_alreadysignedin_delete_account, Snackbar.LENGTH_LONG).show();
                } else {
                    DeleteAccount();
                }
                break;
            case 5:  // Notifications
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    // TODO go to Notifications screen
                }
                break;
            case 6: // TrailOwnerAdmin request
                if(isAnonUser) {
                    Snackbar.make(view, R.string.snackbar_notifications_signin, Snackbar.LENGTH_LONG).show();
                } else {
                    //TODO go to TrailOwnerAdmin screen
                }
                break;
            case 7: // Contact
                // TODO contact dialog
                break;
        }
    }
    //endregion

    //region Delete Account
    private void DeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete_account_confirm));
        builder.setNegativeButton(getResources().getString(R.string.sign_out_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.sign_out_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (connectionDetector.isConnectingToInternet()) {
                    createAccountHelper = new CreateAccountHelper(context, Settings.this);
                    createAccountHelper.DeleteUser(ParseUser.getCurrentUser());
                    StartDeleteDialog();
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        deleteDialog = builder.create();
        deleteDialog.show();
    }

    private void StartDeleteDialog(){
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "DeletingAccount");
    }

    public void DeleteSuccessOrFail(boolean valid, String failMessage) {
        if (valid) {
            createAccountHelper = new CreateAccountHelper(context, Settings.this);
            createAccountHelper.CreateAnonUser();
        } else {
            dialog.dismiss();
            v = settingsList;
            Snackbar.make(v, failMessage, Snackbar.LENGTH_LONG).show();
        }
    }
    //endregion

    //region SignOut
    private void SignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.sign_out_confirm));
        builder.setNegativeButton(getResources().getString(R.string.sign_out_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.sign_out_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (connectionDetector.isConnectingToInternet()) {
                    ParseUser.logOut();
                    createAccountHelper = new CreateAccountHelper(context, Settings.this);
                    createAccountHelper.CreateAnonUser();
                    StartSignOutDialog();
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        signOutDialog = builder.create();
        signOutDialog.show();
    }

    private void StartSignOutDialog(){
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Signing Out");
    }

    public void SignedOut(boolean valid, String failMessage){
        dialog.dismiss();
        v = settingsList;
        if(valid){
            Intent intent = new Intent(context, HomeScreen.class);
            Bundle b = new Bundle();
            b.putString("userName", "");
            b.putString("className", "Settings");
            intent.putExtras(b);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Snackbar.make(v, failMessage, Snackbar.LENGTH_LONG).show();
        }
    }
    //endregion
}
