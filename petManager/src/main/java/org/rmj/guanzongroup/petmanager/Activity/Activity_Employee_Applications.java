/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 9/16/21, 9:35 AM
 * project file last modified : 9/16/21, 9:35 AM
 */

package org.rmj.guanzongroup.petmanager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_BusinessTripList;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_LeaveList;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMEmployeeApplications;

import java.util.Objects;

public class Activity_Employee_Applications extends AppCompatActivity implements VMEmployeeApplications.OnDownloadApplicationListener {
    private VMEmployeeApplications mViewModel;
    private final String[] tabHeaders = {"Leave Applications",
            "Business Trip Applications"};
    private TextView lblBrnchNm, lblBrnchAd, lblHeaderx;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MessageBox poMessage;
    private LoadDialog poDialog;
    private boolean forViewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_applications);

        mViewModel = new ViewModelProvider(this).get(VMEmployeeApplications.class);
        forViewing = getIntent().getBooleanExtra("type", false);

        setupWidgets();

        mViewModel.getUserBranchInfo().observe(Activity_Employee_Applications.this, eBranchInfo -> {
            try{
                lblBrnchNm.setText(eBranchInfo.getBranchNm());
                lblBrnchAd.setText(eBranchInfo.getAddressx());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        mViewModel.DownloadLeaveForApproval(Activity_Employee_Applications.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!forViewing) {
            getMenuInflater().inflate(R.menu.employee_applications, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void setupWidgets(){
        toolbar = findViewById(R.id.toolbar_application);
        tabLayout = findViewById(R.id.tablayout_approval_list);
        viewPager = findViewById(R.id.viewpager_approvals);
        lblHeaderx = findViewById(R.id.lbl_application);
        lblBrnchNm = findViewById(R.id.lbl_headerBranch);
        lblBrnchAd = findViewById(R.id.lbl_headerAddress);

        poMessage = new MessageBox(Activity_Employee_Applications.this);
        poDialog = new LoadDialog(Activity_Employee_Applications.this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tabLayout.addTab(tabLayout.newTab().setText(tabHeaders[0]));
        tabLayout.addTab(tabLayout.newTab().setText(tabHeaders[1]));

        ApplicationsPageAdapter loAdapter = new ApplicationsPageAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(loAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lblHeaderx.setText(tabHeaders[tab.getPosition()]);
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent loIntent;
        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.action_menu_search_leave){
            loIntent = new Intent(Activity_Employee_Applications.this, Activity_Application.class);
            loIntent.putExtra("app", AppConstants.INTENT_LEAVE_APPROVAL);
            loIntent.putExtra("sTransNox", "");
            startActivity(loIntent);
        } else if(item.getItemId() == R.id.action_menu_search_ob){
            loIntent = new Intent(Activity_Employee_Applications.this, Activity_Application.class);
            loIntent.putExtra("app", AppConstants.INTENT_OB_APPROVAL);
            loIntent.putExtra("sTransNox", "");
            startActivity(loIntent);
        } else {
            loIntent = new Intent(Activity_Employee_Applications.this, Activity_Application.class);
            loIntent.putExtra("app", AppConstants.INTENT_APPROVAL_HISTORY);
            loIntent.putExtra("sTransNox", "");
            startActivity(loIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
    }

    @Override
    public void OnDownload(String title, String message) {
        poDialog.initDialog(title, message, false);
        poDialog.show();
    }

    @Override
    public void OnDownloadSuccess() {
        poDialog.dismiss();
    }

    @Override
    public void OnDownloadFailed(String message) {
        poDialog.dismiss();
        showDialogMessage(message);
    }

    private static class ApplicationsPageAdapter extends FragmentStateAdapter {
        private final Fragment[] fragments = {new Fragment_LeaveList(),
                new Fragment_BusinessTripList()};

        public ApplicationsPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }
        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }

    private void showDialogMessage(String message){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_message_24);
        poMessage.setTitle("PET Manager");
        poMessage.setMessage(message);
        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }
}