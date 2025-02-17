/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 6/8/21 1:10 PM
 * project file last modified : 6/8/21 1:10 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchOpeningMonitor;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BranchOpeningAdapter extends RecyclerView.Adapter<BranchOpeningAdapter.OpeningViewHolder> {

    private final Context mContext;
    private final List<DBranchOpeningMonitor.BranchOpeningInfo> poOpening;
    private final OnAdapterItemClickListener mListener;

    public interface OnAdapterItemClickListener{
        void OnClick();
    }

    public BranchOpeningAdapter(Context context, List<DBranchOpeningMonitor.BranchOpeningInfo> poOpening, OnAdapterItemClickListener listener) {
        this.mContext = context;
        this.poOpening = poOpening;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public OpeningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_branch_opening, parent, false);
        return new OpeningViewHolder(view, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull OpeningViewHolder holder, int position) {
        DBranchOpeningMonitor.BranchOpeningInfo loMonitor = poOpening.get(position);
        holder.lblBranch.setText(loMonitor.sBranchNm);
        holder.lblOpenTime.setText(loMonitor.sTimeOpen);
        holder.lblTimeOpened.setText(loMonitor.sOpenNowx);

        try {
            Date loOpening = new SimpleDateFormat("HH:mm:ss").parse(loMonitor.sTimeOpen);
            Date loOpenedx = new SimpleDateFormat("HH:mm:ss").parse(loMonitor.sOpenNowx);
//        DateTimeFormatter format = new DateTimeFormatterBuilder()
//                .appendPattern("hh:mm a")
//                .parseCaseInsensitive()
//                .parseLenient()
//                .toFormatter();
//        LocalTime loTime1 = LocalTime.parse(loMonitor.sTimeOpen, format);
//        LocalTime loTime2 = LocalTime.parse(loMonitor.sOpenNowx, format);
            int lnResult = loOpenedx.compareTo(loOpening);
            if (lnResult <= 0) {
                holder.imgTime.setColorFilter(ContextCompat.getColor(mContext, R.color.branch_opening));
            } else {
                holder.imgTime.setColorFilter(ContextCompat.getColor(mContext, R.color.branch_opening_late));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return poOpening.size();
    }

    public static class OpeningViewHolder extends RecyclerView.ViewHolder{

        public ShapeableImageView imgTime;
        public MaterialTextView lblBranch, lblOpenTime, lblTimeOpened;

        public OpeningViewHolder(@NonNull View itemView, OnAdapterItemClickListener listener) {
            super(itemView);

            imgTime = itemView.findViewById(R.id.shapeableImageView3);
            lblBranch = itemView.findViewById(R.id.lbl_list_branchName);
            lblOpenTime = itemView.findViewById(R.id.lbl_list_openingTime);
            lblTimeOpened = itemView.findViewById(R.id.lbl_list_timeOpened);

            itemView.setOnClickListener(v -> listener.OnClick());
        }
    }
}
