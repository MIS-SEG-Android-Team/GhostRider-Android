package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

public class Activity_SSDD_Category extends AppCompatActivity {

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_category);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);
    }

    private void ImportData(){

        //download transaction details, if transaction number passed. Downloaded categories for new evaluation
        if (getIntent().hasExtra("sTransNox")){

        }else {

            mViewModel.DownloadSSDDCategories(new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                    Toast.makeText(Activity_SSDD_Category.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed(String fsMessage) {

                    poDialog.dismiss();
                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {}

                        @Override
                        public void onNegative() {}
                    });
                }
            });
        }
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, onMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("SSDD Evaluation");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.onPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.onNegative();

            });
        }

        poMessage.show();
    }
}