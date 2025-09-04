package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyMenus;
import org.rmj.g3appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class PolicyMenuAdapter extends RecyclerView.Adapter<PolicyMenuAdapter.VHUserGuide> {

    private final UserGuideFilter poFilter;
    private final List<EPolicyMenus> laMenus;
    private final OnViewGuide callback;

    private List<EPolicyMenus> laMenusFiltered;

    public interface OnViewGuide{
        void OnView(String sMenuTitle, String sDescriptxx, String sMenuIDxx);
    }

    public PolicyMenuAdapter(List<EPolicyMenus> laMenus, OnViewGuide callback) {
        this.poFilter = new UserGuideFilter(this);
        this.laMenus = laMenus;
        this.laMenusFiltered = laMenus;
        this.callback = callback;
    }

    public UserGuideFilter GetFilter(){
        return poFilter;
    }

    @NonNull
    @Override
    public VHUserGuide onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_policy_menus, parent, false);
        return new VHUserGuide(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHUserGuide holder, int position) {

        Spanned htmlTitle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlTitle = Html.fromHtml(laMenusFiltered.get(position).getsNamexx(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlTitle = Html.fromHtml(laMenusFiltered.get(position).getsNamexx());
        }

        holder.file_title.setText(htmlTitle);
        holder.mcv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.OnView(laMenusFiltered.get(position).getsNamexx(), laMenusFiltered.get(position).getsDescription(), laMenusFiltered.get(position).getsTransNoxx());
            }
        });
    }

    @Override
    public int getItemCount() {
        return laMenusFiltered.size();
    }

    public static class VHUserGuide extends RecyclerView.ViewHolder{

        private final View view;
        private final MaterialCardView mcv_item;
        private final MaterialTextView file_title;

        public VHUserGuide(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mcv_item = itemView.findViewById(R.id.mcv_item);
            file_title = itemView.findViewById(R.id.file_title);
        }
    }

    public class UserGuideFilter extends Filter{

        private final PolicyMenuAdapter adapter;

        public UserGuideFilter(PolicyMenuAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0){
                laMenusFiltered = laMenus;
            }else {
                List<EPolicyMenus> filterSearch = new ArrayList<>();

                for (EPolicyMenus loMenus: laMenus){

                    String lsTitle = loMenus.getsNamexx();
                    if (lsTitle.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(loMenus);
                    }
                }

                laMenusFiltered = filterSearch;
            }

            results.values = laMenusFiltered;
            results.count = laMenusFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.laMenusFiltered = (List<EPolicyMenus>) results.values;
            this.adapter.notifyDataSetChanged();
        }
    }
}
