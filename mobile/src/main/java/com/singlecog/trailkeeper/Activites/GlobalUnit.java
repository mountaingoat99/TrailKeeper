package com.singlecog.trailkeeper.Activites;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.singlecog.trailkeeper.R;

/**
 * Created by Anatoliy on 11/3/2015.
 */


public class GlobalUnit extends BaseActivity {

    private View view;
    private Button btnSave;
    private RadioButton rBtnImperial;
    private RadioButton rBtnMetric;
    private boolean globalUnitDefault = true;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_unit);
        view = findViewById(R.id.layout1);
        super.onCreateDrawer(view, this);

        // Google AdMob
        adView = (AdView) findViewById(R.id.adView);
        // comment out for testing
        //AdRequest adRequest = new AdRequest.Builder().build();
        // Comment out for Production
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("TEST_DEVICE_ID")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9C75E9349CF38EF5EB2C6C6100E96A7E") // nexus 7
                .build();
        // always call this
        adView.loadAd(adRequest);

        setUpView();
        loadSavedPreferences();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtnClick();
                finish();

            }
        });

        if (globalUnitDefault) {
            rBtnImperial.toggle();
        } else {
            rBtnMetric.toggle();
        }

    }

    private void loadSavedPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        globalUnitDefault = sp.getBoolean("Imperial", true);
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void setUpView() {
        btnSave = (Button) findViewById(R.id.btn_save);
        rBtnImperial = (RadioButton) findViewById(R.id.radio_imperial);
        rBtnMetric = (RadioButton) findViewById(R.id.radio_metric);
    }

    private void saveBtnClick() {

        if (rBtnImperial.isChecked()) {
            savePreferences("Imperial", true);
        } else {
            savePreferences("Imperial", false);
        }

}

}
