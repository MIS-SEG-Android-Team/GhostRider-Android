package org.rmj.guanzongroup.ghostrider.epacss.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.guanzongroup.ghostrider.epacss.R;

import java.util.List;

public class RecyclerviewUserGuideAdapter extends RecyclerView.Adapter<RecyclerviewUserGuideAdapter.VHUserGuide> {

    private final List<EGuides> laGuides;
    private final OnViewGuide callback;

    public interface OnViewGuide{
        void OnView(EGuides loGuide);
    }

    public RecyclerviewUserGuideAdapter(List<EGuides> laGuides, OnViewGuide callback) {
        this.laGuides = laGuides;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VHUserGuide onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_guides, parent, false);
        return new VHUserGuide(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHUserGuide holder, int position) {

        holder.file_title.setText(laGuides.get(position).getsTitlexx());
        holder.mcv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.OnView(laGuides.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laGuides.size();
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
}
