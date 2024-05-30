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

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.dev.Api.WebFileServer;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.DayCheck;
import org.rmj.g3appdriver.utils.FileRemover;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.CollectionLogAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMCollectionLog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_LogCollection extends AppCompatActivity {
    private static final String TAG = Activity_LogCollection.class.getSimpleName();

    private VMCollectionLog mViewModel;

    private MaterialTextView    txtNoLog,
                        txtNoName,
                        lblTotRemit,
                        lblCashOH,
                        lblCheckOH,
                        lblTotalClt;

    private LinearLayoutManager poManager;
    private TextInputEditText txtDate, txtSearch;
    private RecyclerView recyclerView;
    private LinearLayout lnEmptyList;
    private TextInputLayout tilSearch;
    private LinearLayout linearCashInfo;
    private MaterialButton btnRemit;

    private List<EDCPCollectionDetail> filteredCollectionDetlx;

    private String psCltCheck;
    private String psCltCashx;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VMCollectionLog.class);
        setContentView(R.layout.activity_collection_log);
        initWidgets();

        mViewModel.getAllAddress().observe(Activity_LogCollection.this, eAddressUpdates -> {
            try {
                mViewModel.setAddressList(eAddressUpdates);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        mViewModel.getAllMobileNox().observe(Activity_LogCollection.this, eMobileUpdates -> {
            try {
                mViewModel.setMobileList(eMobileUpdates);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        });

        mViewModel.getCollectionMaster().observe(Activity_LogCollection.this, collectionMaster -> {
            try{
                mViewModel.setCollectionMaster(collectionMaster);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        txtDate.setOnClickListener(view -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_LogCollection.this, (view131, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String lsDate = dateFormatter.format(newDate.getTime());
                mViewModel.setDateTransact(lsDate);
                txtDate.setText(lsDate);
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
            StartTime.show();
        });

        mViewModel.getDateTransact().observe(Activity_LogCollection.this, s -> mViewModel.GetCollectionMaster(s).observe(Activity_LogCollection.this, master -> {
            try {
                if(master == null){
                    lnEmptyList.setVisibility(View.VISIBLE);
                    txtNoName.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    tilSearch.setVisibility(View.GONE);
                    linearCashInfo.setVisibility(View.GONE);

                    lblTotalClt.setText("Total Collection: 0.0");
                    lblTotRemit.setText("Total Remitted: 0.0");
                    lblCashOH.setText("Cash-On-Hand: 0.0");
                    lblCheckOH.setText("Check-On-Hand: 0.0");
                } else {
                    mViewModel.GetTotalCollection(master.getTransNox()).observe(Activity_LogCollection.this, value -> {
                        try {
                            lblTotalClt.setText("Total Collection: " + FormatUIText.getCurrencyUIFormat(value));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lblTotalClt.setText("Total Collection: 0.0");
                        }
                    });

                    mViewModel.GetTotalRemittance(master.getTransNox()).observe(Activity_LogCollection.this, value -> {
                        try {
                            lblTotRemit.setText("Total Remitted: " + FormatUIText.getCurrencyUIFormat(value));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lblTotRemit.setText("Total Remitted: 0.0");
                        }
                    });

                    mViewModel.GetCashOnHand(master.getTransNox()).observe(Activity_LogCollection.this, s1 -> {
                        try {
                            lblCashOH.setText("Cash-On-Hand: " + FormatUIText.getCurrencyUIFormat(s1));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lblCashOH.setText("Cash-On-Hand: 0.0");
                        }
                    });

                    mViewModel.GetCheckOnHand(master.getTransNox()).observe(Activity_LogCollection.this, s12 -> {
                        try {
                            lblCheckOH.setText("Check-On-Hand: " + FormatUIText.getCurrencyUIFormat(s12));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lblCheckOH.setText("Check-On-Hand: 0.0");
                        }
                    });

                    // Remove Old DCP Transaction Image
                    deleteOldImageSchedule();

                    mViewModel.GetCollectionDetail(master.getTransNox()).observe(Activity_LogCollection.this, collectionDetails -> {
                        try {
                            if (collectionDetails.size() > 0) {
                                lnEmptyList.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                tilSearch.setVisibility(View.VISIBLE);
                                txtSearch.setText("");
                                linearCashInfo.setVisibility(View.VISIBLE);

                                filteredCollectionDetlx.clear();
                                filteredCollectionDetlx.addAll(collectionDetails);

                                CollectionLogAdapter poAdapter = new CollectionLogAdapter(filteredCollectionDetlx, position -> {
                                    Intent loIntent = new Intent(Activity_LogCollection.this, Activity_TransactionDetail.class);
                                    loIntent.putExtra("sTransNox", filteredCollectionDetlx.get(position).getTransNox());
                                    loIntent.putExtra("entryNox", filteredCollectionDetlx.get(position).getEntryNox());
                                    loIntent.putExtra("acctNox", filteredCollectionDetlx.get(position).getAcctNmbr());
                                    loIntent.putExtra("fullNme", filteredCollectionDetlx.get(position).getFullName());
                                    loIntent.putExtra("remCodex", filteredCollectionDetlx.get(position).getRemCodex());
                                    loIntent.putExtra("imgNme", filteredCollectionDetlx.get(position).getImageNme());
                                    loIntent.putExtra("sClientID", filteredCollectionDetlx.get(position).getClientID());
                                    loIntent.putExtra("sAddressx", filteredCollectionDetlx.get(position).getAddressx());
                                    loIntent.putExtra("sRemarksx", filteredCollectionDetlx.get(position).getRemarksx());
                                    loIntent.putExtra("nLongitud", filteredCollectionDetlx.get(position).getLongitud());
                                    loIntent.putExtra("nLatitude", filteredCollectionDetlx.get(position).getLatitude());
                                    startActivity(loIntent);
                                });

                                poManager = new LinearLayoutManager(Activity_LogCollection.this);
                                poManager.setOrientation(RecyclerView.VERTICAL);
                                recyclerView.setLayoutManager(poManager);
                                recyclerView.setAdapter(poAdapter);
                                recyclerView.getRecycledViewPool().clear();

                                txtSearch.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        if (!charSequence.toString().trim().isEmpty()) {
                                            poAdapter.getCollectionFilter().filter(charSequence.toString().toLowerCase());
                                            poAdapter.notifyDataSetChanged();
                                            if (poAdapter.getItemCount() == 0) {
                                                txtNoName.setVisibility(View.VISIBLE);
                                                recyclerView.setVisibility(View.GONE);
                                            } else {
                                                txtNoName.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            poAdapter.getCollectionFilter().filter(charSequence.toString().toLowerCase());
                                            poAdapter.notifyDataSetChanged();
                                            txtNoName.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {

                                    }
                                });

                            } else {
                                lnEmptyList.setVisibility(View.VISIBLE);
                                txtNoName.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                tilSearch.setVisibility(View.GONE);
                                linearCashInfo.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        mViewModel.getUnsentImageInfoList().observe(Activity_LogCollection.this, eImageInfos -> {
            try{
                mViewModel.setImageInfoList(eImageInfos);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    private void initWidgets(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar_collectionLog);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerview_collectionLog);
        txtNoLog = findViewById(R.id.txt_no_logs);
        txtNoName = findViewById(R.id.txt_no_name);
        lnEmptyList = findViewById(R.id.linear_empty_list);
        lblTotRemit = findViewById(R.id.lbl_totalRemitCollection);
        lblCashOH = findViewById(R.id.lbl_totalCashOnHand);
        lblCheckOH = findViewById(R.id.lbl_totalCheckOnHand);
        lblTotalClt = findViewById(R.id.lbl_totalCollected);

        txtDate = findViewById(R.id.txt_collectionDate);
        txtSearch = findViewById(R.id.txt_collectionSearch);
        tilSearch = findViewById(R.id.til_collectionSearch);
        linearCashInfo = findViewById(R.id.linear_collectionCashInfo);
        btnRemit = findViewById(R.id.btn_remit);
        filteredCollectionDetlx = new ArrayList<>();

        try {
            @SuppressLint("SimpleDateFormat") Date loDate = new SimpleDateFormat("yyyy-MM-dd").parse(AppConstants.CURRENT_DATE());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat loFormatter = new SimpleDateFormat("MMM dd, yyyy");
            txtDate.setText(loFormatter.format(Objects.requireNonNull(loDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnRemit.setOnClickListener(v -> {
            try {
                Intent loIntent = new Intent(Activity_LogCollection.this, Activity_CollectionRemittance.class);
                String lsDate = Objects.requireNonNull(txtDate.getText()).toString();
                @SuppressLint("SimpleDateFormat") Date loDate = new SimpleDateFormat("MMM dd, yyyy").parse(lsDate);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd");
                loIntent.putExtra("dTransact", loFormatter.format(Objects.requireNonNull(loDate)));
                startActivity(loIntent);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void checkDcpImages(List<EDCPCollectionDetail> poDcpList, OnDCPDownloadListener callBack) {
        new DownloadDcpImageTask(getApplication(), callBack).execute(poDcpList);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModelStore().clear();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteOldImageSchedule() {
        if(DayCheck.isMonday())
            return FileRemover.execute(Environment.getExternalStorageDirectory() + "/Android/data/org.rmj.guanzongroup.ghostrider.epacss/files/DCP");
        else
            return false;
    }

    private static class DownloadDcpImageTask extends AsyncTask<List<EDCPCollectionDetail>, Void, String> {

        private final ConnectionUtil poConn;
        private final EmployeeSession poUser;
        private final AppConfigPreference poConfig;
        private final OnDCPDownloadListener callBack;

        DownloadDcpImageTask(Application application, OnDCPDownloadListener callBack) {
            this.poConn = new ConnectionUtil(application);
            this.poUser = EmployeeSession.getInstance(application);
            this.poConfig = AppConfigPreference.getInstance(application);
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callBack.onDowload();
        }

        @Override
        protected String doInBackground(List<EDCPCollectionDetail>... lists) {
            String lsResult = "";
            List<EDCPCollectionDetail> loDcpList = lists[0];
            try {
                if (!poConn.isDeviceConnected()) {
                    lsResult = AppConstants.NO_INTERNET();
                } else {
                    String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(), poUser.getClientId(), poUser.getUserID());
                    String lsAccess = WebFileServer.RequestAccessToken(lsClient);

                    if (lsClient.isEmpty() || lsAccess.isEmpty()) {
                        lsResult = AppConstants.LOCAL_EXCEPTION_ERROR("Failed to request generated Client or Access token.");
                    } else {
                        if(loDcpList.size() > 0) {
                            for(int x = 0 ; x < loDcpList.size(); x++) {
                                File loFile = new File(loDcpList.get(x).getImageNme());
                                if(loFile.exists()) {
                                    continue;
                                } else {
                                    loFile.mkdirs();
//                                    org.json.simple.JSONObject loDownload = WebFileServer.DownloadFile(lsAccess,
//                                            "0020",
//                                            loDcpList.get(x).getAcctNmbr(),
//                                            loDcpList.get(x).getImageNme(),
//                                            "DCPa",
//                                            loDcpList.get(x).getTransNox(),
//                                            "");

//                                    String lsResponse = (String) loDownload.get("result");
//                                    if (Objects.requireNonNull(lsResponse).equalsIgnoreCase("success")) {
//                                        //convert to image and save to proper file location
//                                        JSONParser loParser = new JSONParser();
//                                        loDownload = (org.json.simple.JSONObject) loParser.parse(loDownload.get("payload").toString());
//                                        String location = fileLoc.getAbsolutePath() + "/";
//                                        if (WebFile.Base64ToFile((String) loDownload.get("data"),
//                                                (String) loDownload.get("hash"),
//                                                location,
//                                                (String) loDownload.get("filename"))){
//                                            Log.d(TAG, "File hash was converted to file successfully.");
//                                            //insert entry to image info
//                                            EImageInfo loImage = new EImageInfo();
//                                            loImage.setTransNox((String) loDownload.get("transnox"));
//                                            loImage.setSourceCD("COAD");
//                                            loImage.setSourceNo(loDcpList.get(x).getTransNox());
//                                            loImage.setDtlSrcNo(loDcpList.get(x).getTransNox());
//                                            loImage.setFileCode("0029");
//                                            loImage.setMD5Hashx((String) loDownload.get("hash"));
//                                            loImage.setFileLoct(fileLoc.getAbsolutePath() +"/" + imageName);
//                                            loImage.setImageNme((String) loDownload.get("filename"));
//                                            loImage.setLatitude("0.0");
//                                            loImage.setLongitud("0.0");
//                                            loImage.setSendDate(new AppConstants().DATE_MODIFIED);
//                                            loImage.setSendStat("1");
//                                            //loImage....
//                                            ScannerConstants.PhotoPath = loImage.getFileLoct();
//                                            poImage.insertDownloadedImageInfo(loImage);
//
//                                            poCreditApp.updateApplicantImageStat(psSourceNo);
//                                            //end - insert entry to image info
//                                            Log.e(TAG,loDownload.get("transnox").toString());
//                                            //todo:
//                                            //insert/update entry to credit_online_application_documents
//                                            //end - convert to image and save to proper file location
//
////                               new string response for success to convert file notification
//                                            loDownload = (org.json.simple.JSONObject) loParser.parse("{\"result\":\"success\",\"convert\":\"true\",\"message\":\""+ fileLoc.getAbsolutePath() +"/" + imageName + "\"}");
//                                            lsResult = String.valueOf(loDownload);
//                                        } else{
//                                            Log.e(TAG, "Unable to convert file.");
//                                            loDownload = (org.json.simple.JSONObject) loParser.parse("{\"result\":\"success\",\"convert\":\"false\",\"message\":\"Unable to convert file.\"}");
//                                            lsResult = String.valueOf(loDownload);
//
//                                        }
//                                    }else{
////                            default string response for error message
//                                        lsResult = String.valueOf(loDownload);
//                                    }
//
//                                    Thread.sleep(1000);

                                }
                            }
                        } else {

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                lsResult = AppConstants.LOCAL_EXCEPTION_ERROR(e.getMessage());
            }
            return lsResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public interface OnDCPDownloadListener {
        void onDowload();
        void onSuccess(String message);
        void onFailed(String message);
    }

}