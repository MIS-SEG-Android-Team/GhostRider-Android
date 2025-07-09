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

package org.rmj.guanzongroup.ghostrider.epacss.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Notifications.data.SampleData;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.OnCheckEmployeeApplicationListener;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMAssociateDashboard;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.AdapterAnnouncements;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.Adapter.EmployeeApplicationAdapter;

import java.util.List;

public class Fragment_Associate_Dashboard extends Fragment {
    private static final String TAG = Fragment_Associate_Dashboard.class.getSimpleName();

    private VMAssociateDashboard mViewModel;

    private View view;

    private MaterialTextView lblFullNme,
            lblUserLvl,
            lblDept,
            lblVersion;

    private RecyclerView rvCompnyAnouncemnt, rvLeaveApp, rvBusTripApp;

    public static Fragment_Associate_Dashboard newInstance() {
        return new Fragment_Associate_Dashboard();
    }


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMAssociateDashboard.class);
        view = inflater.inflate(R.layout.fragment_associate_dashboard, container, false);

        initWidgets();


        mViewModel.getVersionInfo().observe(getViewLifecycleOwner(), s -> lblVersion.setText(s));

        mViewModel.getEmployeeInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                lblFullNme.setText(eEmployeeInfo.getUserName());
                lblUserLvl.setText(DeptCode.parseUserLevel(eEmployeeInfo.getEmpLevID()));
                lblDept.setText(DeptCode.getDepartmentName(eEmployeeInfo.getDeptIDxx()));

                initEmployeeApp(eEmployeeInfo);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getUserBranchInfo().observe(getViewLifecycleOwner(), eBranchInfo -> {
            try {
                if(eBranchInfo != null) {
//                    lblBranch.setText(eBranchInfo.getBranchNm());
//                    lblAddx.setText(eBranchInfo.getAddressx());
                } else {
//                    lblBranch.setText("Downloading Data");
//                    lblAddx.setText("Please wait...");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        initCompanyNotice();
        return view;
    }

    private void initWidgets(){
        lblFullNme = view.findViewById(R.id.lbl_userFullName);
        lblUserLvl = view.findViewById(R.id.lbl_userLevel);
        lblDept = view.findViewById(R.id.lbl_userDepartment);
        rvCompnyAnouncemnt = view.findViewById(R.id.rvCompnyAnouncemnt);
        rvLeaveApp = view.findViewById(R.id.rvLeaveApp);
        rvBusTripApp = view.findViewById(R.id.rvBusTripApp);
        lblVersion = view.findViewById(R.id.lbl_versionInfo);
    }

    private void initCompanyNotice(){
        AdapterAnnouncements loAdapter = new AdapterAnnouncements(SampleData.GetAnnouncementList(), new AdapterAnnouncements.OnItemClickListener() {
            @Override
            public void OnClick(String args) {

            }
        });
        LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
        loManager.setOrientation(RecyclerView.VERTICAL);
        rvCompnyAnouncemnt.setLayoutManager(loManager);
        rvCompnyAnouncemnt.setAdapter(loAdapter);
    }

    private void initEmployeeApp(EEmployeeInfo loInfo){
        mViewModel.CheckApplicationsForApproval(new OnCheckEmployeeApplicationListener() {
            @Override
            public void OnCheck() {
                Log.d(TAG, "Checking employee leave and business trip applications...");
            }

            @Override
            public void OnSuccess() {
                Log.d(TAG, "Leave and business trip applications checked!");
            }

            @Override
            public void OnFailed(String message) {
                Log.e(TAG, message);
            }
        });

        mViewModel.GetLeaveForApproval().observe(requireActivity(), new Observer<List<EEmployeeLeave>>() {
            @Override
            public void onChanged(List<EEmployeeLeave> app) {
                try{
                    if(app == null){
                        return;
                    }

                    if(app.size() == 0){
                        return;
                    }

                    EmployeeApplicationAdapter loAdapter = new EmployeeApplicationAdapter(app, false, new EmployeeApplicationAdapter.OnLeaveItemClickListener() {
                        @Override
                        public void OnClick(String TransNox, String EmpName) {
                            Intent loIntent = new Intent(requireActivity(), Activity_Application.class);
                            loIntent.putExtra("app", AppConstants.INTENT_LEAVE_APPROVAL);
                            loIntent.putExtra("sTransNox", TransNox);
                            loIntent.putExtra("type", !loInfo.getUserName().equalsIgnoreCase(EmpName));
                            startActivity(loIntent);
                        }
                    });

                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    rvLeaveApp.setLayoutManager(loManager);
                    rvLeaveApp.setAdapter(loAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mViewModel.GetOBForApproval().observe(requireActivity(), new Observer<List<EEmployeeBusinessTrip>>() {
            @Override
            public void onChanged(List<EEmployeeBusinessTrip> app) {
                try{
                    if(app == null){
                        return;
                    }

                    if(app.size() == 0){
                        return;
                    }

                    EmployeeApplicationAdapter loAdapter = new EmployeeApplicationAdapter(app, new EmployeeApplicationAdapter.OnOBItemClickListener() {
                        @Override
                        public void OnClick(String TransNox, String EmpName) {
                            Intent loIntent = new Intent(requireActivity(), Activity_Application.class);
                            loIntent.putExtra("app", AppConstants.INTENT_OB_APPROVAL);
                            loIntent.putExtra("type", !loInfo.getUserName().equalsIgnoreCase(EmpName));
                            loIntent.putExtra("sTransNox", TransNox);
                            startActivity(loIntent);
                        }
                    });

                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    rvBusTripApp.setLayoutManager(loManager);
                    rvBusTripApp.setAdapter(loAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}