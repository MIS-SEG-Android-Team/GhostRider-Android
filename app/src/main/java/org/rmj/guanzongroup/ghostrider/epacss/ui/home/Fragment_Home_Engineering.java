package org.rmj.guanzongroup.ghostrider.epacss.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.guanzongroup.com.itinerary.Adapter.AdapterItineraries;
import org.rmj.g3appdriver.GCircle.room.Entities.EItinerary;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.lib.Notifications.data.SampleData;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMHomeEngineering;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.AdapterAnnouncements;

import java.util.List;

public class Fragment_Home_Engineering extends Fragment {

    private VMHomeEngineering mViewModel;

    private View view;
    private MaterialTextView lblFullNme,
            lblEmail,
            lblUserLvl,
            lblDept,
            lblAreaNme,
            lblSyncStat;

    private RecyclerView rvAnnouncement, rvItinerary, rvLeaveApp, rvBusTripApp;

    public static Fragment_Home_Engineering newInstance() {
        return new Fragment_Home_Engineering();
    }

    @SuppressLint("NonConstantResourceId")

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        mViewModel = new ViewModelProvider(this).get(VMHomeEngineering.class);
        view = inflater.inflate(R.layout.fragment_home_engineering, container, false);

        initWidgets();
        initUserInfo();
        iniItineraries();
        initCompanyNotice();
        return view;
    }

    private void initWidgets(){
        lblFullNme = view.findViewById(R.id.lbl_EngrNme);
        lblDept = view.findViewById(R.id.lbl_userDepartment);
        lblSyncStat = view.findViewById(R.id.lblDate);
        rvAnnouncement = view.findViewById(R.id.rvCompnyAnouncemnt);
        rvItinerary = view.findViewById(R.id.rvItinerary);
        rvLeaveApp = view.findViewById(R.id.rvLeaveApp);
        rvBusTripApp = view.findViewById(R.id.rvBusTripApp);
    }

    private void initUserInfo(){
        mViewModel.getEmployeeInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                //lblEmail.setText(eEmployeeInfo.getEmailAdd());
//                lblUserLvl.setText(DeptCode.parseUserLevel(eEmployeeInfo.getEmpLevID()));
                lblFullNme.setText(eEmployeeInfo.getUserName());
                lblDept.setText(DeptCode.getDepartmentName(eEmployeeInfo.getDeptIDxx()));

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void iniItineraries(){
        mViewModel.GetItineraries().observe(requireActivity(), new Observer<List<EItinerary>>() {
            @Override
            public void onChanged(List<EItinerary> entries) {
                try{
                    AdapterItineraries loAdapter = new AdapterItineraries(entries, new AdapterItineraries.OnClickListener() {
                        @Override
                        public void OnClick(EItinerary args) {

                        }
                    });
                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    rvItinerary.setLayoutManager(loManager);
                    rvItinerary.setAdapter(loAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initCompanyNotice(){
        AdapterAnnouncements loAdapter = new AdapterAnnouncements(SampleData.GetAnnouncementList(), new AdapterAnnouncements.OnItemClickListener() {
            @Override
            public void OnClick(String args) {

            }
        });
        LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
        loManager.setOrientation(RecyclerView.VERTICAL);
        rvAnnouncement.setLayoutManager(loManager);
        rvAnnouncement.setAdapter(loAdapter);
    }
}