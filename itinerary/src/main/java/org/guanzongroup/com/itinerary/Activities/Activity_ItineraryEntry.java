package org.guanzongroup.com.itinerary.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.guanzongroup.com.itinerary.R;
import org.guanzongroup.com.itinerary.ViewModel.VMItinerary;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Itinerary.Obj.EmployeeItinerary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Activity_ItineraryEntry extends AppCompatActivity {
    private static final String TAG = Activity_ItineraryEntry.class.getSimpleName();

    private VMItinerary mViewModel;
    private LoadDialog poLoad;
    private MessageBox poDialog;

    private Toolbar toolbar;
    private TextView lblUser, lblDept;
    private TextInputEditText txtDate, txtStrt, txtEndx, txtRmrk;
    private AutoCompleteTextView txtLoct;
    private final EmployeeItinerary.ItineraryEntry loDetail = new EmployeeItinerary.ItineraryEntry();

    public boolean isClicked = false; //use public variable for button click validation to avoid double tap/click...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_ItineraryEntry.this).get(VMItinerary.class);
        setContentView(R.layout.activity_itinerary_entry);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtDate = findViewById(R.id.txt_schedDate);
        txtStrt = findViewById(R.id.txt_startTime);
        txtEndx = findViewById(R.id.txt_endTime);
        txtLoct = findViewById(R.id.txt_location);
        txtRmrk = findViewById(R.id.txt_purpose);
        lblUser = findViewById(R.id.lbl_username);
        lblDept = findViewById(R.id.lbl_userPosition);

        //init default itinerary values
        loDetail.setTransact(AppConstants.CURRENT_DATE());

        poLoad = new LoadDialog(Activity_ItineraryEntry.this);
        poDialog = new MessageBox(Activity_ItineraryEntry.this);


        mViewModel.GetUserInfo().observe(Activity_ItineraryEntry.this, user -> {
            try {
                lblUser.setText(user.sUserName);
                lblDept.setText(DeptCode.getDepartmentName(user.sDeptIDxx));
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        });

        txtDate.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_ItineraryEntry.this, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    txtDate.setText(lsDate);
                    Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                    lsDate = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                    Log.d(TAG, "Save formatted time: " + lsDate);
                    loDetail.setTransact(lsDate);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.show();
        });

        txtStrt.setOnClickListener(v -> {
            try {
                if (txtEndx.getText().toString().trim().isEmpty()) {
                    final Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat loFormat = new SimpleDateFormat("HH:mm aa");
                    final TimePickerDialog StartTime = new TimePickerDialog(Activity_ItineraryEntry.this, (view, hourOfDay, minute) -> {
                        try {
                            Calendar loStart = Calendar.getInstance();
                            Log.d(TAG, "Time: " + hourOfDay + ":" + minute);
                            loStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            loStart.set(Calendar.MINUTE, minute);
                            String lsStart = loFormat.format(loStart.getTime());
                            Log.d(TAG, "Formatted time: " + lsStart);
                            txtStrt.setText(lsStart);

                            Date loDate = new SimpleDateFormat("HH:mm aa").parse(lsStart);
                            String lsFTime = new SimpleDateFormat("HH:mm:ss").format(loDate);
                            Log.d(TAG, "Save formatted time: " + lsFTime);
                            loDetail.setTimeStrt(lsFTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    StartTime.show();
                } else {
                    Date loEnd = new SimpleDateFormat("hh:mm aa").parse(txtEndx.getText().toString().trim());
                    final Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat loFormat = new SimpleDateFormat("HH:mm aa");
                    final TimePickerDialog StartTime = new TimePickerDialog(Activity_ItineraryEntry.this, (view, hourOfDay, minute) -> {
                        try {
                            Calendar loStart = Calendar.getInstance();
                            Log.d(TAG, "Time: " + hourOfDay + ":" + minute);
                            loStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            loStart.set(Calendar.MINUTE, minute);
                            String lsStart = loFormat.format(loStart.getTime());
                            Log.d(TAG, "Formatted time: " + lsStart);
                            Date loDate = new SimpleDateFormat("hh:mm aa").parse(lsStart);
                            if (loEnd.before(loDate)) {
                                Toast.makeText(Activity_ItineraryEntry.this, "Invalid time entry.", Toast.LENGTH_SHORT).show();
                            } else {
                                txtStrt.setText(lsStart);

                                String lsFTime = new SimpleDateFormat("HH:mm:ss").format(loDate);
                                Log.d(TAG, "Save formatted time: " + lsFTime);
                                loDetail.setTimeStrt(lsFTime);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    StartTime.show();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        txtEndx.setOnClickListener(v -> {
            if (txtStrt.getText().toString().trim().isEmpty()) {
                Toast.makeText(Activity_ItineraryEntry.this, "Please set time start.", Toast.LENGTH_SHORT).show();
            } else {
                final Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat loFormat = new SimpleDateFormat("HH:mm aa");
                final TimePickerDialog StartTime = new TimePickerDialog(Activity_ItineraryEntry.this, (view, hourOfDay, minute) -> {
                    try {
                        Date lodStart = new SimpleDateFormat("hh:mm aa").parse(txtStrt.getText().toString().trim());
                        Calendar loStart = Calendar.getInstance();
                        Log.d(TAG, "Time: " + hourOfDay + ":" + minute);
                        loStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        loStart.set(Calendar.MINUTE, minute);
                        String lsStart = loFormat.format(loStart.getTime());
                        Log.d(TAG, "Formatted time: " + lsStart);
                        Date loDate = new SimpleDateFormat("hh:mm aa").parse(lsStart);
                        if (lodStart.after(loDate)){
                            Toast.makeText(Activity_ItineraryEntry.this, "Invalid time entry.", Toast.LENGTH_SHORT).show();
                        } else {
                            txtEndx.setText(lsStart);

                            String lsFTime = new SimpleDateFormat("HH:mm:ss").format(loDate);
                            Log.d(TAG, "Save formatted time: " + lsFTime);
                            loDetail.setTimeEndx(lsFTime);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                StartTime.show();
            }
        });

        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if(!isClicked) {
                isClicked = true;
                loDetail.setLocation(txtLoct.getText().toString().trim());
                loDetail.setRemarksx(Objects.requireNonNull(txtRmrk.getText()).toString().trim());
                isClicked = true;
                mViewModel.SaveItinerary(loDetail, new VMItinerary.OnActionCallback() {
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
                            txtDate.setText("");
                            txtStrt.setText("");
                            txtEndx.setText("");
                            txtRmrk.setText("");
                            txtLoct.setText("");
                            isClicked = false;
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
                            isClicked = false;
                        });
                        poDialog.show();
                    }
                });
            } else {
                Toast.makeText(Activity_ItineraryEntry.this, "Please wait...", Toast.LENGTH_SHORT).show();
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
}