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
import androidx.constraintlayout.utils.widget.ImageFilterView;
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
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMLogClientDetail;

public class Fragment_Log_Client_Detail extends Fragment {

    private VMLogClientDetail mViewModel;
    private DialogDisplayImage poDialogx;
    private ImageFilterView ivTransImage;
    private MaterialTextView txtAcctNo, txtClientName, txtClientAddress, txtCoord, txtTransTp, txtFullname,
    txtAddress,
    txtGender,
    txtCivilStat,
    txtBDate,
    txtBPlace,
    txtTelNox,
    txtMobileNox,
    txtEmail,
    txtRemarks;
    private String[] civilStatus = DCP_Constants.CIVIL_STATUS;
    private String[] gender = {"Male", "Female", "LGBT"};

    public Fragment_Log_Client_Detail() { }

    public static Fragment_Log_Client_Detail newInstance() {
        return new Fragment_Log_Client_Detail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_client_dtl_log, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(VMLogClientDetail.class);

        initWidgets(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtAcctNo.setText(Activity_TransactionDetail.acctNox);
        txtClientName.setText(Activity_TransactionDetail.fullNme);
        txtClientAddress.setText(Activity_TransactionDetail.clientAddress);
        txtCoord.setText(Activity_TransactionDetail.psLocate);
        txtTransTp.setText(Activity_TransactionDetail.psTransTp);
        txtRemarks.setText(Activity_TransactionDetail.remarks);

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

        mViewModel.getClientUpdateInfo(Activity_TransactionDetail.acctNox).observe(getViewLifecycleOwner(), clientDetl -> {
            txtFullname.setText(clientDetl.getLastName() + ", "
                    + clientDetl .getFrstName() + ", "
                    + clientDetl.getMiddName() + " "
                    + clientDetl.getSuffixNm());

            // Set Address
            mViewModel.getBrgyTownProvinceInfo(clientDetl.getBarangay()).observe(getViewLifecycleOwner(), eTownInfo -> {
                txtAddress.setText(clientDetl.getHouseNox() + " " + clientDetl.getAddressx() + ", " + eTownInfo.sBrgyName + ", " + eTownInfo.sTownName +", " + eTownInfo.sProvName);
                txtBPlace.setText(eTownInfo.sTownName +", " + eTownInfo.sProvName);
            });
            mViewModel.getTownProvinceInfo(clientDetl.getTownIDxx()).observe(getViewLifecycleOwner(), eTownInfo -> {
                txtBPlace.setText(eTownInfo.sTownName +", " + eTownInfo.sProvName);
            });

            txtGender.setText(gender[Integer.parseInt(clientDetl.getGenderxx())]);
            txtCivilStat.setText(civilStatus[Integer.parseInt(clientDetl.getCivlStat())]);
            txtBDate.setText(clientDetl.getBirthDte());
            txtBPlace.setText(clientDetl.getBirthPlc());
            txtTelNox.setText(clientDetl.getLandline());
            txtMobileNox.setText(clientDetl.getMobileNo());
            txtEmail.setText(clientDetl.getEmailAdd());
//            txtRemarks.setText(clientDetl.getRemakr);

        });
    }

    private void initWidgets(View v) {
        txtAcctNo = v.findViewById(R.id.txt_acctNo);
        txtClientName = v.findViewById(R.id.txt_clientName);
        txtClientAddress = v.findViewById(R.id.txt_client_address);
        txtCoord = v.findViewById(R.id.lbl_coordinates);
        txtTransTp = v.findViewById(R.id.lbl_list_header);
        txtFullname = v.findViewById(R.id.txt_fullname);
        txtAddress = v.findViewById(R.id.txt_address);
        txtGender = v.findViewById(R.id.txt_gender);
        txtCivilStat = v.findViewById(R.id.txt_civil_stat);
        txtBDate = v.findViewById(R.id.txt_bdate);
        txtBPlace = v.findViewById(R.id.txt_bplace);
        txtTelNox = v.findViewById(R.id.txt_telNox);
        txtMobileNox = v.findViewById(R.id.txt_mobileNox);
        txtEmail = v.findViewById(R.id.txt_email);
        txtRemarks = v.findViewById(R.id.txt_remarks);
        ivTransImage = v.findViewById(R.id.iv_transaction_img);
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