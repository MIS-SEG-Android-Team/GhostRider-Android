package org.rmj.guanzongroup.ghostrider.Adapter;

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
    public void initFragments(Fragment[] mFragmentList){
        this.mFragmentList = mFragmentList;
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
