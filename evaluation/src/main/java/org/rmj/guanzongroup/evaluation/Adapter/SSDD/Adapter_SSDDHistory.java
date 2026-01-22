package org.rmj.guanzongroup.evaluation.Adapter.SSDD;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.guanzongroup.evaluation.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_SSDDHistory extends RecyclerView.Adapter<Adapter_SSDDHistory.VHistory> {

    private final SSDDFilter loFilter;
    private List<ESSDDMaster> laHistoryFiltered;

    private final OnItemClickListener foListener;
    private final List<ESSDDMaster> laHistory;

    public Adapter_SSDDHistory(List<ESSDDMaster> laHistory, OnItemClickListener foListener) {
        this.loFilter = new SSDDFilter(this);
        this.laHistory = laHistory;
        this.laHistoryFiltered = laHistory;
        this.foListener = foListener;
    }

    public SSDDFilter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHistory(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ssdd_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VHistory holder, int position) {

        holder.mtv_transnox.setText(laHistoryFiltered.get(position).getsTransNox());
        holder.mtv_dtransact.setText(laHistoryFiltered.get(position).getdTransact());

        switch (laHistoryFiltered.get(position).getcTranStat()) {
            case "0" -> {
                holder.mtv_status.setText("Open");
                holder.mtv_status.setTextColor(holder.mtv_status.getResources().getColor(R.color.guanzon_dark_grey, null));
            }
            case "1" -> {
                holder.mtv_status.setText("Closed");
                holder.mtv_status.setTextColor(holder.mtv_status.getResources().getColor(R.color.check_green, null));
            }
            case "3" -> {
                holder.mtv_status.setText("Posted");
                holder.mtv_status.setTextColor(holder.mtv_status.getResources().getColor(R.color.guanzon_digital_orange, null));
            }
            default -> {
                holder.mtv_status.setText("Unknown");
                holder.mtv_status.setTextColor(holder.mtv_status.getResources().getColor(R.color.cross_red, null));
            }
        };

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnClick(laHistoryFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laHistoryFiltered.size();
    }

    public interface OnItemClickListener{
        void OnClick(ESSDDMaster loHistory);
    }

    public static class VHistory extends RecyclerView.ViewHolder{

        private View view;
        private MaterialTextView mtv_transnox, mtv_dtransact, mtv_status;

        public VHistory(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_transnox = itemView.findViewById(R.id.mtv_transnox);
            mtv_dtransact = itemView.findViewById(R.id.mtv_dtransact);
            mtv_status = itemView.findViewById(R.id.mtv_status);
        }
    }

    public class SSDDFilter extends Filter{

        private Adapter_SSDDHistory foHistory;

        public SSDDFilter(Adapter_SSDDHistory foHistory){
            this.foHistory = foHistory;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0){
                laHistoryFiltered = laHistory;
            }else {
                List<ESSDDMaster> filterSearch = new ArrayList<>();

                for (ESSDDMaster history: laHistory){

                    if (history.getsTransNox().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(history);
                    }
                }
                laHistoryFiltered = filterSearch;
            }

            results.values = laHistoryFiltered;
            results.count = laHistoryFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foHistory.laHistoryFiltered = (List<ESSDDMaster>) results.values;
            this.foHistory.notifyDataSetChanged();
        }
    }
}
