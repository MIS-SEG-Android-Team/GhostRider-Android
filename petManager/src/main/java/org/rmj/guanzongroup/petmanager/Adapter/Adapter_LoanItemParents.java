package org.rmj.guanzongroup.petmanager.Adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.guanzongroup.petmanager.Activity.Activity_LoanItems;
import org.rmj.guanzongroup.petmanager.Dialog.Dialog_LoanPreview;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewHolder.VH_LoanItemParents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_LoanItemParents extends RecyclerView.Adapter<VH_LoanItemParents> {
    private Context context;
    private EmployeeLoan poEmpLoan;
    private Boolean isAH;
    private Boolean forApproval;
    private HashMap<String, List< EEmpLoan>> poLoans;
    private HashMap<String, List<EEmpLoan>> poLoansFilter;
    private ItemFilter poFilter;
    private onDisplayPreview callBack;
    public interface onDisplayPreview{
        void onDisplay(Dialog_LoanPreview.DialogVal loDetails, Boolean showEmpNm);
    }

    public Adapter_LoanItemParents(Application context, HashMap<String, List<EEmpLoan>> poLoans, Boolean isAH, Boolean forApproval, onDisplayPreview callback){
        this.context = context;
        this.poLoans = poLoans;
        this.poLoansFilter = poLoans;
        this.poEmpLoan = new EmployeeLoan(context);
        this.isAH = isAH;
        this.forApproval = forApproval;
        this.poFilter = new ItemFilter(this);
        this.callBack = callback;
    }
    public ItemFilter getFilter(){
        return poFilter;
    }
    @NonNull
    @Override
    public VH_LoanItemParents onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_loanitemparent, parent, false);
        return new VH_LoanItemParents(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VH_LoanItemParents holder, int position) {
        if (isAH && forApproval){
            /*holder.empname.setText(poEmpLoan.GetEmpName());
            holder.emppos.setText(poEmpLoan.GetEmpDepartment());*/

            holder.layout_empinfo.setVisibility(View.VISIBLE);
        }else {
            holder.layout_empinfo.setVisibility(View.GONE);
        }

        List<String> keySet = new ArrayList<>(poLoansFilter.keySet());
        List<EEmpLoan> foLoans = poLoansFilter.get(keySet.get(position));

        //TODO: SET LIST FOR LOAN DETAILS
        Adapter_LoanItems loChildAdapter = new Adapter_LoanItems(context, poEmpLoan, foLoans, new Adapter_LoanItems.onSelectDetails() {
            @Override
            public void onSelectData(Dialog_LoanPreview.DialogVal loVal) {
                callBack.onDisplay(loVal, forApproval);
            }
        });

        holder.rec_parentitems.setAdapter(loChildAdapter);
        holder.rec_parentitems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }
    @Override
    public int getItemCount() {
        return poLoansFilter.size();
    }

    public class ItemFilter extends Filter{
        private final Adapter_LoanItemParents loAdapterParent;
        public ItemFilter(Adapter_LoanItemParents loAdapterParent){
            this.loAdapterParent = loAdapterParent;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            try {
                if(constraint.length() == 0){
                    poLoansFilter = poLoans;
                } else {
                    HashMap<String, List<EEmpLoan>> filterSearch = new HashMap<>();

                    for (Map.Entry<String, List<EEmpLoan>> entry: poLoans.entrySet()) {
                        //TODO: SCAN EVERY LIST, ADD TO FILTER IF (MATCHED BY EMP NAME, LOAN NAME, STATUS, AMOUNT)
                        if (entry.getKey().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filterSearch.put(entry.getKey(), entry.getValue());
                        }else {
                            List<EEmpLoan> loVal = entry.getValue();
                            List<EEmpLoan> childList = new ArrayList<>();

                            for (int i = 0; i < loVal.size(); i++){

                                String sLoanNm = poEmpLoan.GetLoanName(loVal.get(i).getsLoanIDxx()).toLowerCase();
                                String sStatus = poEmpLoan.GetStatus(loVal.get(i).getcSendStat(), loVal.get(i).getcTranStat()).toLowerCase();

                                if (sLoanNm.contains(constraint.toString().toLowerCase())){
                                    childList.add(loVal.get(i));
                                }else if (sStatus.contains(constraint.toString().toLowerCase())){
                                    childList.add(loVal.get(i));
                                } else if (String.valueOf(loVal.get(i).getnLoanAmtxx()).contains(constraint.toString())) {
                                    childList.add(loVal.get(i));
                                }
                            }

                            filterSearch.put(entry.getKey(), childList);
                        }
                    }

                    poLoansFilter = filterSearch;
                }

                results.values = poLoansFilter;
                results.count = poLoansFilter.size();

                return results;
            }catch (Exception e){
                e.getMessage();
                return null;
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            loAdapterParent.poLoansFilter = (HashMap<String, List<EEmpLoan>>) results.values;
            this.loAdapterParent.notifyDataSetChanged();
        }
    }
}
