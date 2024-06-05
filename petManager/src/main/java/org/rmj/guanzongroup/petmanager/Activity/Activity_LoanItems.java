package org.rmj.guanzongroup.petmanager.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.guanzongroup.petmanager.Adapter.Adapter_LoanItems;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMLoanItems;

import java.util.List;

public class Activity_LoanItems extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView rec_loanitems;
    private VMLoanItems mViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loanitems);

        toolbar = findViewById(R.id.toolbar);
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
                            rec_loanitems.setAdapter(new Adapter_LoanItems(getApplication(), eEmpLoans, true, true));
                        }
                    });
                }
            }
        }else {
            //TODO: IDENTIFY USER LEVEL, FOR OBJECT ACCESS
            mViewModel.GetUserHistory().observe(this, new Observer<List<EEmpLoan>>() {
                @Override
                public void onChanged(List<EEmpLoan> eEmpLoans) {
                    if (Integer.parseInt(mViewModel.GetUserLevel()) > DeptCode.LEVEL_RANK_FILE){
                        rec_loanitems.setAdapter(new Adapter_LoanItems(getApplication(), eEmpLoans, true, false));
                    }else {
                        rec_loanitems.setAdapter(new Adapter_LoanItems(getApplication(), eEmpLoans, false, false));
                    }
                }
            });
        }
    }
}
