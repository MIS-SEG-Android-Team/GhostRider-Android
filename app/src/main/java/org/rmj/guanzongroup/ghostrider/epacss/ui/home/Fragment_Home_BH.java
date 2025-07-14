package org.rmj.guanzongroup.ghostrider.epacss.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Notifications.data.SampleData;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.OnCheckEmployeeApplicationListener;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_BranchPerformanceMonitoring;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMHomeBH;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.AdapterAnnouncements;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.Adapter.EmployeeApplicationAdapter;
import java.util.List;

public class Fragment_Home_BH extends Fragment {
    private static final String TAG = Fragment_Home_AH.class.getSimpleName();
    private MaterialTextView lblFullNme;
    private MaterialTextView lblDept;
    private String lblBranchCD,lblBranchNM;
    private RecyclerView rvCompnyAnouncemnt, rvLeaveApp, rvBusTripApp;
    private CircularProgressIndicator mcIndicator,spIndicator,joIndicator;
    private MaterialTextView mcGoalPerc,mcGoalFraction,spGoalPerc,spGoalFraction,joGoalPerc,joGoalFraction;
    private VMHomeBH mViewModel;
    private MaterialCardView btnPerformance;

    public static Fragment_Home_BH newInstance() {
        return new Fragment_Home_BH();
    }

    @SuppressLint("NonConstantResourceId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        mViewModel = new ViewModelProvider(this).get(VMHomeBH.class);
        View view = inflater.inflate(R.layout.fragment_home_bh, container, false);

        lblFullNme = view.findViewById(R.id.bhName);
        lblDept = view.findViewById(R.id.bhPosition);

        mcIndicator = view.findViewById(R.id.cpi_mc_sales);
        mcGoalPerc = view.findViewById(R.id.lblMCGoal);
        mcGoalFraction = view.findViewById(R.id.lblMCGoalPercent);

        spIndicator = view.findViewById(R.id.cpi_sp_sales);
        spGoalPerc = view.findViewById(R.id.lblSPGoal);
        spGoalFraction = view.findViewById(R.id.lblSPGoalPercent);

        joIndicator = view.findViewById(R.id.cpi_job_order);
        joGoalPerc = view.findViewById(R.id.lblJobOrder);
        joGoalFraction = view.findViewById(R.id.lblJobOrderPercent);

        rvCompnyAnouncemnt= view.findViewById(R.id.rvCompnyAnouncemnt);
        btnPerformance = view.findViewById(R.id.cb_performance);

        rvLeaveApp = view.findViewById(R.id.rvLeaveApp);
        rvBusTripApp = view.findViewById(R.id.rvBusTripApp);

        initUserInfo();
        initGoals();
        initButton();
        initCompanyNotice();
        return view;
    }

    private void initGoals(){
        mViewModel.GetCurrentMCSalesPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String mc_goal) {
                try {
                    if(mc_goal == null){
                        return;
                    }
                    mcGoalPerc.setText(mc_goal);
                    if (mc_goal.contains("/")) {
                        String[] rat = mc_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1]) == 0)) {
                            mcGoalFraction.setText("0%");
                            mcIndicator.setProgress(0);
                        } else {
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            mcGoalFraction.setText(String.valueOf(Math.round(ratio)) + "%");
                            mcIndicator.setProgress((int) (Math.round(ratio)));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mViewModel.GetJobOrderPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String sp_goal) {
                try {
                    if(sp_goal == null){
                        return;
                    }
                    spGoalPerc.setText(sp_goal);
                    if (sp_goal.contains("/")) {
                        String[] rat = sp_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1]) == 0)) {
                            spGoalFraction.setText("0%");
                            spIndicator.setProgress(0);
                        } else {
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            spGoalFraction.setText(String.valueOf(Math.round(ratio)) + "%");
                            spIndicator.setProgress((int) (Math.round(ratio)));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mViewModel.GetJobOrderPerformance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String jo_goal) {
                try {
                    if(jo_goal == null){
                        return;
                    }
                    joGoalPerc.setText(jo_goal);
                    if (jo_goal.contains("/")) {
                        String[] rat = jo_goal.split("/");
                        if ((Double.parseDouble(rat[0]) == 0) || (Double.parseDouble(rat[1]) == 0)) {
                            joGoalFraction.setText("0%");
                            joIndicator.setProgress(0);
                        } else {
                            double ratio = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]) * 100;
                            joGoalFraction.setText(String.valueOf(Math.round(ratio)) + "%");
                            joIndicator.setProgress((int) (Math.round(ratio)));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void initUserInfo(){
        mViewModel.getEmployeeInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                lblFullNme.setText(eEmployeeInfo.getUserName());
                lblDept.setText(DeptCode.parseUserLevel(eEmployeeInfo.getEmpLevID()));
                lblBranchCD = eEmployeeInfo.getBranchCD();
                lblBranchNM = eEmployeeInfo.getBranchNm();
                initEmployeeApp(eEmployeeInfo);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void initButton(){
        btnPerformance.setOnClickListener(new View.OnClickListener() {
            Intent loIntent;
            @Override
            public void onClick(View view) {
                loIntent = new Intent(getActivity(), Activity_BranchPerformanceMonitoring.class);
                loIntent.putExtra("brnCD", lblBranchCD);
                loIntent.putExtra("brnNM", lblBranchNM);
                Log.e("papaso ko na branch",lblBranchNM);
                startActivity(loIntent);
                requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
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
                            loIntent.putExtra("sTransNox", TransNox);
                            loIntent.putExtra("type", !loInfo.getUserName().equalsIgnoreCase(EmpName));
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