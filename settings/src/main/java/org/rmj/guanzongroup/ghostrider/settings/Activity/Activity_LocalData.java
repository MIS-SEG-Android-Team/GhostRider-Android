/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/23/21 2:03 PM
 * project file last modified : 6/23/21 2:03 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Objects.AdapterLocalData;
import org.rmj.guanzongroup.ghostrider.settings.Objects.LocalData;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMLocalData;

import java.util.List;

public class Activity_LocalData extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MaterialButton btnRefresh, btnClearDb;

    private VMLocalData mViewModel;

    private LoadDialog poDialog;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_data);

        mViewModel = new ViewModelProvider(this).get(VMLocalData.class);

        toolbar = findViewById(R.id.toolbar_localData);
        recyclerView = findViewById(R.id.recyclerview_localData);
        btnRefresh = findViewById(R.id.btn_refreshRecords);
        btnClearDb = findViewById(R.id.btn_Clear_Data);

        poDialog = new LoadDialog(Activity_LocalData.this);
        poMessage = new MessageBox(Activity_LocalData.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_LocalData.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mViewModel.getAppLocalData().observe(this, appLocalData -> {
            try{
                mViewModel.setAppLocalData(appLocalData);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getDataList().observe(this, localData -> {
            AdapterLocalData loAdapter = new AdapterLocalData(localData, () -> {

            });
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(loAdapter);
            recyclerView.getRecycledViewPool().clear();
            loAdapter.notifyDataSetChanged();
        });

        btnRefresh.setOnClickListener(v -> mViewModel.RefreshAllRecords(new VMLocalData.OnRefreshDataCallback() {
            @Override
            public void OnLoad(String Title, String Message) {
                poDialog.initDialog(Title, Message, false);
                poDialog.show();
            }

            @Override
            public void OnSuccess() {
                poDialog.dismiss();
            }

            @Override
            public void OnFailed() {
                poDialog.dismiss();
            }
        }));

        btnClearDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                poMessage.setTitle("Manage Local Data");
                poMessage.setMessage("Clearing your device local data can delete your current transaction details such as unposted DCP, Leave Applications, etc... \n" +
                        "\n" +
                        "Tap Clear to back up and export database before clearing local data.");
                poMessage.setPositiveButton("Clear", (view, dialog) -> {
                    dialog.dismiss();
                    mViewModel.ExportDatabase();
                    mViewModel.killProcessesAround(Activity_LocalData.this);
                    mViewModel.ClearData();
                });
                poMessage.setNegativeButton("Cancel", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
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

    private void showResultMessage(String message){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_message_24);
        poMessage.setTitle("Manage Local Data");
        poMessage.setMessage(message);
        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }
}