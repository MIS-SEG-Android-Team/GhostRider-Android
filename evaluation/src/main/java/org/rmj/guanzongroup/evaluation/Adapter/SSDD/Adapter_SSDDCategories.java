package org.rmj.guanzongroup.evaluation.Adapter.SSDD;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.guanzongroup.evaluation.R;
import java.util.List;

public class Adapter_SSDDCategories extends RecyclerView.Adapter<Adapter_SSDDCategories.VHCategories> {

    private final OnItemClickListener foListener;
    private final List<SSDD_Evaluation_Categories> laCategories;

    public Adapter_SSDDCategories(List<SSDD_Evaluation_Categories> laCategories, OnItemClickListener foListener) {
        this.laCategories = laCategories;
        this.foListener = foListener;
    }

    public void SetDataList(List<SSDD_Evaluation_Categories> laParams){
        laCategories.clear();
        laCategories.addAll(laParams);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VHCategories onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHCategories(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ssdd_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VHCategories holder, int position) {

        holder.mtv_category.setText(laCategories.get(position).sCategry);

        //get the total count of all categories
        int nTotalCat = laCategories.size();

        //get the total rating per category
        double ldbl_totalPerCat = 100.0 / nTotalCat;

        //get total rate per star. for one category
        double ldbl_ratePerStar = ldbl_totalPerCat / 5.0;

        //set default values
        holder.rb_rate.setRating((float) (laCategories.get(position).ldbl_rating / ldbl_ratePerStar));

        holder.tie_remarks.setText(laCategories.get(position).lsRemarks);

        //initialize listener
        holder.rb_rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (fromUser){

                    //compute the final rate computation
                    double ldbl_finalRate = rating * ldbl_ratePerStar;

                    foListener.OnRate(position, laCategories.get(position), ldbl_finalRate);
                }
            }
        });

        holder.tie_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.isEmpty() || !holder.tie_remarks.hasFocus()){
                    holder.ib_check.setVisibility(View.GONE);
                    return;
                }
                holder.ib_check.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.ib_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnRemarks(position, laCategories.get(position), holder.tie_remarks.getText().toString());
                v.setVisibility(View.GONE);
            }
        });

        holder.ib_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnDetails(laCategories.get(position).sCategryID);
            }
        });

        holder.ib_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foListener.OnCamera();
            }
        });

        //disable objects based on master status
        switch (laCategories.get(position).lsTranStat){
            case "0":
            case "1":
                holder.rb_rate.setEnabled(true);
                holder.tie_remarks.setEnabled(true);
                holder.ib_camera.setEnabled(true);
                break;
            case "3":
                holder.rb_rate.setEnabled(false);
                holder.tie_remarks.setEnabled(false);
                holder.ib_camera.setEnabled(false);
                break;
            default:
                holder.rb_rate.setEnabled(false);
                holder.tie_remarks.setEnabled(false);
                holder.ib_camera.setEnabled(false);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return laCategories.size();
    }

    public interface OnItemClickListener{
        void OnRate(int fnPosition, SSDD_Evaluation_Categories foDetail, double ffTotalRate);
        void OnRemarks(int fnPosition, SSDD_Evaluation_Categories foDetail, String fsRemarks);
        void OnCamera();
        void OnDetails(String fsCategoryID);
    }

    public static class VHCategories extends RecyclerView.ViewHolder{

        private final MaterialTextView mtv_category;
        private final RatingBar rb_rate;
        private final ImageButton ib_check;
        private final TextInputEditText tie_remarks;
        private final ImageButton ib_camera;
        private final ImageButton ib_info;

        public VHCategories(@NonNull View itemView) {
            super(itemView);
            
            mtv_category = itemView.findViewById(R.id.mtv_category);
            rb_rate = itemView.findViewById(R.id.rb_rate);
            ib_check = itemView.findViewById(R.id.ib_check);
            tie_remarks = itemView.findViewById(R.id.tie_remarks);
            ib_camera = itemView.findViewById(R.id.ib_camera);
            ib_info = itemView.findViewById(R.id.ib_info);
        }
    }

    public static class SSDD_Evaluation_Categories{
        public String lsTranStat;
        public String sCategryID;
        public String sCategry;
        public double ldbl_rating;
        public String lsRemarks;
        public String lsEvaluated;

        public SSDD_Evaluation_Categories(String lsTranStat, String sCategryID, String sCategry, double ldbl_rating, String lsRemarks, String fsEvaluated){
            this.lsTranStat = lsTranStat;
            this.sCategryID = sCategryID;
            this.sCategry = sCategry;
            this.ldbl_rating = ldbl_rating;
            this.lsRemarks = lsRemarks;
            this.lsEvaluated = fsEvaluated;
        }
    }
}
