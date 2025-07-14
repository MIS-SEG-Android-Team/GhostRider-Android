package org.rmj.guanzongroup.authlibrary.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Account.pojo.UserAuthInfo;
import org.rmj.guanzongroup.authlibrary.R;
import org.rmj.guanzongroup.authlibrary.UserInterface.Login.LoginCallback;
import org.rmj.guanzongroup.authlibrary.UserInterface.Login.VMLogin;

import java.util.Objects;

public class Activity_Login extends AppCompatActivity implements LoginCallback {
    private TextInputEditText tie_username;
    private TextInputEditText tie_password;
    private MaterialTextView lblVersion;
    private MaterialTextView mtv_createaccount;
    private MaterialTextView mtv_forgotpassw;
    private MaterialButton btn_log;
    private VMLogin mViewModel;
    private LoadDialog podialog;
    private AppConfigPreference poConfigx;
    private MaterialTextView tvTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mViewModel = new ViewModelProvider(this).get(VMLogin.class);
        podialog = new LoadDialog(this);
        poConfigx = AppConfigPreference.getInstance(this);

        tie_username = findViewById(R.id.username);
        tie_password = findViewById(R.id.password);
        lblVersion = findViewById(R.id.lbl_versionInfo);
        mtv_createaccount = findViewById(R.id.mtv_createaccount);
        mtv_forgotpassw = findViewById(R.id.mtv_forgotpassw);
        btn_log = findViewById(R.id.btn_log);
        tvTerms = findViewById(R.id.tvTerms);

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentTerms = new Intent(Activity_Login.this, Activity_TermsAndConditions.class);
                startActivity(intentTerms);
            }
        });

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(tie_username.getText()).toString();
                String password = Objects.requireNonNull(tie_password.getText()).toString();

                mViewModel.Login(new UserAuthInfo(email,password), Activity_Login.this);
            }
        });

        lblVersion.setText(poConfigx.getVersionInfo());

        mtv_createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_CreateAccount.class);
                startActivity(intent);
            }
        });
        mtv_forgotpassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void OnAuthenticationLoad(String Title, String Message) {
        podialog.initDialog(Title, Message, false);
        podialog.show();
    }

    @Override
    public void OnSuccessLoginResult() {
        podialog.dismiss();

        Log.d("ACTIVITY_LOGIN", "SUCCESSFUL");
        Intent loIntent = new Intent();
        this.setResult(Activity.RESULT_OK, loIntent);
        this.finish();
    }

    @Override
    public void OnFailedLoginResult(String message) {
        podialog.dismiss();
        MessageBox loMessage = new MessageBox(this);
        loMessage.initDialog();
        loMessage.setIcon(R.drawable.baseline_error_24);
        loMessage.setTitle("Guanzon Circle");
        loMessage.setMessage(message);
        loMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
        loMessage.show();
    }
}