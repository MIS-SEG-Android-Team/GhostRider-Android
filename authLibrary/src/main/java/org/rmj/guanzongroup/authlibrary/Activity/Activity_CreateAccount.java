package org.rmj.guanzongroup.authlibrary.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Account.pojo.AccountInfo;
import org.rmj.guanzongroup.authlibrary.R;
import org.rmj.guanzongroup.authlibrary.UserInterface.CreateAccount.CreateAccountCallBack;
import org.rmj.guanzongroup.authlibrary.UserInterface.CreateAccount.VMCreateAccount;

import java.util.Objects;

public class Activity_CreateAccount extends AppCompatActivity implements CreateAccountCallBack {
    private VMCreateAccount mViewModel;
    private LoadDialog dialog;
    private MessageBox loMessage;
    private AppConfigPreference poConfigx;
    private TextInputEditText tieLastname, tieFirstname, tieMiddname,  tieEmail, tiePassword, tiecPassword, tieMobileno, tie_suffix;
    private MaterialTextView lbl_versionInfo;
    private MaterialButton btn_createAccount;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mViewModel = new ViewModelProvider(this).get(VMCreateAccount.class);
        loMessage = new MessageBox(this);
        dialog = new LoadDialog(this);
        poConfigx = AppConfigPreference.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        tieLastname = findViewById(R.id.lastname);
        tieFirstname = findViewById(R.id.tie_ca_firstName);
        tieMiddname = findViewById(R.id.tie_ca_middleName);
        tie_suffix = findViewById(R.id.tie_suffix);
        tieEmail = findViewById(R.id.tie_ca_email);
        tiePassword = findViewById(R.id.tie_ca_password);
        tiecPassword = findViewById(R.id.tie_ca_confirmPass);
        tieMobileno = findViewById(R.id.tie_ca_mobileNumber);
        lbl_versionInfo = findViewById(R.id.lbl_versionInfo);
        btn_createAccount = findViewById(R.id.btn_createAccount);

        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setTitle(""); //set default title for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true); //enable the back button set on toolbar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lbl_versionInfo.setText(poConfigx.getVersionInfo());
        btn_createAccount.setOnClickListener(view -> {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setLastName(Objects.requireNonNull(tieLastname.getText()).toString());
            accountInfo.setFrstName(Objects.requireNonNull(tieFirstname.getText()).toString());
            accountInfo.setMiddName(Objects.requireNonNull(tieMiddname.getText()).toString());
            accountInfo.setSuffix(Objects.requireNonNull(tie_suffix.getText()).toString());
            accountInfo.setEmail(Objects.requireNonNull(tieEmail.getText()).toString());
            accountInfo.setPassword(Objects.requireNonNull(tiePassword.getText()).toString());
            accountInfo.setcPasswrd(Objects.requireNonNull(tiecPassword.getText()).toString());
            accountInfo.setMobileNo(Objects.requireNonNull(tieMobileno.getText()).toString());
            mViewModel.SubmitInfo(accountInfo, Activity_CreateAccount.this);
        });

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
        loMessage.setPositiveButton("Dismiss", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }

    @Override
    public void OnFailedRegistration(String message) {
        dialog.dismiss();
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setTitle("Create Account");
        loMessage.setMessage(message);
        loMessage.setPositiveButton("Dismiss", (view, msgDialog) -> msgDialog.dismiss());
        loMessage.show();
    }
}