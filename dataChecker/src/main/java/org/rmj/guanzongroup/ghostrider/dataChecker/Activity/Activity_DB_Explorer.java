/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dataChecker
 * Electronic Personnel Access Control Security System
 * project file created : 10/16/21, 1:48 PM
 * project file last modified : 10/16/21, 1:34 PM
 */

package org.rmj.guanzongroup.ghostrider.dataChecker.Activity;

import static org.rmj.guanzongroup.ghostrider.dataChecker.ViewModel.VMDBExplorer.PICK_DB_FILE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.ProgressDialog;
import org.rmj.guanzongroup.ghostrider.dataChecker.Adapter.DCPDataAdapter;
import org.rmj.guanzongroup.ghostrider.dataChecker.Obj.DCPData;
import org.rmj.guanzongroup.ghostrider.dataChecker.Obj.UserInfo;
import org.rmj.guanzongroup.ghostrider.dataChecker.R;
import org.rmj.guanzongroup.ghostrider.dataChecker.ViewModel.VMDBExplorer;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_DB_Explorer extends AppCompatActivity {
    private static final String TAG = Activity_DB_Explorer.class.getSimpleName();

    private VMDBExplorer mViewModel;

    private MaterialToolbar toolbar;
    private TextInputEditText txtDataName;
    private MaterialButton btnFind, btnPost;
    private RecyclerView recyclerView;
    private ProgressDialog poDialog;

    private ArrayList<DCPData> poDcp = new ArrayList<>();
    private UserInfo poUser;

    private ActivityResultLauncher<Intent> poLaunch;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_db_explorer);

        mViewModel = new ViewModelProvider(this).get(VMDBExplorer.class);
        poLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                Uri dbData = result.getData().getData();
                mViewModel.ExploreDb(dbData, new VMDBExplorer.ExploreDatabaseCallback() {
                    @Override
                    public void OnDataOwnerRetrieve(String DataOwner) {
                        txtDataName.setText(DataOwner);
                    }

                    @Override
                    public void OnDCPListRetrieve(ArrayList<DCPData> dcpData) {
                        poDcp = dcpData;
                        LinearLayoutManager loManager = new LinearLayoutManager(Activity_DB_Explorer.this);
                        loManager.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setAdapter(new DCPDataAdapter(dcpData));
                        recyclerView.setLayoutManager(loManager);
                    }

                    @Override
                    public void OnOwnerInfoRetrieve(UserInfo info) {
                        poUser = info;
                    }

                    @Override
                    public void OnFailedRetrieveInfo(String message) {

                    }
                });
            }
        });

        setupWidgets();

        btnFind.setOnClickListener(v -> mViewModel.FindDatabase(findDB -> poLaunch.launch(findDB)));
        btnPost.setOnClickListener(v -> mViewModel.PostCollectionDetail(poDcp, poUser, new VMDBExplorer.OnPostCollectionListener() {
            @Override
            public void OnPost(String message) {
                poDialog.initDialog("Posting DCP", "Posting collection details. Please wait...",  false);
                poDialog.show();
            }

            @Override
            public void OnPostSuccess(String message) {
                poDialog.dismiss();
            }

            @Override
            public void OnPostFailed(String message) {
                poDialog.dismiss();
            }
        }));
    }

    private void setupWidgets(){
        toolbar = findViewById(R.id.toolbar_dbExplorer);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        txtDataName = findViewById(R.id.txt_dbName);
        btnFind = findViewById(R.id.btn_findDb);
        btnPost = findViewById(R.id.btn_post);
        recyclerView = findViewById(R.id.recyclerview);

        poDialog = new ProgressDialog(Activity_DB_Explorer.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_DB_FILE && resultCode == RESULT_OK){
            Uri dbData = data.getData();
            mViewModel.ExploreDb(dbData, new VMDBExplorer.ExploreDatabaseCallback() {
                @Override
                public void OnDataOwnerRetrieve(String DataOwner) {
                    txtDataName.setText(DataOwner);
                }

                @Override
                public void OnDCPListRetrieve(ArrayList<DCPData> dcpData) {
                    poDcp = dcpData;
                    LinearLayoutManager loManager = new LinearLayoutManager(Activity_DB_Explorer.this);
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setAdapter(new DCPDataAdapter(dcpData));
                    recyclerView.setLayoutManager(loManager);
                }

                @Override
                public void OnOwnerInfoRetrieve(UserInfo info) {
                    poUser = info;
                }

                @Override
                public void OnFailedRetrieveInfo(String message) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}