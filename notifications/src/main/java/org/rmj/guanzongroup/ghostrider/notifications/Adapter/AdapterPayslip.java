package org.rmj.guanzongroup.ghostrider.notifications.Adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPayslip;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.guanzongroup.ghostrider.notifications.R;

import java.util.List;

public class AdapterPayslip extends RecyclerView.Adapter<AdapterPayslip.VHPaySlip> {

    private final List<DPayslip.Payslip> poPaySlip;
    private final OnDownloadPayslipListener mListener;

    public AdapterPayslip(List<DPayslip.Payslip> poPaySlip, OnDownloadPayslipListener listener) {
        this.poPaySlip = poPaySlip;
        this.mListener = listener;
    }

    public interface OnDownloadPayslipListener{
        void DownloadPayslip(String messageID, String link);
    }

    @NonNull
    @Override
    public VHPaySlip onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_payslip, parent, false);
        return new VHPaySlip(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHPaySlip holder, int position) {
        try {
            DPayslip.Payslip loPaySlip = poPaySlip.get(position);

            String lsResult = loPaySlip.sMessagex.split("\n\n")[0] + "\n\n" + loPaySlip.sMessagex.split("\n\n")[1];

            holder.lblMessage.setText(lsResult);

            int lnStart = loPaySlip.sMessagex.indexOf("[");
            int lnEnd = loPaySlip.sMessagex.indexOf("]");

            String subsTringVal = loPaySlip.sMessagex.substring(lnStart + 1, lnEnd);
            Uri uri = Uri.parse(subsTringVal);
            if (loPaySlip.cMesgStat.equalsIgnoreCase("2")) {
                holder.lnBadge.setVisibility(View.VISIBLE);
            }

            Log.d("FORMAT DATE PAYSLIP", loPaySlip.dReceived);
            holder.lblTimeStamp.setText(FormatUIText.getParseDateTime(loPaySlip.dReceived));

            holder.btnDownload.setOnClickListener(v -> {
                try {
                    mListener.DownloadPayslip(loPaySlip.sMesgIDxx, uri.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return poPaySlip.size();
    }

    public static class VHPaySlip extends RecyclerView.ViewHolder{

        public MaterialTextView lblMessage,
                                lblTimeStamp;

        public MaterialButton btnDownload;

        public LinearLayout lnBadge;

        public VHPaySlip(@NonNull View itemView) {
            super(itemView);
            lblMessage = itemView.findViewById(R.id.lblMessage);
            lblTimeStamp = itemView.findViewById(R.id.lblTimeStamp);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            lnBadge = itemView.findViewById(R.id.linear_badge);
        }
    }
}
