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

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_Log_Client_Detail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_Log_CustomerNotAround;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_Log_OtherTransactions;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_Log_Paid_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments.Fragment_Log_PromiseToPay;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import  com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Objects;

public class Activity_TransactionDetail extends AppCompatActivity {
    private static final String TAG = Activity_TransactionDetail.class.getSimpleName();
    private static Activity_TransactionDetail instance;
    private String psLatitud, psLongitd;
    public static String transNox, fullNme, entryNox, acctNox, remCodex, imgNme, clientID, clientAddress, remarks, psLocate;
    public static String psTransTp;

    public static Activity_TransactionDetail getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionlog);
        instance = this;
        transNox = getIntent().getStringExtra("sTransNox");
        acctNox = getIntent().getStringExtra("acctNox");
        fullNme = getIntent().getStringExtra("fullNme");
        remCodex = getIntent().getStringExtra("remCodex");
        imgNme = getIntent().getStringExtra("imgNme");
        clientID = getIntent().getStringExtra("sClientID");
        clientAddress = getIntent().getStringExtra("sAddressx");
        remarks = getIntent().getStringExtra("sRemarksx");
        psLatitud = getIntent().getStringExtra("nLatitude");
        psLongitd = getIntent().getStringExtra("nLongitud");
        psLocate = "@" + psLatitud + "," + psLongitd;
        psTransTp = DCP_Constants.getRemarksDescription(remCodex);


        MaterialToolbar toolbar = findViewById(R.id.toolbar_transaction);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager_transaction);

        getSupportActionBar().setTitle("");

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getTransactionFragment(remCodex)));
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

    public static class FragmentAdapter extends FragmentStatePagerAdapter {
        private final Fragment fragment;

        public FragmentAdapter(@NonNull FragmentManager fm, Fragment fragment) {
            super(fm);
            this.fragment = fragment;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModelStore().clear();
    }

    private Fragment getTransactionFragment(String transaction){
        if(transaction.equalsIgnoreCase("PAY")) {
            return new Fragment_Log_Paid_Transaction();
        }
        else if(transaction.equalsIgnoreCase("PTP")) {
            return new Fragment_Log_PromiseToPay();
        }
        else if(transaction.equalsIgnoreCase("CNA")){
            return new Fragment_Log_CustomerNotAround();
        }
        else if(transaction.equalsIgnoreCase("FLA") ||
                transaction.equalsIgnoreCase("TA") ||
                transaction.equalsIgnoreCase("FO") ||
                transaction.equalsIgnoreCase("LUn")) {
            return new Fragment_Log_Client_Detail();
        }
        else if((transaction.equalsIgnoreCase("Car"))
                || (transaction.equalsIgnoreCase("UNC"))
                || (transaction.equalsIgnoreCase("MCs"))
                || (transaction.equalsIgnoreCase("MUn"))
                || (transaction.equalsIgnoreCase("MCU"))
                || (transaction.equalsIgnoreCase("DNP"))
                || (transaction.equalsIgnoreCase("NV"))
                || (transaction.equalsIgnoreCase("OTH"))
                || (transaction.equalsIgnoreCase(""))){
            return new Fragment_Log_OtherTransactions();
        }
        return null;
    }
}
