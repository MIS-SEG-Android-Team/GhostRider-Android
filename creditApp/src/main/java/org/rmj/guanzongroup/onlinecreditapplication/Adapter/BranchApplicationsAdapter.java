/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditApp
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.onlinecreditapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.BranchApplicationModel;
import org.rmj.guanzongroup.onlinecreditapplication.R;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class BranchApplicationsAdapter extends RecyclerView.Adapter<BranchApplicationsAdapter.ClientInfoViewHolder> implements Filterable{

    private List<BranchApplicationModel> plLoanApp;
    private List<BranchApplicationModel> plLoanApp1;
    private List<BranchApplicationModel> filteredList;
    private OnApplicationClickListener onApplicationClickListener;

    public BranchApplicationsAdapter(List<BranchApplicationModel> plLoanApp, OnApplicationClickListener onApplicationClickListener) {
        this.plLoanApp = plLoanApp;
        this.plLoanApp1 = plLoanApp;
        this.onApplicationClickListener = onApplicationClickListener;
    }

    @NonNull
    @Override
    public ClientInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_branch_applications, parent, false);
        return new ClientInfoViewHolder(view,onApplicationClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientInfoViewHolder holder, int position) {
        BranchApplicationModel poLoan = plLoanApp.get(position);

        holder.lblTransNoxxx.setText("Transaction No. :" + poLoan.getsTransNox());
        holder.lblClientName.setText(poLoan.getsCompnyNm());
        holder.lblAppltnDate.setText(poLoan.getdTransact());
        holder.lblStatus.setText(poLoan.getTransactionStatus());
    }

    @Override
    public int getItemCount() {
        return plLoanApp.size();
    }

    public class ClientInfoViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView lblTransNoxxx, lblClientName, lblAppltnDate;
        MaterialTextView lblStatus;

        public ClientInfoViewHolder(@NonNull View itemView, OnApplicationClickListener onApplicationClickListener) {
            super(itemView);

            lblTransNoxxx = itemView.findViewById(R.id.lbl_listLog_applicationTransNo);
            lblClientName = itemView.findViewById(R.id.lbl_listLog_applicantName);
            lblAppltnDate = itemView.findViewById(R.id.lbl_listLog_applicationDate);
            lblStatus = itemView.findViewById(R.id.lbl_listLog_applicationWithCI);

            itemView.setOnClickListener(v12 -> {
                if(BranchApplicationsAdapter.this.onApplicationClickListener !=null){
                    int lnPos = getAdapterPosition();
                    if(lnPos != RecyclerView.NO_POSITION){
                        BranchApplicationsAdapter.this.onApplicationClickListener.OnClick(lnPos, plLoanApp);
                    }
                }
            });
        }
    }

    public interface OnApplicationClickListener{
        void OnClick(int position, List<BranchApplicationModel> loanList);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty() || charString.length()==0 || charString == null) {
                    plLoanApp = plLoanApp1;

                } else {
                    filteredList = new ArrayList<>();
                    for (BranchApplicationModel row : plLoanApp1) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or category in match match
                        if (row.getsCompnyNm().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }

                    plLoanApp = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = plLoanApp;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                plLoanApp = (ArrayList<BranchApplicationModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
