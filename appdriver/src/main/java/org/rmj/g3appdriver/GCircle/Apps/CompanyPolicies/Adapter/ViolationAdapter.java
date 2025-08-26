package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Violation;
import org.rmj.g3appdriver.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViolationAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Violation> violations;
    private String searchQuery = "";

    public ViolationAdapter(Context context, List<Violation> violations) {
        this.context = context;
        this.violations = violations;
    }

    public void updateList(List<Violation> newList) {
        this.violations = newList;
        notifyDataSetChanged();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return violations.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return violations.get(groupPosition).getActions().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return violations.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return new ArrayList<>(violations.get(groupPosition).getActions().entrySet()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_group, parent, false);
        }

        TextView title = convertView.findViewById(R.id.groupTitle);
        ImageView arrow = convertView.findViewById(R.id.arrowIcon);

        String groupTitle = violations.get(groupPosition).getName();
        SpannableString spannableTitle = new SpannableString(groupTitle);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerTitle = groupTitle.toLowerCase();
            String lowerQuery = searchQuery.toLowerCase();

            int index = lowerTitle.indexOf(lowerQuery);
            while (index != -1) {
                spannableTitle.setSpan(new StyleSpan(Typeface.BOLD),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int orangeColor = ContextCompat.getColor(context, R.color.guanzon_orange);
                spannableTitle.setSpan(new ForegroundColorSpan(orangeColor),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                index = lowerTitle.indexOf(lowerQuery, index + searchQuery.length());
            }
        }

        title.setText(spannableTitle);
        arrow.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView article = convertView.findViewById(R.id.articleTitle);
        TextView content = convertView.findViewById(R.id.articleContent);

        Map.Entry<String, String> entry = (Map.Entry<String, String>) getChild(groupPosition, childPosition);
        String articleTitle = entry.getKey();
        String articleContent = entry.getValue();

        // Highlight in article title
        SpannableString spannableArticle = new SpannableString(articleTitle);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerTitle = articleTitle.toLowerCase();
            String lowerQuery = searchQuery.toLowerCase();

            int index = lowerTitle.indexOf(lowerQuery);
            while (index != -1) {
                spannableArticle.setSpan(new StyleSpan(Typeface.BOLD),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int orangeColor = ContextCompat.getColor(context, R.color.guanzon_orange);
                spannableArticle.setSpan(new ForegroundColorSpan(orangeColor),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                index = lowerTitle.indexOf(lowerQuery, index + searchQuery.length());
            }
        }
        article.setText(spannableArticle);

        // Highlight in article content
        SpannableString spannableContent = new SpannableString(articleContent);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerContent = articleContent.toLowerCase();
            String lowerQuery = searchQuery.toLowerCase();

            int index = lowerContent.indexOf(lowerQuery);
            while (index != -1) {
                spannableContent.setSpan(new StyleSpan(Typeface.BOLD),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int orangeColor = ContextCompat.getColor(context, R.color.guanzon_orange);
                spannableContent.setSpan(new ForegroundColorSpan(orangeColor),
                        index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                index = lowerContent.indexOf(lowerQuery, index + searchQuery.length());
            }
        }
        content.setText(spannableContent);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
