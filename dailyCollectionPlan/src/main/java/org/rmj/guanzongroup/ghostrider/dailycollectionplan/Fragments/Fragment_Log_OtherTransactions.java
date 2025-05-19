/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_TransactionDetail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogDisplayImage;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMLogOtherTransactions;

public class Fragment_Log_OtherTransactions extends Fragment {
    private static final String TAG = Fragment_Log_OtherTransactions.class.getSimpleName();
    private final String IMAGE_NAME = Activity_TransactionDetail.imgNme;
    private VMLogOtherTransactions mViewModel;
    private DialogDisplayImage poDialogx;
    private MaterialTextView txtAcctNo, txtClientName, txtClientAddress, txtRemarks, txtTransNo, txtTransTp,
            txtCoord, lblTransNo;
    private ShapeableImageView ivTransImage;
    private MaterialDivider divDivider, floatRemarks;

    public static Fragment_Log_OtherTransactions newInstance(String param1, String param2) {
        return new Fragment_Log_OtherTransactions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_transactions_log, container, false);
        mViewModel = new ViewModelProvider(this).get(VMLogOtherTransactions.class);
        initWidgets(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtAcctNo.setText(Activity_TransactionDetail.acctNox);
        txtClientName.setText(Activity_TransactionDetail.fullNme);
        txtClientAddress.setText(Activity_TransactionDetail.clientAddress);
        txtTransNo.setText(Activity_TransactionDetail.transNox);
        txtTransTp.setText((Activity_TransactionDetail.psTransTp.equalsIgnoreCase("OTH"))
                ? "Other Transaction" : Activity_TransactionDetail.psTransTp);
        txtRemarks.setText(Activity_TransactionDetail.remarks);
        //Image Location
        if(!IMAGE_NAME.equalsIgnoreCase("")){
            mViewModel.getImageLocation(Activity_TransactionDetail.acctNox, Activity_TransactionDetail.imgNme)
                    .observe(getViewLifecycleOwner(), eImageInfo -> {
                        try {
                            setPic(eImageInfo.getFileLoct());
                            ivTransImage.setOnClickListener(v -> {
                                poDialogx = new DialogDisplayImage(getActivity(),
                                        Activity_TransactionDetail.acctNox, eImageInfo.getFileLoct());
                                poDialogx.initDialog(dialog -> {
                                    dialog.dismiss();
                                });
                                poDialogx.show();
                            });
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    });
            txtCoord.setText(Activity_TransactionDetail.psLocate);
            txtCoord.setVisibility(View.VISIBLE);
            lblTransNo.setVisibility(View.INVISIBLE);
            txtTransNo.setVisibility(View.GONE);
        } else {
            ivTransImage.setVisibility(View.GONE);
            divDivider.setVisibility(View.GONE);
            floatRemarks.setVisibility(View.VISIBLE);
        }
    }

    private void initWidgets(View v) {
        ivTransImage = v.findViewById(R.id.iv_transaction_img);
        txtAcctNo = v.findViewById(R.id.txt_acctNo);
        txtClientName = v.findViewById(R.id.txt_clientName);
        txtClientAddress = v.findViewById(R.id.txt_client_address);
        lblTransNo = v.findViewById(R.id.lbl_detail_transno);
        txtTransNo = v.findViewById(R.id.txt_transno);
        txtCoord = v.findViewById(R.id.lbl_coordinates);
        txtTransTp = v.findViewById(R.id.lbl_list_header);
        txtRemarks = v.findViewById(R.id.txt_remarks);
        divDivider = v.findViewById(R.id.divider2);
        floatRemarks = v.findViewById(R.id.view_float_remarks);
    }

    private void setPic(String photoPath) {
        // Get the dimensions of the View
        int targetW = ivTransImage.getWidth();
        int targetH = ivTransImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        Bitmap bOutput;
        float degrees = 90;//rotation degree
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ivTransImage.setImageBitmap(bOutput);
    }

}