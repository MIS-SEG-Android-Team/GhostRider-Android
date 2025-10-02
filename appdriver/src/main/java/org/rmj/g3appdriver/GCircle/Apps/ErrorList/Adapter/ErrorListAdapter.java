package org.rmj.g3appdriver.GCircle.Apps.ErrorList.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class ErrorListAdapter extends RecyclerView.Adapter<ErrorListAdapter.VHSMSError> {

    private List<EErrorLogs> paErrorsFiltered;
    private final List<EErrorLogs> paErrors;
    public final OnItemViewListener foListener;
    private final ErrorLogsFilter poFilter;

    public interface OnItemViewListener {
        void OnItemClick(EErrorLogs foSMS);
    }

    public ErrorListAdapter(List<EErrorLogs> foOutgoingSMS, OnItemViewListener foListener) {
        this.paErrors = foOutgoingSMS;
        this.paErrorsFiltered = foOutgoingSMS;
        this.foListener = foListener;
        this.poFilter = new ErrorLogsFilter(this);
    }

    public ErrorLogsFilter GetFilter(){
        return poFilter;
    }

    @NonNull
    @Override
    public VHSMSError onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_error_log, parent, false);
        return new VHSMSError(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VHSMSError holder, int position) {

        holder.mtv_transaction.setText(paErrorsFiltered.get(position).getsSourceTransNo());
        holder.mtv_messageerror.setText(paErrorsFiltered.get(position).getsMessagex());
        holder.mtv_date.setText(paErrorsFiltered.get(position).getdLogDate());

        holder.mtv_messageerror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnItemClick(paErrorsFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return paErrorsFiltered.size();
    }

    public class ErrorLogsFilter extends Filter{

        private final ErrorListAdapter poAdapter;

        public ErrorLogsFilter(ErrorListAdapter poAdapter){
            this.poAdapter = poAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if(constraint.length() == 0){
                paErrorsFiltered = paErrors;
            } else {
                List<EErrorLogs> filterSearch = new ArrayList<>();
                for (EErrorLogs logs: paErrors){

                    if(logs.getsMessagex().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            logs.getsSourceTransNo().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            logs.getdLogDate().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(logs);
                    }
                }
                paErrorsFiltered = filterSearch;
            }
            results.values = paErrorsFiltered;
            results.count = paErrorsFiltered.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            poAdapter.paErrorsFiltered = (List<EErrorLogs>) results.values;
            this.poAdapter.notifyDataSetChanged();
        }
    }

    public static class VHSMSError extends RecyclerView.ViewHolder {

        private final MaterialTextView mtv_transaction;
        private final MaterialTextView mtv_messageerror;
        private final MaterialTextView mtv_date;

        public VHSMSError(View itemView) {
            super(itemView);

            mtv_transaction = itemView.findViewById(R.id.mtv_transaction);
            mtv_messageerror = itemView.findViewById(R.id.mtv_messageerror);
            mtv_date = itemView.findViewById(R.id.mtv_date);
        }
    }
}
