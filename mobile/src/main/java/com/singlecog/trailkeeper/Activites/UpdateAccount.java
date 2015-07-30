package com.singlecog.trailkeeper.Activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.BaseActivity;
import com.singlecog.trailkeeper.Activites.TrailKeeperApplication;
import com.singlecog.trailkeeper.R;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;

public class UpdateAccount extends BaseActivity {

    private static String LOG = "UpdateAccount";
    private LinearLayout layout2, layout3, layoutET;
    private RelativeLayout layout1, mainLayout;
    private EditText username, password;
    private Button btnSignIn, btnUpdateEmail, btnUpdateUsername, btnUpdatePassword, btnSendVerify;
    private String emailString, newEmailString, userNameString, newUserNameString, passwordString;
    private View v;
    private boolean isSignedIn = false;
    private final Context context = this;
    private InputMethodManager imm;
    private ProgressDialog dialog;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        super.onCreateDrawer();
        SetUpView();
        if (savedInstanceState != null) {
            userNameString = savedInstanceState.getString("username");
            username.setText(userNameString);
            passwordString = savedInstanceState.getString("password");
            password.setText(passwordString);
            isSignedIn = savedInstanceState.getBoolean("isSignedIn");
        }
        v = mainLayout;
        showCorrectView();
        connectionDetector = new ConnectionDetector(getApplicationContext());
        SetUpSignInClickEvent();
        SetUpVerifyClickEvent();
        SetUpEmailClick();
        SetUpUsernameClick();
        SetUpPasswordClick();
    }

    //region Activity Methods
    private void SetUpView(){
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSendVerify = (Button)findViewById(R.id.btn_verify_email);
        btnUpdateEmail = (Button)findViewById(R.id.btn_change_email);
        btnUpdatePassword = (Button)findViewById(R.id.btn_change_password);
        btnUpdateUsername = (Button)findViewById(R.id.btn_change_username);
        layout1 = (RelativeLayout)findViewById(R.id.layout_btn1);
        mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
        layout2 = (LinearLayout)findViewById(R.id.layout_btn2);
        layout3 = (LinearLayout)findViewById(R.id.layout_btn3);
        layoutET = (LinearLayout)findViewById(R.id.layout_edittexts);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void showCorrectView() {
        if (isSignedIn) {
            layout1.setVisibility(View.GONE);
            layoutET.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            layout1.setVisibility(View.VISIBLE);
            layoutET.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
            username.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(username.getText().length() > 0)
            outState.putString("username", username.getText().toString());
        if(password.getText().length() > 0)
            outState.putString("password", password.getText().toString());
        outState.putBoolean("isSignedIn", isSignedIn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_update_account, menu);
        return true;
    }
    //endregion

    //region Update Email

    private void SetUpEmailClick() {
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //endRegion

    //region UpdateUsername

    private void SetUpUsernameClick() {
        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //endRegion

    //region Update Password

    private void SetUpPasswordClick() {
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    PasswordReset(emailString);
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    private void PasswordReset(String password){
        CreateAccountHelper accountHelper = new CreateAccountHelper(context, this);
        accountHelper.ResetPassword(password);
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Sending Reset Email");
    }

    public void PasswordResetSuccess(boolean valid, String failMessage) {
        dialog.dismiss();
        if(valid) {
            AlertDialogHelper.showAlertDialog(context, "Reset Password", "Please check your email");
        } else {
            AlertDialogHelper.showAlertDialog(context, "Reset Password", failMessage);
        }
    }

    //endRegion

    //region Verify Email

    private void SetUpVerifyClickEvent() {
        btnSendVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (!TrailKeeperApplication.isEmailVerified()) {
                        SendAccount();
                    } else {
                        Snackbar.make(v, "Your email is already verified", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    public void SendAccount() {
        CreateAccountHelper helper = new CreateAccountHelper(context, this);
        helper.ResendVerifyUserEmail();
    }

    public void VerifySuccess(boolean valid, String failMessage) {
        dialog.dismiss();
        if (valid) {
            Log.i(LOG, "Verify Success");
            ShowSuccessfulVerifyMessage();
        } else {
            Log.i(LOG, "Verify Failed");
            Toast.makeText(this, failMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void ShowSuccessfulVerifyMessage() {
        AlertDialogHelper.showAlertDialog(this, "Email Sent", "Please check your email and verify your account to have access to all the features in TrailKeeper.");
    }

    //endregion

    //region Sign In Methods
    private void SetUpSignInClickEvent() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if ((username.getText().length() <= 0) || (password.getText().length() <= 0)) {
                        if (username.getText().length() <= 0) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Snackbar.make(v, "Please enter a Username", Snackbar.LENGTH_LONG).show();
                        }
                        if (password.getText().length() <= 0) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Snackbar.make(v, "Please enter a Password", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        userNameString = username.getText().toString().trim();
                        passwordString = password.getText().toString().trim();
                        SignInToAccount(userNameString, passwordString);
                    }
                } else {
                    AlertDialogHelper.showAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    private void SignInToAccount(String userName, String password) {
        CreateAccountHelper createAccountHelper = new CreateAccountHelper(context, this);
        createAccountHelper.SignIn(userName, password);
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Signing in");
    }

    public void SignInSuccess(boolean valid, String failMessage, String email) {
        dialog.dismiss();
        if (valid){
            Log.i(LOG, "Sign In Success");
            emailString = email;
            SignInWasSuccessful();
        }else{
            Log.i(LOG, "Sign In Failed");
            Toast.makeText(this, failMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void SignInWasSuccessful() {
        userNameString = ParseUser.getCurrentUser().getUsername();
        isSignedIn = true;
        showCorrectView();
    }
    //endregion
}
