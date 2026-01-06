package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.ViewPagerProperty;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategory_Images;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.util.List;


public class Activity_SSDD_Category_Details extends AppCompatActivity {

    private VMSSDEvaluation mViewModel;
    private MessageBox poMessage;

    private MaterialTextView mtv_category, mtv_transnox, mtv_evaluated, mtv_remarks, mtv_rate, mtv_noimage;
    private ViewPager2 vpage_images;
    private MaterialTextView mtv_company, mtv_version, mtv_dev;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_category_details);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poMessage = new MessageBox(this);

        mtv_category = findViewById(R.id.mtv_category);
        mtv_transnox = findViewById(R.id.mtv_transnox);
        mtv_evaluated = findViewById(R.id.mtv_evaluated);
        mtv_remarks = findViewById(R.id.mtv_remarks);
        mtv_noimage = findViewById(R.id.mtv_noimage);
        mtv_rate = findViewById(R.id.mtv_rate);
        vpage_images = findViewById(R.id.rcv_images);
        mtv_company = findViewById(R.id.mtv_company);
        mtv_version = findViewById(R.id.mtv_version);
        mtv_dev = findViewById(R.id.mtv_dev);

        if (!getIntent().hasExtra("transnox")) {

            InitMessage(1, R.drawable.baseline_error_24, "No transaction number has been detected", "Okay", "", new onMessageButton() {
                @Override
                public void onPositive() { finish(); }

                @Override
                public void onNegative() {}
            });
        }else if(!getIntent().hasExtra("categoryid")){

            InitMessage(1, R.drawable.baseline_error_24, "No category id has been detected", "Okay", "", new onMessageButton() {
                @Override
                public void onPositive() { finish(); }

                @Override
                public void onNegative() {}
            });
        }else {

            mtv_company.setText(R.string.sLblCompName);
            mtv_version.setText(R.string.lblBuildVersion);
            mtv_dev.setText(R.string.sLblCopyright);

            InitObservers();
        }
    }

    private void InitObservers(){

        mViewModel.GetCategoryDetail(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("categoryid")).observe(Activity_SSDD_Category_Details.this, new Observer<ESSDDetail>() {
            @Override
            public void onChanged(ESSDDetail essdDetail) {

                if (essdDetail == null){

                    InitMessage(1, R.drawable.baseline_error_24, "No details found for this category", "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                    return;
                }

                mtv_category.setText(mViewModel.GetCategory(essdDetail.getsCategrID()).getsDescript());
                mtv_transnox.setText(essdDetail.getsTransNox());
                mtv_evaluated.setText(essdDetail.getdEvaluate());
                mtv_remarks.setText(essdDetail.getsRemarksx());
                mtv_rate.setText(essdDetail.getnRatingxx());

                mViewModel.GetCategoryImages(essdDetail.getsTransNox(), essdDetail.getsCategrID()).observe(Activity_SSDD_Category_Details.this, new Observer<List<ESSDDImages>>() {
                    @Override
                    public void onChanged(List<ESSDDImages> essddImages) {

                        if (essddImages == null){
                            mtv_noimage.setVisibility(View.VISIBLE);
                            vpage_images.setVisibility(View.GONE);
                            return;
                        }

                        mtv_noimage.setVisibility(View.GONE);
                        vpage_images.setVisibility(View.VISIBLE);

                        Adapter_SSDDCategory_Images adapter = new Adapter_SSDDCategory_Images(essddImages, vpage_images);
                        vpage_images.setAdapter(adapter);

                        adapter.notifyDataSetChanged();

                        /**
                         * Set viewpager properties and design
                         * Custom library for designing viewpager.
                         * Add new designs , for future layout viewpager design
                         * Guillier 03/28/2025
                         **/
                        ViewPagerProperty loViewPagerProperty = new ViewPagerProperty(vpage_images);

                        //todo get device density width
                        int densWidth = (int) (vpage_images.getResources().getDisplayMetrics().xdpi);

                        //todo formula to retain padding (density width - ( 30% of density width ))
                        loViewPagerProperty.initSliderPadding(
                                new ViewPagerProperty.Padding_Property((int) (densWidth - (densWidth * 0.3)), (int) (densWidth - (densWidth * 0.3)),
                                        0, 0, false, false, 3));

                        loViewPagerProperty.initSliderPageTransformer();

                    }
                });

            }
        });
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, onMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("SSDD Evaluation");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.onPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.onNegative();

            });
        }

        poMessage.show();
    }
}