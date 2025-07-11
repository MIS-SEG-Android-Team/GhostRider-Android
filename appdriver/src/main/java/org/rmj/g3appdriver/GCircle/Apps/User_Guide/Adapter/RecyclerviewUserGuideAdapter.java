package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewUserGuideAdapter extends RecyclerView.Adapter<RecyclerviewUserGuideAdapter.VHUserGuide> {

    private final UserGuideFilter poFilter;
    private final List<EGuides> laGuides;
    private final OnViewGuide callback;

    private List<EGuides> laGuidesFiltered;

    public interface OnViewGuide{
        void OnView(EGuides loGuide);
    }

    public RecyclerviewUserGuideAdapter(List<EGuides> laGuides, OnViewGuide callback) {
        this.poFilter = new UserGuideFilter(this);
        this.laGuides = laGuides;
        this.laGuidesFiltered = laGuides;
        this.callback = callback;
    }

    public UserGuideFilter GetFilter(){
        return poFilter;
    }

    @NonNull
    @Override
    public VHUserGuide onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_guides, parent, false);
        return new VHUserGuide(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHUserGuide holder, int position) {

        holder.file_title.setText(laGuidesFiltered.get(position).getsTitlexx());
        holder.mcv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.OnView(laGuidesFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laGuidesFiltered.size();
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

        private final RecyclerviewUserGuideAdapter adapter;

        public UserGuideFilter(RecyclerviewUserGuideAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0){
                laGuidesFiltered = laGuides;
            }else {
                List<EGuides> filterSearch = new ArrayList<>();

                for (EGuides guides: laGuides){

                    String lsTitle = guides.getsTitlexx();
                    if (lsTitle.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterSearch.add(guides);
                    }
                }

                laGuidesFiltered = filterSearch;
            }

            results.values = laGuidesFiltered;
            results.count = laGuidesFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.laGuidesFiltered = (List<EGuides>) results.values;
            this.adapter.notifyDataSetChanged();
        }
    }
}
