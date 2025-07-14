/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 9/16/21, 10:47 AM
 * project file last modified : 9/16/21, 10:47 AM
 */

package org.rmj.guanzongroup.ganado.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.utils.ImageFileManager;
import org.rmj.guanzongroup.ganado.R;
import org.rmj.guanzongroup.ganado.ViewModel.MCHondaImages;
import org.rmj.guanzongroup.ganado.ViewModel.MCKawasakiImages;
import org.rmj.guanzongroup.ganado.ViewModel.MCSuzukiImages;
import org.rmj.guanzongroup.ganado.ViewModel.MCYamahaImages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductSelectionAdapter extends RecyclerView.Adapter<ProductSelectionAdapter.ApplicationViewHolder> {

    private List<EMcModel> poModel;
    private List<EMcModel> poModelFilteredList;
    private OnModelClickListener listener;

    public interface OnModelClickListener {
        void OnClick(String ModelID, String BrandID, String ImgLink);
    }

    public ProductSelectionAdapter(List<EMcModel> poModel, OnModelClickListener listener) {
        this.poModel = poModel;
        this.listener = listener;
        this.poModelFilteredList = new ArrayList<>(poModel);
    }


    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mcmodelgrid_item, parent, false);
        return new ApplicationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {

        String lsImgUrl = "";
         //loModel = poModel.get(position);
        EMcModel loModel = poModelFilteredList.get(position);
        holder.itemName.setText(loModel.getModelNme());
//        holder.setImage(loModel.getModelIDx(),loModel.getBrandIDx());
        try {

            switch (loModel.getBrandIDx()) {

                case "M0W1001"://honda
                    lsImgUrl = MCHondaImages.getModelImageResource(loModel.getModelIDx());
                    break;
                case "M0W1002"://suzuki
                    lsImgUrl = MCSuzukiImages.getModelImageResource(loModel.getModelIDx());
                    break;
                case "M0W1003": // yamaha
                    lsImgUrl = MCYamahaImages.getModelImageResource(loModel.getModelIDx());
                    break;
                case "M0W1009": // kawasaki
                    lsImgUrl = MCKawasakiImages.getModelImageResource(loModel.getModelIDx());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalLsImgUrl = lsImgUrl;
        if (lsImgUrl != "") {
            ImageFileManager.LoadImageToView(lsImgUrl, holder.itemImg);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClick(
                        loModel.getModelIDx(),
                        loModel.getBrandIDx(),
                        finalLsImgUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return poModelFilteredList.size();
    }


    public class ApplicationViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView itemName;

        ShapeableImageView itemImg;


        public ApplicationViewHolder(@NonNull View view) {
            super(view);
            itemName = view.findViewById(R.id.itemName);
            itemImg = view.findViewById(R.id.imagemodel0);
        }
    }
    public void filterModel(String query) {
        poModelFilteredList.clear();

        if (query.isEmpty()) {
            poModelFilteredList.addAll(poModel);
        } else {
            query = query.toLowerCase(Locale.getDefault());

            for (EMcModel model : poModel) {
                if (model.getModelNme().toLowerCase(Locale.getDefault()).contains(query)) {
                    poModelFilteredList.add(model);
                }
            }
        }

        notifyDataSetChanged();
    }



}