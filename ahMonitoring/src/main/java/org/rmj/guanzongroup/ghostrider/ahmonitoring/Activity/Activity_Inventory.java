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

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.InventoryItemAdapter;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogBranchSelection;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogInventoryBranch;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogPostInventory;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMInventory;

import java.util.List;
import java.util.Objects;

public class Activity_Inventory extends AppCompatActivity {
    private VMInventory mViewModel;

    private RecyclerView recyclerView;
    private MaterialTextView lblCountx, lblBranchNm, lblStatus;
    private MaterialButton btnSelect, btnPost;

    private LoadDialog poDialog;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VMInventory.class);
        setContentView(R.layout.activity_inventory);
        initWidgets();

        if(getIntent().hasExtra("BranchCd")) {
            mViewModel.setBranchCd(getIntent().getStringExtra("BranchCd"));
        } else {
            mViewModel.GetBranchList(new VMInventory.OnGetBranchListListener() {
                @Override
                public void OnLoad() {
                    poDialog.initDialog("Random Stock Inventory", "Checking selfie log entries. Please wait...", false);
                    poDialog.show();
                }

                @Override
                public void OnRetrieve(List<EBranchInfo> list) {
                    poDialog.dismiss();
                    DialogInventoryBranch loBranch = new DialogInventoryBranch(Activity_Inventory.this, list);
                    loBranch.initDialog(new DialogBranchSelection.OnBranchSelectedCallback() {
                        @Override
                        public void OnSelect(String BranchCode, AlertDialog dialog) {
                            mViewModel.setBranchCd(BranchCode);
                            dialog.dismiss();
                        }

                        @Override
                        public void OnCancel() {
                            finish();
                        }
                    });
                }

                @Override
                public void OnFailed(String message) {
                    poDialog.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_error_24);
                    poMessage.setTitle("Inventory Failed");
                    poMessage.setMessage(message);
                    poMessage.setPositiveButton("Okay", (view, dialog) -> {
                        dialog.dismiss();
                        finish();
                    });
                    poMessage.show();
                }
            });
        }

        mViewModel.GetBranchCd().observe(Activity_Inventory.this, branchCd -> {
            mViewModel.GetBranchInfo(branchCd).observe(Activity_Inventory.this, branch -> {
                try{
                    lblBranchNm.setText(branch.getBranchNm());
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            if(!branchCd.isEmpty()){
                SetupInventoryMaster(branchCd);
            }

            btnPost.setOnClickListener(v -> {
                if(branchCd.isEmpty()) {
                    Toast.makeText(Activity_Inventory.this, "Unable to post empty inventory", Toast.LENGTH_SHORT).show();
                } else {
                    new DialogPostInventory(Activity_Inventory.this).initDialog(Remarks -> mViewModel.PostInventory(branchCd, Remarks, new VMInventory.OnSaveInventoryMaster() {
                        @Override
                        public void OnSave() {
                            poDialog.initDialog("Random Stock Inventory", "Saving entries. Please wait...", false);
                            poDialog.show();
                        }

                        @Override
                        public void OnSuccess() {
                            poDialog.dismiss();
                            poMessage.initDialog();
                            poMessage.setIcon(R.drawable.baseline_message_24);
                            poMessage.setTitle("Random Stock Inventory");
                            poMessage.setMessage("Inventory has been save. Successfully.");
                            poMessage.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                                finish();
                            });
                            poMessage.show();
                        }

                        @Override
                        public void OnFailed(String message) {
                            poDialog.dismiss();
                            poMessage.initDialog();
                            poMessage.setIcon(R.drawable.baseline_error_24);
                            poMessage.setTitle("Random Stock Inventory");
                            poMessage.setMessage(message);
                            poMessage.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                            });
                            poMessage.show();
                        }
                    }));
                }
            });
        });
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
    }

    public void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_inventory);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerview_inventory);
        lblCountx = findViewById(R.id.lbl_inventoryCount);
        lblBranchNm = findViewById(R.id.lbl_branchNm);
        lblStatus = findViewById(R.id.lbl_inventoryStatus);
        btnPost = findViewById(R.id.btn_post);
        btnSelect = findViewById(R.id.btn_selectBranch);

        poDialog = new LoadDialog(Activity_Inventory.this);
        poMessage = new MessageBox(Activity_Inventory.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            poMessage.initDialog();
            poMessage.setIcon(R.drawable.baseline_contact_support_24);
            poMessage.setTitle("Random Stock Inventory");
            poMessage.setMessage("Exit Random Stock Inventory?");
            poMessage.setPositiveButton("Yes", (view, dialog) -> {
                dialog.dismiss();
                overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
                finish();
            });
            poMessage.setNegativeButton("No", (view, dialog) -> {
                dialog.dismiss();
            });
            poMessage.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_contact_support_24);
        poMessage.setTitle("Random Stock Inventory");
        poMessage.setMessage("Exit Random Stock Inventory?");
        poMessage.setPositiveButton("Yes", (view, dialog) -> {
            dialog.dismiss();
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        });
        poMessage.setNegativeButton("No", (view, dialog) -> {
            dialog.dismiss();
        });
        poMessage.show();
    }

    private void SetupInventoryMaster(String BranchCde){
        mViewModel.CheckBranchInventory(BranchCde, new VMInventory.OnCheckLocalRecords() {
            @Override
            public void OnCheck() {
                poDialog.initDialog("Random Stock Inventory", "Checking entries. Please wait...", false);
                poDialog.show();
            }

            @Override
            public void OnProceed() {
                poDialog.dismiss();
                mViewModel.DownloadInventory(BranchCde, new VMInventory.OnDownloadInventory() {
                    @Override
                    public void OnRequest(String title, String message) {
                        poDialog.initDialog("Random Stock Inventory", "Downloading inventory items. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poDialog.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("Random Stock Inventory");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                        });
                        poMessage.show();
                    }
                });
            }

            @Override
            public void OnInventoryFinished(String message) {
                poDialog.dismiss();
                poDialog.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_message_24);
                poMessage.setTitle("Random Stock Inventory");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                    finish();
                });
                poMessage.show();
            }
        });

        mViewModel.GetInventoryMaster(BranchCde).observe(Activity_Inventory.this, master -> {
            try{
                if(master == null){
                    btnPost.setEnabled(false);
                    recyclerView.setVisibility(View.GONE);
                    lblStatus.setVisibility(View.VISIBLE);
                } else if(!master.getTranStat().equalsIgnoreCase("2")){
                    recyclerView.setVisibility(View.VISIBLE);
                    lblStatus.setVisibility(View.GONE);
                    btnPost.setEnabled(true);

                    String lsTransNo = master.getTransNox();
                    mViewModel.GetInventoryItems(lsTransNo).observe(Activity_Inventory.this, details -> {

                        lblCountx.setText("Updated Inventory Items : " + details.size());

                        LinearLayoutManager manager = new LinearLayoutManager(Activity_Inventory.this);
                        manager.setOrientation(RecyclerView.VERTICAL);

                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(new InventoryItemAdapter(details, (TransNox, PartID, BarCode, isUpdated, args) -> {
                            if(!isUpdated) {
                                Intent loIntent = new Intent(Activity_Inventory.this, Activity_InventoryEntry.class);
                                loIntent.putExtra("transno", TransNox);
                                loIntent.putExtra("partID", PartID);
                                loIntent.putExtra("barcode", BarCode);
                                startActivity(loIntent);
                            } else {
                                poMessage.initDialog();
                                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                                poMessage.setTitle("Random Stock Inventory");
                                poMessage.setMessage("Update inventory details for " + args[0] + "? \n" +
                                        "\n" +
                                        "Stock: " + args[2] + "\n" +
                                        "Remarks: " + args[1]);
                                poMessage.setPositiveButton("Update", (view, dialog) -> {
                                    dialog.dismiss();
                                    Intent loIntent = new Intent(Activity_Inventory.this, Activity_InventoryEntry.class);
                                    loIntent.putExtra("transno", TransNox);
                                    loIntent.putExtra("partID", PartID);
                                    loIntent.putExtra("barcode", BarCode);
                                    startActivity(loIntent);
                                });
                                poMessage.setNegativeButton("Cancel", (view, dialog) -> dialog.dismiss());
                                poMessage.show();
                            }
                        }));
                    });
                } else {
                    recyclerView.setVisibility(View.GONE);
                    lblStatus.setVisibility(View.VISIBLE);
                    lblStatus.setText("Inventory is already posted");
                    btnPost.setEnabled(false);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}