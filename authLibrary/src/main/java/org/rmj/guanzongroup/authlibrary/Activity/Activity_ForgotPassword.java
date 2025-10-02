package org.rmj.guanzongroup.authlibrary.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.authlibrary.R;
import org.rmj.guanzongroup.authlibrary.UserInterface.ForgotPassword.VMForgotPassword;

import java.util.Objects;

public class Activity_ForgotPassword extends AppCompatActivity implements VMForgotPassword.RequestPasswordCallback {
    private VMForgotPassword mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMsgBox;
    private AppConfigPreference poConfigx;
    private MaterialTextView lblVersion;
    private TextInputEditText tie_email;
    private MaterialButton btn_log;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        poDialog = new LoadDialog(this);
        poMsgBox = new MessageBox(this);
        poConfigx = AppConfigPreference.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        lblVersion = findViewById(R.id.lbl_versionInfo);
        tie_email = findViewById(R.id.email);
        btn_log = findViewById(R.id.btn_log);

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

        mViewModel = new ViewModelProvider(this).get(VMForgotPassword.class);
        lblVersion.setText(poConfigx.getVersionInfo());

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(tie_email.getText()).toString().trim();

                mViewModel.RequestPassword(email, new VMForgotPassword.RequestPasswordCallback() {
                    @Override
                    public void OnSendRequest(String title, String message) {
                        poDialog.initDialog(title, message, false);
                        poDialog.show();

                        poMsgBox.initDialog();
                    }

                    @Override
                    public void OnSuccessRequest() {
                        poDialog.dismiss();
                        poMsgBox.setIcon(R.drawable.baseline_message_24);
                        poMsgBox.setTitle("Result");
                        poMsgBox.setMessage("Successfully sent request.");
                        poMsgBox.setPositiveButton("Okay", new MessageBox.DialogButton() {
                            @Override
                            public void OnButtonClick(View view, AlertDialog dialog) {
                                dialog.dismiss();
                            }
                        });

                        poMsgBox.show();
                    }

                    @Override
                    public void OnFailedRequest(String message) {
                        poDialog.dismiss();
                        poMsgBox.setIcon(R.drawable.baseline_error_24);
                        poMsgBox.setTitle("Result");
                        poMsgBox.setMessage("Failed to send request: " + message);
                        poMsgBox.setPositiveButton("Okay", new MessageBox.DialogButton() {
                            @Override
                            public void OnButtonClick(View view, AlertDialog dialog) {
                                dialog.dismiss();
                            }
                        });

                        poMsgBox.show();
                    }
                });
            }
        });
    }

    @Override
    public void OnSendRequest(String title, String message) {
        poDialog.initDialog(title, message, false);
        poDialog.show();
    }

    @Override
    public void OnSuccessRequest() {
        poDialog.dismiss();
        poMsgBox.initDialog();
        poMsgBox.setIcon(R.drawable.baseline_message_24);
        poMsgBox.setTitle("Forgot Password");
        poMsgBox.setMessage("You'll be receiving an email from MIS, Please check your email account");
        poMsgBox.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        poMsgBox.show();
    }

    @Override
    public void OnFailedRequest(String message) {
        poDialog.dismiss();
        poMsgBox.initDialog();
        poMsgBox.setIcon(R.drawable.baseline_error_24);
        poMsgBox.setTitle("Forgot Password");
        poMsgBox.setMessage(message);
        poMsgBox.setPositiveButton("Okay", (view, msgDialog) -> msgDialog.dismiss());
        poMsgBox.show();
    }
}