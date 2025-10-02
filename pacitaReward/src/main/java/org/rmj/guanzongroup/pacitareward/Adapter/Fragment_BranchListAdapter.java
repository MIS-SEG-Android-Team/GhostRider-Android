package org.rmj.guanzongroup.pacitareward.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Fragment_BranchListAdapter extends FragmentStateAdapter {
    public Fragment[] fragments;
    public Fragment_BranchListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void initFragments(Fragment[] fragments){
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }
    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
