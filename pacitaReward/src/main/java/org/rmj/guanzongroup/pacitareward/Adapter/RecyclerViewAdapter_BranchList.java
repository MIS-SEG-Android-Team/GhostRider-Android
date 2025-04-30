package org.rmj.guanzongroup.pacitareward.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewHolder.RecyclerViewHolder_BranchList;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_BranchList extends RecyclerView.Adapter<RecyclerViewHolder_BranchList> {
    private final List<EBranchInfo> paBranch;
    private List<EBranchInfo> paBranchFilter;
    private final BranchFilter poFilter;
    private final OnBranchSelectListener listener;

    public interface  OnBranchSelectListener{
        void OnSelect(String BranchCode, String BranchName);
    }
    public RecyclerViewAdapter_BranchList(List<EBranchInfo> paBranch, OnBranchSelectListener listener) {
        this.paBranchFilter = paBranch;
        this.paBranch = paBranch;
        this.poFilter = new BranchFilter(this);
        this.listener = listener;
    }
    public BranchFilter getFilter(){
        return poFilter;
    }

    @NonNull
    @Override
    public RecyclerViewHolder_BranchList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_evaluation_branches, parent, false);
        return new RecyclerViewHolder_BranchList(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder_BranchList holder, int position) {
        EBranchInfo loBranch = paBranchFilter.get(position);

        holder.item_branch.setText(loBranch.getBranchNm());
        holder.item_branchcode.setText(loBranch.getBranchCd());
        holder.item_branchloc.setText(loBranch.getAddressx());
        holder.view.setOnClickListener(v -> listener.OnSelect(loBranch.getBranchCd(), loBranch.getBranchNm()));
    }
    @Override
    public int getItemCount() {
        return paBranchFilter.size();
    }

    public class BranchFilter extends Filter{

        private final RecyclerViewAdapter_BranchList poAdapter;

        public BranchFilter(RecyclerViewAdapter_BranchList poAdapter) {
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
