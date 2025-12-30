package org.rmj.guanzongroup.pacitareward.Adapter.PacitaReward;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.pojo.BranchRate;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewHolder.PacitaReward.RecyclerViewHolder_RecordDetails;

import java.util.List;

public class RecyclerViewAdapter_RecordDetails extends RecyclerView.Adapter<RecyclerViewHolder_RecordDetails> {
    private final List<BranchRate> poEvaluations;

    public RecyclerViewAdapter_RecordDetails(List<BranchRate> foEvaluations){
        this.poEvaluations = foEvaluations;
    }

    @NonNull
    @Override
    public RecyclerViewHolder_RecordDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_evaluation_details, parent, false);
        return new RecyclerViewHolder_RecordDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder_RecordDetails holder, int position) {
        String cPasRate = poEvaluations.get(position).getcPasRatex();
        String scritera = poEvaluations.get(position).getsRateName();

        holder.mtv_criteria.setText(scritera);

       if (cPasRate.equals("0")){
            holder.siv_rate.setBackgroundResource(R.drawable.ic_baseline_sad);
        } else if (cPasRate.equals("1")) {
            holder.siv_rate.setBackgroundResource(R.drawable.ic_baseline_smile);
        }else {
           holder.siv_rate.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        int lnListSize = poEvaluations.size();
        return lnListSize;
    }
}
