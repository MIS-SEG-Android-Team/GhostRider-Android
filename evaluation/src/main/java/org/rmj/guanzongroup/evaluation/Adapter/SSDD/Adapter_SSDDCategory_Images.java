package org.rmj.guanzongroup.evaluation.Adapter.SSDD;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.guanzongroup.evaluation.R;

import java.util.List;

public class Adapter_SSDDCategory_Images extends RecyclerView.Adapter<Adapter_SSDDCategory_Images.VHSSDDCategory_Images>{

    private List<EImageInfo> laImages;
    private ViewPager2 loViewPager;
    private OnImageClickListener foCallback;

    public interface OnImageClickListener{
        void OnImageClick(EImageInfo loImage);
    }

    public Adapter_SSDDCategory_Images(List<EImageInfo> laImages, ViewPager2 loViewPager, OnImageClickListener foCallback){
        this.laImages = laImages;
        this.loViewPager = loViewPager;
        this.foCallback = foCallback;
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
                .load(laImages.get(position).getFileLoct())
                .fitCenter()
                .error(R.drawable.img_imageview_place_holder)
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
                foCallback.OnImageClick(laImages.get(position));
            }
        });

        loViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                foCallback.OnImageClick(laImages.get(loViewPager.getCurrentItem()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return laImages.size();
    }

    public static class VHSSDDCategory_Images extends RecyclerView.ViewHolder {

        private ImageButton img_ssdd;

        public VHSSDDCategory_Images(@NonNull View itemView) {
            super(itemView);

            img_ssdd = itemView.findViewById(R.id.img_ssdd);
        }
    }
}
