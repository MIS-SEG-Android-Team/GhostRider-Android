package org.guanzongroup.com.itinerary.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.guanzongroup.com.itinerary.Adapter.AdapterItineraries;
import org.guanzongroup.com.itinerary.Dialog.DialogEntryDetail;
import org.guanzongroup.com.itinerary.R;
import org.guanzongroup.com.itinerary.ViewModel.VMUsersItineraries;
import org.rmj.g3appdriver.GCircle.room.Entities.EItinerary;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_UsersItineraries extends AppCompatActivity {
    private static final String TAG = Activity_UsersItineraries.class.getSimpleName();

    private VMUsersItineraries mViewModel;

    private LoadDialog poLoad;
    private MessageBox poDialog;

    private Toolbar toolbar;
    private TextView lblUser, lblDept;
    private TextInputEditText txtFrom, txtThru;
    private RecyclerView recyclerView;
    private MaterialButton btnFilter;

    private String psFrom, psThru;

    public boolean isFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_UsersItineraries.this).get(VMUsersItineraries.class);
        setContentView(R.layout.activity_users_itineraries);

        poLoad = new LoadDialog(Activity_UsersItineraries.this);
        poDialog = new MessageBox(Activity_UsersItineraries.this);

        String lsEmployID = getIntent().getStringExtra("sEmployID");
        String lsUserName = getIntent().getStringExtra("sUserName");
        String lsDeptName = DeptCode.getDepartmentName(getIntent().getStringExtra("sDeptIDxx"));
        String lsDateFrom = getIntent().getStringExtra("dDateFrom");
        String lsDateThru = getIntent().getStringExtra("dDateThru");
        psFrom = lsDateFrom;
        psThru = lsDateThru;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lblUser = findViewById(R.id.lbl_username);
        lblDept = findViewById(R.id.lbl_userPosition);
        txtFrom = findViewById(R.id.txt_dateFrom);
        txtThru = findViewById(R.id.txt_dateThru);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager loManager = new LinearLayoutManager(Activity_UsersItineraries.this);
        loManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(loManager);
        btnFilter = findViewById(R.id.btn_filter);
        lblUser.setText(lsUserName);
        lblDept.setText(lsDeptName);

        DownloadItinerary(lsEmployID, lsDateFrom, lsDateThru);
        txtFrom.setText(new AppConstants().CURRENT_DATE_WORD);

        txtFrom.setText(FormatUIText.formatGOCasBirthdate(lsDateFrom));
        txtThru.setText(FormatUIText.formatGOCasBirthdate(lsDateThru));

        txtFrom.setOnClickListener(v -> {
            if(txtThru.getText().toString().trim().isEmpty()) {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_UsersItineraries.this, (view131, year, monthOfYear, dayOfMonth) -> {
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
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_UsersItineraries.this, (view131, year, monthOfYear, dayOfMonth) -> {
                    try{
                        Date loThru = new SimpleDateFormat("MMMM dd, yyyy").parse(txtThru.getText().toString().trim());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String lsDate = dateFormatter.format(newDate.getTime());
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        if(loThru.before(loDate)){
                            Toast.makeText(Activity_UsersItineraries.this, "Invalid date entry.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Activity_UsersItineraries.this, "Date started must be set before the set time end.", Toast.LENGTH_SHORT).show();
            } else {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                final DatePickerDialog StartTime = new DatePickerDialog(Activity_UsersItineraries.this, (view131, year, monthOfYear, dayOfMonth) -> {
                    try{
                        Date loFrom = new SimpleDateFormat("MMMM dd, yyyy").parse(txtFrom.getText().toString().trim());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String lsDate = dateFormatter.format(newDate.getTime());
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        if(loFrom.after(loDate)){
                            Toast.makeText(Activity_UsersItineraries.this, "Invalid date entry.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Activity_UsersItineraries.this, "Please select starting and end date to filter.", Toast.LENGTH_SHORT).show();
            } else if(!isFiltered){
                DownloadItinerary(lsEmployID, psFrom, psThru);
                isFiltered = true;
                btnFilter.setText("Clear Filter");
            } else {
                isFiltered = false;
                txtFrom.setText(new AppConstants().CURRENT_DATE_WORD);
                txtThru.setText(new AppConstants().CURRENT_DATE_WORD);
                DownloadItinerary(lsEmployID, lsDateFrom, lsDateThru);
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

    private void DownloadItinerary(String employId, String lsDateFrom, String lsDateThru){
        mViewModel.DownloadItineraryForUser(employId, lsDateFrom, lsDateThru, new VMUsersItineraries.OnDownloadUserEntriesListener() {
            @Override
            public void OnImport(String title, String message) {
                poLoad.initDialog(title, message, false);
                poLoad.show();
            }

            @Override
            public void OnSuccess(List<EItinerary> args) {
                poLoad.dismiss();
                previewItinerary(args);
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();
                poDialog.initDialog();
                poDialog.setTitle("Employee Itinerary");
                poDialog.setMessage(message);
                poDialog.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                });
                poDialog.show();
            }
        });
    }

    private void previewItinerary(List<EItinerary> foVal){
        recyclerView.setAdapter(new AdapterItineraries(foVal, args -> new DialogEntryDetail(Activity_UsersItineraries.this).showDialog(args)));
    }
}