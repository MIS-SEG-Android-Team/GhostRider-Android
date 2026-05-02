/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 5/5/21 1:27 PM
 * project file last modified : 5/5/21 1:27 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.rmj.guanzongroup.ghostrider.epacss.R;

import java.util.ArrayList;
import java.util.List;

public class NewsEventsAdapter extends RecyclerView.Adapter<NewsEventsAdapter.CustomViewHolder> {

    private Context context;
    private List<NewsEventsModel> items;

    public NewsEventsAdapter(Context context, List<NewsEventsModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.news_events, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final NewsEventsModel newsModel = items.get(position);
        holder.itemTitle.setText(items.get(position).getTitle());
        holder.itemDay.setText(items.get(position).getNewsDay());
        holder.itemMonth.setText(items.get(position).getNewsMonth());
        holder.itemPostedBy.setText(items.get(position).getNewsPostedBy());
        holder.setImage(newsModel.getImage());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView itemImage;
        private MaterialTextView itemTitle, itemDay, itemMonth, itemPostedBy;

        public CustomViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.newsImg);
            itemTitle = view.findViewById(R.id.newsTitle);
            itemDay = view.findViewById(R.id.newsDay);
            itemMonth = view.findViewById(R.id.newsMonth);
            itemPostedBy = view.findViewById(R.id.newsPostedBy);
        }
        public void setImage(String image) {
            Picasso.get().load(image).into(itemImage);
        }
    }
    
}
