/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/23/21 2:03 PM
 * project file last modified : 6/23/21 2:01 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Fragment;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.rmj.g3appdriver.dev.DeptCode;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.SessionManager;
import org.rmj.guanzongroup.ghostrider.dataChecker.Activity.Activity_DB_Explorer;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_CheckUpdate;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_Developer;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_HelpList;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_LocalData;
import org.rmj.guanzongroup.ghostrider.settings.Dialog.Dialog_ChangePassword;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMSettings;
import org.rmj.guanzongroup.ghostrider.settings.themeController.ThemeHelper;
import org.rmj.guanzongroup.ghostrider.settings.utils.DatabaseExport;

import java.util.Objects;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static org.rmj.g3appdriver.etc.AppConstants.CAMERA_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.DEV_MODE;
import static org.rmj.g3appdriver.etc.AppConstants.LOCATION_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.STORAGE_REQUEST;

public class Fragment_Settings  extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat themePreference;
    private Preference locationPref,
            cameraPref,
            phonePref,
            exportPref,
            localData,
            Accountxx,
            chkUpdate,
            debugMode,
            helpPref;
    private VMSettings mViewModel;
    private DatabaseExport dbExport;
    private MessageBox loMessage;
    private LoadDialog poDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        themePreference = getPreferenceManager().findPreference("themePrefs");
        cameraPref = getPreferenceManager().findPreference("cameraPrefs");
        locationPref = getPreferenceManager().findPreference("locationPrefs");
        phonePref = getPreferenceManager().findPreference("phonePrefs");
        exportPref = getPreferenceManager().findPreference("exportPrefs");
        localData = getPreferenceManager().findPreference("localDataPrefs");
        Accountxx = getPreferenceManager().findPreference("accountPrefs");
        chkUpdate = getPreferenceManager().findPreference("appUpdatePrefs");
        debugMode = getPreferenceScreen().findPreference("appDebugModePref");
        helpPref = getPreferenceManager().findPreference("appHelpPrefs");

        dbExport = new DatabaseExport(getActivity(), "Database", "GGC_ISysDBF.db");
        loMessage = new MessageBox(getActivity());
        poDialog = new LoadDialog(getActivity());
        mViewModel = new ViewModelProvider(this).get(VMSettings.class);

        mViewModel.isLocPermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
            GetLocation();
        });

        mViewModel.isCamPermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
            Log.e("Camera ", String.valueOf(isGranted));
            checkPermissionCamera();
        });

        mViewModel.getCameraSummary().observe(getViewLifecycleOwner(),s -> cameraPref.setSummary(s));

        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                Boolean themeOption = (Boolean) newValue;
                if (themeOption) {
                    themePreference.getSummaryOn();
                } else {
                    themePreference.getSummaryOff();
                }
                ThemeHelper.applyTheme(themeOption);
                return true;
            });
        }
        if (cameraPref != null) {
            cameraPref.setOnPreferenceClickListener(preference -> {
                if ((ActivityCompat.checkSelfPermission(requireActivity(), CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    mViewModel.getCamPermissions().observe(getViewLifecycleOwner(), strings -> {
                        ActivityCompat.requestPermissions(getActivity(),strings, CAMERA_REQUEST);
                    });
                }else {
                    loMessage.initDialog();
                    loMessage.setNegativeButton("Okay", (view, dialog) -> dialog.dismiss());
                    loMessage.setTitle("GhostRider Permissions");
                    loMessage.setMessage("You have already granted this permission.");
                    loMessage.show();
                }
                return true;
            });
        }

        if (locationPref != null) {
            locationPref.setOnPreferenceClickListener(preference -> {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),LOCATION_REQUEST);
                return true;
            });
        }

        if (phonePref != null) {
            phonePref.setOnPreferenceClickListener(preference -> {
                if ((ActivityCompat.checkSelfPermission(requireActivity(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
                    mViewModel.getPhPermissions().observe(getViewLifecycleOwner(), strings -> {
                        ActivityCompat.requestPermissions(getActivity(),strings, STORAGE_REQUEST);

                    });
                }else {

                    loMessage.initDialog();
                    loMessage.setNegativeButton("Okay", (view, dialog) -> dialog.dismiss());
                    loMessage.setTitle("GhostRider Permissions");
                    loMessage.setMessage("You have already granted this permission.");
                    loMessage.show();
                }
                return true;
            });
        }
        if (exportPref != null) {
           exportPref.setOnPreferenceClickListener(preference -> {
               try {
                   mViewModel.isStoragePermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
                      if (!isGranted){
                          mViewModel.getStoragePermission().observe(getViewLifecycleOwner(), strings -> {
                              ActivityCompat.requestPermissions(requireActivity(),strings, STORAGE_REQUEST);
                          });
                      }else {
                          dbExport.export();
                      }
                   });

               }catch (SecurityException e){
                   Log.e("Security Exception " , e.getMessage());
               }
               catch (Exception e){
                   Log.e("Exception " , e.getMessage());
               }

               return false;
           });
        }

        if(localData != null){
            localData.setOnPreferenceClickListener(preference -> {
                Intent loIntent = new Intent(getActivity(), Activity_LocalData.class);
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }

        if(Accountxx != null){
            Accountxx.setOnPreferenceClickListener(preference -> {
                Dialog_ChangePassword loPass = new Dialog_ChangePassword(getActivity(), (OldPass, NewPass, dialog) -> mViewModel.ChangePassword(OldPass, NewPass, new VMSettings.ChangePasswordCallback() {
                    @Override
                    public void OnLoad(String Title, String Message) {
                        poDialog.initDialog(Title, Message, false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poDialog.dismiss();
                        loMessage.initDialog();
                        loMessage.setTitle("Change Password");
                        loMessage.setMessage("Account updated successfully");
                        loMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        loMessage.show();
                        dialog.dismiss();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poDialog.dismiss();
                        loMessage.initDialog();
                        loMessage.setTitle("Change Password");
                        loMessage.setMessage(message);
                        loMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        loMessage.show();
                    }
                }));
                loPass.show();
                return false;
            });
        }
        if(chkUpdate != null){
            chkUpdate.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), Activity_CheckUpdate.class));
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }

        if(debugMode != null){
            SessionManager poUser = new SessionManager(requireActivity());
            if (!poUser.getDeptID().equalsIgnoreCase(DeptCode.MANAGEMENT_INFORMATION_SYSTEM)){
                debugMode.setVisible(false);
            } else {
                debugMode.setVisible(true);
            }
            debugMode.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), Activity_Developer.class));
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }
        if(helpPref != null){
            helpPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), Activity_HelpList.class));
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }

    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }
    @Override
    public void onStart()
    {
        super.onStart();
        checkPermissionCamera();
        checkPermissionPhoneState();
//        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
    }
    @Override
    public void onResume()
    {
        super.onResume();
        checkPermissionCamera();
        checkPermissionPhoneState();
//        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
    }

    boolean checkPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraPref.setSummary("Camera Permission Enabled");
                mViewModel.getCameraSummary().observe(getViewLifecycleOwner(), s -> cameraPref.setSummary(s));
                return true;
            } else {
                cameraPref.setSummary("Camera Permission Disabled");
                mViewModel.getCameraSummary().observe(getViewLifecycleOwner(), s -> cameraPref.setSummary(s));

                return false;
            }
        }
        return true;
    }
    boolean checkPermissionPhoneState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                phonePref.setSummary("Phone State Permission Enabled");
                return true;
            } else {
                phonePref.setSummary("Phone State Permission Disabled");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int requestResult, Intent data) {
        if(requestCode == LOCATION_REQUEST){
            mViewModel.setLocationPermissionsGranted(true);
        } else if(requestCode == DEV_MODE){
            if(requestResult == Activity.RESULT_OK) {
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().finish();
            }
        }
    }
    public void GetLocation(){


        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            locationPref.setSummary(R.string.location_off_summary);

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){


            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            } else {
                locationPref.setSummary(R.string.location_on_summary);
            }
        }
    }
}