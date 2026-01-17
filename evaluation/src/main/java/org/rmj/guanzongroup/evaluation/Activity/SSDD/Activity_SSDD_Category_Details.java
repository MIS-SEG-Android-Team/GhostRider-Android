package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Activitiy.Activity_PDFViewer;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.OnSwipeListener;
import org.rmj.g3appdriver.etc.ViewPagerProperty;
import org.rmj.g3appdriver.utils.GestureListener;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategory_Images;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.util.List;


public class Activity_SSDD_Category_Details extends AppCompatActivity implements OnSwipeListener {

    private VMSSDEvaluation mViewModel;
    private MessageBox poMessage;
    private LoadDialog poDialog;

    private MaterialTextView mtv_category, mtv_transnox, mtv_evaluated, mtv_remarks, mtv_rate, mtv_noimage;
    private ViewPager2 vpage_images;
    private MaterialTextView mtv_company, mtv_version, mtv_dev,  mtv_filename, mtv_filedate;
    private ShapeableImageView siv_preview;
    private MaterialCardView mcv_details;
    private ImageButton btn_upload, btn_memo;

    private ConstraintLayout layout_imginfo;

    private GestureDetector gestureDetector;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_category_details);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poMessage = new MessageBox(this);
        poDialog = new LoadDialog(this);

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

        siv_preview = findViewById(R.id.siv_preview);
        mtv_filename = findViewById(R.id.mtv_filename);
        mtv_filedate = findViewById(R.id.mtv_filedate);
        btn_upload = findViewById(R.id.btn_upload);
        btn_memo = findViewById(R.id.btn_memo);

        mcv_details = findViewById(R.id.mcv_details);
        layout_imginfo = findViewById(R.id.layout_imginfo);

        InitActivity();
    }

    private void InitActivity(){

        try {

            gestureDetector = new GestureDetector(Activity_SSDD_Category_Details.this, new GestureListener(Activity_SSDD_Category_Details.this));

            if (!getIntent().hasExtra("transnox")) {

                InitMessage(1, R.drawable.baseline_error_24, "No transaction number has been detected", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() { finish(); }

                    @Override
                    public void onNegative() {}
                });
            }else if(!getIntent().hasExtra("categoryid")){

                InitMessage(0, R.drawable.baseline_error_24, "No category id has been detected", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() { finish(); }

                    @Override
                    public void onNegative() {}
                });
            }else {

                //download images
                mViewModel.DownloadImageCategory(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("categoryid"), new VMSSDEvaluation.OnDownloadCallback() {
                    @Override
                    public void OnLoad(String fsTitlexx, String fsMessage) {}

                    @Override
                    public void OnSuccess() {
                        Toast.makeText(Activity_SSDD_Category_Details.this, "Images imported successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        Toast.makeText(Activity_SSDD_Category_Details.this, fsMessage, Toast.LENGTH_LONG).show();
                    }
                });

                InitObservers();

                mtv_company.setText(R.string.sLblCompName);
                mtv_version.setText(R.string.lblBuildVersion);
                mtv_dev.setText(R.string.sLblCopyright);

                siv_preview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });
            }

        }catch (Exception e){
            mViewModel.SaveError(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void OnSwipeUp() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mcv_details, "Y", mcv_details.getBottom(), mcv_details.getTop());
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void OnSwipeDown() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mcv_details, "Y", mcv_details.getTop(), mcv_details.getBottom());
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void OnSwipeRight() {}

    @Override
    public void OnSwipeLeft() {}

    private void InitObservers(){

        mViewModel.GetCategoryDetail(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("categoryid")).observe(Activity_SSDD_Category_Details.this, new Observer<ESSDDetail>() {
            @Override
            public void onChanged(ESSDDetail essdDetail) {

                if (essdDetail == null){

                    InitMessage(0, R.drawable.baseline_error_24, "No details found for this category", "Okay", "", new onMessageButton() {
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
                mtv_rate.setText(essdDetail.getnRatingxx());

                if (essdDetail.getdEvaluate() == null || essdDetail.getdEvaluate().isEmpty()){
                    mtv_evaluated.setText("N/A");
                }else {
                    mtv_evaluated.setText(essdDetail.getdEvaluate());
                }

                if (essdDetail.getsRemarksx() == null || essdDetail.getsRemarksx().isEmpty()){
                    mtv_remarks.setText("N/A");
                }else {
                    mtv_remarks.setText(essdDetail.getsRemarksx());
                }

                btn_memo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ESSDDCategories loCategory = mViewModel.GetCategory(essdDetail.getsCategrID());

                        if (loCategory == null){
                            Toast.makeText(Activity_SSDD_Category_Details.this, "Category information not found", Toast.LENGTH_LONG);
                            return;
                        }

                        Intent loIntent = new Intent(Activity_SSDD_Category_Details.this, Activity_PDFViewer.class);
                        loIntent.putExtra("pdf_url", loCategory.getsMemoLink());
                        loIntent.putExtra("pdf_index", loCategory.getnPageNumber());
                        startActivity(loIntent);
                        overridePendingTransition(org.rmj.g3appdriver.R.anim.anim_intent_slide_in_right, org.rmj.g3appdriver.R.anim.anim_intent_slide_out_left);
                    }
                });
            }
        });

        mViewModel.GetCategoryImages(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("categoryid")).observe(Activity_SSDD_Category_Details.this, new Observer<List<EImageInfo>>() {
            @Override
            public void onChanged(List<EImageInfo> essddImages) {

                if (essddImages == null || essddImages.size() < 1){
                    mtv_noimage.setVisibility(View.VISIBLE);
                    vpage_images.setVisibility(View.GONE);
                    layout_imginfo.setVisibility(View.GONE);
                    return;
                }

                mtv_noimage.setVisibility(View.GONE);
                vpage_images.setVisibility(View.VISIBLE);
                layout_imginfo.setVisibility(View.VISIBLE);

                Adapter_SSDDCategory_Images adapter = new Adapter_SSDDCategory_Images(essddImages, vpage_images, new Adapter_SSDDCategory_Images.OnImageClickListener() {
                    @Override
                    public void OnImageClick(EImageInfo loImage) {

                        Glide.with(siv_preview.getRootView())
                                .load(loImage.getFileLoct())
                                .fitCenter()
                                .into(siv_preview);

                        mtv_filename.setText(loImage.getImageNme());
                        mtv_filedate.setText(loImage.getCaptured());

                        //allow uploading
                        if (!loImage.getSendStat().equals("1")){
                            btn_upload.setImageResource(R.drawable.baseline_cloud_upload_24);
                            btn_upload.setEnabled(true);

                            //set button method to upload
                            btn_upload.setOnClickListener(v ->

                                mViewModel.SubmitImage(loImage, new VMSSDEvaluation.OnSubmitCallback() {
                                    @Override
                                    public void OnLoad(String fsTitlexx, String fsMessage) {
                                        poDialog.initDialog(fsTitlexx, fsMessage, false);
                                        poDialog.show();
                                    }

                                    @Override
                                    public void OnSuccess(String fsTransnox) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnFailed(String fsMessage) {
                                        poDialog.dismiss();
                                        InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                                            @Override
                                            public void onPositive() {}

                                            @Override
                                            public void onNegative() {}
                                        });
                                    }
                                }));

                        }else {

                            //allow downloading, if file is not found
                            if (!mViewModel.IsFileExist(loImage.getFileLoct())){
                                btn_upload.setImageResource(R.drawable.ic_baseline_file_download_24);
                                btn_upload.setEnabled(false);
                                return;
                            }
                            //display icon for successful upload
                            btn_upload.setImageResource(R.drawable.baseline_check_circle_24);
                            btn_upload.setEnabled(false);
                        }
                    }
                });
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
                        new ViewPagerProperty.Padding_Property((int) (densWidth - (densWidth * 0.4)), (int) (densWidth - (densWidth * 0.4)),
                                0, 0, false, false, 3));

                loViewPagerProperty.initSliderPageTransformer();

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