package org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.rmj.guanzongroup.ghostrider.Fragment.Fragment_PanaloContainer;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Etc.FragmentAdapter;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ui.home.Fragment_Home_Engineering;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMHomeEngineering;
import org.rmj.guanzongroup.ghostrider.notifications.Fragment.Fragment_Notifications;

public class Fragment_Eng_Dashboard extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private VMHomeEngineering mViewModel;
    private String mParam1;
    private String mParam2;

    public static Fragment_Eng_Dashboard newInstance(String param1, String param2) {
        Fragment_Eng_Dashboard fragment = new Fragment_Eng_Dashboard();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Fragment[] loFragments = new Fragment[]{
                new Fragment_Home_Engineering(),
                new Fragment_PanaloContainer(),
                new Fragment_Notifications()};

        ViewPager2 viewpager = view.findViewById(R.id.viewpager);
        FragmentAdapter loAdapter = new FragmentAdapter(getChildFragmentManager(), getLifecycle());
        loAdapter.initFragments(loFragments);

        loAdapter.initFragments(loFragments);
        viewpager.setAdapter(loAdapter);

        BottomNavigationView botNav = view.findViewById(R.id.botNav);
        botNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    viewpager.setCurrentItem(0);
                    break;
                case R.id.nav_panalo:
                    viewpager.setCurrentItem(1);
                    break;
                case R.id.nav_notifications:
                    viewpager.setCurrentItem(2);
                    break;
            }
            return true;
        });
        return view;
    }

}