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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.rmj.g3appdriver.etc.GToast;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_TransactionDetail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.AddressInfoAdapter_Log;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.MobileInfoAdapter_Log;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogDisplayImage;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMLogCustomerNotAround;

public class Fragment_Log_CustomerNotAround extends Fragment {
    private static final String TAG = Fragment_Log_CustomerNotAround.class.getSimpleName();
    private static final int MOBILE_DIALER = 104;
    private VMLogCustomerNotAround mViewModel;
    private MobileInfoAdapter_Log mobileAdapter;
    private AddressInfoAdapter_Log addressAdapter;
    private DialogDisplayImage poDialogx;
    private MaterialTextView txtAcctNo, txtClientName, txtClientAddress, txtTransNo, txtTransTp, txtCoord;
    private RecyclerView rvMobileNox, rvAddress;
    private ShapeableImageView ivTransImage;
    private LinearLayout lnMobilenox, lnAddressx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_not_around_log, container, false);
        mViewModel = new ViewModelProvider(this).get(VMLogCustomerNotAround.class);
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
        txtTransTp.setText(Activity_TransactionDetail.psTransTp);
        mViewModel.setClientID(Activity_TransactionDetail.clientID);
        //Image Location
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

        mViewModel.getCNA_MobileDataList().observe(getViewLifecycleOwner(), cna_mobileInfos -> {
            if (!cna_mobileInfos.isEmpty()) {
                try {
                    int lnLastVal = cna_mobileInfos.size()-1;
                    lnMobilenox.setVisibility(View.VISIBLE);
                    txtCoord.setText("@" + cna_mobileInfos.get(lnLastVal).nLatitude +
                            "," + cna_mobileInfos.get(lnLastVal).nLongitud);
                    mobileAdapter = new MobileInfoAdapter_Log(new MobileInfoAdapter_Log.OnItemInfoClickListener() {
                        @Override
                        public void OnDelete(int position) {
                            GToast.CreateMessage(getActivity(), "Mobile number deleted.", GToast.INFORMATION).show();
                        }

                        @Override
                        public void OnMobileNoClick(String MobileNo) {
                            Intent mobileIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", MobileNo, null));
                            startActivityForResult(mobileIntent, MOBILE_DIALER);
                        }
                    });
                    rvMobileNox.setAdapter(mobileAdapter);
                    mobileAdapter.setMobileNox(cna_mobileInfos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.getCNA_AddressDataList().observe(getViewLifecycleOwner(), cna_addressInfos -> {
            if (!cna_addressInfos.isEmpty()) {
                try {
                    int lnLastVal = cna_addressInfos.size()-1;
                    lnAddressx.setVisibility(View.VISIBLE);
                    txtCoord.setText("@" + cna_addressInfos.get(lnLastVal).nLatitude
                            + "," + cna_addressInfos.get(lnLastVal).nLongitud);
                    addressAdapter = new AddressInfoAdapter_Log();
                    rvAddress.setAdapter(addressAdapter);
                    addressAdapter.setAddress(cna_addressInfos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initWidgets(View v) {
        ivTransImage = v.findViewById(R.id.iv_transaction_img);
        txtAcctNo = v.findViewById(R.id.txt_acctNo);
        txtClientName = v.findViewById(R.id.txt_clientName);
        txtClientAddress = v.findViewById(R.id.txt_client_address);
        txtTransNo = v.findViewById(R.id.txt_transno);
        txtTransTp = v.findViewById(R.id.lbl_list_header);
        txtCoord = v.findViewById(R.id.lbl_coordinates);
        lnMobilenox = v.findViewById(R.id.ln_mobileNox);
        lnAddressx = v.findViewById(R.id.ln_addressx);
        rvMobileNox = v.findViewById(R.id.rv_mobileNox);
        rvMobileNox.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMobileNox.setHasFixedSize(true);
        rvAddress = v.findViewById(R.id.rv_addressx);
        rvAddress.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAddress.setHasFixedSize(true);
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
