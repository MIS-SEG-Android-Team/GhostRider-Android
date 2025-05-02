package com.guanzongroup.sales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.guanzongroup.sales.R;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;

import java.util.List;

public class RecyclerView_Barcodes extends RecyclerView.Adapter<RecyclerView_Barcodes.RecyclerView_BarcodesViewHolder> {

    private final Context context;
    private final List<EBarcode> eBarcodes;

    private onAction callback;

    public interface onAction{
        void onCheckAction(String lastIDChecked, Integer state);
        void onDelete(String barcodeIDxx);
    }

    public RecyclerView_Barcodes(Context context, List<EBarcode> eBarcodes, onAction callback) {
        this.context = context;
        this.eBarcodes = eBarcodes;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView_BarcodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new RecyclerView_BarcodesViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_barcode_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView_BarcodesViewHolder holder, int position) {

        holder.mtv_barcode.setText(eBarcodes.get(position).getBarcode());

        switch (eBarcodes.get(position).getChecked()){

            case 0:
                holder.mcb_barcodecb.setCheckedState(MaterialCheckBox.STATE_UNCHECKED);
                break;
            case 1:
                holder.mcb_barcodecb.setCheckedState(MaterialCheckBox.STATE_CHECKED);
                break;
        }

        holder.mcb_barcodecb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    callback.onCheckAction(eBarcodes.get(position).getBarcodeIdxx(), 1);
                }else {
                    callback.onCheckAction(eBarcodes.get(position).getBarcodeIdxx(), 0);
                }
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDelete(eBarcodes.get(position).getBarcodeIdxx());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eBarcodes.size();
    }

    public static class RecyclerView_BarcodesViewHolder extends RecyclerView.ViewHolder{

        private MaterialCheckBox mcb_barcodecb;
        private MaterialTextView mtv_barcode;
        private ImageButton btn_delete;

        public RecyclerView_BarcodesViewHolder(@NonNull View itemView) {
            super(itemView);

            mcb_barcodecb = itemView.findViewById(R.id.mcb_barcodecb);
            mtv_barcode = itemView.findViewById(R.id.mtv_barcode);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
