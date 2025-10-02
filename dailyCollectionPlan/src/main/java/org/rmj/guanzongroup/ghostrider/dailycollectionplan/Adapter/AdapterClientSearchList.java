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

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import java.util.List;

public class AdapterClientSearchList extends RecyclerView.Adapter<AdapterClientSearchList.ClientListHolder> {

    private final List<EDCPCollectionDetail> collectionDetails;
    private final OnAdapterClickListener mListener;

    public interface OnAdapterClickListener{
        void OnClick(EDCPCollectionDetail detail);
    }

    public AdapterClientSearchList(List<EDCPCollectionDetail> collectionDetails, OnAdapterClickListener listener) {
        this.collectionDetails = collectionDetails;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ClientListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_client_search, parent, false);
        return new ClientListHolder(view, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ClientListHolder holder, int position) {
        EDCPCollectionDetail loDetail = collectionDetails.get(position);
        holder.loDetail = collectionDetails.get(position);
        if(!loDetail.getAcctNmbr().isEmpty()) {
            holder.lblAccNox.setText("Account No. : " + loDetail.getAcctNmbr());
        } else {
            holder.lblAccNox.setText("MC Serial No. : " + loDetail.getSerialNo());
        }
        holder.lblClientNm.setText(loDetail.getFullName());
        String lsAddress = "";
        if(!loDetail.getHouseNox().isEmpty()){
            lsAddress = lsAddress + loDetail.getHouseNox() + ", ";
        }
        if(!loDetail.getAddressx().isEmpty()){
            lsAddress = lsAddress + loDetail.getAddressx() + ", ";
        }
        if(!loDetail.getTownName().isEmpty()){
            lsAddress = lsAddress + loDetail.getTownName();
        }
        holder.lblAddress.setText(lsAddress);
    }

    @Override
    public int getItemCount() {
        return collectionDetails.size();
    }

    public static class ClientListHolder extends RecyclerView.ViewHolder{
        EDCPCollectionDetail loDetail;
        MaterialTextView lblAccNox, lblClientNm, lblAddress;

        public ClientListHolder(@NonNull View itemView, OnAdapterClickListener listener) {
            super(itemView);

            lblAccNox = itemView.findViewById(R.id.lbl_AccountNo);
            lblClientNm = itemView.findViewById(R.id.lbl_clientNm);
            lblAddress = itemView.findViewById(R.id.lbl_addressx);

            itemView.setOnClickListener(v -> {
                if(getBindingAdapterPosition() != RecyclerView.NO_POSITION){
                    listener.OnClick(loDetail);
                }
            });
        }
    }
}
