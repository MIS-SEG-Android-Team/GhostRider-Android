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

package org.rmj.guanzongroup.authlibrary.UserInterface.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import  com.google.android.material.checkbox.MaterialCheckBox;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Account.pojo.UserAuthInfo;
import org.rmj.guanzongroup.authlibrary.R;

import java.util.Objects;

public class Fragment_Login extends Fragment implements LoginCallback{
    private static final String TAG = Fragment_Login.class.getSimpleName();
    private VMLogin mViewModel;
    private LoadDialog dialog;
    private TextInputEditText tieEmail, tiePassword, tieMobileNo;
    private TextInputLayout tilMobileNo;

    private MaterialTextView tvForgotPassword, tvCreateAccount, tvTerms, lblVersion;
    private MaterialButton btnLogin;
    private NavController navController;
    private MaterialCheckBox cbAgree;

    private AppConfigPreference poConfigx;

    public static Fragment_Login newInstance() {
        return new Fragment_Login();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(VMLogin.class);
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        dialog = new LoadDialog(requireActivity());
        poConfigx = AppConfigPreference.getInstance(requireActivity());
        navController = Navigation.findNavController(requireActivity(), R.id.fragment_auth_container);
        tieEmail = v.findViewById(R.id.tie_loginEmail);
        tiePassword = v.findViewById(R.id.tie_loginPassword);
        tieMobileNo = v.findViewById(R.id.tie_loginMobileNo);
        tilMobileNo = v.findViewById(R.id.til_loginMobileNo);
        tvForgotPassword = v.findViewById(R.id.tvForgotPassword);
        tvTerms = v.findViewById(R.id.tvTerms);
        tvCreateAccount = v.findViewById(R.id.tvCreateAccount);
        lblVersion = v.findViewById(R.id.lbl_versionInfo);
        cbAgree = v.findViewById(R.id.cbAgree);
        btnLogin = v.findViewById(R.id.btn_login);

        cbAgree.setChecked(mViewModel.isAgreed());

        cbAgree.setOnCheckedChangeListener((buttonView, isChecked) -> mViewModel.setAgreedOnTerms(isChecked));

        btnLogin.setOnClickListener(view -> {
            String email = Objects.requireNonNull(tieEmail.getText()).toString();
            String password = Objects.requireNonNull(tiePassword.getText()).toString();
            String mobileNo = Objects.requireNonNull(tieMobileNo.getText()).toString();
            mViewModel.Login(new UserAuthInfo(email,password), Fragment_Login.this);
        });

        lblVersion.setText(poConfigx.getVersionInfo());

        tvCreateAccount.setOnClickListener(view -> navController.navigate(R.id.action_fragment_Login_to_fragment_CreateAccount));
        tvTerms.setOnClickListener(view -> navController.navigate(R.id.action_fragment_Login_to_fragment_TermsAndConditions));
        tvForgotPassword.setOnClickListener(view -> navController.navigate(R.id.action_fragment_Login_to_fragment_ForgotPassword));
        return v;
    }

    @Override
    public void OnAuthenticationLoad(String Title, String Message) {
        dialog.initDialog(Title, Message, false);
        dialog.show();
    }

    @Override
    public void OnSuccessLoginResult() {
        dialog.dismiss();

        Intent loIntent = new Intent();
        requireActivity().setResult(Activity.RESULT_OK, loIntent);
        requireActivity().finish();
    }

    @Override
    public void OnFailedLoginResult(String message) {
        dialog.dismiss();

        MessageBox loMessage = new MessageBox(requireActivity());
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setTitle("Guanzon Circle");
        loMessage.setMessage(message);
        loMessage.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
        loMessage.show();
    }
}