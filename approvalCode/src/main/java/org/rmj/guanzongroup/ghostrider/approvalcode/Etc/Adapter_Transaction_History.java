package org.rmj.guanzongroup.ghostrider.approvalcode.Etc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Transaction_History extends RecyclerView.Adapter<Adapter_Transaction_History.VH_Transaction_History>{

    private DataFilter loFilter;
    private OnItemClick foCallback;
    private List<ECASRequests> laData;
    private List<ECASRequests> laDataFiltered;

    public Adapter_Transaction_History(List<ECASRequests> laData, OnItemClick foCallback) {
        this.loFilter = new DataFilter(this);
        this.foCallback = foCallback;
        this.laData = laData;
        this.laDataFiltered = laData;
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

    @Override
    public void onBindViewHolder(@NonNull VH_Transaction_History holder, int position) {
        holder.mtv_sourceno.setText(laDataFiltered.get(position).getsSourceNo());
        holder.mtv_transactdt.setText(laDataFiltered.get(position).getdTransact());
        holder.mtv_remarks.setText(laDataFiltered.get(position).getsRemarksx());

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

    public class VH_Transaction_History extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView mtv_sourceno;
        private MaterialTextView mtv_transactdt;
        private MaterialTextView mtv_remarks;

        public VH_Transaction_History(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_sourceno = itemView.findViewById(R.id.mtv_sourceno);
            mtv_transactdt = itemView.findViewById(R.id.mtv_transactdt);
            mtv_remarks = itemView.findViewById(R.id.mtv_remarks);
        }
    }

    public class DataFilter extends Filter {

        private Adapter_Transaction_History loAdapter;

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

                    if (requests.getsRemarksx().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            requests.getsSourceNo().toLowerCase().contains(constraint.toString().toLowerCase())){
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
