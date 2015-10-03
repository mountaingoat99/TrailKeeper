package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;

import Helpers.AlertDialogHelper;
import Helpers.ConnectionDetector;
import Helpers.CreateAccountHelper;
import Helpers.ProgressDialogHelper;

public class SignIn extends BaseActivity {

    private static String LOG = "SignIn";
    private EditText username, password;
    private Button btnSignIn, btnResetPassword, btnFindUsername;
    private String userNameString, passwordString;
    private final Context context = this;
    private InputMethodManager imm;
    private ProgressDialog dialog;
    private ConnectionDetector connectionDetector;
    private View view;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        view = findViewById(R.id.layout1);
        super.onCreateDrawer(view, this);
        setUpView();
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
        SetUpResetClickEvent();
        SetUpFindUserName();
    }

    //region Activity Methods
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(username.getText().length() > 0)
            outState.putString("username", username.getText().toString());
        if(password.getText().length() > 0)
            outState.putString("password", password.getText().toString());
    }

    private void setUpView() {
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        btnSignIn = (Button)findViewById(R.id.btn_sign_up);
        btnResetPassword = (Button)findViewById(R.id.btn_reset_password);
        btnFindUsername = (Button)findViewById(R.id.btn_find_username);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
    }

    private void SignInToAccount(String userName, String password) {
        CreateAccountHelper createAccountHelper = new CreateAccountHelper(context, this);
        createAccountHelper.SignIn(userName, password);
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Signing in");
    }

    public void SignInSuccess(boolean valid, String failMessage) {
        dialog.dismiss();
        if (valid){
            SignInWasSuccessful();
            Log.i(LOG, "Sign In Success");
        }else{
            if (failMessage.contains("parameters")) {
                failMessage = "Something is wrong, please check your username and password!";
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Snackbar.make(view, failMessage, Snackbar.LENGTH_LONG).show();
            Log.i(LOG, "Sign In Failed");
        }
    }

    private void SignInWasSuccessful() {
        Intent intent = new Intent(context, HomeScreen.class);
        userNameString = ParseUser.getCurrentUser().getUsername();
        Bundle b = new Bundle();
        b.putString("userName", userNameString);
        b.putString("className", "SignIn");
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //endregion

    //region Find Username Methods
    private void SetUpFindUserName() {
        btnFindUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindUserNameDialog();
            }
        });
    }

    private void FindUserNameDialog() {
        final Dialog resetDialog = new Dialog(this);
        resetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resetDialog.setContentView(R.layout.dialog_reset_email);
        final EditText resetPasswordEditText = (EditText) resetDialog.findViewById(R.id.edittext_email);
        Button resetButton = (Button) resetDialog.findViewById(R.id.btn_send_email);
        resetButton.setText("Find Username");
        Button cancelButton = (Button) resetDialog.findViewById(R.id.btn_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDialog.dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (resetPasswordEditText.getText().length() > 0) {
                        if (CreateAccountHelper.isValidEmail(resetPasswordEditText.getText())) {
                            resetDialog.dismiss();
                            FindUsername(resetPasswordEditText.getText().toString());
                        } else {
                            Snackbar.make(v, "Please enter a valid email", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(v, "Please enter a valid email", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        resetDialog.show();
    }

    private void FindUsername(String password){
        CreateAccountHelper accountHelper = new CreateAccountHelper(context, this);
        accountHelper.FindUserName(password);
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Finding Username");
    }

    public void FindUsernameSuccess(boolean valid, String foundUserName) {
        dialog.dismiss();
        if(valid) {
            username.setText(foundUserName);
            Toast.makeText(this, "You are in luck, we found it", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "We did not find it, it's either the wrong email, or you don't have an account", Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region Password Reset Methods
    private void SetUpResetClickEvent() {
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordResetDialog();
            }
        });
    }

    private void PasswordResetDialog() {
        final Dialog resetDialog = new Dialog(this);
        resetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resetDialog.setContentView(R.layout.dialog_reset_email);
        final EditText resetPasswordEditText = (EditText) resetDialog.findViewById(R.id.edittext_email);
        Button resetButton = (Button) resetDialog.findViewById(R.id.btn_send_email);
        Button cancelButton = (Button) resetDialog.findViewById(R.id.btn_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDialog.dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (resetPasswordEditText.getText().length() > 0) {
                        if (CreateAccountHelper.isValidEmail(resetPasswordEditText.getText())) {
                            resetDialog.dismiss();
                            PasswordReset(resetPasswordEditText.getText().toString());
                        } else {
                            Snackbar.make(v, "Please enter a valid email", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(v, "Please enter a valid email", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialogHelper.showCustomAlertDialog(context, "No Connection", "You have no wifi or data connection");
                }
            }
        });
        resetDialog.show();
    }

    private void PasswordReset(String password){
        CreateAccountHelper accountHelper = new CreateAccountHelper(context, this);
        accountHelper.ResetPassword(password);
        dialog = ProgressDialogHelper.ShowProgressDialog(context, "Sending Reset Email");
    }

    public void PasswordResetSuccess(boolean valid, String failMessage) {
        dialog.dismiss();
        if(valid) {
            AlertDialogHelper.showCustomAlertDialog(context, "Reset Password", "Please check your email");
        } else {
            AlertDialogHelper.showCustomAlertDialog(context, "Reset Password", failMessage);
        }
    }
    //endregion
}
