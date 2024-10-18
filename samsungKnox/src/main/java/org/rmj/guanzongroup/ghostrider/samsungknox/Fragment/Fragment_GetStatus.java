/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.samsungKnox
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.samsungknox.Fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.samsungknox.Etc.ViewModelCallBack;
import org.rmj.guanzongroup.ghostrider.samsungknox.R;
import org.rmj.guanzongroup.ghostrider.samsungknox.ViewModel.VMGetStatus;

import java.util.Objects;

public class Fragment_GetStatus extends Fragment implements ViewModelCallBack {
    public static final String TAG = Fragment_GetStatus.class.getSimpleName();
    private VMGetStatus mViewModel;

    private MessageBox loMessage;
    private LoadDialog dialog;

    private MaterialTextView lblDeviceID, lblStatus, lblDetails, lblLastUpdate;
    private TextInputEditText txtDeviceID;
    private MaterialButton btnCheck;
    private ConstraintLayout consStatus;

    public static Fragment_GetStatus newInstance() {
        return new Fragment_GetStatus();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMGetStatus.class);
        View v = inflater.inflate(R.layout.fragment_get_status, container, false);
        dialog = new LoadDialog(getActivity());
        loMessage = new MessageBox(getActivity());
        lblDeviceID = v.findViewById(R.id.lbl_knoxDeviceIMEI);
        lblStatus = v.findViewById(R.id.lbl_knoxDeviceStatus);
        lblDetails = v.findViewById(R.id.lbl_knoxDetailStatus);
        lblLastUpdate = v.findViewById(R.id.lbl_knoxStatusDate);
        consStatus = v.findViewById(R.id.cn_ShowStatus);
        txtDeviceID = v.findViewById(R.id.txt_knoxImei);

        btnCheck = v.findViewById(R.id.btn_knoxCheckStatus);
        btnCheck.setOnClickListener(view -> {
            String lsDeviceID = Objects.requireNonNull(txtDeviceID.getText()).toString();
            mViewModel.GetDeviceStatus(lsDeviceID, Fragment_GetStatus.this);
        });

        return v;
    }

    @Override
    public void OnLoadRequest(String Title, String Message, boolean Cancellable) {
        dialog.initDialog(Title, Message, Cancellable);
        dialog.show();
    }

    @Override
    public void OnRequestSuccess(String args, String args1, String args2, String args3) {
        dialog.dismiss();
        lblDeviceID.setText(args);
        lblStatus.setText(args1);
        lblDetails.setText(args2);
        lblLastUpdate.setText(args3);
        txtDeviceID.setText("");
    }

    @Override
    public void OnRequestFailed(String message) {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setMessage(message);
        loMessage.setTitle("Device Status");
        loMessage.setPositiveButton("Dismiss", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }
}