/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 8/20/21 2:17 PM
 * project file last modified : 8/20/21 2:17 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.google.android.material.appbar.MaterialToolbar;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchOpening;

public class Activity_BranchOpeningHistory extends AppCompatActivity {

    private VMBranchOpening mViewModel;

    private MaterialToolbar toolBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_BranchOpeningHistory.this).get(VMBranchOpening.class);
        setContentView(R.layout.activity_branch_opening_history);

        getExtras();
        setWidgets();
    }

    private void setWidgets() {

    }

    private void getExtras() {

    }
}