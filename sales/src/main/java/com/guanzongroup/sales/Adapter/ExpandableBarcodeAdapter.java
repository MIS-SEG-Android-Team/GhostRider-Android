package com.guanzongroup.sales.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.guanzongroup.sales.R;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcodeDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableBarcodeAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<EBarcode> barcodeMasterList;
    private final HashMap<String, List<EBarcodeDetail>> barcodeDetailList;
    private OnCheckedListener callback;

    public interface OnCheckedListener{
        void OnChecked(Integer checkStatus, String barcode);
    }

    public ExpandableBarcodeAdapter(Context context, List<EBarcode> barcodeMasterList, HashMap<String, List<EBarcodeDetail>> barcodeDetailList, OnCheckedListener listener){
        this.context = context;
        this.barcodeMasterList = barcodeMasterList;
        this.barcodeDetailList = barcodeDetailList;
        this.callback = listener;
    }

    @Override
    public int getGroupCount() {
        return barcodeMasterList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(barcodeDetailList.get(barcodeMasterList.get(groupPosition).getBarcodeIdxx())).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return barcodeMasterList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(barcodeDetailList.get(barcodeMasterList.get(groupPosition).getBarcodeIdxx())).get(childPosition);
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_expanded_barcode_list_header, parent, false);

        MaterialTextView mtv_barcode = convertView.findViewById(R.id.mtv_barcode);
        MaterialCheckBox mcb_barcodecb = convertView.findViewById(R.id.mcb_barcodecb);

        EBarcode master = (EBarcode) getGroup(groupPosition);

        if (master.getsDescriptxx() != null && !master.getsDescriptxx().isEmpty()){
            mtv_barcode.setText(master.getsDescriptxx());
        }else {
            mtv_barcode.setText(master.getBarcode());
        }

        mcb_barcodecb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (master.getChecked().equals(1)){
                    callback.OnChecked(1, master.getBarcodeIdxx());
                }else {
                    callback.OnChecked(0, master.getBarcodeIdxx());
                }
            }
        });

        return convertView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_expanded_barcode_list_detail, parent, false);

        MaterialTextView mtv_barcode = convertView.findViewById(R.id.mtv_barcode);
        EBarcodeDetail item = (EBarcodeDetail) getChild(groupPosition, childPosition);

        mtv_barcode.setText(item.getsDescript());

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
