/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.authLibrary
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.authlibrary.UserInterface.CreateAccount;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import  com.google.android.material.checkbox.MaterialCheckBox;


import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Account.pojo.AccountInfo;
import org.rmj.guanzongroup.authlibrary.R;

import java.util.Objects;

public class Fragment_CreateAccount extends Fragment implements CreateAccountCallBack {

    private VMCreateAccount mViewModel;
    private LoadDialog dialog;
    private MessageBox loMessage;
    private TextInputEditText tieLastname, tieFirstname, tieMiddname, tieSuffix, tieEmail, tiePassword, tiecPassword, tieMobileno;
    private MaterialButton btnSubmit;
    private MaterialTextView lblVersion;

    private AppConfigPreference poConfigx;

    public static Fragment_CreateAccount newInstance() {
        return new Fragment_CreateAccount();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMCreateAccount.class);
        View v = inflater.inflate(R.layout.fragment_create_account, container, false);
        loMessage = new MessageBox(getActivity());
        dialog = new LoadDialog(getActivity());
        poConfigx = AppConfigPreference.getInstance(getActivity());

        tieLastname = v.findViewById(R.id.tie_ca_lastName);
        tieFirstname = v.findViewById(R.id.tie_ca_firstName);
        tieMiddname = v.findViewById(R.id.tie_ca_middleName);
        tieSuffix = v.findViewById(R.id.tie_ca_suffix);
        tieEmail = v.findViewById(R.id.tie_ca_email);
        tiePassword = v.findViewById(R.id.tie_ca_password);
        tiecPassword = v.findViewById(R.id.tie_ca_confirmPass);
        tieMobileno = v.findViewById(R.id.tie_ca_mobileNumber);
        lblVersion = v.findViewById(R.id.lbl_versionInfo);
        btnSubmit = v.findViewById(R.id.btn_createAccount);

        lblVersion.setText(poConfigx.getVersionInfo());
        btnSubmit.setOnClickListener(view -> {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setLastName(Objects.requireNonNull(tieLastname.getText()).toString());
            accountInfo.setFrstName(Objects.requireNonNull(tieFirstname.getText()).toString());
            accountInfo.setMiddName(Objects.requireNonNull(tieMiddname.getText()).toString());
            accountInfo.setSuffix(Objects.requireNonNull(tieSuffix.getText()).toString());
            accountInfo.setEmail(Objects.requireNonNull(tieEmail.getText()).toString());
            accountInfo.setPassword(Objects.requireNonNull(tiePassword.getText()).toString());
            accountInfo.setcPasswrd(Objects.requireNonNull(tiecPassword.getText()).toString());
            accountInfo.setMobileNo(Objects.requireNonNull(tieMobileno.getText()).toString());
            mViewModel.SubmitInfo(accountInfo, Fragment_CreateAccount.this);
        });
        return v;
    }

    @Override
    public void OnAccountLoad(String Title, String Message) {
        dialog.initDialog(Title, Message, false);
        dialog.show();
    }

    @Override
    public void OnSuccessRegistration() {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_message_24);
        loMessage.setTitle("Create Account");
        loMessage.setMessage("A verification email has been sent to your email account. Please check your inbox or spam folder.");
        loMessage.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }

    @Override
    public void OnFailedRegistration(String message) {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setTitle("Create Account");
        loMessage.setMessage(message);
        loMessage.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }
}