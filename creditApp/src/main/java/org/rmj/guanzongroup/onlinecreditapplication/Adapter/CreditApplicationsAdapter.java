package org.rmj.guanzongroup.onlinecreditapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.guanzongroup.onlinecreditapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CreditApplicationsAdapter extends RecyclerView.Adapter<CreditApplicationsAdapter.VHApplication> {

    private List<DCreditApplication.ApplicationLog> poListFilter;


    private final FilterList loFilter;
    private final List<DCreditApplication.ApplicationLog> poList;
    private final OnItemActionClickListener listener;

    public interface OnItemActionClickListener{
        void OnPreview(DCreditApplication.ApplicationLog creditapp);
    }

    public CreditApplicationsAdapter(List<DCreditApplication.ApplicationLog> poList, OnItemActionClickListener listener) {
        this.loFilter = new FilterList(this);
        this.poList = poList;
        this.poListFilter = poList;
        this.listener = listener;
    }

    public FilterList GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VHApplication onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_credit_applications, parent, false);
        return new VHApplication(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHApplication holder, int position) {

        DCreditApplication.ApplicationLog loApp = poListFilter.get(position);

        holder.lblName.setText(loApp.sClientNm);
        holder.lblDate.setText(FormatUIText.getParseDateTime(loApp.dCreatedx));
        holder.lblDSent.setText(FormatUIText.getParseDateTime(loApp.dReceived));
        holder.lblDAppv.setText(FormatUIText.getParseDateTime(loApp.dVerified));

        if(loApp.cSendStat.equalsIgnoreCase("0")) {
            holder.lblStat.setText("Pending");
            holder.lblStat.setTextColor(ContextCompat.getColor(holder.lblStat.getContext(), R.color.guanzon_dark_grey));
            holder.lblStat.setAlpha(0.5f);
        } else {
            holder.lblStat.setText("Sent");
            holder.lblStat.setTextColor(ContextCompat.getColor(holder.lblStat.getContext(), R.color.check_green));
            holder.lblStat.setAlpha(1f);
        }
        holder.itemView.setOnClickListener(v -> listener.OnPreview(loApp));
    }

    @Override
    public int getItemCount() {
        return poListFilter.size();
    }

    public static class VHApplication extends RecyclerView.ViewHolder{

        public MaterialTextView lblName, lblDate, lblStat, lblDSent, lblDAppv;

        public VHApplication(@NonNull View itemView) {
            super(itemView);
            lblName = itemView.findViewById(R.id.lbl_applicantName);
            lblStat = itemView.findViewById(R.id.lbl_status);
            lblDate = itemView.findViewById(R.id.lbl_dateCreated);
            lblDSent = itemView.findViewById(R.id.lbl_dateSent);
            lblDAppv = itemView.findViewById(R.id.lbl_dateApproved);
        }
    }

    public class FilterList extends Filter {
        private final CreditApplicationsAdapter loAdapter;

        public FilterList(CreditApplicationsAdapter loAdapter) {
            this.loAdapter = loAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.length() == 0){
                poListFilter = poList;
            }else {
                List<DCreditApplication.ApplicationLog> filterSearch = new ArrayList<>();

                for (DCreditApplication.ApplicationLog loLogs : poList){

                    if (loLogs.sClientNm.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(loLogs);
                    }
                }
                poListFilter = filterSearch;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = poListFilter;
            filterResults.count = poListFilter.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            loAdapter.poListFilter = (List<DCreditApplication.ApplicationLog>) results.values;
            this.loAdapter.notifyDataSetChanged();
        }
    }
}
