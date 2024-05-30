/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 10/25/21, 11:51 AM
 * project file last modified : 10/25/21, 11:51 AM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.divider.MaterialDivider;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Model.RandomItem;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMInventoryEntry;

import java.util.Objects;

public class Activity_InventoryEntry extends AppCompatActivity {
    private static final String TAG = Activity_InventoryEntry.class.getSimpleName();

    private VMInventoryEntry mViewModel;
    private RandomItem poItem;

    private MessageBox poMessage;

    private MaterialTextView lblTransNox, lblItemxx, lblBarcode;
    private TextInputEditText txtDate,
            txtRemarks1,
            txtEntryNox1,
            txtActualQty,
            txtQtyOnHand,
            txtWareHouse,
            txtSection,
            txtBin;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_entry);
        initWidgets();

        mViewModel = new ViewModelProvider(Activity_InventoryEntry.this).get(VMInventoryEntry.class);

        String lsTransNox = getIntent().getStringExtra("transno");
        String lsPartIDxx = getIntent().getStringExtra("partID");
        String lsBarCodex = getIntent().getStringExtra("barcode");

        mViewModel.getInventoryItemDetail(lsTransNox, lsPartIDxx, lsBarCodex).observe(Activity_InventoryEntry.this, item -> {
            try{
                poItem = new RandomItem(item.getTransNox(), item.getPartsIDx(), item.getBarrCode());
                lblItemxx.setText(item.getDescript());
                lblBarcode.setText(item.getBarrCode());
                lblTransNox.setText(item.getTransNox());
                txtDate.setText(FormatUIText.formatGOCasBirthdate(AppConstants.CURRENT_DATE()));
                txtEntryNox1.setText(String.valueOf(item.getEntryNox()));
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHnd()));
                txtWareHouse.setText(item.getWHouseNm());
                txtSection.setText(item.getSectnNme());
                txtBin.setText(item.getBinNamex());

                if(item.getTranStat().equalsIgnoreCase("1")){
                    txtActualQty.setText(String.valueOf(item.getActCtr01()));
                    txtRemarks1.setText(item.getRemarksx());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnSave.setOnClickListener(v -> {
            if(!Objects.requireNonNull(txtActualQty.getText()).toString().isEmpty()) {
                poItem.setItemQtyx(Integer.parseInt(txtActualQty.getText().toString()));
                poItem.setRemarksx(Objects.requireNonNull(txtRemarks1.getText()).toString());
                mViewModel.saveInventoryUpdate(poItem, new VMInventoryEntry.OnInventoryUpdateCallBack() {
                    @Override
                    public void OnUpdate(String message) {
                        GToast.CreateMessage(Activity_InventoryEntry.this, message, GToast.INFORMATION).show();
                        finish();
                        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
                    }

                    @Override
                    public void OnError(String message) {
                        GToast.CreateMessage(Activity_InventoryEntry.this, message, GToast.INFORMATION).show();
                        finish();
                        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
                    }
                });
            } else {
                GToast.CreateMessage(Activity_InventoryEntry.this, "Please enter quantity", GToast.ERROR).show();
            }
        });
    }
    public void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_inventory_transaction);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        lblTransNox = findViewById(R.id.lbl_invTransNox);
        lblItemxx = findViewById(R.id.lbl_inventoryItem);
        lblBarcode = findViewById(R.id.lbl_itemBarcode);
        txtDate = findViewById(R.id.txt_invDate);
        txtRemarks1 = findViewById(R.id.txt_invRemarks1);
        txtEntryNox1 = findViewById(R.id.txt_invEntryNox1);
        txtActualQty = findViewById(R.id.txt_invQty);
        txtQtyOnHand = findViewById(R.id.txt_invQtyOnHand);
        txtWareHouse = findViewById(R.id.txt_invWareHouse);
        txtSection = findViewById(R.id.txt_invSection);
        txtBin = findViewById(R.id.txt_invBin);
        btnSave = findViewById(R.id.btn_saveInventory);

        poMessage = new MessageBox(Activity_InventoryEntry.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }
}