package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Item;
import org.rmj.g3appdriver.R;

import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {
    private Context context;
    private List<Item> items;
    private OnViewArticle callback;

    public interface OnViewArticle{
        void OnViewSubtitle(String subTitlexx);
        void OmViewArticle(String sCodexx, String sTitlexx, int sImagexx, String sSubTitlexx);
    }

    public itemAdapter(Context context, List<Item> items, OnViewArticle callback) {
        this.context = context;
        this.items = items;
        this.callback = callback;
    }

    public void updateList(List<Item> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);
        int resId = context.getResources().getIdentifier(item.image.replace(".png", ""), "drawable", context.getPackageName());

        holder.title.setText(item.title);
        holder.description.setText(item.description);
        holder.imageView.setImageResource(resId);

        holder.subtitle.setOnClickListener(v->{
            callback.OnViewSubtitle(item.subtitle);
        });

        holder.itemView.setOnClickListener(v -> {
            callback.OmViewArticle(item.sCodexx, item.title, resId, item.subtitle);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,subtitle;
        TextView title,  description;

        public ViewHolder(View view) {

            super(view);

            imageView = view.findViewById(R.id.cardImage);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.infoIcon);
            description = view.findViewById(R.id.description);
        }
    }
}
