package org.rmj.guanzongroup.pacitareward.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.pojo.BranchRate;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewHolder.RecyclerViewHolder_BranchRate;

import java.util.List;

public class RecyclerViewAdapter_BranchRate extends RecyclerView.Adapter<RecyclerViewHolder_BranchRate> {
    private final String TAG = RecyclerViewAdapter_BranchRate.class.getSimpleName();
    private final Context context;
    private final List<BranchRate> poRatings;
    private final onSelect mListener;

    public interface onSelect{
        void onItemSelect(String EntryNox, String result);
    }

    public RecyclerViewAdapter_BranchRate(Context context, List<BranchRate> foRatings, onSelect mListener){
        this.context = context;
        this.mListener = mListener;
        this.poRatings = foRatings;
    }

    @NonNull
    @Override
    public RecyclerViewHolder_BranchRate onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_rate_evaluation, parent, false);
        return new RecyclerViewHolder_BranchRate(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder_BranchRate holder, int position) {
        BranchRate loRate = poRatings.get(position);

        Log.d(TAG, loRate.getsRateName());

        holder.item_question.setText(loRate.getsRateName());

        holder.pass_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                mListener.onItemSelect(loRate.getsRateIDxx().toString(), "1");
            }
        });

        holder.fail_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                mListener.onItemSelect(loRate.getsRateIDxx().toString(), "0");
            }
        });

        Log.d(TAG, "Entry No. " + loRate.getsRateIDxx() + ", Result: " + loRate.getcPasRatex());

        if(loRate.getcPasRatex().equalsIgnoreCase("1")){
            holder.toggleGroup.check(R.id.pass_btn);
            holder.toggleGroup.uncheck(R.id.fail_btn);
        } else if(loRate.getcPasRatex().equalsIgnoreCase("0")){
            holder.toggleGroup.check(R.id.fail_btn);
            holder.toggleGroup.uncheck(R.id.pass_btn);
        } else {
            holder.toggleGroup.uncheck(R.id.fail_btn);
            holder.toggleGroup.uncheck(R.id.pass_btn);
        }
    }

    @Override
    public int getItemCount() {
        return poRatings.size();
    }
}
