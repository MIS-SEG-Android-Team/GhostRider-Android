package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article1;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article2;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article3;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article4;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article5;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article6;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article7;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article8;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity.Activity_Article9;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Item;
import org.rmj.g3appdriver.R;

import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {
    Context context;
    List<Item> items;

    public itemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
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
        holder.imageView.setImageResource(resId);
        holder.title.setText(item.title);
        holder.description.setText(item.description);
        holder.subtitle.setOnClickListener(v->{

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        View dialogView = inflater.inflate(R.layout.custom_layout, null);

        builder.setView(dialogView);
        MaterialTextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText(item.subtitle);
        AlertDialog alertDialog = builder.create();

        // Optional: Make it non-cancelable
        alertDialog.setCancelable(false);

        // Set button click listener
        MaterialButton button = dialogView.findViewById(R.id.dialog_button);
        button.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Remove default corners
        alertDialog.show();
  });
    holder.itemView.setOnClickListener(v -> {

        if (item.title.equals("Article 1")) {
            Intent intent = new Intent(context, Activity_Article1.class);
            context.startActivity(intent);
        }
        else if (item.title.equals("Article 2")) {
            Intent intent = new Intent(context, Activity_Article2.class);
            context.startActivity(intent);}
        else if (item.title.equals("Article 3")) {
                Intent intent = new Intent(context, Activity_Article3.class);
                context.startActivity(intent);}
        else if (item.title.equals("Article 4")) {
                    Intent intent = new Intent(context, Activity_Article4.class);
                    context.startActivity(intent);}
        else if (item.title.equals("Article 5")) {
                        Intent intent = new Intent(context, Activity_Article5.class);
                        context.startActivity(intent);
        }
        else if (item.title.equals("Article 6")) {
            Intent intent = new Intent(context, Activity_Article6.class);
            context.startActivity(intent);
        }
        else if (item.title.equals("Article 7")) {
            Intent intent = new Intent(context, Activity_Article7.class);
            context.startActivity(intent);
        }
        else if (item.title.equals("Article 8")) {
            Intent intent = new Intent(context, Activity_Article8.class);
            context.startActivity(intent);
        }
        else if (item.title.equals("Article 9")) {
            Intent intent = new Intent(context, Activity_Article9.class);
            context.startActivity(intent);
        }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
