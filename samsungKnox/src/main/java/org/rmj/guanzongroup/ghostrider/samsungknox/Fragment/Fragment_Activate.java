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

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.samsungknox.Etc.ViewModelCallBack;
import org.rmj.guanzongroup.ghostrider.samsungknox.Model.ActivationModel;
import org.rmj.guanzongroup.ghostrider.samsungknox.R;
import org.rmj.guanzongroup.ghostrider.samsungknox.ViewModel.VMActivate;

import java.util.Objects;

public class Fragment_Activate extends Fragment implements ViewModelCallBack {

    private VMActivate mViewModel;
    private TextInputEditText txtImei, txtRemrks;
    private MaterialButton btnActivate;
    private LoadDialog dialog;
    private MessageBox loMessage;

    public static Fragment_Activate newInstance() {
        return new Fragment_Activate();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMActivate.class);
        View v = inflater.inflate(R.layout.fragment_activate, container, false);
        dialog = new LoadDialog(getActivity());
        loMessage = new MessageBox(getActivity());
        txtImei = v.findViewById(R.id.txt_knoxImei);
        txtRemrks = v.findViewById(R.id.txt_knoxRemarks);
        btnActivate = v.findViewById(R.id.btn_knoxActivate);

        btnActivate.setOnClickListener(view -> {
            String lsDevcID = Objects.requireNonNull(txtImei.getText()).toString().trim();
            String lsRemrks = Objects.requireNonNull(txtRemrks.getText()).toString().trim();
            ActivationModel model = new ActivationModel(lsDevcID, lsRemrks);
            mViewModel.ActivateDevice(model, Fragment_Activate.this);
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
        Toast.makeText(getActivity(), args, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnRequestFailed(String message) {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setMessage(message);
        loMessage.setTitle("Activate Device");
        loMessage.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }
}