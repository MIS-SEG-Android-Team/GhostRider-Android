package org.rmj.guanzongroup.pacitareward.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.pacitareward.Adapter.Fragment_BranchListAdapter;
import org.rmj.guanzongroup.pacitareward.Fragments.Fragment_BranchList;
import org.rmj.guanzongroup.pacitareward.Fragments.Fragment_HistoryEval;
import org.rmj.guanzongroup.pacitareward.R;

import java.util.List;
import java.util.Objects;

public class Activity_BranchList extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MaterialTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homerecords);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        Fragment[] fragments ={new Fragment_BranchList(), new Fragment_HistoryEval()};

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setTitle(""); //set default title for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true); //enable the back button set on toolbar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager.setAdapter(new Fragment_BranchListAdapter(getSupportFragmentManager(), fragments));
        tabLayout.setupWithViewPager(viewPager, true);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.baseline_view_list_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.baseline_history_24);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    title.setText("Branch List");
                } else if (tab.getPosition() == 1) {
                    title.setText("Evaluation History");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}