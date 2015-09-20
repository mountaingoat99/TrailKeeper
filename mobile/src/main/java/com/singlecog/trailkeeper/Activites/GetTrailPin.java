package com.singlecog.trailkeeper.Activites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.singlecog.trailkeeper.R;

import java.util.List;

import Helpers.AlertDialogHelper;
import models.ModelTrails;

public class GetTrailPin extends BaseActivity {

    private static String LOG = "GetTrailPin";
    private RelativeLayout mainLayout;
    private TextView txtName, txtEmail, txtReason;
    private AutoCompleteTextView txtTrailName;
    private Button btnGetPin;
    private List<String> trailNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_trail_pin);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
        super.onCreateDrawer(mainLayout, this);

        trailNames = ModelTrails.GetTrailNames();

        setUpViews();
        setUpOnClick();
    }

    private void setUpOnClick() {
        btnGetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().length() > 0 && txtTrailName.getText().length() > 0
                        && txtEmail.getText().length() > 0 && txtReason.getText().length() > 0) {
                    SendEmail(v);
                } else {
                    Snackbar.make(v, "Please Enter All Fields", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SendEmail(View v) {
        String emailBody = txtName.getText().toString().trim() +  " - Email: " + txtEmail.getText().toString().trim()
                            + " - is requesting a Trail Pin for Trail: " + txtTrailName.getText().toString().trim()
                            + " because \"" + txtReason.getText().toString().trim() + "\"";

        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        // prompts email clients only
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"singlecogsoftware@outlook.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Trail Pin Request: " + txtTrailName.getText().toString().trim());
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
        txtEmail.setText("");
        txtTrailName.setText("");
        txtReason.setText("");
    }

    private void setUpViews() {
        txtName = (TextView)findViewById(R.id.edittext_name);
        txtEmail = (TextView)findViewById(R.id.edittext_email);
        txtReason = (TextView)findViewById(R.id.edittext_reason);
        txtTrailName = (AutoCompleteTextView)findViewById(R.id.edittext_trail_name);
        btnGetPin = (Button)findViewById(R.id.btn_get_pin);

        // set the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trailNames);
        txtTrailName.setAdapter(adapter);
        txtTrailName.setThreshold(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_get_trail_pin, menu);
        return true;
    }
}
