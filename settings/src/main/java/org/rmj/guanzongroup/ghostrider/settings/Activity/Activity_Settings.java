/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/23/21 2:03 PM
 * project file last modified : 6/17/21 1:39 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Fragment.Fragment_Settings;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMSettings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static org.rmj.g3appdriver.etc.AppConstants.CAMERA_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.CONTACT_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.STORAGE_REQUEST;

public class Activity_Settings extends AppCompatActivity {
    Fragment_Settings fragment_settings;

    private VMSettings mViewModel;
    private MessageBox loMessage;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRANTED, DENIED, BLOCKED_OR_NEVER_ASKED })
    public @interface PermissionStatus {}
    public @interface PermissionStatusPhoneState {}

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        mViewModel = new ViewModelProvider(this).get(VMSettings.class);
        loMessage = new MessageBox(Activity_Settings.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.e("instancestate", String.valueOf(savedInstanceState));


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new Fragment_Settings())
                    .commit();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NewApi")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("Request Code", String.valueOf(requestCode));
        Log.e("Permission Code", String.valueOf(permissions));
        Log.e("grantResults", String.valueOf(grantResults));

        switch (requestCode){
            case CAMERA_REQUEST:{
                for(int x = 0 ; x < grantResults.length; x++){
                    getPermissionStatus(this,permissions[x]);
                }
                int index = getPermissionStatus(this, CAMERA);
                if (index == 0){
                    mViewModel.setCameraSummary("Camera Permission Enabled");
                    mViewModel.setCamPermissionsGranted(true);
                }
                if (index == 1){
                    mViewModel.setCameraSummary("Camera Permission Disabled");
                    mViewModel.setCamPermissionsGranted(false);
                }
                if (index == 2){
                    mViewModel.setCameraSummary("Camera Permission Disabled");
                    mViewModel.setCamPermissionsGranted(false);
                    showDialogNeverAsk();
                }
                Log.e("CAMERA Permission", String.valueOf(getPermissionStatus(this, CAMERA)));
                break;
            }
            case CONTACT_REQUEST: {

                int index = getPermissionStatus(this, READ_PHONE_STATE);
                if (index == 0){
                    mViewModel.setCameraSummary("Phone State Permission Enabled");
                    mViewModel.setPhonePermissionsGranted(true);
                }
                if (index == 1){
                    mViewModel.setCameraSummary("Phone State Permission Disabled");
                    mViewModel.setPhonePermissionsGranted(false);
                }
                if (index == 2){
                    mViewModel.setCameraSummary("Phone State Permission Disabled");
                    mViewModel.setPhonePermissionsGranted(false);
                    showDialogNeverAsk();
                }
                Log.e("CONTACT Permission", String.valueOf(getPermissionStatus(this, READ_PHONE_STATE)));
                break;
            }
            case  STORAGE_REQUEST: {
                int index = getPermissionStatus(this, READ_EXTERNAL_STORAGE);
                if (index == 0){
                    mViewModel.setCameraSummary("Storage State Permission Enabled");
                    mViewModel.setPhonePermissionsGranted(true);
                }
                if (index == 1){
                    mViewModel.setCameraSummary("Storage State Permission Disabled");
                    mViewModel.setPhonePermissionsGranted(false);
                }
                if (index == 2){
                    mViewModel.setCameraSummary("Storage State Permission Disabled");
                    mViewModel.setPhonePermissionsGranted(false);

                }
                Log.e("Storage Permission", String.valueOf(getStoragePermissionStatus(this, READ_EXTERNAL_STORAGE)));

            }
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @PermissionStatus
    public static int getPermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                return BLOCKED_OR_NEVER_ASKED;
            }
            return DENIED;
        }else {
            return GRANTED;
        }
    }

    @PermissionStatus
    public static int getStoragePermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                return BLOCKED_OR_NEVER_ASKED;
            }
            return DENIED;
        }else {
            return GRANTED;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogNeverAsk(){

        loMessage.initDialog();
        loMessage.setNegativeButton("Okay", (view, dialog) -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);

           // Manifest.permission.READ_EXTERNAL_STORAGE = String.valueOf(PackageManager.PERMISSION_GRANTED);
        });
        loMessage.setTitle("Never Ask");
        loMessage.setMessage("Without this permission the app is unable to complete some process in the app. Please allow camera in app settings permissions. ");
        loMessage.show();
    }


}