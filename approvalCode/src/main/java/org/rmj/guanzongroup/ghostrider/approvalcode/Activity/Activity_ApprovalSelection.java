/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.approvalCode
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.approvalcode.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.rmj.guanzongroup.ghostrider.approvalcode.Etc.AdapterApprovalAuth;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalSelection;

public class Activity_ApprovalSelection extends AppCompatActivity {
    public static final String TAG = Activity_ApprovalSelection.class.getSimpleName();

    private VMApprovalSelection mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_selection);

        mViewModel = new ViewModelProvider(this).get(VMApprovalSelection.class);

        String lsSysType = getIntent().getStringExtra("sysCode");
        MaterialToolbar toolbar = findViewById(R.id.toolbar_approvalSelection);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_approvalAuth);
        mViewModel.getReferenceAuthList(lsSysType).observe(this, requestList -> {
            LinearLayoutManager manager = new LinearLayoutManager(Activity_ApprovalSelection.this);
            manager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setAdapter(new AdapterApprovalAuth(requestList, (SystemCode, SCAType) -> {
                Intent loIntentx = new Intent(Activity_ApprovalSelection.this, Activity_ApprovalCode.class);
                if(SystemCode.equalsIgnoreCase("CA")) {
                    loIntentx.putExtra("sysCode", "1");
                } else{
                    loIntentx.putExtra("sysCode", "0");
                }
                loIntentx.putExtra("systype", lsSysType);
                loIntentx.putExtra("sSystemCd", SystemCode);
                loIntentx.putExtra("sSCATypex", SCAType);
                startActivity(loIntentx);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }));
            recyclerView.setLayoutManager(manager);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

}