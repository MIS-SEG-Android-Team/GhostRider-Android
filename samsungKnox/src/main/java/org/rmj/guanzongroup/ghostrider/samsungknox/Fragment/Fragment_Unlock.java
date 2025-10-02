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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.samsungknox.Etc.ViewModelCallBack;
import org.rmj.guanzongroup.ghostrider.samsungknox.R;
import org.rmj.guanzongroup.ghostrider.samsungknox.ViewModel.VMUnlock;

import java.util.Objects;

public class Fragment_Unlock extends Fragment implements ViewModelCallBack {

    private VMUnlock mViewModel;
    private TextInputEditText txtImei;
    private MaterialButton btnGetPIN;
    private MessageBox loMessage;
    private LoadDialog dialog;

    public static Fragment_Unlock newInstance() {
        return new Fragment_Unlock();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMUnlock.class);
        View v = inflater.inflate(R.layout.fragment_unlock, container, false);
        dialog = new LoadDialog(getActivity());
        loMessage = new MessageBox(getActivity());
        txtImei = v.findViewById(R.id.txt_knoxImei);
        btnGetPIN = v.findViewById(R.id.btn_knoxUnlock);
        btnGetPIN.setOnClickListener(view -> {
            String lsDevcIDx = Objects.requireNonNull(txtImei.getText()).toString();
            mViewModel.UnlockDevice(lsDevcIDx, Fragment_Unlock.this);
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
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_message_24);
        loMessage.setMessage(args);
        loMessage.setTitle("Unlock Device");
        loMessage.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }

    @Override
    public void OnRequestFailed(String message) {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setMessage(message);
        loMessage.setTitle("Unlock Device");
        loMessage.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }
}