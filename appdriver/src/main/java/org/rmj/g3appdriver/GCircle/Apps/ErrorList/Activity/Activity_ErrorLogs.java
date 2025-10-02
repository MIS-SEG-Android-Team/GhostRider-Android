package org.rmj.g3appdriver.GCircle.Apps.ErrorList.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Apps.ErrorList.Adapter.ErrorListAdapter;
import org.rmj.g3appdriver.GCircle.Apps.ErrorList.Dialog.Dialog_Error_Details;
import org.rmj.g3appdriver.GCircle.Apps.ErrorList.ViewModel.VMErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.List;

public class Activity_ErrorLogs extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rcv_messages;
    private TextInputEditText tie_search;
    private ErrorListAdapter poAdapter;
    private VMErrorLogs mViewModel;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_errors);

        toolbar = findViewById(R.id.toolbar);
        rcv_messages = findViewById(R.id.rcv_messages);
        tie_search = findViewById(R.id.tie_search);

        mViewModel = new ViewModelProvider(this).get(VMErrorLogs.class);
        poMessage = new MessageBox(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Error Transactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Circle");
        poMessage.setPositiveButton("Close", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
            }
        });

        mViewModel.GetErrorList().observe(Activity_ErrorLogs.this, new Observer<List<EErrorLogs>>() {
            @Override
            public void onChanged(List<EErrorLogs> eErrorLogs) {

                if (eErrorLogs.size() > 0){

                    poAdapter = new ErrorListAdapter(eErrorLogs, new ErrorListAdapter.OnItemViewListener() {
                        @Override
                        public void OnItemClick(EErrorLogs foSMS) {
                            Dialog_Error_Details poDialog = new Dialog_Error_Details(Activity_ErrorLogs.this);
                            poDialog.initDialog(foSMS, true);
                            poDialog.show();

                            mViewModel.IsRead(foSMS.getnErrorLogID());
                        }
                    });
                    rcv_messages.setAdapter(poAdapter);
                    rcv_messages.setLayoutManager(new LinearLayoutManager(Activity_ErrorLogs.this, LinearLayoutManager.VERTICAL, false));

                    poAdapter.notifyDataSetChanged();

                    tie_search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try{
                                if(s != null) {
                                    if (!s.toString().trim().isEmpty()) {
                                        String query = s.toString();
                                        poAdapter.GetFilter().filter(query);
                                        poAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        });
    }

}