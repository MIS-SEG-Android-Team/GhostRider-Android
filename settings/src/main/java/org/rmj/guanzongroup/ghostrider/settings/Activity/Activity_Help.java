/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 7/8/21 8:36 AM
 * project file last modified : 7/8/21 8:36 AM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rmj.g3appdriver.etc.AppAssistantConfig;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMHelp;
import org.rmj.guanzongroup.ghostrider.settings.adapter.ViewPagerAdapter;
import org.rmj.guanzongroup.ghostrider.settings.etc.SettingsConstants;

public class Activity_Help extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout layout_dot;
    private TextView[] dots;
    private ViewPagerAdapter adapter;
    private Button btn_previous, btnNext;
    private VMHelp mViewModel;
    private int help;
    private int[] img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        initWidgets();
        AppConfigPreference.getInstance(Activity_Help.this).setIsAppFirstLaunch(false);

        help = getIntent().getIntExtra("help", 0);
        img = SettingsConstants.getHelpImages(Activity_Help.this,getIntent().getIntExtra("help", 0));
        adapter = new ViewPagerAdapter(Activity_Help.this, img);

        addBottomDots(0);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == img.length - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText("Got It");
                } else {
                    // still pages are left
                    btnNext.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btn_previous.setOnClickListener(v -> {
            int current = getItem(-1);
            if (current < img.length) {
                viewPager.setCurrentItem(current);
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = getItem(+1);
            if (current < img.length) {
                viewPager.setCurrentItem(current);
            } else {
                setResult(Activity.RESULT_OK);
                setTutorialFinish();
                finish();
            }
        });

    }

    public void initWidgets(){
        layout_dot = findViewById(R.id.layoutDots);
        viewPager = findViewById(R.id.viewPagerHelp);
        btn_previous = findViewById(R.id.btn_previous);
        btnNext =  findViewById(R.id.btn_next);
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
    public void addBottomDots(int page_position) {
        dots = new TextView[img.length];
        layout_dot.removeAllViews();

        if (page_position == 0){
            btn_previous.setVisibility(View.GONE);
        }else{
            btn_previous.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9673;"));
            dots[i].setTextSize(8);
            //set default dot color
            dots[i].setTextColor(getResources().getColor(R.color.material_lightgrey));
            layout_dot.addView(dots[i]);
        }
        //set active dot color
        dots[page_position].setTextColor(getResources().getColor(R.color.guanzon_orange));
    }
    private void setTutorialFinish(){
        if (help == AppConstants.INTENT_SELFIE_LOGIN) {
            AppAssistantConfig.getInstance(Activity_Help.this).setHELP_SLOGIN_NOTICE(true);
        } else if (help == AppConstants.INTENT_DOWNLOAD_DCP) {
            AppAssistantConfig.getInstance(Activity_Help.this).setHELP_DCP_DL_NOTICE(true);
        } else if (help == AppConstants.INTENT_IMPORT_DCP) {
            AppAssistantConfig.getInstance(Activity_Help.this).setHELP_DCP_IMPORT_NOTICE(true);
        }else if (help == AppConstants.INTENT_ADD_COLLECTION_DCP) {
            AppAssistantConfig.getInstance(Activity_Help.this).setASSIST_DCP_ADD(true);
        }else if (help == AppConstants.INTENT_TRANSACTION_DCP) {
            AppAssistantConfig.getInstance(Activity_Help.this).setASSIST_DCP_TRANSACTION(true);
        }else if (help == AppConstants.INTENT_DCP_POST_COLLECTION) {
            AppAssistantConfig.getInstance(Activity_Help.this).setASSIST_DCP_POST(true);
        }else if (help == AppConstants.INTENT_DCP_REMITTANCE) {
            AppAssistantConfig.getInstance(Activity_Help.this).setASSIST_DCP_REMIT(true);
        }else if (help == AppConstants.INTENT_DCP_LOG) {
            AppAssistantConfig.getInstance(Activity_Help.this).setASSIST_DCP_LOG(true);
        }else if (help == AppConstants.INTENT_DCP_LIST) {
            AppAssistantConfig.getInstance(Activity_Help.this).setHELP_DCP_NOTICE(true);
        }
    }
}