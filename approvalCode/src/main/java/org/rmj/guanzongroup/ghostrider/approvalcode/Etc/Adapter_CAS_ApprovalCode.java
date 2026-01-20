/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.approvalCode
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.approvalcode.Etc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.rmj.g3appdriver.GCircle.room.Entities.ECASApprovalCode;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;

import java.util.List;

public class Adapter_CAS_ApprovalCode extends RecyclerView.Adapter<Adapter_CAS_ApprovalCode.CASViewHolder> {

    private List<ECASApprovalCode> laCASList;
    private OnAuthItemClickListener poListener;

    public Adapter_CAS_ApprovalCode(List<ECASApprovalCode> laCASList, OnAuthItemClickListener foListener) {
        this.laCASList = laCASList;
        this.poListener = foListener;
    }

    @NonNull
    @Override
    public CASViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_auth, parent, false);
        return new CASViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CASViewHolder holder, int position) {
        ECASApprovalCode loRequest = laCASList.get(position);

        holder.btnScaAuth.setText(loRequest.getsDescript());
        holder.btnScaAuth.setOnClickListener(view -> poListener.OnClick(loRequest.getsSourceCD(), loRequest.getsDescript()));
    }

    @Override
    public int getItemCount() {
        return laCASList.size();
    }

    public static class CASViewHolder extends RecyclerView.ViewHolder{

        MaterialButton btnScaAuth;

        public CASViewHolder(@NonNull View itemView) {
            super(itemView);
            btnScaAuth = itemView.findViewById(R.id.btn_listItem);
        }
    }

    public interface OnAuthItemClickListener{
        void OnClick(String SystemCode, String SCAType);
    }
}
