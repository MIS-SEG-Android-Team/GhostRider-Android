package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.R;


public class Dialog_File_Upload {
    private final AlertDialog poDialog;
    private final ConstraintLayout layout_file;
    private final TextInputEditText tie_fileupload;
    public final MaterialTextView mtv_file;
    public final ImageButton btn_upload;
    public final LinearProgressIndicator load_prog;
    public final MaterialButton btn_select;
    public final MaterialButton btn_cancel;

    public interface OnAction{
        void OnSelectFile(Intent intent);
        void OnUpload(String filname);
    }

    public Dialog_File_Upload(Context context, OnAction callback){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_upload, null);
        poBuilder.setCancelable(false)
                .setView(view);
        poDialog = poBuilder.create();
        poDialog.setCancelable(false);

        layout_file = view.findViewById(R.id.layout_file);
        tie_fileupload = view.findViewById(R.id.tie_fileupload);
        mtv_file = view.findViewById(R.id.mtv_file);
        btn_upload = view.findViewById(R.id.btn_upload);
        load_prog = view.findViewById(R.id.load_prog);
        btn_select = view.findViewById(R.id.btn_select);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tie_fileupload.getText() == null || tie_fileupload.getText().toString().isEmpty()){
                    Toast.makeText(context, "Enter description first", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");

                callback.OnSelectFile(intent);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                load_prog.setIndeterminate(true);
                load_prog.setVisibility(View.VISIBLE);
                load_prog.setIndicatorColor(ContextCompat.getColor(context, R.color.guanzon_digital_orange));

                btn_select.setEnabled(false);
                btn_cancel.setEnabled(false);
                btn_upload.setEnabled(false);

                callback.OnUpload(tie_fileupload.getText().toString());
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poDialog.isShowing()){
                    poDialog.dismiss();
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void SetResult(Boolean isSuccess, String result){

        layout_file.setVisibility(View.VISIBLE);
        mtv_file.setText(result);

        if (!isSuccess){
            btn_upload.setEnabled(false);
        }else {
            btn_upload.setEnabled(true);
        }
    }

    public void Show(){
        poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialog.show();
    }
}