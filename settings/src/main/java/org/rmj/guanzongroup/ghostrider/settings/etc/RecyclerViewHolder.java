package org.rmj.guanzongroup.ghostrider.settings.etc;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.guanzongroup.ghostrider.settings.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView tvHeader;
    public TextView tvDetails;
    public TextView tvFixedConcerns;
    View view;
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        tvHeader = itemView.findViewById(R.id.lblLogHeader);
        tvDetails = itemView.findViewById(R.id.lblLogDescr);
        tvFixedConcerns = itemView.findViewById(R.id.lblLogFixedConcerns);
        view = itemView;
    }
}
