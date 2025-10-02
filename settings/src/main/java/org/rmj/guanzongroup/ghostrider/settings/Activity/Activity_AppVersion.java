package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.GMSUtility;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMAppVersion;
import org.rmj.guanzongroup.ghostrider.settings.R;

public class Activity_AppVersion extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialTextView build_version;
    private MaterialButton btn_checkupdate;

    private VMAppVersion mViewModel;
    private LoadDialog poload;
    private MessageBox pomessage;
    private GMSUtility poGMS;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_version);

        mViewModel = new ViewModelProvider(Activity_AppVersion.this).get(VMAppVersion.class);

        poload = new LoadDialog(Activity_AppVersion.this);
        pomessage = new MessageBox(Activity_AppVersion.this);
        poGMS = new GMSUtility(Activity_AppVersion.this);

        toolbar = findViewById(R.id.toolbar);
        build_version = findViewById(R.id.build_version);

        btn_checkupdate = findViewById(R.id.btn_checkupdate);

        pomessage.initDialog();
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       String build_name = AppConfigPreference.getInstance(Activity_AppVersion.this).getVersionName(); //get build version
       build_version.setText(build_name);

        btn_checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewModel.GetUpdate(poGMS, new VMAppVersion.onDownload() {
                    @Override
                    public void onDownloading(int status) {
                        poload.initDialog("Guanzon Circle", "Downloading Updates " + status, false);
                        poload.show();
                    }

                    @Override
                    public void onSuccess() {
                        pomessage.setTitle("Guanzon Circle");
                        pomessage.setIcon(R.drawable.baseline_message_24);
                        pomessage.setMessage("Successfully updated. Please restart the app.");
                        pomessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                            @Override
                            public void OnButtonClick(View view, AlertDialog dialog) {
                                dialog.dismiss();
                                System.exit(0);
                            }
                        });
                        pomessage.show();
                    }

                    @Override
                    public void onFailed(String message) {

                        pomessage.setTitle("Guanzon Circle");
                        pomessage.setIcon(R.drawable.baseline_error_24);
                        pomessage.setMessage(message);
                        pomessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                            @Override
                            public void OnButtonClick(View view, AlertDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        pomessage.show();
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}