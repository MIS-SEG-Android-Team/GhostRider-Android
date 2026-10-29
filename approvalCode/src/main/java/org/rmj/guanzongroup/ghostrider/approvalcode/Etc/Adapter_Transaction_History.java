package org.rmj.guanzongroup.ghostrider.approvalcode.Etc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Transaction_History extends RecyclerView.Adapter<Adapter_Transaction_History.VH_Transaction_History>{

    private DataFilter loFilter;
    private OnItemClick foCallback;
    private List<ECASRequests> laData;
    private List<ECASRequests> laDataFiltered;
    private final Branch loBranch;

    public Adapter_Transaction_History(Application foInstance, List<ECASRequests> laData, OnItemClick foCallback) {
        this.loFilter = new DataFilter(this);
        this.foCallback = foCallback;
        this.laData = laData;
        this.laDataFiltered = laData;
        this.loBranch = new Branch(foInstance);
    }

    public DataFilter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VH_Transaction_History onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cas_approval, parent,false);
        return new VH_Transaction_History(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH_Transaction_History holder, int position) {

        holder.mtv_sourceno.setText(laDataFiltered.get(position).getsSourceNo());
        holder.mtv_transactdt.setText(laDataFiltered.get(position).getdTransact());

        String lsBranchNm = loBranch.getBranchInfoNonLive(laDataFiltered.get(position).getsSourceNo().substring(0, 4)) == null ? laDataFiltered.get(position).getsSourceNo().substring(0, 4) :
                                loBranch.getBranchInfoNonLive(laDataFiltered.get(position).getsSourceNo().substring(0, 4)).getBranchNm();

        holder.mtv_branch.setText("Requested By: " + lsBranchNm);
        holder.mtv_supplier.setText(laDataFiltered.get(position).getxSupplier() == null ? "N/A" : laDataFiltered.get(position).getxSupplier());
        holder.mtv_amt.setText(laDataFiltered.get(position).getnTotal() == null ? "0.00" : laDataFiltered.get(position).getnTotal());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foCallback.OnClick(laDataFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laDataFiltered.size();
    }

    public interface OnItemClick{
        void OnClick(ECASRequests loRequest);
    }

    public static class VH_Transaction_History extends RecyclerView.ViewHolder {

        private final View view;
        private final MaterialTextView mtv_sourceno;
        private final MaterialTextView mtv_transactdt;
        private final MaterialTextView mtv_supplier;
        private final MaterialTextView mtv_amt;
        private final MaterialTextView mtv_branch;

        public VH_Transaction_History(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_sourceno = itemView.findViewById(R.id.mtv_sourceno);
            mtv_transactdt = itemView.findViewById(R.id.mtv_transactdt);
            mtv_branch = itemView.findViewById(R.id.mtv_branch);
            mtv_supplier = itemView.findViewById(R.id.mtv_supplier);
            mtv_amt = itemView.findViewById(R.id.mtv_amt);
        }
    }

    public class DataFilter extends Filter {

        private final Adapter_Transaction_History loAdapter;

        public DataFilter(Adapter_Transaction_History loAdapter){
            this.loAdapter = loAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0){
                laDataFiltered = laData;
            }else {

                List<ECASRequests> filterSearch = new ArrayList<>();
                for (ECASRequests requests: laData){

                    String lsBranchNm = loBranch.getBranchInfoNonLive(requests.getsSourceNo().substring(0, 4)) == null ? "" :
                            loBranch.getBranchInfoNonLive(requests.getsSourceNo().substring(0, 4)).getBranchNm();

                    if (requests.getxSupplier().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            requests.getsSourceNo().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            (lsBranchNm.toLowerCase().contains(constraint.toString().toLowerCase()) && !lsBranchNm.isEmpty()) ){

                            filterSearch.add(requests);
                    }
                }
                laDataFiltered = filterSearch;
            }
            results.values = laDataFiltered;
            results.count = laDataFiltered.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            loAdapter.laDataFiltered = (List<ECASRequests>) results.values;
            this.loAdapter.notifyDataSetChanged();
        }
    }

}
