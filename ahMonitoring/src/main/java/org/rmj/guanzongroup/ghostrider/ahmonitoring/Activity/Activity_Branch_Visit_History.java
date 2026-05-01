package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.Adapter_Branch_Vist_History;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchVisit;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_Branch_Visit_History extends AppCompatActivity {

    private VMBranchVisit mViewModel;
    private MessageBox loMessage;

    private List<DBranchVisitMaster.MasterHistory> laHistories;
    private String lsDfrom;
    private String lsDto;

    private TextInputEditText txt_search;
    private ImageButton ib_filter;
    private RecyclerView recyclerview_history;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_branch_visit_history);

        mViewModel = new ViewModelProvider(this).get(VMBranchVisit.class);
        loMessage = new MessageBox(this);

        lsDfrom = GetLastMonth();
        lsDto = GetCurrentDate();

        InitWidgets();
        InitData(lsDfrom, lsDto);
    }

    private String GetCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    private String GetLastMonth(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.MONTH, -1);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(today.getTime());
        }
    }

    private void InitMessage(int mode, String message, onMessageButton callback){

        loMessage.initDialog();
        loMessage.setTitle("Guanzon Circle");
        loMessage.setMessage(message);

        switch (mode){

            case 1, 2:
                if (mode == 1){
                    loMessage.setIcon(R.drawable.baseline_message_24);
                }

                if (mode == 2){
                    loMessage.setIcon(R.drawable.baseline_error_24);
                }

                loMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });

                break;

            case 3:
                loMessage.setIcon(R.drawable.baseline_contact_support_24);
                loMessage.setPositiveButton("Yes", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });
                loMessage.setNegativeButton("No", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onNegative();
                    }
                });

                break;
        }

        loMessage.show();
    }

    private void InitWidgets(){
        txt_search = findViewById(R.id.txt_search);
        ib_filter = findViewById(R.id.ib_filter);
        recyclerview_history = findViewById(R.id.recyclerview_history);
    }

    private void InitData(String fsDfrom, String fsDto){

        mViewModel.ImportMaster(fsDfrom, fsDto, new VMBranchVisit.OnImportChecklist() {
            @Override
            public void OnLoad() {
                Toast.makeText(Activity_Branch_Visit_History.this, "Downloading history . .", Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnFinished(String fsMessage) {
                Toast.makeText(Activity_Branch_Visit_History.this, fsMessage, Toast.LENGTH_LONG).show();

                InitObservers();
            }
        });
    }

    private void InitObservers(){

        mViewModel.GetHistory().observe(Activity_Branch_Visit_History.this, new Observer<List<DBranchVisitMaster.MasterHistory>>() {
            @Override
            public void onChanged(List<DBranchVisitMaster.MasterHistory> masterHistories) {

                if (masterHistories == null){

                    InitMessage(3, "History not found! Do you want to re-download data?", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            InitData(lsDfrom, lsDto);
                        }

                        @Override
                        public void onNegative() {
                            finish();
                        }
                    });
                    return;
                }

                laHistories = masterHistories;
                recyclerview_history.setAdapter(
                        new Adapter_Branch_Vist_History(laHistories, new Adapter_Branch_Vist_History.OnItemListener() {
                            @Override
                            public void OnSelectMaster(DBranchVisitMaster.MasterHistory foTrans) {

                                //open details for viewing only
                                Intent loIntent = new Intent(Activity_Branch_Visit_History.this, Activity_Branch_Visit_Checklist.class);
                                loIntent.putExtra("type", "1");
                                loIntent.putExtra("source", foTrans.sTransNox);
                                loIntent.putExtra("branch", foTrans.sBranchCd);

                                startActivity(loIntent);
                            }
                        })
                );

            }
        });
    }
}