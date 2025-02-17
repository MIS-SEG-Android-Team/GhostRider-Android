/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.petmanager.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESelfieLog;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSelfieLog;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.lib.FileManager.GImage;
import org.rmj.guanzongroup.petmanager.R;

import java.util.List;

public class TimeLogAdapter extends RecyclerView.Adapter<TimeLogAdapter.TimeLogViewHolder> {

    private final List<DSelfieLog.LogTime> selfieLogList;

    private final OnTimeLogActionListener mListener;

    public interface OnTimeLogActionListener{
        void OnImagePreview(String sTransNox);
        void OnClickResend(String TransNox);
    }

    public TimeLogAdapter(List<DSelfieLog.LogTime> selfieLogList, OnTimeLogActionListener listener) {
        this.selfieLogList = selfieLogList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TimeLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_time_log, parent, false);
        return new TimeLogViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TimeLogViewHolder holder, int position) {
        try {
            DSelfieLog.LogTime logSelfie = selfieLogList.get(position);
            String[] dTime = logSelfie.dLogTimex.split(" ");
            holder.lblTimeLog.setText(FormatUIText.HHMMSS_TO_HHMMA_12(dTime[1]));
            if(logSelfie.sBranchCd != null) {
                holder.lblBranchCD.setText(logSelfie.sBranchNm);
            } else {
                holder.lblBranchCD.setVisibility(View.GONE);
            }

            if (logSelfie.cSlfSentx.equalsIgnoreCase("1")) {
                holder.lblStatusx.setText("Sent");
                holder.lblStatusx.setTextColor(Color.parseColor("#008000"));
                holder.btnResend.setVisibility(View.GONE);
            } else {
                holder.lblStatusx.setText("Sending");
                holder.lblStatusx.setTextColor(Color.RED);
                holder.btnResend.setVisibility(View.VISIBLE);
            }

            if (logSelfie.cImgSentx.equalsIgnoreCase("1")) {
                holder.lblImgStatus.setText("Uploaded");
                holder.lblImgStatus.setTextColor(Color.parseColor("#008000"));
            } else {
                holder.lblImgStatus.setText("Uploading");
                holder.lblImgStatus.setTextColor(Color.RED);
            }

            holder.btnResend.setOnClickListener(v -> {
                if(position != RecyclerView.NO_POSITION){
                    mListener.OnClickResend(logSelfie.sTransNox);
                }
            });

            holder.imgPreview.setOnClickListener(v -> {
                if(position != RecyclerView.NO_POSITION){
                    mListener.OnImagePreview(logSelfie.sFileLoct);
                }
            });

            holder.imgPreview.setImageBitmap(GImage.GetImagePreview(logSelfie.sFileLoct));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return selfieLogList.size();
    }

    public static class TimeLogViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView lblBranchCD,lblTimeLog, lblImgStatus, lblStatusx;
        ShapeableImageView imgPreview;
        MaterialButton btnResend;

        public TimeLogViewHolder(@NonNull View itemView) {
            super(itemView);

            lblImgStatus = itemView.findViewById(R.id.lbl_selfie_status);
            lblBranchCD = itemView.findViewById(R.id.lbl_list_BranchCD);
            lblTimeLog = itemView.findViewById(R.id.lbl_list_logTime);
            lblStatusx = itemView.findViewById(R.id.lbl_time_in_status);
            imgPreview = itemView.findViewById(R.id.img_selfie);
            btnResend = itemView.findViewById(R.id.btn_resend);
        }
    }
}
