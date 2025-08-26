package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import org.rmj.g3appdriver.R;

public class Activity_Menus extends AppCompatActivity {

    private MaterialCardView mcv_item1, mcv_item2, mcv_item3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menus);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mcv_item1 = findViewById(R.id.mcv_item1);
        mcv_item2 = findViewById(R.id.mcv_item2);
        mcv_item3 = findViewById(R.id.mcv_item3);

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

    }
}