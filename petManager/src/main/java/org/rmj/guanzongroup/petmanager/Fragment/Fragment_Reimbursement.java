/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.petmanager.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.GToast;
import org.rmj.guanzongroup.petmanager.Adapter.ReimbursementAdapter;
import org.rmj.guanzongroup.petmanager.Model.ReimburseInfo;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMReimbursement;

import java.util.Objects;

public class Fragment_Reimbursement extends Fragment implements VMReimbursement.ExpActionListener {
    private static final String TAG = Fragment_Reimbursement.class.getSimpleName();
    private VMReimbursement mViewModel;

    private TextInputEditText txtPurpose, txtDetail, txtAmount, txtTotAmnt;
    private RecyclerView recyclerView;
    private MaterialButton btnAdd, btnSubmit;
    private int pnExpPost;
    private ReimbursementAdapter loAdapter;

    public static Fragment_Reimbursement newInstance() {
        return new Fragment_Reimbursement();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reimbursement, container, false);
        initWidgets(v);
        return v;
    }

    private void initWidgets(View view){
        txtPurpose = view.findViewById(R.id.txt_expPurpose);
        txtDetail = view.findViewById(R.id.txt_expDetail);
        txtAmount = view.findViewById(R.id.txt_amountExp);
        txtTotAmnt = view.findViewById(R.id.txt_totAmntExp);
        recyclerView  = view.findViewById(R.id.recyclerview_expenses);
        btnAdd = view.findViewById(R.id.btn_expAdd);
        btnSubmit = view.findViewById(R.id.btn_submit);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(VMReimbursement.class);

        mViewModel.getExpensesList().observe(getViewLifecycleOwner(), reimburseInfos -> {
            LinearLayoutManager lomanager = new LinearLayoutManager(getActivity());
            lomanager.setOrientation(RecyclerView.VERTICAL);
            loAdapter = new ReimbursementAdapter(reimburseInfos, new ReimbursementAdapter.OnExpButtonClickListener() {
                @Override
                public void OnEdit(int position, String Detail, int Amount) {
                    pnExpPost = position;
                    txtDetail.setText(Detail);
                    txtAmount.setText(String.valueOf(Amount));
                    btnAdd.setText("Save");
                }

                @Override
                public void OnDelete(int position, String Detail, int Amount) {
                    mViewModel.deleteExpDetail(position);
                    loAdapter.notifyDataSetChanged();
                }
            });

            recyclerView.setLayoutManager(lomanager);
            recyclerView.setAdapter(loAdapter);
        });
        mViewModel.getTotalAmount().observe(getViewLifecycleOwner(), integer -> txtTotAmnt.setText(String.valueOf(integer)));

        btnAdd.setOnClickListener(v -> {
            if(btnAdd.getText().toString().toLowerCase().equalsIgnoreCase("add")) {
                String lsDetail = Objects.requireNonNull(txtDetail.getText()).toString();
                String lsAmount = Objects.requireNonNull(txtAmount.getText()).toString();
                ReimburseInfo info = new ReimburseInfo(lsDetail, lsAmount);
                mViewModel.addExpDetail(info, Fragment_Reimbursement.this);
            } else {
                String lsDetail = Objects.requireNonNull(txtDetail.getText()).toString();
                String lsAmount = Objects.requireNonNull(txtAmount.getText()).toString();
                ReimburseInfo info = new ReimburseInfo(lsDetail, lsAmount);
                mViewModel.updateExpDetail(pnExpPost, info, Fragment_Reimbursement.this);
                btnAdd.setText("Add");
            }
            loAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onSuccess(String message) {
        Log.e(TAG, message);
        txtDetail.setText("");
        txtAmount.setText("");
    }

    @Override
    public void onFailed(String message) {
        Log.e(TAG, message);
        GToast.CreateMessage(getActivity(), message, GToast.ERROR).show();
    }
}