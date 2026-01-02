package org.rmj.guanzongroup.evaluation.Adapter.SSDD;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.guanzongroup.evaluation.R;
import java.util.List;

public class Adapter_SSDDCategories extends RecyclerView.Adapter<Adapter_SSDDCategories.VHCategories> {

    private final OnItemClickListener foListener;
    private final List<ESSDDCategories> laCategories;

    public Adapter_SSDDCategories(List<ESSDDCategories> laCategories, OnItemClickListener foListener) {
        this.laCategories = laCategories;
        this.foListener = foListener;
    }

    @NonNull
    @Override
    public VHCategories onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHCategories(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ssdd_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VHCategories holder, int position) {

        holder.mtv_category.setText(laCategories.get(position).getsDescript());

        holder.rb_rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    foListener.OnRate(rating);
                }
            }
        });

        holder.ib_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnDetails(laCategories.get(position));
            }
        });

        holder.ib_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnCamera();
            }
        });
    }

    @Override
    public int getItemCount() {
        return laCategories.size();
    }

    public interface OnItemClickListener{
        void OnRate(float ffTotalRate);
        void OnCamera();
        void OnDetails(ESSDDCategories foCategory);
    }

    public static class VHCategories extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_category;
        private RatingBar rb_rate;
        private TextInputEditText tie_remarks;
        private ImageButton ib_camera, ib_info;

        public VHCategories(@NonNull View itemView) {
            super(itemView);

            mtv_category = itemView.findViewById(R.id.mtv_category);
            rb_rate = itemView.findViewById(R.id.rb_rate);
            tie_remarks = itemView.findViewById(R.id.tie_remarks);
            ib_camera = itemView.findViewById(R.id.rb_rate);
            ib_info = itemView.findViewById(R.id.ib_info);
        }
    }
}
