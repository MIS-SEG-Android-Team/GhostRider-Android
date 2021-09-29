/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/29/21 1:26 PM
 * project file last modified : 6/29/21 1:26 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.rmj.g3appdriver.dev.CodeGenerator;
import org.rmj.g3appdriver.dev.MySQLAESCrypt;
import org.rmj.guanzongroup.ghostrider.settings.R;

import static org.rmj.guanzongroup.ghostrider.settings.etc.SettingsConstants.GCARD_SCAN;

public class Activity_DigitalGcard extends AppCompatActivity {

    private CodeGenerator loGenerator;

    private Toolbar toolbar;
    private MaterialButton btnScan;
    private TextView lblSource,
                    lblImei,
                    lblGcard,
                    lblUserID,
                    lblMobile,
                    lblDateTm,
                    lblAvlPts,
                    lblModelx,
                    lblTransN,
                    lblTranPN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_gcard);
        loGenerator = new CodeGenerator();
        toolbar = findViewById(R.id.toolbar_gcard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnScan = findViewById(R.id.btn_scanQrCode);
        lblSource = findViewById(R.id.lbl_codeSource);
        lblImei = findViewById(R.id.lbl_codeImei);
        lblGcard = findViewById(R.id.lbl_codeCardNo);
        lblUserID = findViewById(R.id.lbl_codeUserID);
        lblMobile = findViewById(R.id.lbl_codeMobileNo);
        lblDateTm = findViewById(R.id.lbl_codeDateTime);
        lblAvlPts = findViewById(R.id.lbl_codePoints);
        lblModelx = findViewById(R.id.lbl_codeModel);
        lblTransN = findViewById(R.id.lbl_codeTransNox);
        lblTranPN = findViewById(R.id.lbl_codeGetPin);

        btnScan.setOnClickListener(v -> {
            Intent loIntent = new Intent(Activity_DigitalGcard.this, Activity_QrCodeScanner.class);
            startActivityForResult(loIntent, GCARD_SCAN);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GCARD_SCAN &&
                resultCode == Activity.RESULT_OK){
            String lsData = data.getStringExtra("qrdata");
            parseQrCodeData(lsData);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    private void parseQrCodeData(String data){
        try{
            loGenerator.setEncryptedQrCode(data);
            lblSource.setText(loGenerator.getTransSource());
            lblImei.setText(loGenerator.getTransDevImei());
            lblGcard.setText(loGenerator.getGCardNumber());
            lblUserID.setText(loGenerator.getUserID());
            lblMobile.setText(loGenerator.getMobileNumber());
            lblDateTm.setText(loGenerator.getDTransact());
            lblAvlPts.setText(loGenerator.getPointsxx());
            lblModelx.setText(loGenerator.getSourceCD());
            lblTransN.setText(loGenerator.getTransNox());
            lblTranPN.setText(loGenerator.getTransactionPIN());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}