package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.guanzongroup.evaluation.Callback.OnSSDDItemClick;
import org.rmj.guanzongroup.evaluation.Fragments.SSDD.Fragment_SSDD_Evaluation;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.util.ArrayList;
import java.util.List;

public class Activity_SSDD_Evaluation extends AppCompatActivity implements OnSSDDItemClick {

    private ViewPager2 vpage_list;

    private VMSSDEvaluation mViewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_evaluation);

        mViewmodel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        vpage_list = findViewById(R.id.vpage_list);

        try {

            //initialize default fragment list
            List<Fragment> laFragment= new ArrayList<>();
            laFragment.add(new Fragment_SSDD_Evaluation());

            //initialize adapter list
            InitFragment(laFragment);

        }catch (Exception e){
            mViewmodel.SaveError(getClass().getSimpleName(), e.getMessage());
        }
    }

    private void InitFragment(List<Fragment> foFragment){

        //initialize adapter
        Fragment_SSDD_Adapter loAdapter = new Fragment_SSDD_Adapter(getSupportFragmentManager(), getLifecycle());
        loAdapter.InitFragment(foFragment);

        vpage_list.setAdapter(loAdapter);
        vpage_list.setCurrentItem(loAdapter.getItemCount());
    }

    @Override
    public void OnSelectDepartment(ESSDDepartments loDepartment) {

        try {

            //initliaze bundle params
            Bundle loBundle = new Bundle();
            loBundle.putString("dept_id", loDepartment.getsDeptIDxx());

            List<Fragment> laFragment= new ArrayList<>();

            //initalize fragment parameter
            Fragment_SSDD_Evaluation loFragment = new Fragment_SSDD_Evaluation();
            loFragment.setArguments(loBundle);

            laFragment.add(new Fragment_SSDD_Evaluation());
            laFragment.add(loFragment);

            //initialize fragment
            InitFragment(laFragment);

        }catch (Exception e){
            mViewmodel.SaveError(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void OnSelectEvaluation(ESSDDMaster loMaster) {
        Log.d("GAGANA BA?", "GUMANA NGA");
    }

    private static class Fragment_SSDD_Adapter extends FragmentStateAdapter {

        private List<Fragment> laFragment;

        public Fragment_SSDD_Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void InitFragment(List<Fragment> foFragment){
            this.laFragment = foFragment;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return laFragment.get(position);
        }

        @Override
        public int getItemCount() {
            return laFragment.size();
        }
    }

}