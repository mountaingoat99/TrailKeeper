package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.singlecog.trailkeeper.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;
import models.ModelTrails;

public class CreateAccount extends BaseActivity {

    private static String LOG = "CreateAccount";
    private EditText email, username, password;
    private Button btnSignUp;
    private String usernameString;
    private View v;
    private final Context context = this;
    private InputMethodManager imm;
    private ProgressDialog dialog;
    private ConnectionDetector connectionDetector;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        v = findViewById(R.id.main_layout);
        super.onCreateDrawer(v, this);
        SetUpViews();
        if (savedInstanceState != null) {
            String emailString = savedInstanceState.getString("email");
            email.setText(emailString);
            usernameString = savedInstanceState.getString("username");
            username.setText(usernameString);
            String passwordString = savedInstanceState.getString("password");
            password.setText(passwordString);
        }
        connectionDetector = new ConnectionDetector(getApplicationContext());
        email.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
        v = layout;
        SetUpClickEvent();
    }

    //region Activity Methods
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(email.getText().length() > 0)
            outState.putString("email", email.getText().toString());
        if(username.getText().length() > 0)
            outState.putString("username", username.getText().toString());
        if(password.getText().length() > 0)
            outState.putString("password", password.getText().toString());
    }


    private void SetUpViews(){
        email = (EditText)findViewById(R.id.edittext_email);
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        btnSignUp = (Button)findViewById(R.id.btn_sign_up);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }
    //endregion

    //region Create Account Methods
    private void SetUpClickEvent() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    // first lets make sure all the fields are legit
                    if (!CreateAccountHelper.isValidEmail(email.getText()) ||
                            !CreateAccountHelper.isValidUserName(username.getText().toString().trim()) ||
                            !CreateAccountHelper.isValidPassword(password.getText().toString().trim())) {
                        if (!CreateAccountHelper.isValidEmail(email.getText())) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Snackbar.make(v, R.string.snackbar_invalid_email, Snackbar.LENGTH_LONG).show();
                        } else if (!CreateAccountHelper.isValidUserName(username.getText().toString().trim())) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Snackbar.make(v, R.string.snackbar_invalid_username, Snackbar.LENGTH_LONG).show();
                        } else if (!CreateAccountHelper.isValidPassword(password.getText().toString().trim())) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Snackbar.make(v, R.string.snackbar_invalid_password, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        CallCreateAccount();
                    }
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    private void CallCreateAccount() {
        CreateAccountHelper createAccountHelper = new CreateAccountHelper(context, this);
        createAccountHelper.CreateParseUserAccount(username.getText().toString(),
                password.getText().toString(),
                email.getText().toString());
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Creating Account");
    }

    public void UpdateCreateAccountSuccessMessage(boolean valid, String failMessage) {
        dialog.dismiss();
        if (valid){
            UpdateInstallationInfo();
            CreateSuccessMessage();
            Log.i(LOG, "Account Creation Success");
        }else{
            //AlertDialogHelper.showCustomAlertDialog(context, "Sorry!", failMessage);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            Snackbar.make(v, failMessage, Snackbar.LENGTH_LONG).show();
            Log.i(LOG, "Account Creation Failed");
        }
    }

    public void UpdateInstallationInfo() {
        // here we want to associate an installation with a user
        ParseInstallation parseInstallation = null;
        try {
            parseInstallation = ParseInstallation.getCurrentInstallation().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert parseInstallation != null;
        parseInstallation.add("user", ParseUser.getCurrentUser());
        parseInstallation.add("userName", ParseUser.getCurrentUser().getUsername());
        parseInstallation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(LOG, "updated new Installation ");
                } else {
                    Log.i(LOG, "Failed to update installation: " + e.getMessage());
                }
            }
        });

        CreateAccountHelper.SetUsersCanComment(true);
    }

    // if successful we go right back to the StartScreen
    private void CreateSuccessMessage() {
        Intent intent = new Intent(context, HomeScreen.class);
        usernameString = ParseUser.getCurrentUser().getUsername();
        Bundle b = new Bundle();
        b.putString("userName", usernameString);
        b.putString("className", "CreateAccount");
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //endregion

}
