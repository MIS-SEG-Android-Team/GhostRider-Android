package org.rmj.guanzongroup.evaluation.Adapter.SSDD;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;
import org.rmj.guanzongroup.evaluation.R;

import java.util.List;

public class Adapter_SSDDCategory_Images extends RecyclerView.Adapter<Adapter_SSDDCategory_Images.VHSSDDCategory_Images>{

    private List<ESSDDImages> laImages;
    private ViewPager2 loViewPager;

    public Adapter_SSDDCategory_Images(List<ESSDDImages> laImages, ViewPager2 loViewPager){
        this.laImages = laImages;
        this.loViewPager = loViewPager;
    }

    @NonNull
    @Override
    public VHSSDDCategory_Images onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category_details, parent, false);
        return new VHSSDDCategory_Images(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHSSDDCategory_Images holder, int position) {

        Glide.with(holder.itemView)
                .load(laImages.get(position))
                .fitCenter()
                .into(holder.img_ssdd);

        if (loViewPager.getCurrentItem() == position){
            holder.img_ssdd.setElevation(1f);
            holder.img_ssdd.setAlpha(1f);
        }else {
            holder.img_ssdd.setElevation(0.15f);
            holder.img_ssdd.setAlpha(0.8f);
        }

        holder.img_ssdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return laImages.size();
    }

    public class VHSSDDCategory_Images extends RecyclerView.ViewHolder {

        private ImageButton img_ssdd;

        public VHSSDDCategory_Images(@NonNull View itemView) {
            super(itemView);

            img_ssdd = itemView.findViewById(R.id.img_ssdd);
        }
    }
}
