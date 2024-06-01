package org.rmj.guanzongroup.ghostrider.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.experimental.UseExperimental;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleStatus;
import org.rmj.guanzongroup.ghostrider.Adapter.FragmentAdapter;
import org.rmj.guanzongroup.ghostrider.Dialog.DialogMechanics;
import org.rmj.guanzongroup.ghostrider.Dialog.DialogRaffle;
import org.rmj.guanzongroup.ghostrider.R;
import org.rmj.guanzongroup.ghostrider.ViewModel.VMPanaloContainer;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_PanaloContainer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_PanaloContainer extends Fragment {

    private VMPanaloContainer mViewModel;
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MaterialCardView btnMechanics;

    private Fragment[] fragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_PanaloContainer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_PanaloContainer.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_PanaloContainer newInstance(String param1, String param2) {
        Fragment_PanaloContainer fragment = new Fragment_PanaloContainer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewModel = new ViewModelProvider(requireActivity()).get(VMPanaloContainer.class);
        view= inflater.inflate(R.layout.fragment_panalo_container, container, false);
        fragment = new Fragment[]{new Fragment_Raffle(), new Fragment_Rewards()};
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.ViewPager);
        btnMechanics = view.findViewById(R.id.btnMechanics);

        tabLayout.addTab(tabLayout.newTab().setText("Raffle Draw"));
        tabLayout.addTab(tabLayout.newTab().setText("Redemption"));

        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragment));

        if(fragment.length > 1){
            tabLayout.setupWithViewPager(viewPager);
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Raffle Draw");
            Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Redemption");
        }

        mViewModel.GetRaffleStatus().observe(requireActivity(), new Observer<ERaffleStatus>() {
            @Override
            public void onChanged(ERaffleStatus status) {
                try{
                    if(status == null) {
                        return;
                    }

                    InitializeRaffleBadge(status.getHasRffle());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initBtnMechanics();

        DialogRaffle diagRaffle = new DialogRaffle(requireActivity());
        diagRaffle.show();

        return view;
    }

    @UseExperimental(markerClass = ExperimentalBadgeUtils.class)
    private void InitializeRaffleBadge(int status){
        TabLayout.Tab loTab = tabLayout.getTabAt(0);
        BadgeDrawable loBadge = loTab.getOrCreateBadge();
        View loMenuView = tabLayout.getTabAt(0).getCustomView();
        switch (status){
            case 2:
            case 1:
                BadgeUtils.attachBadgeDrawable(loBadge, loMenuView);
                break;
            default:
                loTab.removeBadge();
                break;
        }

        if(!requireActivity().getIntent().hasExtra("panalo")){
            return;
        }

        tabLayout.getTabAt(0).select();
    }
    private  void initBtnMechanics(){
        btnMechanics.setOnClickListener(new View.OnClickListener() {
            Intent loIntent;
            @Override
            public void onClick(View view) {
                DialogMechanics dialogMechanics = new DialogMechanics(getActivity());
                dialogMechanics.show();
                Toast.makeText(requireActivity(),"Mechanics", Toast.LENGTH_SHORT).show();
            }
        });
    }
}