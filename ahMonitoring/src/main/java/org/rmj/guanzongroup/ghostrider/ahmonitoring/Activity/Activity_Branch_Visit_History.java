package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
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
    private Adapter_Branch_Vist_History loAdapter;

    private List<DBranchVisitMaster.MasterHistory> laHistories;
    private String lsDfrom;
    private String lsDto;
    private String lsTranstat;

    private ConstraintLayout layout_record;
    private LinearLayout layout_norecord;
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
        lsTranstat = "cTranStat IN ('0', '1', '2')";

        InitWidgets();
        InitData(lsDfrom, lsDto);
        InitListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @SuppressLint("SimpleDateFormat")
    private String GetDateFormat(Long fsDate){
        return new SimpleDateFormat("yyyy-MM-dd").format(fsDate);
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
        layout_record = findViewById(R.id.layout_record);
        layout_norecord = findViewById(R.id.layout_norecord);
        txt_search = findViewById(R.id.txt_search);
        ib_filter = findViewById(R.id.ib_filter);
        recyclerview_history = findViewById(R.id.recyclerview_history);
    }

    private void InitData(String fsDfrom, String fsDto){

        mViewModel.ImportMaster(fsDfrom, fsDto, new VMBranchVisit.OnImport() {
            @Override
            public void OnLoad() {
                Toast.makeText(Activity_Branch_Visit_History.this, "Downloading history . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFinished(String fsMessage) {
                Toast.makeText(Activity_Branch_Visit_History.this, fsMessage, Toast.LENGTH_SHORT).show();
                InitObservers();
            }
        });
    }

    private void InitObservers(){

        mViewModel.GetHistory(lsTranstat).observe(Activity_Branch_Visit_History.this, new Observer<List<DBranchVisitMaster.MasterHistory>>() {
            @Override
            public void onChanged(List<DBranchVisitMaster.MasterHistory> masterHistories) {

                if (masterHistories == null){

                    InitMessage(3, "Transactions from " + lsDfrom + " to " + lsDto + " not found! Do you want to re-download data?", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            InitData(lsDfrom, lsDto);
                        }

                        @Override
                        public void onNegative() {
                            finish();
                        }
                    });
                    layout_norecord.setVisibility(VISIBLE);
                    layout_record.setVisibility(GONE);
                    return;
                }
                laHistories = masterHistories;

                layout_norecord.setVisibility(GONE);
                layout_record.setVisibility(VISIBLE);

                loAdapter = new Adapter_Branch_Vist_History(laHistories, new Adapter_Branch_Vist_History.OnItemListener() {
                    @Override
                    public void OnSelectMaster(DBranchVisitMaster.MasterHistory foTrans) {

                        //open details for viewing only
                        Intent loIntent = new Intent(Activity_Branch_Visit_History.this, Activity_Branch_Visit_Checklist.class);
                        loIntent.putExtra("type", "1");
                        loIntent.putExtra("source", foTrans.sTransNox);
                        loIntent.putExtra("branch", foTrans.sBranchCd);

                        startActivity(loIntent);
                    }
                });

                recyclerview_history.setAdapter(loAdapter);
                recyclerview_history.setLayoutManager(new LinearLayoutManager(Activity_Branch_Visit_History.this, LinearLayoutManager.VERTICAL, false));

            }
        });
    }

    private void InitListener(){

        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(Activity_Branch_Visit_History.this, view);
                loMenu.getMenuInflater().inflate(R.menu.menu_branch_monitoring_history, loMenu.getMenu());
                loMenu.show();

                //object listener
                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_by_date){ //filter by date

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = GetDateFormat(selection.first);
                                    lsDto = GetDateFormat(selection.second);

                                    //ask user before downloading
                                    InitMessage(3, "Download transactions from " + lsDfrom + " to " + lsDto + "?", new onMessageButton() {
                                        @Override
                                        public void onPositive() {
                                            InitData(lsDfrom, lsDto);
                                        }

                                        @Override
                                        public void onNegative() {}
                                    });
                                }
                            });
                            loPicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

                            return true;
                        }else if (item.getItemId() == R.id.menu_item_all){ //filter by status
                            if (loAdapter == null){
                                return false;
                            }
                            lsTranstat = "cTranStat IN ('0', '1', '2')";
                            InitObservers();
                            return true;
                        }else if (item.getItemId() == R.id.menu_item_open){ //filter by status
                            if (loAdapter == null){
                                return false;
                            }
                            lsTranstat = "cTranStat = '0'";
                            InitObservers();
                            return true;
                        }else if (item.getItemId() == R.id.menu_item_closed){ //filter by status
                            if (loAdapter == null){
                                return false;
                            }
                            lsTranstat = "cTranStat = '1'";
                            InitObservers();
                            return true;
                        }else if (item.getItemId() == R.id.menu_item_posted){ //filter by status
                            if (loAdapter == null){
                                return false;
                            }
                            lsTranstat = "cTranStat = '2'";
                            InitObservers();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null){
                    return;
                }
                loAdapter.GetFilter().filter(charSequence);
                loAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}