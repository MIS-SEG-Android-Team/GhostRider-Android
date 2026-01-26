package org.rmj.guanzongroup.onlinecreditapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.guanzongroup.onlinecreditapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MCAdapter extends ArrayAdapter<CreditOnlineApplication.MCSerial> {

    private final List<CreditOnlineApplication.MCSerial> objects;
    private final int lnLayoutResource;
    private List<CreditOnlineApplication.MCSerial> objectsFiltered;

    public MCAdapter(@NonNull Context context, int resource, @NonNull List<CreditOnlineApplication.MCSerial> objects) {
        super(context, resource, objects);

        this.lnLayoutResource = resource;
        this.objects = objects;
        this.objectsFiltered = objects;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(lnLayoutResource, parent, false);

        MaterialTextView mtv_serial = view.findViewById(R.id.mtv_serial);
        MaterialTextView mtv_engine = view.findViewById(R.id.mtv_engine);
        MaterialTextView mtv_frame = view.findViewById(R.id.mtv_frame);

        mtv_serial.setText(objectsFiltered.get(position).lsDescr);
        mtv_engine.setText(objectsFiltered.get(position).lsEngine);
        mtv_frame.setText(objectsFiltered.get(position).lsFrame);

        return view;
    }

    @Nullable
    @Override
    public CreditOnlineApplication.MCSerial getItem(int position) {
        return objectsFiltered.get(position);
    }

    @Override
    public int getCount() {
        return objectsFiltered.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.isEmpty()){
                    objectsFiltered = objects;
                }else {

                    List<CreditOnlineApplication.MCSerial> laFiltered = new ArrayList<>();
                    for (CreditOnlineApplication.MCSerial loSerial : objects){

                        if (loSerial.lsFrame.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                loSerial.lsEngine.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                loSerial.lsSerialID.toLowerCase().contains(constraint.toString().toLowerCase())){
                            laFiltered.add(loSerial);
                        }
                    }
                    objectsFiltered = laFiltered;
                }

                results.values = objectsFiltered;
                results.count = objectsFiltered.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                objectsFiltered = (List<CreditOnlineApplication.MCSerial>) results.values;
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((CreditOnlineApplication.MCSerial) resultValue).lsSerialID;
            }
        };
    }
}
