package com.singlecog.trailkeeper.Activites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;


import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;

public class AccountSettings extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView settingsList;
    String[] settingsArray;
    private final Context context = this;
    private boolean isAnonUser;
    private ProgressDialog dialog;
    private View v;
    private CreateAccountHelper createAccountHelper;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        v = findViewById(R.id.linearlayout_root_main);
        super.onCreateDrawer(v, this);
        connectionDetector = new ConnectionDetector(context);
        isAnonUser = CreateAccountHelper.IsAnonUser();
        settingsList = (ListView)findViewById(R.id.listViewAccountSettings);
        CheckUserName();
        populateListView();
        settingsList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_account_settings, menu);
        return true;
    }
    //region ListItem Methods
    private void populateListView() {
        settingsArray = getResources().getStringArray(R.array.account_settings_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.activity_setting_listitem, settingsArray);
        settingsList.setAdapter(adapter);
        adapter.isEnabled(5);
    }

    private void CheckUserName() {
        String name;
        TextView userText = (TextView)findViewById(R.id.username);
        if (isAnonUser) {
            userText.setText(getResources().getString(R.string.not_signed_in));
        } else {
            name = getResources().getString(R.string.user) + ParseUser.getCurrentUser().getUsername();
            userText.setText(name);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String LOG = "AccountSettings";
        Log.i(LOG, "User clicked item: " + id + " at position: " + position);
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
                Intent intentUpdate = new Intent(context, UpdateAccount.class);
                startActivity(intentUpdate);
                break;
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
        }
    }
    //endregion

    //region Delete Account
    private void DeleteAccount() {
        final Dialog deleteDialog = new Dialog(this);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.dialog_delete_account);
        Button btnCancel = (Button)deleteDialog.findViewById(R.id.btn_cancel);
        Button btnYes = (Button)deleteDialog.findViewById(R.id.btn_yes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    createAccountHelper = new CreateAccountHelper(context, AccountSettings.this);
                    createAccountHelper.DeleteUser(ParseUser.getCurrentUser());
                    StartDeleteDialog();
                    deleteDialog.dismiss();
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        deleteDialog.show();
    }

    private void StartDeleteDialog(){
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "DeletingAccount");
    }

    public void DeleteSuccessOrFail(boolean valid, String failMessage) {
        if (valid) {
            createAccountHelper = new CreateAccountHelper(context, AccountSettings.this);
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
        final Dialog signoutDialog = new Dialog(this);
        signoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signoutDialog.setContentView(R.layout.dialog_signout);
        Button btnCancel = (Button)signoutDialog.findViewById(R.id.btn_cancel);
        Button btnYes = (Button)signoutDialog.findViewById(R.id.btn_yes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    ParseUser.logOut();
                    createAccountHelper = new CreateAccountHelper(context, AccountSettings.this);
                    createAccountHelper.CreateAnonUser();
                    StartSignOutDialog();
                    signoutDialog.dismiss();
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        signoutDialog.show();
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
