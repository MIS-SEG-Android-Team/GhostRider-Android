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
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.SingleClickListener;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import com.google.android.material.textview.MaterialTextView;


import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    public interface OnItemClickListener{
        void OnClick(int position);
        void OnMobileNoClickListener(String MobileNo);
        void OnAddressClickListener(String Address, String[] args);
        void OnActionButtonClick();
    }

    private final List<EDCPCollectionDetail> plCollection;
    private List<EDCPCollectionDetail> collctFilter;

    private final CollectionSearch poSearch;
    private final OnItemClickListener mListener;

    public CollectionAdapter(List<EDCPCollectionDetail> plCollection, OnItemClickListener mListener) {
        this.plCollection = plCollection;
        this.collctFilter = plCollection;
        this.mListener = mListener;
        this.poSearch = new CollectionSearch(this);
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_collection, parent, false);
        return new CollectionViewHolder(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        EDCPCollectionDetail collection = collctFilter.get(position);
        try {
            holder.loPlan = collection;
            holder.lblAcctNo.setText("Account No. : " + collection.getAcctNmbr());
            holder.lblClient.setText(collection.getFullName());
            holder.lblAdd1xx.setText(collection.getAddressx());
            holder.lblMobile.setText(collection.getMobileNo());
            if (collection.getRemCodex() == null || collection.getRemCodex().isEmpty()){
                holder.lblStatus.setText("");
            }else{
                holder.lblStatus.setText(DCP_Constants.getRemarksDescription(collection.getRemCodex()));
            }
            holder.lblMnthAm.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(collection.getMonAmort())));
            holder.lblAmount.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(collection.getAmtDuexx())));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return collctFilter.size();
    }

    public CollectionSearch getCollectionSearch(){
        return poSearch;
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder{

        public EDCPCollectionDetail loPlan;
        public MaterialTextView lblAcctNo;
        public MaterialTextView lblClient;
        public MaterialTextView lblAdd1xx;
        public MaterialTextView lblMobile;
        public MaterialTextView lblAmount;
        public MaterialTextView lblStatus;
        public MaterialTextView lblMnthAm;

        public CollectionViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            lblAcctNo = itemView.findViewById(R.id.lbl_AccountNo);
            lblClient = itemView.findViewById(R.id.lbl_clientNm);
            lblAdd1xx = itemView.findViewById(R.id.lbl_dcpAddress1);
            lblMobile = itemView.findViewById(R.id.lbl_dcpContact);
            lblMnthAm = itemView.findViewById(R.id.lbl_dcpAmortization);
            lblAmount = itemView.findViewById(R.id.lbl_dcpAmountDue);
            lblStatus = itemView.findViewById(R.id.lbl_dcpStatus);

            itemView.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.OnClick(position);
                    }
                }
            });

            lblAdd1xx.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.OnAddressClickListener("Sample", null);
                }
            });

            lblMobile.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.OnMobileNoClickListener(loPlan.getMobileNo());
                }
            });
        }
    }

    public class CollectionSearch extends Filter{

        private final CollectionAdapter poAdapter;

        public CollectionSearch(CollectionAdapter poAdapter) {
            super();
            this.poAdapter = poAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults results = new FilterResults();
            if(charSequence.length() == 0){
                collctFilter = (plCollection);
            } else {
                List<EDCPCollectionDetail> filterSearch = new ArrayList<>();
                for(EDCPCollectionDetail plan : plCollection){
                    String lsClientNme = plan.getFullName().toLowerCase();
                    if(lsClientNme.contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(plan);
                    }
                }
                collctFilter = filterSearch;
            }

            results.values = collctFilter;
            results.count = collctFilter.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            poAdapter.collctFilter = (List<EDCPCollectionDetail>) filterResults.values;
            this.poAdapter.notifyDataSetChanged();
        }
    }
}
