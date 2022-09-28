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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GRider.Constants.AppConstants;
import org.rmj.g3appdriver.GRider.Database.DataAccessObject.DDCPCollectionDetail;
import org.rmj.g3appdriver.GRider.Database.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GRider.Database.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GRider.Etc.LoadDialog;
import org.rmj.g3appdriver.GRider.Etc.MessageBox;
import org.rmj.g3appdriver.lib.integsys.Dcp.ImportParams;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.CollectionAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogAccountDetail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogAddCollection;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogConfirmPost;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.Dialog_ClientSearch;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.Dialog_DebugEntry;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMCollectionList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_CollectionList extends AppCompatActivity {
    private static final String TAG = Activity_CollectionList.class.getSimpleName();

    private static final int MOBILE_DIALER = 104;
    private static final int PICK_TEXT_FILE = 105;
    private static final int EXPORT_TEXT_FILE = 106;

    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private VMCollectionList mViewModel;

    private TextInputEditText txtSearch;
    private TextInputLayout tilSearch;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private TextView lblBranch, lblAddxx, lblDate;
    private JSONArray expCollectDetl;

    private MaterialButton btnDownload, btnImport;
    private LinearLayout lnImportPanel, lnPosted;
    private TextView lblNoName;

    private String FILENAME;
    private final String FILE_TYPE = "-mob.txt";
    private String fileContent= "";

    private JSONObject poDcpData;

    private List<DDCPCollectionDetail.CollectionDetail> plDetail;

    private final ActivityResultLauncher<Intent> poImport = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            Intent loIntent = result.getData();
            Uri uri = loIntent.getData();
//            importDataFromFile(uri);
        }
    });

    private final ActivityResultLauncher<Intent> poExport = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent loIntent = result.getData();
                Uri uri = loIntent.getData();
//                exportCollectionList(uri, poDcpData);
//                mViewModel.setExportedDCP(false);
            }
        }
    });

    private final ActivityResultLauncher<Intent> poDialer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){

        } else {

        }
    });

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_list);
        mViewModel = new ViewModelProvider(this).get(VMCollectionList.class);
        expCollectDetl = new JSONArray();
        initWidgets();

        mViewModel.GetUserInfo().observe(Activity_CollectionList.this, new Observer<EEmployeeInfo>() {
            @Override
            public void onChanged(EEmployeeInfo user) {
                try {
                    lblBranch.setText(user.getBranchNm());
                    lblDate.setText(new AppConstants().CURRENT_DATE_WORD);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mViewModel.GetColletionList().observe(Activity_CollectionList.this, collectionDetails -> {
            try{
                if(collectionDetails.size() > 0) {
                    tilSearch.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    lnImportPanel.setVisibility(View.GONE);
                    FILENAME = collectionDetails.get(0).getTransNox();
                    Log.e("Master List TransNox",collectionDetails.get(0).getTransNox() );
                } else {
                    tilSearch.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    lnImportPanel.setVisibility(View.VISIBLE);
                    btnDownload.setOnClickListener(v -> {
                        showDownloadDcp();
                    });

                    btnImport.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/plain");

                        // Optionally, specify a URI for the file that should appear in the
                        // system file picker when it loads.
                        try {
                            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, new URI(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        poImport.launch(intent);
                    });
                }

                CollectionAdapter loAdapter = new CollectionAdapter(collectionDetails, new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void OnClick(int position) {
                        showTransaction(position,collectionDetails);
                    }

                    @Override
                    public void OnMobileNoClickListener(String MobileNo) {
                        Intent mobileIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", MobileNo, null));
                        poDialer.launch(mobileIntent);
                    }

                    @Override
                    public void OnAddressClickListener(String Address, String[] args) {
                        //TODO: Future update... add google map API for auto searching address location...
                    }

                    @Override
                    public void OnActionButtonClick() {
                        //TODO: Future update
                    }
                });

                txtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            loAdapter.getCollectionSearch().filter(charSequence.toString());
                            if(loAdapter.getItemCount() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                lblNoName.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                lblNoName.setVisibility(View.GONE);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(loAdapter);
                recyclerView.getRecycledViewPool().clear();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_collectionList);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtSearch = findViewById(R.id.txt_collectionSearch);
        tilSearch = findViewById(R.id.til_collectionSearch);

        btnDownload = findViewById(R.id.btn_download);
        btnImport = findViewById(R.id.btn_import);
        lnImportPanel = findViewById(R.id.ln_import_panel);
        lblNoName = findViewById(R.id.txt_no_name);

        recyclerView = findViewById(R.id.recyclerview_collectionList);
        layoutManager = new LinearLayoutManager(Activity_CollectionList.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        lblBranch = findViewById(R.id.lbl_headerBranch);
        lblAddxx = findViewById(R.id.lbl_headerAddress);
        lblDate = findViewById(R.id.lbl_headerDate);

        poDialogx = new LoadDialog(Activity_CollectionList.this);
        poMessage = new MessageBox(Activity_CollectionList.this);

        plDetail = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_dcp_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.action_menu_add_collection){
            showAddDcpCollection();
        } else if (item.getItemId() == R.id.action_menu_post_collection) {
            showPostCollection();
        } else if (item.getItemId() == R.id.action_menu_image_log) {
            Intent loIntent = new Intent(Activity_CollectionList.this, Activity_ImageLog.class);
            startActivity(loIntent);
        }
        return super.onOptionsItemSelected(item);
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == MOBILE_DIALER){
//            if(resultCode == RESULT_OK){
//
//            }
//        }
//        else if (requestCode == AppConstants.INTENT_DOWNLOAD_DCP && resultCode == RESULT_OK){
//            showDownloadDcp();
//        }else if (requestCode == AppConstants.INTENT_ADD_COLLECTION_DCP && resultCode == RESULT_OK){
//            showAddDcpCollection();
//        }else if (requestCode == AppConstants.INTENT_TRANSACTION_DCP && resultCode == RESULT_OK){
//            mViewModel.GetColletionList().observe(this, collectionDetails -> showTransaction(DCP_Constants.collectionPos, collectionDetails));
//        }else if(requestCode == PICK_TEXT_FILE && resultCode == RESULT_OK){
//
//        }else if(requestCode == EXPORT_TEXT_FILE && resultCode == RESULT_OK){
//
//        }
    }
    public void showPostCollection(){
        mViewModel.CheckDcpForPosting(new VMCollectionList.OnCheckDcpForPosting() {
            @Override
            public void OnLoad() {
                poDialogx.initDialog("Daily Collection Plan", "Searching client. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess() {
                PostCollection("");
            }

            @Override
            public void OnIncompleteDcp() {
                DialogConfirmPost loPost = new DialogConfirmPost(Activity_CollectionList.this);
                loPost.iniDialog(new DialogConfirmPost.DialogPostUnfinishedListener() {
                    @Override
                    public void OnConfirm(AlertDialog dialog, String Remarks) {
                        dialog.dismiss();
                        PostCollection(Remarks);
                    }

                    @Override
                    public void OnCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
                loPost.show();
            }

            @Override
            public void OnFailed(String message) {
                poMessage.initDialog();
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    public void showTransaction(int position, List<EDCPCollectionDetail> collectionDetails){
        DialogAccountDetail loDialog = new DialogAccountDetail(Activity_CollectionList.this);
        loDialog.initAccountDetail(Activity_CollectionList.this ,collectionDetails.get(position), (dialog, remarksCode) -> {
            Intent loIntent = new Intent(Activity_CollectionList.this, Activity_Transaction.class);
            loIntent.putExtra("remarksx", remarksCode);
            loIntent.putExtra("transnox", collectionDetails.get(position).getTransNox());
            loIntent.putExtra("entrynox", collectionDetails.get(position).getEntryNox());
            loIntent.putExtra("accntnox", collectionDetails.get(position).getAcctNmbr());
            startActivity(loIntent);
            dialog.dismiss();
        });
        loDialog.show();
    }

    public void showAddDcpCollection(){
        try {
            DialogAddCollection loDialog = new DialogAddCollection(Activity_CollectionList.this);
            loDialog.initDialog(new DialogAddCollection.OnDialogButtonClickListener() {
                @Override
                public void OnDownloadClick(Dialog Dialog, String accountNo, String fsType) {
                    Dialog.dismiss();
                    mViewModel.SearchClient(accountNo, new VMCollectionList.OnDownloadClientList() {
                        @Override
                        public void OnDownload() {
                            poDialogx.initDialog("Daily Collection Plan", "Searching client. Please wait...", false);
                            poDialogx.show();
                        }

                        @Override
                        public void OnSuccessDownload(List<EDCPCollectionDetail> detail) {
                            Dialog_ClientSearch loClient = new Dialog_ClientSearch(Activity_CollectionList.this);
                            loClient.initDialog(detail, new Dialog_ClientSearch.OnClientSelectListener() {
                                @Override
                                public void OnSelect(AlertDialog clientList, EDCPCollectionDetail detail) {
                                    // Validation if user accidentally tap on list on
                                    poMessage.initDialog();
                                    poMessage.setTitle("Add Collection");
                                    poMessage.setMessage("Add " + detail.getFullName() + " with account number " +
                                            detail.getAcctNmbr() + " to list of collection?");
                                    poMessage.setPositiveButton("Yes", (view, msgDialog) -> {
                                        clientList.dismiss();
                                        mViewModel.AddCollection(detail, new VMCollectionList.OnActionCallback() {
                                            @Override
                                            public void OnLoad() {
                                                msgDialog.dismiss();
                                                poDialogx.initDialog("Daily Collection Plan", "Adding client to DCP list. Please wait...", false);
                                                poDialogx.show();
                                            }

                                            @Override
                                            public void OnSuccess() {
                                                poDialogx.dismiss();
                                                poMessage.initDialog();
                                                poMessage.setTitle("Daily Collection Plan");
                                                poMessage.setMessage( detail.getFullName() + " has been added to collection list.");
                                                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                                poMessage.show();
                                            }

                                            @Override
                                            public void OnFailed(String message) {
                                                poDialogx.dismiss();
                                                poMessage.initDialog();
                                                poMessage.setTitle("Daily Collection Plan");
                                                poMessage.setMessage(message);
                                                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                                poMessage.show();
                                            }
                                        });
                                    });
                                    poMessage.setNegativeButton("No", (view, msgDialog) -> msgDialog.dismiss());
                                    poMessage.show();
                                }

                                @Override
                                public void OnCancel(AlertDialog clientDialog) {
                                    clientDialog.dismiss();
                                    // Show Add collection dialog if user cancels search client list */
                                    loDialog.show();
                                }
                            });
                            loClient.show();
                        }

                        @Override
                        public void OnFailedDownload(String message) {

                        }
                    });
                }

                @Override
                public void OnCancel(Dialog Dialog) {
                    Dialog.dismiss();
                }
            });
            loDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showDownloadDcp(){
        boolean isTesting = mViewModel.IsTesting();
        if(isTesting){
            Dialog_DebugEntry loDebug = new Dialog_DebugEntry(Activity_CollectionList.this);
            loDebug.iniDialog(args -> {
                try {
                    JSONObject loJson = new JSONObject(args);
                    String EmployID = loJson.getString("employid");
                    String ReferDte = loJson.getString("date");
                    DownloadDCP(new ImportParams(EmployID, ReferDte, "1"));
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            loDebug.show();
        } else {
            DownloadDCP(null);
        }
    }

    private void DownloadDCP(ImportParams foVal){
        mViewModel.DownloadDCP(foVal, new VMCollectionList.OnActionCallback() {
            @Override
            public void OnLoad() {
                poDialogx.initDialog("Daily Collection Plan",
                        "Download DCP List ... Please wait.", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess() {
                poDialogx.dismiss();
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    private void PostCollection(String fsVal){
        mViewModel.PostCollectionList(fsVal, new VMCollectionList.OnActionCallback() {
            @Override
            public void OnLoad() {
                poDialogx.initDialog("Daily Collection Plan",
                        "Posting collection details. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess() {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage("Dcp posted successfully.");
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }
}