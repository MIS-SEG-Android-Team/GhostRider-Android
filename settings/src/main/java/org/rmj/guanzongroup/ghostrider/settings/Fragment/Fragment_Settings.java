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

import androidx.annotation.NonNull;
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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_AppVersion;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_Developer;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_HelpList;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_LocalData;
import org.rmj.guanzongroup.ghostrider.settings.Dialog.Dialog_ChangePassword;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMSettings;
import org.rmj.guanzongroup.ghostrider.settings.utils.DatabaseExport;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static org.rmj.g3appdriver.etc.AppConstants.CAMERA_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.DEV_MODE;
import static org.rmj.g3appdriver.etc.AppConstants.LOCATION_REQUEST;
import static org.rmj.g3appdriver.etc.AppConstants.STORAGE_REQUEST;

public class Fragment_Settings  extends PreferenceFragmentCompat {

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraPref = getPreferenceManager().findPreference("cameraPrefs");
        locationPref = getPreferenceManager().findPreference("locationPrefs");
        phonePref = getPreferenceManager().findPreference("phonePrefs");
        exportPref = getPreferenceManager().findPreference("exportPrefs");
        localData = getPreferenceManager().findPreference("localDataPrefs");
        Accountxx = getPreferenceManager().findPreference("accountPrefs");
        chkUpdate = getPreferenceManager().findPreference("appUpdatePrefs");
        debugMode = getPreferenceScreen().findPreference("appDebugModePref");
        helpPref = getPreferenceManager().findPreference("appHelpPrefs");

        dbExport = new DatabaseExport(requireActivity());
        loMessage = new MessageBox(requireActivity());
        poDialog = new LoadDialog(requireActivity());
        mViewModel = new ViewModelProvider(this).get(VMSettings.class);

        mViewModel.isLocPermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
            GetLocation();
        });

        mViewModel.isCamPermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
            Log.e("Camera ", String.valueOf(isGranted));
            checkPermissionCamera();
        });

        mViewModel.getCameraSummary().observe(getViewLifecycleOwner(),s -> cameraPref.setSummary(s));

        if (cameraPref != null) {
            cameraPref.setOnPreferenceClickListener(preference -> {
                if ((ActivityCompat.checkSelfPermission(requireActivity(), CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    mViewModel.getCamPermissions().observe(getViewLifecycleOwner(), strings -> {
                        ActivityCompat.requestPermissions(requireActivity(),strings, CAMERA_REQUEST);
                    });
                }else {
                    loMessage.initDialog();
                    loMessage.setIcon(R.drawable.baseline_message_24);
                    loMessage.setNegativeButton("Okay", (v, dialog) -> dialog.dismiss());
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
                        ActivityCompat.requestPermissions(requireActivity(),strings, STORAGE_REQUEST);

                    });
                }else {

                    loMessage.initDialog();
                    loMessage.setNegativeButton("Okay", (v, dialog) -> dialog.dismiss());
                    loMessage.setIcon(R.drawable.baseline_message_24);
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
                    dbExport.export();
//                   mViewModel.isStoragePermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
//                      if (!isGranted){
//                          mViewModel.getStoragePermission().observe(getViewLifecycleOwner(), strings -> {
//                              ActivityCompat.requestPermissions(requireActivity(),strings, STORAGE_REQUEST);
//                          });
//                      }else {
//                      }
//                   });

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
                Intent loIntent = new Intent(requireActivity(), Activity_LocalData.class);
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }

        if(Accountxx != null){
            Accountxx.setOnPreferenceClickListener(preference -> {
                Dialog_ChangePassword loPass = new Dialog_ChangePassword(requireActivity(), (OldPass, NewPass, dialog) -> mViewModel.ChangePassword(OldPass, NewPass, new VMSettings.ChangePasswordCallback() {
                    @Override
                    public void OnLoad(String Title, String Message) {
                        poDialog.initDialog(Title, Message, false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poDialog.dismiss();
                        loMessage.initDialog();
                        loMessage.setIcon(R.drawable.baseline_message_24);
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
                        loMessage.setIcon(R.drawable.baseline_error_24);
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
                startActivity(new Intent(requireActivity(), Activity_AppVersion.class));
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }

        if(debugMode != null){
            /**
            TODO allow users to change IP CONNECTION,
             intended for MOBILE FIESTA setup and APPLICATION TESTING.
             But also back up, incase their is a change of the production server link.

            EmployeeSession poUser = EmployeeSession.getInstance(requireActivity());
            if (!poUser.getDeptID().equalsIgnoreCase(DeptCode.MANAGEMENT_INFORMATION_SYSTEM)){
                debugMode.setVisible(false);
            } else {
                debugMode.setVisible(true);
            }
             **/
            debugMode.setVisible(true);
            debugMode.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), Activity_Developer.class));
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                return false;
            });
        }
        if(helpPref != null){
            helpPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), Activity_HelpList.class));
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