package com.singlecog.trailkeeper.Activites;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.singlecog.trailkeeper.Activites.BaseActivity;
import com.singlecog.trailkeeper.R;

import ParseObjects.ParseTrailUser;

public class CreateAccount extends BaseActivity {

    private ParseTrailUser parseTrailUser;
    private CardView loginCard, successCard;
    private LinearLayout layout;
    private EditText email, username, password;
    private TextView successFailMessage, welcomeMessage, userDataMessage;
    private Button btnSignUp;
    private String emailString, usernameString, passwordString;
    private final Context context = this;
    private InputMethodManager imm;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        super.onCreateDrawer();
        SetUpViews();

        successCard.setVisibility(View.GONE);
        layout = (LinearLayout)findViewById(R.id.layout1);
        View v = layout;
        SetUpClickEvent(v);
    }

    private void SetUpClickEvent(View v) {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // first lets make sure all the fields are legit
                if(!ParseTrailUser.isValidEmail(email.getText()) ||
                        username.getText().length() > 4 ||
                        password.getText().length() > 8) {
                    if (!ParseTrailUser.isValidEmail(email.getText())) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Snackbar.make(v, R.string.snackbar_invalid_email, Snackbar.LENGTH_LONG).show();
                    } else if (username.getText().length() > 4) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Snackbar.make(v, R.string.snackbar_invalid_username, Snackbar.LENGTH_LONG).show();
                    } else if (password.getText().length() > 8) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Snackbar.make(v, R.string.snackbar_invalid_password, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    // TODO update parse user
                }
            }
        });
    }

    private String CreateSuccessMessage(ParseUser currentUser) {
        return getResources().getString(R.string.create_account_success_message_start)
                + currentUser.getUsername()
                + getResources().getString(R.string.create_account_success_message_end);
    }

    private void SetUpViews(){
        email = (EditText)findViewById(R.id.edittext_email);
        username = (EditText)findViewById(R.id.edittext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        successFailMessage = (TextView)findViewById(R.id.txt_success_fail_message);
        welcomeMessage = (TextView)findViewById(R.id.txt_username);
        userDataMessage = (TextView)findViewById(R.id.txt_user_data_message);
        btnSignUp = (Button)findViewById(R.id.btn_sign_up);
        loginCard = (CardView)findViewById(R.id.create_account_card_view);
        successCard = (CardView)findViewById(R.id.account_success_card_view);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }
}
