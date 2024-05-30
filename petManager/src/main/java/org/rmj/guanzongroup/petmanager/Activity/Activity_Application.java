/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.petmanager.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_Approval;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_BusinessTripApproval;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_Employee_Applications;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_LeaveApplication;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_LeaveApproval;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_ObApplication;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_Reimbursement;
import org.rmj.guanzongroup.petmanager.Fragment.Fragment_SelfieLog;
import org.rmj.guanzongroup.petmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Application extends AppCompatActivity {
    public static final String TAG = Activity_Application.class.getSimpleName();
    private static Activity_Application instance;

    private String sTransNox = "";

    public static Activity_Application getInstance(){
        return instance;
    }

    public String getTransNox(){
        return sTransNox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        instance = this;
        int application = getIntent().getIntExtra("app", 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_application);

        ViewPager viewPager = findViewById(R.id.viewpager_application);

        if (application == AppConstants.INTENT_OB_APPLICATION) {
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_ObApplication()));
        } else if(application == AppConstants.INTENT_LEAVE_APPLICATION){
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_LeaveApplication()));
        } else if(application == AppConstants.INTENT_SELFIE_LOGIN){
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_SelfieLog()));
        } else if(application == AppConstants.INTENT_REIMBURSEMENT){
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_Reimbursement()));
        } else if(application == AppConstants.INTENT_APPLICATION_APPROVAL){
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_Approval()));
        } else if(application == AppConstants.INTENT_LEAVE_APPROVAL){
            sTransNox = getIntent().getStringExtra("sTransNox");
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_LeaveApproval()));
        } else if(application == AppConstants.INTENT_OB_APPROVAL){
            sTransNox = getIntent().getStringExtra("sTransNox");
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_BusinessTripApproval()));
        } else {
            viewPager.setAdapter(new ApplicationPageAdapter(getSupportFragmentManager(), new Fragment_Employee_Applications()));
            toolbar.setTitle("");
        }
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ApplicationPageAdapter extends FragmentStatePagerAdapter{

        private final List<Fragment> fragment = new ArrayList<>();

        public ApplicationPageAdapter(@NonNull FragmentManager fm, Fragment fragment) {
            super(fm);
            this.fragment.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragment.get(position);
        }

        @Override
        public int getCount() {
            return fragment.size();
        }
    }
}