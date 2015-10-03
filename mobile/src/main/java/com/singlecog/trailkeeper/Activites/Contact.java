package com.singlecog.trailkeeper.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.singlecog.trailkeeper.R;

import Helpers.CreateAccountHelper;


public class Contact extends BaseActivity {

    private RelativeLayout mainLayout;
    private TextView txtName, txtFeedback;
    private Button btnSendEmail;
    private boolean isAnonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
        super.onCreateDrawer(mainLayout, this);
        isAnonUser = CreateAccountHelper.IsAnonUser();

        setUpViews();
        setUpOnClick();
    }

    private void setUpOnClick() {
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().length() > 0 && txtFeedback.getText().length() > 0) {
                    SendEmail(v);
                } else {
                    Snackbar.make(v, "Please Enter All Fields", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SendEmail(View v) {
        String emailBody;
        if(!isAnonUser) {
            emailBody = txtName.getText().toString().trim() + ", Username : " + ParseUser.getCurrentUser().getUsername()
                    + " sent Feedback: \"" + txtFeedback.getText().toString().trim() + "\"";
            } else {
            emailBody = txtName.getText().toString().trim() + ", No UserName"
                    + " sent Feedback: \"" + txtFeedback.getText().toString().trim() + "\"";
        }

        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        // prompts email clients only
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"singlecogsoftware@outlook.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Feedback about Trail Keeper");
        email.putExtra(Intent.EXTRA_TEXT, emailBody);
        try {
            // the user can choose the email client
            startActivity(Intent.createChooser(email, "Choose an email client from..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(v, "No email client installed.", Snackbar.LENGTH_LONG).show();
        }
        ClearFieldsShowMessage();
    }

    private void ClearFieldsShowMessage(){
        txtName.setText("");
        txtFeedback.setText("");
    }

    private void setUpViews() {
        txtName = (TextView)findViewById(R.id.edittext_name);
        txtFeedback = (TextView)findViewById(R.id.edittext_feedback);
        btnSendEmail = (Button)findViewById(R.id.btn_send_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // right now we won't show anything here, but we may add menu items
        // to the individual layouts so we won't move this to the super class
        //getMenuInflater().inflate(R.menu.menu_find_trail, menu);
        return true;
    }
}
