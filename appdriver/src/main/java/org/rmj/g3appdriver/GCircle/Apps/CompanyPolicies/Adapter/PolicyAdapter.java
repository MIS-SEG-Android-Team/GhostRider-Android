package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.PolicySection;
import org.rmj.g3appdriver.R;

import java.util.List;
import java.util.Locale;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.ViewHolder> {
    private Context context;
    private List<PolicySection> items;
    private String searchQuery = "";
    private boolean isExpanded = false;

    public PolicyAdapter(Context context, List<PolicySection> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, readMore;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            readMore = view.findViewById(R.id.readMore);
            description = view.findViewById(R.id.description);
        }
    }

    public void updateList(List<PolicySection> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query.trim().toLowerCase(Locale.ROOT) : "";
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_policy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PolicySection item = items.get(position);

        // Title: Bold + Underline + Highlight

        holder.title.setText(item.title);

        // Description: HTML + Highlight
        Spanned htmlDescription;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlDescription = Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlDescription = Html.fromHtml(item.description);
        }

        SpannableString spannableDesc = new SpannableString(htmlDescription);
        applyHighlight(spannableDesc, htmlDescription.toString());
        holder.description.setText(spannableDesc);

        // Read More toggle
        holder.readMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            holder.description.setMaxLines(isExpanded ? Integer.MAX_VALUE : 3);
            holder.readMore.setText(isExpanded ? "Read Less" : "Read More");
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void applyHighlight(Spannable spannable, String text) {
        if (searchQuery.isEmpty()) return;

        String lowerText = text.toLowerCase(Locale.ROOT);
        int index = lowerText.indexOf(searchQuery);

        while (index != -1) {
            int end = index + searchQuery.length();

            // Bold
            spannable.setSpan(new StyleSpan(Typeface.BOLD), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Orange color
            int orangeColor = ContextCompat.getColor(context, R.color.guanzon_orange);
            spannable.setSpan(new ForegroundColorSpan(orangeColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            index = lowerText.indexOf(searchQuery, end);
        }
    }
}
