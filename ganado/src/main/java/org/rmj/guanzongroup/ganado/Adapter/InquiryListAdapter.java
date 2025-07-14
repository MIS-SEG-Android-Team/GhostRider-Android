/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 9/16/21, 10:47 AM
 * project file last modified : 9/16/21, 10:47 AM
 */

package org.rmj.guanzongroup.ganado.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EGanadoOnline;
import org.rmj.g3appdriver.lib.Ganado.Obj.ProductInquiry;
import org.rmj.g3appdriver.lib.Ganado.model.GConstants;
import org.rmj.guanzongroup.ganado.Dialog.DialogInquiryHistory;
import org.rmj.guanzongroup.ganado.R;

import java.util.List;

public class InquiryListAdapter extends RecyclerView.Adapter<InquiryListAdapter.ApplicationViewHolder> {
    private List<EGanadoOnline> poModel;
    private ProductInquiry poApp;
    private OnModelClickListener listener;
    private boolean forViewing = false;
    private Application mContex;
    public interface OnModelClickListener{
        void OnClick(String TransNox);
    }

    public InquiryListAdapter(Application mContex, List<EGanadoOnline> poModel, OnModelClickListener listener) {
        this.poModel = poModel;
        this.mContex = mContex;
        this.listener = listener;
        this.poApp = new ProductInquiry(mContex);
    }


    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inquiry_item, parent, false);
        return new ApplicationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        try {
            EGanadoOnline loModel = poModel.get(position);
            holder.lblClientNm.setText(loModel.getClientNm());

            String lsSendStat;

            if(loModel.getSendStat().equalsIgnoreCase("1")){
                lsSendStat = "Sent";
            } else {
                lsSendStat = "Pending";
            }

            JSONObject object = new JSONObject(loModel.getClntInfo());
            JSONObject object1 = new JSONObject(loModel.getProdInfo());
            DGanadoOnline.McInfo eganado = poApp.GetMCInfo(object1.getString("sModelIDx"),object1.getString("sBrandIDx"),object1.getString("sColorIDx"));

            holder.lblSendStat.setText(lsSendStat);
            holder.lblMobileNo.setText(object.getString("sMobileNo"));

            holder.lblUnitDesc.setText(eganado.ModelNme + " (" + eganado.ColorNme + ")");
            holder.lblInqTypex.setText(GConstants.PAYMENT_FORM[Integer.parseInt(loModel.getPaymForm())]);
            String lsTranStat = loModel.getTranStat();
            if (lsTranStat == null || lsTranStat.isEmpty()){
                lsTranStat = "0";
            }

            holder.lblInqStats.setText(GConstants.INQUIRY_STATUS[Integer.parseInt(lsTranStat)]);
            holder.itemView.setOnClickListener(v -> {
                if(listener != null){
                    listener.OnClick(loModel.getTransNox());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return poModel.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder{
        View view;
        MaterialTextView lblClientNm, lblSendStat, lblMobileNo, lblUnitDesc, lblInqTypex, lblInqStats;
        LinearLayout lnStatus;
        ShapeableImageView imgType;
        public ApplicationViewHolder(@NonNull View view) {
            super(view);

            this.view = view;
            lblClientNm = view.findViewById(R.id.lbl_clientName);
            lblSendStat = view.findViewById(R.id.lbl_status);
            lblMobileNo = view.findViewById(R.id.lbl_mobileNo);
            lblUnitDesc = view.findViewById(R.id.lbl_modelDesc);
            lblInqTypex = view.findViewById(R.id.lbl_InquiryType);
            lblInqStats = view.findViewById(R.id.lbl_recorStats);
        }
    }
}
