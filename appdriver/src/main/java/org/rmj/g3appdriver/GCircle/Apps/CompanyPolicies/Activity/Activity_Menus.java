package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.R;

public class Activity_Menus extends AppCompatActivity {

    private MaterialCardView mcv_item1, mcv_item2, mcv_item3;
    private FloatingActionButton fabDownload;
    private VMGuide mViewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menus);

        mcv_item1 = findViewById(R.id.mcv_item1);
        mcv_item2 = findViewById(R.id.mcv_item2);
        mcv_item3 = findViewById(R.id.mcv_item3);
        fabDownload = findViewById(R.id.btn_download);

        mViewmodel = new ViewModelProvider(this).get(VMGuide.class);

        mcv_item1.setOnClickListener(v -> {

            Intent intent = new Intent(Activity_Menus.this, Activity_Summary.class);
            startActivity(intent);
        });
        mcv_item2.setOnClickListener(v->{
            Intent intent = new Intent(Activity_Menus.this, Activity_MainArticle.class);
            startActivity(intent);
        });
        mcv_item3.setOnClickListener(v->{
            Intent intent = new Intent(Activity_Menus.this, Activity_Definition.class);
        startActivity(intent);

        });

        fabDownload.setOnClickListener(v -> {
            mViewmodel.DownloadPolicySummary(new VMGuide.OnDownloadGuides() {
                @Override
                public void OnDownloading() {

                }

                @Override
                public void OnSuccess() {

                }

                @Override
                public void OnFailed(String message) {

                }
            });
        });

    }
}