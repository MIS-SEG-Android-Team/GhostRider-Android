/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 7/9/21 2:17 PM
 * project file last modified : 7/9/21 2:17 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import org.rmj.guanzongroup.ghostrider.settings.Model.SettingsModel;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.adapter.ExpandableListHelpAdapter;
import org.rmj.guanzongroup.ghostrider.settings.etc.PopulateExpandableHelpList;
import org.rmj.guanzongroup.ghostrider.settings.etc.SettingsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_HelpList extends AppCompatActivity {

    private Toolbar toolbar;
    public static List<SettingsModel> listHelpDataHeader = new ArrayList<>();
    public static HashMap<SettingsModel, List<SettingsModel>> listHelpDataChild = new HashMap<>();
    private SettingsData settingsHelpData;
    public static  ExpandableListView expHelpView;
    private PopulateExpandableHelpList populateExpandableHelpList;
    public static ExpandableListHelpAdapter helpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list);
        toolbar = findViewById(R.id.toolbar_help_list);
        settingsHelpData = new SettingsData();
        settingsHelpData.SettingsData(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Help");
        expHelpView = findViewById(R.id.lvExp);
        populateExpandableHelpList = new PopulateExpandableHelpList();
        populateExpandableHelpList.populate(this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}