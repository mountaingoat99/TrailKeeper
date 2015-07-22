package com.singlecog.trailkeeper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.BaseActivity;
import com.singlecog.trailkeeper.Activites.HomeScreen;

import Helpers.CreateAccountHelper;

public class SignIn extends BaseActivity {

    private static String LOG = "SignIn";
    private LinearLayout layout;
    private EditText username, password;
    private Button btnSignIn;
    private String userNameString, passwordString;
    private View v;
    private final Context context = this;
    private InputMethodManager imm;
    private ProgressDialog dialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.onCreateDrawer();
        setUpView();
        if (savedInstanceState != null) {
            userNameString = savedInstanceState.getString("username");
            username.setText(userNameString);
            passwordString = savedInstanceState.getString("password");
            password.setText(passwordString);
        }
        username.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        layout = (LinearLayout)findViewById(R.id.layout1);
        v = layout;
        SetUpClickEvent(v);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(username.getText().length() > 0)
            outState.putString("username", username.getText().toString());
        if(password.getText().length() > 0)
            outState.putString("password", password.getText().toString());
    }

    private void SetUpClickEvent(View v) {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((username.getText().length() <= 0) || (password.getText().length() <= 0)){
                    if(username.getText().length() <= 0){
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Snackbar.make(v, "Please enter a Username", Snackbar.LENGTH_LONG).show();
                    }
                    if(password.getText().length() <= 0) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Snackbar.make(v, "Please enter a Password", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    userNameString = username.getText().toString().trim();
                    passwordString = password.getText().toString().trim();
                    SignInToAccount(userNameString, passwordString);
                }
            }
        });
    }

    private void SignInToAccount(String userName, String password) {
        CreateAccountHelper createAccountHelper = new CreateAccountHelper(context, this);
        createAccountHelper.SignIn(userName, password);
        dialog = new ProgressDialog(SignIn.this);
        dialog.setMessage("Signing In");
        dialog.show();
    }

    public void SignInSuccess(boolean valid, String failMessage) {
        dialog.dismiss();
        if (valid){
            SignInWasSuccessful();
            Log.i(LOG, "Sign In Success");
        }else{
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            Snackbar.make(v, failMessage, Snackbar.LENGTH_LONG).show();
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

    private void setUpView() {
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        btnSignIn = (Button)findViewById(R.id.btn_sign_up);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }
}
