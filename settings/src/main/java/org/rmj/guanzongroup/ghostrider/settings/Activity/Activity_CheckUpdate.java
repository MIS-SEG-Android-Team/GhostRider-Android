/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/28/21 11:11 AM
 * project file last modified : 6/28/21 11:11 AM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.button.MaterialButton;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.utils.UpdateChecker;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMCheckUpdate;

import java.util.Objects;

public class Activity_CheckUpdate extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout newUpdate;
    private RelativeLayout lnProgress;
    private TextView lblCurrent, lblnewVrsn, lblProgress, lblMessage;
    private ProgressBar prgUpdate, prgDownload;
    private MaterialButton btnUpdate;
    private VMCheckUpdate mViewModel;

    private MessageBox poMessage;

    private boolean isDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_update);
        mViewModel = new ViewModelProvider(this).get(VMCheckUpdate.class);
        poMessage = new MessageBox(Activity_CheckUpdate.this);
        toolbar = findViewById(R.id.toolbar_checkUpdate);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        newUpdate = findViewById(R.id.linear_newVersion);
        lnProgress = findViewById(R.id.linear_progress);
        lblCurrent = findViewById(R.id.lbl_currentVersion);
        lblProgress = findViewById(R.id.lbl_progressUpdate);
        prgUpdate = findViewById(R.id.progress_update);
        prgDownload = findViewById(R.id.progress_download);
        lblnewVrsn = findViewById(R.id.lbl_newVersion);
        lblMessage = findViewById(R.id.lbl_errorMessage);
        btnUpdate = findViewById(R.id.btn_Update);

        Sprite loDrawable = new FadingCircle();
        loDrawable.setColor(getResources().getColor(R.color.guanzon_digital_orange));
        prgDownload.setIndeterminateDrawable(loDrawable);

        mViewModel.getVersionInfo().observe(this, s -> lblCurrent.setText(s));

        btnUpdate.setOnClickListener(v -> {
            String lsModel = android.os.Build.MODEL;
            String lsCompx = android.os.Build.MANUFACTURER;
//            String lsCompx = "Samsung";
            if(lsCompx.toLowerCase().equalsIgnoreCase("huawei")){
                mViewModel.DownloadUpdate(new VMCheckUpdate.SystemUpateCallback() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void OnDownloadUpdate(String title, String message) {
                        lnProgress.setVisibility(View.VISIBLE);
                        btnUpdate.setText("Downloading Update...");
                        btnUpdate.setEnabled(false);
                        isDownloading = true;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void OnProgressUpdate(int progress) {
                        prgUpdate.setIndeterminate(false);
                        prgUpdate.setMax(100);
                        prgUpdate.setProgress(progress);
                        lblProgress.setText(progress+"%");
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void OnFinishDownload(Intent intent) {
                        startActivity(intent);
                        lnProgress.setVisibility(View.GONE);
                        btnUpdate.setText("Updated");
                        btnUpdate.setEnabled(true);
                        isDownloading = false;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void OnFailedDownload(String message) {
                        lnProgress.setVisibility(View.GONE);
                        btnUpdate.setText("Download Update");
                        btnUpdate.setEnabled(true);
                        isDownloading = false;
                        lblMessage.setVisibility(View.VISIBLE);
                        lblMessage.setText(message);
                    }
                });
            } else {
                UpdateChecker loUpdate = new UpdateChecker(Activity_CheckUpdate.this, Activity_CheckUpdate.this);
                loUpdate.Check_Update((result, updateManager) -> {
                    if(result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                        try {
                            updateManager.startUpdateFlowForResult(result,
                                    AppUpdateType.IMMEDIATE,
                                    Activity_CheckUpdate.this,
                                    UpdateChecker.InAppUpdateResult.REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("System Update");
                        poMessage.setMessage("No update available.");
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == UpdateChecker.InAppUpdateResult.REQUEST_CODE){
            if(resultCode != RESULT_OK){
                System.exit(1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(!isDownloading) {
                finish();
            } else {
                GToast.CreateMessage(Activity_CheckUpdate.this, "Unable to exit while downloading updates...", GToast.WARNING).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isDownloading) {
            finish();
        } else {
            GToast.CreateMessage(Activity_CheckUpdate.this, "Unable to exit while downloading updates...", GToast.WARNING).show();
        }
    }
}