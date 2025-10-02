package org.guanzongroup.com.itinerary.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.guanzongroup.com.itinerary.Adapter.AdapterEmployees;
import org.guanzongroup.com.itinerary.Adapter.AdapterItineraries;
import org.guanzongroup.com.itinerary.Dialog.DialogDateSelection;
import org.guanzongroup.com.itinerary.Dialog.DialogEntryDetail;
import org.guanzongroup.com.itinerary.R;
import org.guanzongroup.com.itinerary.ViewModel.VMItinerary;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_ItineraryLog extends AppCompatActivity {
    private static final String TAG = Activity_ItineraryLog.class.getSimpleName();

    private VMItinerary mViewModel;
    private LoadDialog poLoad;
    private MessageBox poDialog;

    private Toolbar toolbar;
    private TextView lblUser, lblDept;
    private TextInputEditText txtFrom, txtThru;
    private RecyclerView recyclerView;
    private MaterialButton btnFilter;

    private String psFrom = AppConstants.CURRENT_DATE(), psThru = AppConstants.CURRENT_DATE();

    public boolean isFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_ItineraryLog.this).get(VMItinerary.class);
        setContentView(R.layout.activity_itinerary_log);

        poLoad = new LoadDialog(Activity_ItineraryLog.this);
        poDialog = new MessageBox(Activity_ItineraryLog.this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lblUser = findViewById(R.id.lbl_username);
        lblDept = findViewById(R.id.lbl_userPosition);
        txtFrom = findViewById(R.id.txt_dateFrom);
        txtThru = findViewById(R.id.txt_dateThru);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager loManager = new LinearLayoutManager(Activity_ItineraryLog.this);
        loManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(loManager);
        btnFilter = findViewById(R.id.btn_filter);

        mViewModel.GetUserInfo().observe(Activity_ItineraryLog.this, user -> {
            try {
                lblUser.setText(user.sUserName);
                lblDept.setText(DeptCode.getDepartmentName(user.sDeptIDxx));

                int lnUserLvl = Integer.parseInt(user.sEmpLevID);

                if(lnUserLvl == DeptCode.LEVEL_DEPARTMENT_HEAD){
                    mViewModel.ImportUsers(new VMItinerary.OnImportUsersListener() {
                        @Override
                        public void OnImport(String title, String message) {
                            poLoad.initDialog(title, message, false);
                            poLoad.show();
                        }

                        @Override
                        public void OnSuccess(List<JSONObject> args) {
                            poLoad.dismiss();
                            InitEmployeeList(args);
                        }

                        @Override
                        public void OnFailed(String message) {
                            poLoad.dismiss();
                            poDialog.initDialog();
                            poDialog.setIcon(R.drawable.baseline_error_24);
                            poDialog.setTitle("Employee Itinerary");
                            poDialog.setMessage(message);
                            poDialog.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                            });
                            poDialog.show();
                        }
                    });
                    findViewById(R.id.cl_dateFilter).setVisibility(View.GONE);
                } else if(lnUserLvl == DeptCode.LEVEL_GENERAL_MANAGER){
                    findViewById(R.id.cl_dateFilter).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.cl_dateFilter).setVisibility(View.VISIBLE);
                    mViewModel.DownloadItinerary(psFrom, psThru, new VMItinerary.OnActionCallback() {
                        @Override
                        public void OnLoad(String title, String message) {
                            poLoad.initDialog(title, message, false);
                            poLoad.show();
                        }

                        @Override
                        public void OnSuccess(String args) {
                            poLoad.dismiss();
                            poDialog.initDialog();
                            poDialog.setIcon(R.drawable.baseline_message_24);
                            poDialog.setTitle("Employee Itinerary");
                            poDialog.setMessage(args);
                            poDialog.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                            });
                            poDialog.show();
                        }

                        @Override
                        public void OnFailed(String message) {
                            poLoad.dismiss();
                            poDialog.initDialog();
                            poDialog.setIcon(R.drawable.baseline_error_24);
                            poDialog.setTitle("Employee Itinerary");
                            poDialog.setMessage(message);
                            poDialog.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                            });
                            poDialog.show();
                        }
                    });
                    previewItinerary();
                }
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        });

        txtFrom.setText(new AppConstants().CURRENT_DATE_WORD);
        txtThru.setText(new AppConstants().CURRENT_DATE_WORD);

        txtFrom.setOnClickListener(v -> {
            if(txtThru.getText().toString().trim().isEmpty()) {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_ItineraryLog.this, (view131, year, monthOfYear, dayOfMonth) -> {
                    try{
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String lsDate = dateFormatter.format(newDate.getTime());
                        txtFrom.setText(lsDate);
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        psFrom = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                        Log.d(TAG, "Date start: " + psFrom + ", UI format: " + lsDate);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            } else {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_ItineraryLog.this, (view131, year, monthOfYear, dayOfMonth) -> {
                    try{
                        Date loThru = new SimpleDateFormat("MMMM dd, yyyy").parse(txtThru.getText().toString().trim());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String lsDate = dateFormatter.format(newDate.getTime());
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        if(loThru.before(loDate)){
                            Toast.makeText(Activity_ItineraryLog.this, "Invalid date entry.", Toast.LENGTH_SHORT).show();
                        } else {
                            txtFrom.setText(lsDate);
                            psFrom = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            }
        });

        txtThru.setOnClickListener(v -> {
            if(txtFrom.getText().toString().trim().isEmpty()){
                Toast.makeText(Activity_ItineraryLog.this, "Date started must be set before the set time end.", Toast.LENGTH_SHORT).show();
            } else {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_ItineraryLog.this, (view131, year, monthOfYear, dayOfMonth) -> {
                    try{
                        Date loFrom = new SimpleDateFormat("MMMM dd, yyyy").parse(txtFrom.getText().toString().trim());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String lsDate = dateFormatter.format(newDate.getTime());
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        if(loFrom.after(loDate)){
                            Toast.makeText(Activity_ItineraryLog.this, "Invalid date entry.", Toast.LENGTH_SHORT).show();
                        } else {
                            txtThru.setText(lsDate);
                            psThru = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            }
        });

        btnFilter.setOnClickListener(v -> {
            if(psFrom == null && psThru == null) {
                Toast.makeText(Activity_ItineraryLog.this, "Please select starting and end date to filter.", Toast.LENGTH_SHORT).show();
            } else if(!isFiltered) {
                mViewModel.DownloadItinerary(psFrom, psThru, new VMItinerary.OnActionCallback() {
                    @Override
                    public void OnLoad(String title, String message) {
                        poLoad.initDialog(title, message, false);
                        poLoad.show();
                    }

                    @Override
                    public void OnSuccess(String args) {
                        poLoad.dismiss();
                        poDialog.initDialog();
                        poDialog.setIcon(R.drawable.baseline_message_24);
                        poDialog.setTitle("Employee Itinerary");
                        poDialog.setMessage(args);
                        poDialog.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                        });
                        poDialog.show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poLoad.dismiss();
                        poDialog.initDialog();
                        poDialog.setIcon(R.drawable.baseline_error_24);
                        poDialog.setTitle("Employee Itinerary");
                        poDialog.setMessage(message);
                        poDialog.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                        });
                        poDialog.show();
                    }
                });

                mViewModel.GetItineraryForFilteredDate(psFrom, psThru).observe(Activity_ItineraryLog.this, eItineraries -> {
                    try {
                        isFiltered = true;
                        recyclerView.setAdapter(new AdapterItineraries(eItineraries, args -> new DialogEntryDetail(Activity_ItineraryLog.this).showDialog(args)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                btnFilter.setText("Clear Filter");
            } else {
                isFiltered = false;
                previewItinerary();
                txtFrom.setText(new AppConstants().CURRENT_DATE_WORD);
                txtThru.setText(new AppConstants().CURRENT_DATE_WORD);
                btnFilter.setText("Filter Records");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void previewItinerary(){
        if(!isFiltered) {
            mViewModel.GetItineraryListForCurrentDay().observe(Activity_ItineraryLog.this, eItineraries -> {
                try {
                    recyclerView.setAdapter(new AdapterItineraries(eItineraries, args -> new DialogEntryDetail(Activity_ItineraryLog.this).showDialog(args)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void InitEmployeeList(List<JSONObject> foVal) {
        try {
            recyclerView.setAdapter(new AdapterEmployees(foVal, (args, args1, args2) -> {
                new DialogDateSelection(Activity_ItineraryLog.this).showDialog((args3, args4) -> {
                    Intent loIntent = new Intent(Activity_ItineraryLog.this, Activity_UsersItineraries.class);
                    loIntent.putExtra("sEmployID", args);
                    loIntent.putExtra("sUserName", args1);
                    loIntent.putExtra("sDeptIDxx", args2);
                    loIntent.putExtra("dDateFrom", args3);
                    loIntent.putExtra("dDateThru", args4);
                    startActivity(loIntent);
                });
            }));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}