/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 6/7/21 2:34 PM
 * project file last modified : 6/7/21 2:34 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.*;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogBranchSelection;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogCashCountBranch;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Model.CashCountInfoModel;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMCashCounter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class Activity_CashCounter extends AppCompatActivity {

    public static final String TAG = Activity_CashCounter.class.getSimpleName();
    private static Activity_CashCounter instance;
    private VMCashCounter mViewModel;

    public static Activity_CashCounter getInstance() {
        return instance;
    }
    //Array for Peso Bill
    private TextInputEditText[] qtyPeso;
    private TextInputEditText[] totalPeso;
    private MaterialTextView[] cprice;

    //Array for Coin Bill
    private TextInputEditText[] qtyCoin;
    private TextInputEditText[] totalCoin;
    private MaterialTextView[] cpriceCoin;

    private MaterialTextView lblPesoTotal, lblCoinsTotal, lblGrandTotal;
    private MaterialTextView lblBranch;
    private MaterialButton btnNext;

    private MessageBox poMessage;
    private LoadDialog poLoad;
    private CashCountInfoModel infoModel;
    DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cash_counter);

        instance = Activity_CashCounter.this;
        poMessage = new MessageBox(Activity_CashCounter.this);
        poLoad = new LoadDialog(Activity_CashCounter.this);
        mViewModel = new ViewModelProvider(this).get(VMCashCounter.class);
        infoModel = new CashCountInfoModel();

        initWidgets();

        if(getIntent().hasExtra("BranchCd")) {
            mViewModel.setBranchCd(getIntent().getStringExtra("BranchCd"));
        } else {
            mViewModel.GetBranchesList(new VMCashCounter.OnGetBranchesList() {
                @Override
                public void OnLoad() {
                    poLoad.initDialog("Cash Count", "Checking selfie log entries. Please wait...", false);
                    poLoad.show();
                }

                @Override
                public void OnRetrieve(List<EBranchInfo> list) {
                    poLoad.dismiss();

                    DialogCashCountBranch loBranch = new DialogCashCountBranch(Activity_CashCounter.this, list);
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
                    poLoad.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_error_24);
                    poMessage.setTitle("Cash Count");
                    poMessage.setMessage(message);
                    poMessage.setPositiveButton("Okay", (view, dialog) -> {
                        dialog.dismiss();
                        finish();
                    });
                    poMessage.show();
                }
            });
        }

        mViewModel.GetBranchCd().observe(Activity_CashCounter.this, branchCd -> mViewModel.GetBranchForCashCount(branchCd).observe(Activity_CashCounter.this, eBranchInfo -> {
            try {
                lblBranch.setText(eBranchInfo.getBranchNm());

                if(branchCd.isEmpty()){
                    btnNext.setEnabled(false);
                }
                btnNext.setOnClickListener(v->{
                    Intent intent = new Intent(Activity_CashCounter.this, Activity_CashCountSubmit.class);
                    intent.putExtra("params", String.valueOf(mViewModel.getJsonData().getValue()));
                    intent.putExtra("BranchCd", branchCd);
                    startActivity(intent);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        //Peso Bill Computation
        for (int x = 0; x < qtyPeso.length; x++) {
            final int i = x;
            final String cprices = String.valueOf(cprice[i].getText());
            qtyPeso[x].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int total = 0;
                    String price = "", qty = "";
                    price = String.valueOf(cprice[i].getText());
                    qty = String.valueOf(qtyPeso[i].getText());
                    if (qty.isEmpty()){
                        qty = "0";
                        totalPeso[i].setText("0");
                        if (i == 0) {
                            mViewModel.setP1000((double) total, Integer.parseInt(qty));
                        }
                        else if (i == 1) {
                            mViewModel.setP500((double) total, Integer.parseInt(qty));
                        }
                        else if (i == 2) {
                            mViewModel.setP200((double) total, Integer.parseInt(qty));
                        }
                        else if (i == 3) {
                            mViewModel.setP100((double) total, Integer.parseInt(qty));
                        }
                        else if (i == 4) {
                            mViewModel.setP50((double) total, Integer.parseInt(qty));
                        }
                        else if (i == 5) {
                            mViewModel.setP20((double) total, Integer.parseInt(qty));
                        }
                    } else {
                        total = Integer.parseInt(price.substring(1)) * Integer.parseInt(qty);
                        if (i == 0) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte1000p(String.valueOf(total));
                            mViewModel.setP1000((double) total, Integer.parseInt(qty));
                        } else if (i == 1) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte0500p(String.valueOf(total));
                            mViewModel.setP500((double) total, Integer.parseInt(qty));
                        } else if (i == 2) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte0200p(String.valueOf(total));
                            mViewModel.setP200((double) total, Integer.parseInt(qty));
                        } else if (i == 3) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte0100p(String.valueOf(total));
                            mViewModel.setP100((double) total, Integer.parseInt(qty));
                        } else if (i == 4) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte0050p(String.valueOf(total));
                            mViewModel.setP50((double) total, Integer.parseInt(qty));
                        } else if (i == 5) {
                            totalPeso[i].setText(formatter.format(total));
                            infoModel.setnNte0020p(String.valueOf(total));
                            mViewModel.setP20((double) total, Integer.parseInt(qty));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

//        lblPesoTotal.setText(formatter.format(PesoTotal));
        //Coin Bill Computation
        int a = 0;
        for (a = 0; a < qtyCoin.length; a++) {
            final int b = a;
            final String cpricecoin = String.valueOf(cpriceCoin[b].getText());
            int finalA = a;
            qtyCoin[a].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Double totalcoin = 0.0;
                    int totalcoins = 0;
                    String coinprice = "", qtycoins = "";
                    coinprice = String.valueOf(cpriceCoin[b].getText());
                    qtycoins = String.valueOf(qtyCoin[b].getText());
                    if (qtycoins.isEmpty()) {
                        qtycoins = "0";
                        totalCoin[b].setText("0");
                        if (b == 0) {
                            mViewModel.setP10((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 1) {
                            mViewModel.setP5((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 2) {
                            mViewModel.setP1((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 3) {
                            mViewModel.setC25(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 4) {
                            mViewModel.setC10(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 5) {
                            mViewModel.setC5(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 6) {
                            mViewModel.setC1(totalcoin, Integer.parseInt(qtycoins));
                        }
                    } else {
                        if (b <= 2) {
                            totalcoins = Integer.parseInt(coinprice.substring(1)) * Integer.parseInt(qtycoins);
                            totalCoin[b].setText(formatter.format(totalcoins));
                        } else {
                            totalcoin = (Double.parseDouble(coinprice.substring(1)) * Integer.parseInt(qtycoins)) / 100;
                            totalCoin[b].setText(formatter.format(totalcoin));
                        }
                        if (b == 0) {
                            totalCoin[b].setText(formatter.format(totalcoins));
                            mViewModel.setP10((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 1) {
                            totalCoin[b].setText(formatter.format(totalcoins));
                            mViewModel.setP5((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 2) {
                            totalCoin[b].setText(formatter.format(totalcoins));
                            mViewModel.setP1((double) totalcoins, Integer.parseInt(qtycoins));
                        } else if (b == 3) {
                            totalCoin[b].setText(formatter.format(totalcoin));
                            mViewModel.setC25(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 4) {
                            totalCoin[b].setText(formatter.format(totalcoin));
                            mViewModel.setC10(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 5) {
                            totalCoin[b].setText(formatter.format(totalcoin));
                            mViewModel.setC5(totalcoin, Integer.parseInt(qtycoins));
                        } else if (b == 6) {
                            totalCoin[b].setText(formatter.format(totalcoin));
                            mViewModel.setC1(Double.valueOf(totalcoin), Integer.parseInt(qtycoins));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        mViewModel.getPesoTotalAmount().observe(this, val -> lblPesoTotal.setText(formatter.format(val)));
        mViewModel.getCoinsTotalAmount().observe(this, val -> lblCoinsTotal.setText(formatter.format(val)));
        mViewModel.getGrandTotalAmount().observe(this, val -> lblGrandTotal.setText(formatter.format(val)));
    }

    public void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_cashCounter);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        lblBranch = findViewById(R.id.lbl_headerBranch);
        btnNext = findViewById(R.id.btn_cash_count);
        lblPesoTotal = findViewById(R.id.lblPesoTotal);
        lblCoinsTotal = findViewById(R.id.lblCoinsTotal);
        lblGrandTotal = findViewById(R.id.lblGrandTotal);
        qtyPeso = new TextInputEditText[] {
                findViewById(R.id.edt1000p),
                findViewById(R.id.edt500p),
                findViewById(R.id.edt200p),
                findViewById(R.id.edt100p),
                findViewById(R.id.edt50p),
                findViewById(R.id.edt20p)
        };
        cprice = new MaterialTextView[] {
                findViewById(R.id.tv1000p),
                findViewById(R.id.tv500p),
                findViewById(R.id.tv200p),
                findViewById(R.id.tv100p),
                findViewById(R.id.tv50p),
                findViewById(R.id.tv20p)
        };
        totalPeso = new TextInputEditText[] {
                findViewById(R.id.edtTotal1000p),
                findViewById(R.id.edtTotal500p),
                findViewById(R.id.edtTotal200p),
                findViewById(R.id.edtTotal100p),
                findViewById(R.id.edtTotal50p),
                findViewById(R.id.edtTotal20p)
        };

        qtyCoin = new TextInputEditText[] {
                findViewById(R.id.edt10pc),
                findViewById(R.id.edt5pc),
                findViewById(R.id.edt1pc),
                findViewById(R.id.edt25cc),
                findViewById(R.id.edt10cc),
                findViewById(R.id.edt5cc),
                findViewById(R.id.edt1cc),
        };
        cpriceCoin = new MaterialTextView[] {
                findViewById(R.id.tv10pc),
                findViewById(R.id.tv5pc),
                findViewById(R.id.tv1pc),
                findViewById(R.id.tv25cc),
                findViewById(R.id.tv10cc),
                findViewById(R.id.tv5cc),
                findViewById(R.id.tv1cc)
        };
        totalCoin = new TextInputEditText[] {
                findViewById(R.id.edtTotal10pc),
                findViewById(R.id.edtTotal5pc),
                findViewById(R.id.edtTotal1pc),
                findViewById(R.id.edtTotal25cc),
                findViewById(R.id.edtTotal10cc),
                findViewById(R.id.edtTotal5cc),
                findViewById(R.id.edtTotal1cc)
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            ConfirmExist();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ConfirmExist();
    }

    private void ConfirmExist(){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_contact_support_24);
        poMessage.setTitle("Cash Count");
        poMessage.setMessage("Exit cash count?");
        poMessage.setPositiveButton("Yes", (view, dialog) -> {
            dialog.dismiss();
            finish();
        });
        poMessage.setNegativeButton("No", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }
}