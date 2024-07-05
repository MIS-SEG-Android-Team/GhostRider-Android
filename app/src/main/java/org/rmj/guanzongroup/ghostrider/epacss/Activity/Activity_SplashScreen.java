/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.Activity;

import static org.rmj.g3appdriver.utils.ServiceScheduler.FIFTEEN_MINUTE_PERIODIC;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.textview.MaterialTextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.TransparentToolbar;
import org.rmj.g3appdriver.utils.AppDirectoryCreator;
import org.rmj.g3appdriver.utils.ServiceScheduler;
import org.rmj.guanzongroup.authlibrary.Activity.Activity_Login;
import org.rmj.guanzongroup.ghostrider.epacss.BuildConfig;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.Service.DataDownloadService;
import org.rmj.guanzongroup.ghostrider.epacss.Service.GMessagingService;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMSplashScreen;

import java.util.ArrayList;
import java.util.List;

public class Activity_SplashScreen extends AppCompatActivity {
    public static final String TAG = Activity_SplashScreen.class.getSimpleName();

    private VMSplashScreen mViewModel;

    private ProgressBar prgrssBar;
    private MaterialTextView lblVrsion;

    private MessageBox poDialog;

    private ActivityResultLauncher<String[]> poRequest;

    private ActivityResultLauncher<Intent> poLogin;

    private ServiceScheduler loSched;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        mViewModel = new ViewModelProvider(this).get(VMSplashScreen.class);
        poDialog = new MessageBox(Activity_SplashScreen.this);
        prgrssBar = findViewById(R.id.progress_splashscreen);
        lblVrsion = findViewById(R.id.lbl_versionInfo);
        lblVrsion.setText(BuildConfig.VERSION_NAME);
        loSched = new ServiceScheduler(this);

        InitActivityResultLaunchers();

        new TransparentToolbar(Activity_SplashScreen.this).SetupActionbar();

        startService(new Intent(Activity_SplashScreen.this, GMessagingService.class));
        Log.e(TAG, "Firebase messaging service started.");

        InitializeAppContentDisclosure();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    mViewModel.SaveFirebaseToken(token);
                    AppConfigPreference.getInstance(Activity_SplashScreen.this).setAppToken(token);
                });

        AppDirectoryCreator loCreator = new AppDirectoryCreator();
        if(loCreator.createAppDirectory(Activity_SplashScreen.this)){
            Log.e(TAG, loCreator.getMessage());
        } else {
            Log.e(TAG, loCreator.getMessage());
        }
    }

    private void InitializeAppContentDisclosure(){
        boolean isFirstLaunch = AppConfigPreference.getInstance(Activity_SplashScreen.this).isAppFirstLaunch();
        if(isFirstLaunch) {
            MessageBox loMessage = new MessageBox(Activity_SplashScreen.this);
            loMessage.initDialog();
            loMessage.setTitle("Guanzon Circle");
            loMessage.setMessage("Guanzon Circle collects location data for Selfie Log, DCP and other major features of the app" +
                    " even when the app is closed or not in use.");
            loMessage.setPositiveButton("Agree", (view, dialog) -> {
                dialog.dismiss();
                CheckPermissions();
            });
            loMessage.setNegativeButton("Disagree", (view, dialog) -> {
                dialog.dismiss();
                finish();
            });
            loMessage.show();
            findViewById(R.id.lblFirstLaunchNotice).setVisibility(View.VISIBLE);
        } else {
            CheckPermissions();
        }
    }

    private void CheckPermissions(){
        List<String> lsPermissions = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.INTERNET);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.GET_ACCOUNTS);
        }
        if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            lsPermissions.add(Manifest.permission.CAMERA);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ActivityCompat.checkSelfPermission(Activity_SplashScreen.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                lsPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        poRequest.launch(lsPermissions.toArray(new String[0]));
    }

    private void InitializeAppData(){
        mViewModel.InitializeData(new VMSplashScreen.OnInitializeCallback() {
            @Override
            public void OnProgress(String args, int progress) {
                prgrssBar.setProgress(progress);
            }

            @Override
            public void OnSuccess() {
                startActivity(new Intent(Activity_SplashScreen.this, Activity_Main.class));
                finish();
            }

            @Override
            public void OnNoSession() {
                poLogin.launch(new Intent(Activity_SplashScreen.this, Activity_Login.class));
            }

            @Override
            public void OnFailed(String message) {
                poDialog.initDialog();
                poDialog.setTitle("Guanzon Circle");
                poDialog.setMessage(message);
                poDialog.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                    finish();
                });
                poDialog.show();
            }
        });
    }

    private void InitActivityResultLaunchers(){
        poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            InitializeAppData();
        });

        poLogin = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d(TAG, String.valueOf(result.getResultCode()));
            if (result.getResultCode() == RESULT_OK) {
                startActivity(new Intent(Activity_SplashScreen.this, Activity_Main.class));

                loSched.scheduleJob(Activity_SplashScreen.this, DataDownloadService.class, FIFTEEN_MINUTE_PERIODIC, AppConstants.DataServiceID);
                finish();
            } else if (result.getResultCode() == RESULT_CANCELED) {
                finish();
            }
        });
    }
}