package org.rmj.guanzongroup.ghostrider.epacss.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchOpeningMonitor;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Notifications.data.SampleData;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.OnCheckEmployeeApplicationListener;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_AreaPerformanceMonitoring;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_CashCounter;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_Inventory;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.BranchOpeningAdapter;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMHomeAH;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.AdapterAnnouncements;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.Adapter.EmployeeApplicationAdapter;

import java.util.List;

public class Fragment_Home_AH extends Fragment {
    private static final String TAG = Fragment_Home_AH.class.getSimpleName();

    private VMHomeAH mViewModel;
    private View view;
    private MaterialTextView lblFullNme ,lblDept;
    private MaterialTextView mcGoalPerc,mcGoalFraction,spGoalPerc,spGoalFraction,joGoalPerc,joGoalFraction;
    private MaterialCardView CashCount,Inventory,btnPerformance;
    private CircularProgressIndicator mcIndicator,spIndicator,joIndicator;
    private RecyclerView rvBranchOpen;
    private RecyclerView rvCompnyAnouncemnt, rvLeaveApp, rvBusTripApp;

    public static Fragment_Home_AH newInstance() {
        return new Fragment_Home_AH();
    }

    @SuppressLint({"NonConstantResourceId", "MissingInflatedId"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(VMHomeAH.class);
        view = inflater.inflate(R.layout.fragment_home_ah, container, false);

        initWidgets();
        initButton();
        initUserInfo();
        initGoals();
        initBranchOpening();
        initCompanyNotice();

        return view;
    }

    private void initWidgets(){

        lblFullNme = view.findViewById(R.id.lblEmpNme);
        lblDept = view.findViewById(R.id.lblEmpPosition);
        CashCount = view.findViewById(R.id.cv_cashCount);
        Inventory = view.findViewById(R.id.cv_inventory);

        mcIndicator = view.findViewById(R.id.cpi_mc_sales);
        mcGoalPerc = view.findViewById(R.id.lblMCGoal);
        mcGoalFraction = view.findViewById(R.id.lblMCGoalPercent);

        spIndicator = view.findViewById(R.id.cpi_sp_sales);
        spGoalPerc = view.findViewById(R.id.lblSPGoal);
        spGoalFraction = view.findViewById(R.id.lblSPGoalPercent);

        joIndicator = view.findViewById(R.id.cpi_job_order);
        joGoalPerc = view.findViewById(R.id.lblJobOrderPercent);
        joGoalFraction = view.findViewById(R.id.lblJobOrder);

        btnPerformance = view.findViewById(R.id.cb_performance);

        rvBranchOpen = view.findViewById(R.id.rvBranchOpen);
        rvCompnyAnouncemnt = view.findViewById(R.id.rvCompnyAnouncemnt);
        rvLeaveApp = view.findViewById(R.id.rvLeaveApp);
        rvBusTripApp = view.findViewById(R.id.rvBusTripApp);
    }

    public void initButton(){
        btnPerformance.setOnClickListener(new View.OnClickListener() {
            Intent loIntent;
            @Override
            public void onClick(View view) {
                loIntent = new Intent(getActivity(), Activity_AreaPerformanceMonitoring.class);
                loIntent.putExtra("index","0");
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }
        });
        CashCount.setOnClickListener(new View.OnClickListener() {
            Intent loIntent;
            @Override
            public void onClick(View view) {
                loIntent = new Intent(getActivity(), Activity_CashCounter.class);
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }

        });
        Inventory.setOnClickListener(new View.OnClickListener() {
            Intent loIntent;
            @Override
            public void onClick(View view) {
                loIntent = new Intent(getActivity(), Activity_Inventory.class);
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }

        });
    }
    private void initGoals(){
        mViewModel.GetCurrentMCSalesPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String mc_goal) {
                try {
                    mcGoalPerc.setText(mc_goal);
                    if (mc_goal.contains("/")) {
                        String[] rat = mc_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1]) == 0)) {
                            mcGoalFraction.setText("0%");
                            mcIndicator.setProgress(0);
                        } else {
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            mcGoalFraction.setText(Math.round(ratio) + "%");
                            mcIndicator.setProgress((int) ratio);
                            Log.e("ito ung goal mc", String.valueOf(Math.round(ratio)));
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.GetCurentSPSalesPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String sp_goal) {
                try {
                    spGoalPerc.setText(sp_goal);
                    if (sp_goal.contains("/")){
                        String[] rat = sp_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1])== 0)){
                            spGoalFraction.setText( "0%");
                            spIndicator.setProgress(0);
                        }else{
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            spGoalFraction.setText(Math.round(ratio) + "%");
                            spIndicator.setProgress((int) ratio);
                            Log.e("1 goal perc", sp_goal);
                            Log.e("ito ung goal perc", String.valueOf(Math.round(ratio)));
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.GetJobOrderPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String jo_goal) {
                try {
                    joGoalFraction.setText(jo_goal);
                    if (jo_goal.contains("/")) {
                        String[] rat = jo_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1]) == 0)) {
                            joGoalPerc.setText("0%");
                            joIndicator.setProgress(0);
                        } else {
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            joGoalPerc.setText(Math.round(ratio) + "%");
                            joIndicator.setProgress((int) ratio);
                            Log.e("ito ung goal jo", String.valueOf(Math.round(ratio)));
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initUserInfo(){
        mViewModel.getEmployeeInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                //lblEmail.setText(eEmployeeInfo.getEmailAdd());
//                lblUserLvl.setText(DeptCode.parseUserLevel(eEmployeeInfo.getEmpLevID()));
                lblFullNme.setText(eEmployeeInfo.getUserName());
                lblDept.setText(DeptCode.parseUserLevel(eEmployeeInfo.getEmpLevID()));

                initEmployeeApp(eEmployeeInfo);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void initBranchOpening(){
        mViewModel.GetRecentlyOpenBranches().observe(requireActivity(), new Observer<List<DBranchOpeningMonitor.BranchOpeningInfo>>() {
            @Override
            public void onChanged(List<DBranchOpeningMonitor.BranchOpeningInfo> opening) {
                try{
                    if(opening == null){
                        return;
                    }

                    BranchOpeningAdapter loAdapter = new BranchOpeningAdapter(requireActivity(), opening , new BranchOpeningAdapter.OnAdapterItemClickListener() {
                        @Override
                        public void OnClick() {

                        }

                    });
                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    rvBranchOpen.setLayoutManager(loManager);
                    rvBranchOpen.setAdapter(loAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
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