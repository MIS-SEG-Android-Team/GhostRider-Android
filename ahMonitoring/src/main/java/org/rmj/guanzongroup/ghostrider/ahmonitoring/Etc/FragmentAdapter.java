/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 2/18/22, 2:31 PM
 * project file last modified : 2/18/22, 2:31 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Etc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    private Fragment[] mFragmentList;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void initFragments(Fragment[] flFragmentList){
        this.mFragmentList = flFragmentList;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList[position];
    }
    @Override
    public int getItemCount() {
        return mFragmentList.length;
    }
}
