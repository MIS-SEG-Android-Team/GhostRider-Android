package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Branch_Vist_History extends RecyclerView.Adapter<Adapter_Branch_Vist_History.VHBranchHistory> {

    public List<DBranchVisitMaster.MasterHistory> laMAsterList;
    public List<DBranchVisitMaster.MasterHistory> laMAsterListFiltered;
    private final OnItemListener loListener;
    private final BranchHistoryFilter poFilter;

    public Adapter_Branch_Vist_History(List<DBranchVisitMaster.MasterHistory> faMAsterList, OnItemListener foListener){
        laMAsterList = faMAsterList;
        laMAsterListFiltered = faMAsterList;
        loListener = foListener;
        poFilter = new BranchHistoryFilter(this);
    }

    public BranchHistoryFilter GetFilter(){
        return poFilter;
    }

    public interface OnItemListener{
        void OnSelectMaster(DBranchVisitMaster.MasterHistory foTrans);
    }

    @NonNull
    @Override
    public VHBranchHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHBranchHistory(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_branch_visit_history, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VHBranchHistory holder, int position) {

        holder.mtv_branch.setText(laMAsterListFiltered.get(position).sBranchNm);
        switch (Integer.parseInt(laMAsterListFiltered.get(position).cTranStat)){
            case 0:
                holder.mtv_status.setText("Open");
                break;
            case 1:
                holder.mtv_status.setText("Closed");
                holder.mtv_status.setTextColor(Color.parseColor("#F88222"));
                break;
            case 2:
                holder.mtv_status.setText("Posted");
                holder.mtv_status.setTextColor(Color.GREEN);
                break;
            default:
                holder.mtv_status.setText("Unknown");
        }
        holder.mtv_transno.setText(laMAsterListFiltered.get(position).sTransNox);
        holder.mtv_date.setText(laMAsterListFiltered.get(position).dTransact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loListener.OnSelectMaster(laMAsterListFiltered.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return laMAsterListFiltered.size();
    }

    public class BranchHistoryFilter extends Filter {

        private Adapter_Branch_Vist_History loAdapter;

        public BranchHistoryFilter(Adapter_Branch_Vist_History foAdapter){
            loAdapter = foAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            final FilterResults results = new FilterResults();

            if(charSequence.length() == 0){
                laMAsterListFiltered = laMAsterList;
            } else {

                List<DBranchVisitMaster.MasterHistory> filterSearch = new ArrayList<>();
                for (DBranchVisitMaster.MasterHistory branchHistory: laMAsterList){

                    if(branchHistory.sBranchNm.toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            branchHistory.sTransNox.toLowerCase().contains(charSequence.toString().toLowerCase())){

                        filterSearch.add(branchHistory);
                    }
                }
                laMAsterListFiltered = filterSearch;
            }
            results.values = laMAsterListFiltered;
            results.count = laMAsterListFiltered.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            loAdapter.laMAsterListFiltered = (List<DBranchVisitMaster.MasterHistory>) filterResults.values;
            this.loAdapter.notifyDataSetChanged();
        }
    }

    public static class VHBranchHistory extends RecyclerView.ViewHolder{

        public View view;
        public MaterialTextView mtv_branch, mtv_status, mtv_transno, mtv_date;

        public VHBranchHistory(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_branch = itemView.findViewById(R.id.mtv_branch);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_transno = itemView.findViewById(R.id.mtv_transno);
            mtv_date = itemView.findViewById(R.id.mtv_date);
        }

    }
}
