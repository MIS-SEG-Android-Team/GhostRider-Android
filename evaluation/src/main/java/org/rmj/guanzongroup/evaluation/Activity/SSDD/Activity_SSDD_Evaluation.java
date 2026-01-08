package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.guanzongroup.evaluation.Fragments.SSDD.Fragment_SSDD_Evaluation;
import org.rmj.guanzongroup.evaluation.R;

public class Activity_SSDD_Evaluation extends AppCompatActivity {

    private ViewPager2 vpage_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_evaluation);

        vpage_list = findViewById(R.id.vpage_list);

        InitFragment();
    }

    private void InitFragment(){

        Fragment_SSDD_Evaluation loFragment = new Fragment_SSDD_Evaluation();

        Log.d("GAGANA KABA?", loFragment.IsViewReady().toString());

        //inialize adapter for fragment
        if (loFragment.IsViewReady()){

            loFragment.InitFragment(new Fragment_SSDD_Evaluation.OnSSDDItemClick() {
                @Override
                public void OnSelectDepartment(ESSDDepartments loDepartment) {
                    Bundle loBundle = new Bundle();
                    loBundle.putString("dept_id", loDepartment.getsDeptIDxx());

                    loFragment.setArguments(loBundle);
                }

                @Override
                public void OnSelectEvaluation(ESSDDMaster loMaster) {

                }
            });
        }

        //initialize adapter
        Fragment_SSDD_Adapter loAdapter = new Fragment_SSDD_Adapter(getSupportFragmentManager(), getLifecycle());
        loAdapter.InitFragment(loFragment);
        vpage_list.setAdapter(loAdapter);

        Log.d("GAGANA KABA?", loFragment.IsViewReady().toString());
    }

    private static class Fragment_SSDD_Adapter extends FragmentStateAdapter {

        private Fragment loFragment;

        public Fragment_SSDD_Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void InitFragment(Fragment loFragment){
            this.loFragment = loFragment;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return loFragment;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}