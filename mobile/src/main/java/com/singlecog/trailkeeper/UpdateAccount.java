package com.singlecog.trailkeeper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.singlecog.trailkeeper.Activites.HomeScreen;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;

public class UpdateAccount extends BaseActivity {

    private static String LOG = "UpdateAccount";
    private LinearLayout layout2, layout3, layoutET;
    private RelativeLayout layout1;
    private EditText username, password;
    private Button btnSignIn, btnUpdateEmail, btnUpdateUsername, btnUpdatePassword, btnSendVerify;
    private String emailString, newEmailString, userNameString, newUserNameString, passwordString;
    private View v;
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
        }
        connectionDetector = new ConnectionDetector(getApplicationContext());
        username.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        SetUpSignInClickEvent();
    }

    //region Activity Methods
    private void SetUpView(){
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSendVerify = (Button)findViewById(R.id.btn_verify_email);
        btnUpdateEmail = (Button)findViewById(R.id.btn_change_email);
        btnUpdatePassword = (Button)findViewById(R.id.btn_reset_password);
        btnUpdateUsername = (Button)findViewById(R.id.btn_change_username);
        layout1 = (RelativeLayout)findViewById(R.id.layout_btn1);
        layout2 = (LinearLayout)findViewById(R.id.layout_btn2);
        layout3 = (LinearLayout)findViewById(R.id.layout_btn3);
        layoutET = (LinearLayout)findViewById(R.id.layout_edittexts);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(username.getText().length() > 0)
            outState.putString("username", username.getText().toString());
        if(password.getText().length() > 0)
            outState.putString("password", password.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_update_account, menu);
        return true;
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
        layout1.setVisibility(View.GONE);
        layoutET.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);
    }
    //endregion
}
