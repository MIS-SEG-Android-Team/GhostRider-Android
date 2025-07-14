package org.rmj.guanzongroup.ghostrider.notifications.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationRecipient;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_Browser;
import org.rmj.guanzongroup.ghostrider.notifications.R;
import org.rmj.guanzongroup.ghostrider.notifications.ViewModel.VMViewNotification;

public class Activity_ViewNotification extends AppCompatActivity {
    private static final String TAG = Activity_ViewNotification.class.getSimpleName();

    private VMViewNotification mViewModel;

    private MaterialToolbar toolbar;
    private MaterialTextView lblTitle,
                            lblSendr,
                            lblRcpnt,
                            lblDatex,
                            lblBodyx,
                            lblReceivedDate,
                            lblOpenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_ViewNotification.this).get(VMViewNotification.class);
        setContentView(R.layout.activity_view_notification);
        initWidgets();

        if(!getIntent().hasExtra("sMesgIDxx")){
            return;
        }

        String lsMesgID = getIntent().getStringExtra("sMesgIDxx");
        mViewModel.SendResponse(lsMesgID);
        mViewModel.GetNotificationMaster(lsMesgID).observe(Activity_ViewNotification.this, new Observer<ENotificationMaster>() {
            @Override
            public void onChanged(ENotificationMaster master) {
                try {
                    if (master == null) {
                        return;
                    }

                    lblTitle.setText(master.getMsgTitle());
                    lblSendr.setText(master.getCreatrNm() == null ? "null" : "SYSTEM");
                    lblBodyx.setText(master.getMessagex());
                    lblDatex.setText("Date Created: " + FormatUIText.getParseDateTime(master.getCreatedx()));

                    lblBodyx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int lnStart = master.getMessagex().indexOf("[");
                            int lnEnd = master.getMessagex().indexOf("]");

                            String subsTringVal = master.getMessagex().substring(lnStart + 1, lnEnd);
                            Uri uri = Uri.parse(subsTringVal);

                            Log.d(TAG, uri.toString());

                            Intent loIntent = new Intent(Activity_ViewNotification.this, Activity_Browser.class);
                            loIntent.putExtra("url_link",uri.toString());

                            startActivity(loIntent);
                            overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mViewModel.GetNotificationDetail(lsMesgID).observe(Activity_ViewNotification.this, new Observer<ENotificationRecipient>() {
            @Override
            public void onChanged(ENotificationRecipient detail) {
                try{
                    if(detail == null){
                        return;
                    }

                    lblRcpnt.setText(detail.getRecpntNm());
                    lblReceivedDate.setText("Date Received: " + FormatUIText.getParseDateTime(detail.getReceived()));
                    lblOpenDate.setText("Date Opened: " + FormatUIText.getParseDateTime(detail.getReadxxxx()));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initWidgets(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblTitle = findViewById(R.id.lbl_messageTitle);
        lblSendr = findViewById(R.id.lbl_messageSender);
        lblRcpnt = findViewById(R.id.lbl_messageRecipient);
        lblDatex = findViewById(R.id.lbl_messageDateTime);
        lblBodyx = findViewById(R.id.lbl_messageBody);
        lblReceivedDate = findViewById(R.id.lbl_ReceivedDateTime);
        lblOpenDate = findViewById(R.id.lbl_OpenDateTime);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}