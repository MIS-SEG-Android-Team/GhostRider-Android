package org.rmj.guanzongroup.pacitareward.Adapter.SSDD;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.guanzongroup.pacitareward.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_SSDDepartments extends RecyclerView.Adapter<Adapter_SSDDepartments.VHDepartments> {

    private SSDDFilter loFilter;
    private List<ESSDDepartments> laDepartmentsFiltered;

    private final OnItemClickListener foListener;
    private final List<ESSDDepartments> laDepartments;

    public Adapter_SSDDepartments(List<ESSDDepartments> laDepartments, OnItemClickListener foListener) {
        this.loFilter = new SSDDFilter(this);
        this.laDepartments = laDepartments;
        this.laDepartmentsFiltered = laDepartments;
        this.foListener = foListener;
    }

    public SSDDFilter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VHDepartments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHDepartments(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ssdd_department, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VHDepartments holder, int position) {
        holder.item_branch.setText(laDepartmentsFiltered.get(position).getsDescript());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnClick(laDepartmentsFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laDepartmentsFiltered.size();
    }

    public interface OnItemClickListener{
        void OnClick(ESSDDepartments loDepartment);
    }

    public static class VHDepartments extends RecyclerView.ViewHolder{

        private View view;
        private MaterialTextView item_branch;

        public VHDepartments(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            item_branch = itemView.findViewById(R.id.item_branch);
        }
    }

    public class SSDDFilter extends Filter{

        private Adapter_SSDDepartments foDept;

        public SSDDFilter(Adapter_SSDDepartments foDept){
            this.foDept = foDept;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0){
                laDepartmentsFiltered = laDepartments;
            }else {
                List<ESSDDepartments> filterSearch = new ArrayList<>();

                for (ESSDDepartments departments: filterSearch){

                    if (departments.getsDescript().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(departments);
                    }
                }
                laDepartmentsFiltered = filterSearch;
            }

            results.values = laDepartmentsFiltered;
            results.count = laDepartmentsFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foDept.laDepartmentsFiltered = (List<ESSDDepartments>) results.values;
            this.foDept.notifyDataSetChanged();
        }
    }
}
