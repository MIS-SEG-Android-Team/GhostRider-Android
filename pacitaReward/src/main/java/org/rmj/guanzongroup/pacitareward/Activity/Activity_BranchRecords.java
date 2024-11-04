package org.rmj.guanzongroup.pacitareward.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita.BranchRecords;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.pacitareward.Adapter.RecyclerViewAdapter_BranchRecord;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewModel.VMBranchRecords;

import java.util.List;

public class Activity_BranchRecords extends AppCompatActivity {
    private VMBranchRecords mviewModel;
    private MaterialToolbar toolbar;
    private RecyclerView branch_rec;
    private String intentDataBranchcd;
    private String intentDataBranchName;
    private MaterialTextView mtv_title;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_records);

        intentDataBranchcd = getIntent().getStringExtra("Branch Code");
        intentDataBranchName = getIntent().getStringExtra("Branch Name");

        mviewModel = new ViewModelProvider(this).get(VMBranchRecords.class);

        toolbar = findViewById(R.id.toolbar);
        mtv_title = findViewById(R.id.mtv_title);
        branch_rec = findViewById(R.id.branch_rec);

        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setTitle(""); //set default title for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true); //enable the back button set on toolbar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mtv_title.setText(intentDataBranchName);

        poLoad = new LoadDialog(Activity_BranchRecords.this);
        poMessage = new MessageBox(Activity_BranchRecords.this);

        mviewModel.initializeRecords(intentDataBranchcd, new VMBranchRecords.BranchRecordsCallBack() {
            @Override
            public void onInitialize(String message) {
                poLoad.initDialog("Branch Records", message, false);
                poLoad.show();
            }

            @Override
            public void onSuccess(String message) {
                poLoad.dismiss();
                mviewModel.getBranchRecords(intentDataBranchcd).observe(Activity_BranchRecords.this, new Observer<List<BranchRecords>>() {
                    @Override
                    public void onChanged(List<BranchRecords> branchRecords) {
                        if (branchRecords.size() <= 0){
                            return;
                        }

                        RecyclerViewAdapter_BranchRecord recyclerViewAdapterBranchRecord = new RecyclerViewAdapter_BranchRecord(
                                Activity_BranchRecords.this, branchRecords, new RecyclerViewAdapter_BranchRecord.onSelectItem() {
                            @Override
                            public void onItemSelected(String transactNox) {
                                Intent intent = new Intent(Activity_BranchRecords.this, Activity_BranchRecord_Details.class);
                                intent.putExtra("Branch Code", intentDataBranchcd);
                                intent.putExtra("Branch Name", intentDataBranchName);
                                intent.putExtra("Transaction No", transactNox);
                                startActivity(intent);
                            }
                        });

                        branch_rec.setAdapter(recyclerViewAdapterBranchRecord);
                        branch_rec.setLayoutManager(new LinearLayoutManager(Activity_BranchRecords.this));
                    }
                });
            }

            @Override
            public void onError(String message) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Transaction Result");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
                poMessage.show();
            }
        });
    }
}