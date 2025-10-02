/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.samsungKnox
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.samsungknox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_Activate;
import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_GetOfflinePin;
import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_GetPin;
import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_GetStatus;
import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_Unlock;
import org.rmj.guanzongroup.ghostrider.samsungknox.Fragment.Fragment_Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Knox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knox);

        int position = getIntent().getIntExtra("knox", 0);

        Toolbar toolbar = findViewById(R.id.toolbar_knox);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewPager2 viewPager = findViewById(R.id.viewpager_knox);
        FragmentAdapter loAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());

        loAdapter.initFragments(getKnoxFragment(position));
        viewPager.setAdapter(loAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    private static class FragmentAdapter extends FragmentStateAdapter {
        private List<Fragment> fragment = new ArrayList<>();
        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }
        public void initFragments(Fragment fragment){
            this.fragment.add(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragment.get(position);
        }
        @Override
        public int getItemCount() {
            return fragment.size();
        }
    }
    private Fragment getKnoxFragment(int position){
        Fragment[] knoxFragments = {
                new Fragment_Upload(),
                new Fragment_Activate(),
                new Fragment_Unlock(),
                new Fragment_GetPin(),
                new Fragment_GetOfflinePin(),
                new Fragment_GetStatus()};
        return knoxFragments[position];
    }
}