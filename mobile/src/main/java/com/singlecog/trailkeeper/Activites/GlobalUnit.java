package com.singlecog.trailkeeper.Activites;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_unit);
        view = findViewById(R.id.layout1);
        super.onCreateDrawer(view, this);

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
