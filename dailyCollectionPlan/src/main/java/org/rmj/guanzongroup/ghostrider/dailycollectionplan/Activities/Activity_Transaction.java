/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Activitiy.Activity_Manual;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_CustomerNotAround;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_IncTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_LoanUnit;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_PaidTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_PromiseToPay;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import com.google.android.material.appbar.MaterialToolbar;


import java.util.Objects;

public class Activity_Transaction extends AppCompatActivity {
    private static final String TAG = Activity_Transaction.class.getSimpleName();
    private static Activity_Transaction instance;
    private String TransNox = "";
    private int EntryNox;
    private String AccntNox = "";
    private String Remarksx = "";

    public static Activity_Transaction getInstance(){
        return instance;
    }

    public String getTransNox(){
        return TransNox;
    }

    public int getEntryNox(){
        return EntryNox;
    }

    public String getAccntNox(){
        return AccntNox;
    }

    public String getRemarksCode(){
        return Remarksx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction);

        instance = this;

        try {
            Remarksx = getIntent().getStringExtra("remarksx");
            TransNox = getIntent().getStringExtra("transnox");
            EntryNox = getIntent().getIntExtra("entrynox", 0);
            AccntNox = getIntent().getStringExtra("accntnox");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RuntimeException r) {
            r.printStackTrace();
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_transaction);
        ViewPager2 viewPager = findViewById(R.id.viewpager_transaction);
        ImageButton helpbtn = findViewById(R.id.helpbtn);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FragmentAdapter loAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        loAdapter.initFragment(getTransactionFragment(Remarksx));
        viewPager.setAdapter(loAdapter);

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loIntent = new Intent(Activity_Transaction.this, Activity_Manual.class);
                startActivity(loIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModelStore().clear();
    }

    public static class FragmentAdapter extends FragmentStateAdapter {

        private Fragment fragment;

        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void initFragment(Fragment fragment){
            this.fragment = fragment;
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragment;
        }
        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private Fragment getTransactionFragment(String transaction){
        if(transaction.equalsIgnoreCase("Paid")){
            return new Fragment_PaidTransaction();
        } else if(transaction.equalsIgnoreCase("Promise to Pay")){
            return new Fragment_PromiseToPay();
        } else if(transaction.equalsIgnoreCase("Customer Not Around")){
            return new Fragment_CustomerNotAround();
        } else if(transaction.equalsIgnoreCase("Loan Unit") ||
                transaction.equalsIgnoreCase("False Ownership") ||
                transaction.equalsIgnoreCase("Transferred/Assumed")){
            return new Fragment_LoanUnit();
        } else if(transaction.equalsIgnoreCase("Car nap") ||
                transaction.equalsIgnoreCase("For Legal Action") ||
                transaction.equalsIgnoreCase("Uncooperative") ||
                transaction.equalsIgnoreCase("Missing Customer") ||
                transaction.equalsIgnoreCase("Missing Unit") ||
                transaction.equalsIgnoreCase("Missing Client and Unit") ||
                transaction.equalsIgnoreCase("Did Not Pay") ||
                transaction.equalsIgnoreCase("Not Visited")){

            Fragment_IncTransaction fragmentTrans = new Fragment_IncTransaction();

            Bundle params = new Bundle();
            params.putString("transaction", transaction);

            fragmentTrans.setArguments(params);
            return fragmentTrans;
        }
        return null;
    }
}