/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 8/16/21 1:56 PM
 * project file last modified : 8/16/21 1:55 PM
 */

package org.rmj.guanzongroup.petmanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import org.rmj.g3appdriver.etc.ActivityFragmentAdapter;
import org.rmj.guanzongroup.petmanager.R;

public class Fragment_Approval extends Fragment {
    private View view;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    public static Fragment_Approval newInstance() {
        return new Fragment_Approval();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_approval, container, false);
        setupWidgets();

        return view;
    }

    private void setupWidgets(){
        viewPager = view.findViewById(R.id.viewpager_leave_ob_fragment_view);
        tabLayout = view.findViewById(R.id.tabLayout_leave_ob_fragment_indicator);

        ActivityFragmentAdapter adapter = new ActivityFragmentAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new Fragment_LeaveApproval(), "Leave");
        adapter.addFragment(new Fragment_BusinessTripApproval(), "Business Trip");
        adapter.addFragment(new Fragment_Employee_Applications(), "Your Applications");

        viewPager.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Leave"));
        tabLayout.addTab(tabLayout.newTab().setText("Business Trip"));
        tabLayout.addTab(tabLayout.newTab().setText("Your Applications"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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