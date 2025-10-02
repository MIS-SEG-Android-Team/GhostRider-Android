package org.rmj.guanzongroup.petmanager.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.guanzongroup.petmanager.Adapter.Adapter_LoanItemParents;
import org.rmj.guanzongroup.petmanager.Adapter.Adapter_LoanItems;
import org.rmj.guanzongroup.petmanager.Dialog.Dialog_LoanPreview;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMLoanItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_LoanItems extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputEditText txt_search;
    private RecyclerView rec_loanitems;
    private VMLoanItems mViewModel;
    private Adapter_LoanItemParents rcvAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loanitems);

        toolbar = findViewById(R.id.toolbar);
        txt_search = findViewById(R.id.txt_search);
        rec_loanitems = findViewById(R.id.rec_loanitems);
        mViewModel = new ViewModelProvider(this).get(VMLoanItems.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //TODO: GET INTENT EXTRA, IDENTIFY IF NAVIGATION IS 'FOR APPROVAL' OR 'HISTORY'
        if (getIntent().hasExtra("args")){
            String lsArgs = getIntent().getStringExtra("args");
            if (lsArgs.equalsIgnoreCase("loanapproval")){

                //TODO: IDENTIFY USER LEVEL, FOR OBJECT ACCESS
                if (Integer.parseInt(mViewModel.GetUserLevel()) > DeptCode.LEVEL_RANK_FILE){
                    mViewModel.GetForApproval().observe(this, new Observer<List<EEmpLoan>>() {
                        @Override
                        public void onChanged(List<EEmpLoan> eEmpLoans) {
                            if (eEmpLoans.size() > 0){

                                HashMap<String, List<EEmpLoan>> mapVal = new HashMap<>();
                                for (int i = 0; i < eEmpLoans.size(); i++){
                                    EEmpLoan loVal = eEmpLoans.get(i);

                                    //TODO: IF MAP CONTAINS KEY FOR EMPLOYEE ID
                                    if (mapVal.containsKey(loVal.getsEmployID())){
                                        //TODO: ADD TO EXISTING KEY's LIST
                                        mapVal.get(loVal.getsEmployID()).add(loVal);
                                    }else {
                                        //TODO: CREATE NEW VARIABLE ARRAY AS NEW MAP THEN ADD
                                        List<EEmpLoan> newList = new ArrayList<>();
                                        newList.add(loVal);

                                        mapVal.put(loVal.getsEmployID(), newList);
                                    }
                                }

                                rcvAdapter = new Adapter_LoanItemParents(getApplication(), mapVal, true, true, new Adapter_LoanItemParents.onDisplayPreview() {
                                    @Override
                                    public void onDisplay(Dialog_LoanPreview.DialogVal loDetails, Boolean showEmpNm) {
                                        Dialog_LoanPreview prevDialog = new Dialog_LoanPreview(Activity_LoanItems.this, loDetails, showEmpNm);
                                        prevDialog.show();
                                    }
                                });

                                rcvAdapter.notifyDataSetChanged();
                                rec_loanitems.setAdapter(rcvAdapter);
                                rec_loanitems.setLayoutManager(new LinearLayoutManager(Activity_LoanItems.this, LinearLayoutManager.VERTICAL, false));
                            }
                        }
                    });
                }
            }
        }else {
            //TODO: IDENTIFY USER LEVEL, FOR OBJECT ACCESS
            mViewModel.GetUserHistory().observe(this, new Observer<List<EEmpLoan>>() {
                @Override
                public void onChanged(List<EEmpLoan> eEmpLoans) {
                    if (eEmpLoans.size() > 0){

                        HashMap<String, List<EEmpLoan>> mapVal = new HashMap<>();
                        for (int i = 0; i < eEmpLoans.size(); i++){
                            EEmpLoan loVal = eEmpLoans.get(i);

                            //TODO: IF MAP CONTAINS KEY FOR EMPLOYEE ID
                            if (mapVal.containsKey(loVal.getsEmployID())){
                                //TODO: ADD TO EXISTING KEY's LIST
                                mapVal.get(loVal.getsEmployID()).add(loVal);
                            }else {
                                //TODO: CREATE NEW VARIABLE ARRAY AS NEW MAP THEN ADD
                                List<EEmpLoan> newList = new ArrayList<>();
                                newList.add(loVal);

                                mapVal.put(loVal.getsEmployID(), newList);
                            }
                        }

                        if (Integer.parseInt(mViewModel.GetUserLevel()) > DeptCode.LEVEL_RANK_FILE){
                            rcvAdapter = new Adapter_LoanItemParents(getApplication(), mapVal, true, false, new Adapter_LoanItemParents.onDisplayPreview() {
                                @Override
                                public void onDisplay(Dialog_LoanPreview.DialogVal loDetails, Boolean showEmpNm) {
                                    Dialog_LoanPreview prevDialog = new Dialog_LoanPreview(Activity_LoanItems.this, loDetails, showEmpNm);
                                    prevDialog.show();
                                }
                            });

                            rcvAdapter.notifyDataSetChanged();
                            rec_loanitems.setAdapter(rcvAdapter);
                            rec_loanitems.setLayoutManager(new LinearLayoutManager(Activity_LoanItems.this, LinearLayoutManager.VERTICAL, false));
                        }else {
                            rcvAdapter = new Adapter_LoanItemParents(getApplication(), mapVal, false, false, new Adapter_LoanItemParents.onDisplayPreview() {
                                @Override
                                public void onDisplay(Dialog_LoanPreview.DialogVal loDetails, Boolean showEmpNm) {
                                    Dialog_LoanPreview prevDialog = new Dialog_LoanPreview(Activity_LoanItems.this, loDetails, showEmpNm);
                                    prevDialog.show();
                                }
                            });

                            rcvAdapter.notifyDataSetChanged();
                            rec_loanitems.setAdapter(rcvAdapter);
                            rec_loanitems.setLayoutManager(new LinearLayoutManager(Activity_LoanItems.this, LinearLayoutManager.VERTICAL, false));
                        }
                    }
                }
            });
        }

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try{

                    String query = s.toString();
                    rcvAdapter.getFilter().filter(query);
                    rcvAdapter.notifyDataSetChanged();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    String query = s.toString();
                    rcvAdapter.getFilter().filter(query);
                    rcvAdapter.notifyDataSetChanged();
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
