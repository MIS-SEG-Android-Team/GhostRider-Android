/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMInventory;

public class Fragment_Inventory extends Fragment {

    private VMInventory mViewModel;

    private RecyclerView recyclerView;

    public static Fragment_Inventory newInstance() {
        return new Fragment_Inventory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = v.findViewById(R.id.recyclerview_inventory);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(VMInventory.class);
//        mViewModel.getRandomItemList().observe(getViewLifecycleOwner(), randomItems -> {
//            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
//            manager.setOrientation(RecyclerView.VERTICAL);
//            recyclerView.setLayoutManager(manager);
//            recyclerView.setAdapter(new InventoryItemAdapter(randomItems, (TransNox, ItemCode, Description) -> {
//                Intent loIntent = new Intent(getActivity(), Activity_Inventory.class);
//                loIntent.putExtra("transno", TransNox);
//                loIntent.putExtra("code", ItemCode);
//                loIntent.putExtra("desc", Description);
//                startActivity(loIntent);
//            }));
//        });
    }
}