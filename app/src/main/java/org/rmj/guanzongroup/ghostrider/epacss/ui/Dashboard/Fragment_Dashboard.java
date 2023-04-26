package org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.experimental.UseExperimental;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.rmj.g3appdriver.dev.Database.GCircle.Entities.ERaffleStatus;
import org.rmj.guanzongroup.ghostrider.Fragment.Fragment_PanaloContainer;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Etc.FragmentAdapter;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMDashboard;
import org.rmj.guanzongroup.ghostrider.epacss.ui.home.Fragment_Associate_Dashboard;
import org.rmj.guanzongroup.ghostrider.notifications.Fragment.Fragment_Notifications;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Dashboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager viewPager;
    private VMDashboard mViewModel;
    private BottomNavigationView botNav;

    public Fragment_Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Dashboard newInstance(String param1, String param2) {
        Fragment_Dashboard fragment = new Fragment_Dashboard();
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
        mViewModel = new ViewModelProvider(requireActivity()).get(VMDashboard.class);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Fragment[] loFragments = new Fragment[]{
                new Fragment_Associate_Dashboard(),
                new Fragment_PanaloContainer(),
                new Fragment_Notifications()};


        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), loFragments));

        botNav = view.findViewById(R.id.botNav);
        botNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.nav_panalo:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.nav_notifications:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });

        mViewModel.GetRaffleStatus().observe(requireActivity(), new Observer<ERaffleStatus>() {
            @Override
            public void onChanged(ERaffleStatus status) {
                try{
                    if(status == null){
                        return;
                    }
                    int lnStatus = status.getHasRffle();
                    CreatePanaloBadge(lnStatus);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        InitializePanaloDashboard();

        mViewModel.GetAllUnreadNotificationCount().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                if(count == null){
                    return;
                }

                if(count == 0){
                    botNav.removeBadge(R.id.nav_notifications);
                    return;
                }

                BadgeDrawable loBadge = botNav.getOrCreateBadge(R.id.nav_notifications);
                loBadge.setNumber(count);
            }
        });
        return view;
    }

    @UseExperimental(markerClass = ExperimentalBadgeUtils.class)
    private void CreatePanaloBadge(int status){
        BadgeDrawable loBadge;
        Menu loMenu = botNav.getMenu();
        MenuItem loMenuItem;
        View loMenuView;
        switch (status){
            case 1:
            case 2:
                loBadge = botNav.getOrCreateBadge(R.id.nav_panalo);
                loMenuItem = loMenu.getItem(1);
                loMenuView = loMenuItem.getActionView();
                BadgeUtils.attachBadgeDrawable(loBadge, loMenuView);
                break;
            default:
                botNav.removeBadge(R.id.nav_panalo);
                break;
        }
    }

    private void InitializePanaloDashboard(){
        if(!requireActivity().getIntent().hasExtra("panalo")){
            return;
        }

        botNav.setSelectedItemId(R.id.nav_panalo);
    }
}