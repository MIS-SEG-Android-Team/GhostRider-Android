package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterInventoryBranch extends RecyclerView.Adapter<AdapterInventoryBranch.BranchListViewHolder> {

    private final List<EBranchInfo> paBranch;
    private List<EBranchInfo> paBranchFilter;
    private final OnBranchSelectListener listener;
    private final BranchFilter poFilter;

    public interface OnBranchSelectListener{
        void OnSelect(String BranchCode, String BranchName);
    }

    public AdapterInventoryBranch(List<EBranchInfo> paBranch, OnBranchSelectListener listener) {
        this.paBranchFilter = paBranch;
        this.paBranch = paBranch;
        this.listener = listener;
        this.poFilter = new BranchFilter(this);
    }

    public BranchFilter getFilter(){
        return poFilter;
    }

    @NonNull
    @Override
    public BranchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inventory_branch, parent, false);
        return new BranchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchListViewHolder holder, int position) {
        EBranchInfo loBranch = paBranchFilter.get(position);
        holder.lblBranchCd.setText(loBranch.getBranchCd());
        holder.lblBranchNm.setText(loBranch.getBranchNm());
        holder.itemView.setOnClickListener(v -> listener.OnSelect(loBranch.getBranchCd(), loBranch.getBranchNm()));
    }

    @Override
    public int getItemCount() {
        return paBranchFilter.size();
    }

    public static class BranchListViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        MaterialTextView lblBranchCd, lblBranchNm;

        public BranchListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            lblBranchCd = itemView.findViewById(R.id.lbl_branchCd);
            lblBranchNm = itemView.findViewById(R.id.lbl_branchNm);
        }
    }

    public class BranchFilter extends Filter{

        private final AdapterInventoryBranch poAdapter;

        public BranchFilter(AdapterInventoryBranch poAdapter) {
            super();
            this.poAdapter = poAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            if(constraint.length() == 0){
                paBranchFilter = paBranch;
            } else {
                List<EBranchInfo> filterSearch = new ArrayList<>();
                for (EBranchInfo branch:paBranch){
                    String lsBranchNm = branch.getBranchNm();
                    if(lsBranchNm.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(branch);
                    }
                }
                paBranchFilter = filterSearch;
            }
            results.values = paBranchFilter;
            results.count = paBranchFilter.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            poAdapter.paBranchFilter = (List<EBranchInfo>) results.values;
            this.poAdapter.notifyDataSetChanged();
        }
    }
}
