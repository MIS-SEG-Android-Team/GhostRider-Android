package org.rmj.guanzongroup.ghostrider.epacss.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMGuide;
import org.rmj.guanzongroup.ghostrider.epacss.adapter.RecyclerviewUserGuideAdapter;

import java.util.List;
import java.util.Objects;


public class Activity_Manual extends AppCompatActivity {

    private VMGuide mViewmodel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private LinearLayout layout_nodisp;
    private RecyclerView rcv_guides;
    private Toolbar toolbar;

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual);

        mViewmodel = new ViewModelProvider(this).get(VMGuide.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);

        InitViews();
        InitObservers();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guidelines, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        if (item.getItemId() == R.id.action_add_guideline){
            
        } else if (item.getItemId() == R.id.action_download_guideline) {

            poDialog.initDialog("User Guide", "Downloading Guides", false);

            mViewmodel.DownloadGuides(new VMGuide.OnDownloadGuides() {
                @Override
                public void OnDownloading() {
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                }

                @Override
                public void OnFailed(String message) {
                    poDialog.dismiss();

                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnDialogButtonCallback callback){

        poMessage.initDialog();
        poMessage.setTitle("Daily Collection Plan");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.OnPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.OnNegative();

            });
        }

        poMessage.show();
    }
    private void InitViews(){

        layout_nodisp = findViewById(R.id.layout_nodisp);
        rcv_guides = findViewById(R.id.rcv_guides);
        toolbar = findViewById(R.id.toolbar_collectionList);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
    }
    private void InitObservers(){

        mViewmodel.GetGuides().observe(Activity_Manual.this, new Observer<List<EGuides>>() {
            @Override
            public void onChanged(List<EGuides> eGuides) {

                if (eGuides.size() > 0){

                    layout_nodisp.setVisibility(GONE);
                    rcv_guides.setVisibility(VISIBLE);

                    InitAdapter(eGuides);
                }else {
                    layout_nodisp.setVisibility(VISIBLE);
                    rcv_guides.setVisibility(GONE);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void InitAdapter(List<EGuides> loGuides){

        RecyclerviewUserGuideAdapter loAdapter = new RecyclerviewUserGuideAdapter(loGuides, new RecyclerviewUserGuideAdapter.OnViewGuide() {
            @Override
            public void OnView(EGuides loGuide) {

                Intent loIntent = new Intent(Activity_Manual.this, Activity_PDFViewer.class);
                loIntent.putExtra("pdf_url", loGuide.getsURlxx());
                startActivity(loIntent);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }
        });

        loAdapter.notifyDataSetChanged();
        rcv_guides.setAdapter(loAdapter);
        rcv_guides.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}