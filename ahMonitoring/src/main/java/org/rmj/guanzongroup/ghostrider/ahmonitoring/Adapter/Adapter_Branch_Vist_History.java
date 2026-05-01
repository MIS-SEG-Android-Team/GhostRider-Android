package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.List;

public class Adapter_Branch_Vist_History extends RecyclerView.Adapter<Adapter_Branch_Vist_History.VHBranchHistory> {

    private List<DBranchVisitMaster.MasterHistory> laMAsterList;
    private OnItemListener loListener;

    public Adapter_Branch_Vist_History(List<DBranchVisitMaster.MasterHistory> faMAsterList, OnItemListener foListener){
        laMAsterList = faMAsterList;
        loListener = foListener;
    }

    public interface OnItemListener{
        void OnSelectMaster(DBranchVisitMaster.MasterHistory foTrans);
    }

    @NonNull
    @Override
    public VHBranchHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHBranchHistory(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_branch_visit_history, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VHBranchHistory holder, int position) {

        holder.mtv_branch.setText(laMAsterList.get(position).sBranchNm);
        switch (Integer.parseInt(laMAsterList.get(position).cTranStat)){
            case 0:
                holder.mtv_branch.setText("Open");
                break;
            case 1:
                holder.mtv_branch.setText("Closed");
                break;
            case 2:
                holder.mtv_branch.setText("Posted");
                break;
            default:
                holder.mtv_branch.setText("Unknown");
        }
        holder.mtv_transno.setText(laMAsterList.get(position).sTransNox);
        holder.mtv_date.setText(laMAsterList.get(position).dTransact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loListener.OnSelectMaster(laMAsterList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return laMAsterList.size();
    }

    public static class VHBranchHistory extends RecyclerView.ViewHolder{

        public View view;
        public MaterialTextView mtv_branch, mtv_status, mtv_transno, mtv_date;

        public VHBranchHistory(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_branch = itemView.findViewById(R.id.mtv_branch);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_transno = itemView.findViewById(R.id.mtv_transno);
            mtv_date = itemView.findViewById(R.id.mtv_date);
        }

    }
}
