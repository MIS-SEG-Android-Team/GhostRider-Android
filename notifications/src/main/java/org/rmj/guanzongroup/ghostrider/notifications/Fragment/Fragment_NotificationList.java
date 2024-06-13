/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.notifications
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.notifications.Fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DNotification;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.guanzongroup.ghostrider.notifications.Activity.Activity_ViewNotification;
import org.rmj.guanzongroup.ghostrider.notifications.Adapter.NotificationListAdapter;
import org.rmj.guanzongroup.ghostrider.notifications.R;
import org.rmj.guanzongroup.ghostrider.notifications.ViewModel.VMNotificationList;

import java.util.List;

public class Fragment_NotificationList extends Fragment {

    private VMNotificationList mViewModel;

    private RecyclerView recyclerView;
    private NotificationListAdapter poAdapter;
    private LinearLayoutManager manager;
    private ConstraintLayout ln_empty;
    private boolean isLoading = false;

    /** Limit of displayaed list before load on fetch **/
    private int pnLimitxx = 10;

    public static Fragment_NotificationList newInstance() {
        return new Fragment_NotificationList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMNotificationList.class);
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        setupWidgets(view);

        mViewModel.GetOtherNotificationList().observe(requireActivity(), new Observer<List<DNotification.NotificationListDetail>>() {
            @Override
            public void onChanged(List<DNotification.NotificationListDetail> list) {
                try{
                    if(list == null){
                        return;
                    }

                    if(list.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        ln_empty.setVisibility(View.VISIBLE);

                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    ln_empty.setVisibility(View.GONE);

                    poAdapter = new NotificationListAdapter(list, new NotificationListAdapter.OnItemClickListener() {
                        @Override
                        public void OnClick(String messageID) {
                            Intent loIntent = new Intent(getActivity(), Activity_ViewNotification.class);
                            loIntent.putExtra("sMesgIDxx", messageID);
                            startActivity(loIntent);
                            requireActivity().overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                        }

                        @Override
                        public void OnActionButtonClick(String message) {
                            GToast.CreateMessage(getActivity(), message, GToast.INFORMATION).show();
                        }
                    });
                    poAdapter.notifyDataSetChanged();
                    manager = new LinearLayoutManager(getActivity());
                    manager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(poAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
//        initScrollListener();

        return view;
    }

    private void setupWidgets(View v) {
        recyclerView = v.findViewById(R.id.recyclerview);
        ln_empty = v.findViewById(R.id.ln_empty);
    }

//    private void initScrollListener() {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!isLoading) {
//                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == notificationItemLists.size() - 1) {
//                        //bottom of list!
//                        loadMore();
//                        isLoading = true;
//                    }
//                }
//            }
//        });
//
//
//    }

//    private void loadMore() {
//        notificationItemLists.add(null);
//        poAdapter.notifyItemInserted(notificationItemLists.size() - 1);
//
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                notificationItemLists.remove(notificationItemLists.size() - 1);
//                int scrollPosition = notificationItemLists.size();
//                poAdapter.notifyItemRemoved(scrollPosition);
//                int currentSize = scrollPosition;
//
//                /** Call method that selects limited list to load
//                 *  (For second batch of list to display) **/
//
////                callTheMethod(currentSize, pnLimitxx) {
////                    notificationItemLists.addAll(list);
////                    poAdapter.notifyDataSetChanged();
////                    isLoading = false;
////                }
//
//            }
//        }, 2000);
//    }
}