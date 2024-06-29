/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeRole;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.ImportData.ImportEmployeeRole;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Etc.FragmentAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_CollectionList;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_LogCollection;
import org.rmj.guanzongroup.ghostrider.epacss.Dialog.DialogQrCode;
import org.rmj.guanzongroup.ghostrider.epacss.Object.ChildObject;
import org.rmj.guanzongroup.ghostrider.epacss.Object.ParentObject;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.Service.DataSyncService;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMMainActivity;
import org.rmj.guanzongroup.ghostrider.epacss.adapter.ExpandableListDrawerAdapter;
import org.rmj.g3appdriver.etc.AppDeptIcon;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_Settings;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Activity_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = Activity_Main.class.getSimpleName();
    private VMMainActivity mViewModel;
    private DataSyncService poNetRecvr;

    private MessageBox loMessage;
    private LoadDialog poDialog;
    private Intent loIntent;
    private boolean cSlfiex;

    private ShapeableImageView img_banner;
    private ImageView imgDept;
    private MaterialTextView lblDept;
    private ExpandableListDrawerAdapter listAdapter;
    private ExpandableListView expListView;
    private DrawerLayout drawer;
    private ViewPager2 viewPager;

    private final List<ParentObject> poParentLst = new ArrayList<>();
    private List<ChildObject> poChildLst;
    private final HashMap<ParentObject, List<ChildObject>> poChild = new HashMap<>();

    private AppConfigPreference loConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mViewModel = new ViewModelProvider(this).get(VMMainActivity.class);
        poNetRecvr = mViewModel.getInternetReceiver();
        loConfig = AppConfigPreference.getInstance(this);

        initWidgets();
        InitUserFeatures();
        initReceiver();
        initPostedDCP();

        mViewModel.getEmployeeInfo().observe(this, eEmployeeInfo -> {
            try{
                AppConfigPreference.getInstance(Activity_Main.this).setIsAppFirstLaunch(false);
                imgDept.setImageResource(AppDeptIcon.getIcon(eEmployeeInfo.getDeptIDxx()));
                lblDept.setText(DeptCode.getDepartmentName(eEmployeeInfo.getDeptIDxx()));
                cSlfiex = eEmployeeInfo.getSlfieLog().equalsIgnoreCase("1");

                Fragment[] loFragment = new Fragment[]{mViewModel.GetUserFragments(eEmployeeInfo)};

                FragmentAdapter loAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
                loAdapter.initFragments(loFragment);

                viewPager.setAdapter(loAdapter);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void InitUserFeatures(){
        mViewModel.getEmployeeRole().observe(this, roles -> {

            try{
                mViewModel.getChildRoles().observe(this, childMenus -> {

                    poParentLst.clear();
                    poChild.clear();

                    for(int x = 0; x < roles.size(); x++){
                        EEmployeeRole loRole = roles.get(x);
                        ParentObject loParent = new ParentObject(loRole.getObjectNm(), loRole.getHasChild());

                        poParentLst.add(loParent);
                        poChildLst = new ArrayList<>();

                        for (int i = 0; i < childMenus.size(); i++){

                            EEmployeeRole loChild = childMenus.get(i);
                            String lsParent = loRole.getObjectNm();

                            if(lsParent.equalsIgnoreCase(loChild.getParentxx())){
                                if("selfie log".equalsIgnoreCase(loChild.getObjectNm().toLowerCase(Locale.ROOT))) {
                                    if(cSlfiex || loChild.getRecdStat().equalsIgnoreCase("1")) {
                                        ChildObject loCMenu = new ChildObject(loChild.getObjectNm());
                                        poChildLst.add(loCMenu);
                                        poChild.put(loParent, poChildLst);
                                    }
                                } else if (loChild.getRecdStat().equalsIgnoreCase("1")){
                                    ChildObject loCMenu = new ChildObject(loChild.getObjectNm());
                                    poChildLst.add(loCMenu);
                                    poChild.put(loParent, poChildLst);
                                }
                            }
                        }
                    }

                    listAdapter = new ExpandableListDrawerAdapter(Activity_Main.this,
                            poParentLst, poChild);
                    expListView.setAdapter(listAdapter);
                    expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
                        if(!poParentLst.get(groupPosition).isParent()){
                            loIntent = poParentLst.get(groupPosition).getIntent(Activity_Main.this);
                            if(loIntent == null){
                                loMessage.initDialog();
                                loMessage.setTitle("Dashboard");
                                loMessage.setMessage("Feature not available.");
                                loMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                loMessage.show();
                            } else {
                                startActivity(loIntent);
                                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                            }
                            return true;
                        }
                        return false;
                    });
                    expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                        loIntent = poChild.get(poParentLst.get(groupPosition)).get(childPosition).getIntent(Activity_Main.this);
                        if(loIntent == null){
                            loMessage.initDialog();
                            loMessage.setTitle("Dashboard");
                            loMessage.setMessage("Feature not available.");
                            loMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                            loMessage.show();
                        } else {
                            startActivity(loIntent);
                            overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                        }
                        return true;
                    });
                    expListView.collapseGroup(1);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void initWidgets(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*Edited by mike*/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        loMessage = new MessageBox(Activity_Main.this);
        poDialog = new LoadDialog(Activity_Main.this);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(50), width - GetPixelFromDips(10));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        viewPager = findViewById(R.id.viewpager);
        img_banner = view.findViewById(R.id.img_banner);
        imgDept = view.findViewById(R.id.img_deptLogo);
        lblDept = view.findViewById(R.id.lbl_deptNme);

        lblDept.setOnClickListener(v -> {
            ImportEmployeeRole loImport = new ImportEmployeeRole(getApplication());
            loImport.RefreshEmployeeRole(new ImportEmployeeRole.OnImportEmployeeRoleCallback() {
                @Override
                public void OnRequest() {
                    poDialog.initDialog("Guanzon Circle", "Refreshing employee access. Please wait...", false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                }

                @Override
                public void OnFailed(String message) {
                    poDialog.dismiss();
                    loMessage.initDialog();
                    loMessage.setTitle("Guanzon Circle");
                    loMessage.setMessage(message);
                    loMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                    loMessage.show();
                }
            });
        });
        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployeeSession poSession = EmployeeSession.getInstance(Activity_Main.this);
                String imgLink = "https://webfsgk.guanzongroup.com.ph/empid/" + poSession.getEmployeeID() + ".png";

                mViewModel.DisplayURLImage(imgLink, new VMMainActivity.onDownload() {
                    @Override
                    public void onLoad(String title, String message) {
                        poDialog.initDialog(title, message, true);
                        poDialog.show();
                    }
                    @Override
                    public void onDisplay(Bitmap loImg) {
                        poDialog.dismiss();

                        DialogQrCode qrDialog = new DialogQrCode(Activity_Main.this);
                        qrDialog.setBitmap(loImg);

                        qrDialog.show();
                    }
                    @Override
                    public void onFailed(String message) {
                        poDialog.dismiss();

                        loMessage.initDialog();
                        loMessage.setTitle("Employee QR");
                        loMessage.setMessage(message);
                        loMessage.setPositiveButton("Close", (view1, dialog) -> dialog.dismiss());

                        loMessage.show();
                    }
                });
            }
        });
    }

    /*Edited by mike*/
    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void initReceiver(){
        IntentFilter loFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(poNetRecvr, loFilter);
        AppConfigPreference.getInstance(Activity_Main.this).setIsMainActive(true);
        Log.e(TAG, "Internet status receiver has been registered.");
    }

    private void initPostedDCP(){
        mViewModel.getLatestPostedDCP().observe(this, new Observer<EDCPCollectionMaster>() {
            @Override
            public void onChanged(EDCPCollectionMaster edcpCollectionMaster) {
                if (edcpCollectionMaster != null){

                    if (edcpCollectionMaster.getTranStat() != null){
                        if (edcpCollectionMaster.getTranStat().equals("2")){

                            if (edcpCollectionMaster.getSendStat() != null){

                                if (edcpCollectionMaster.getSendStat().equals("1")){
                                    //todo: set dcp status to posted
                                    loConfig.setDCPStatus(true);
                                }else {
                                    //todo: set dcp status for posting
                                    loConfig.setDCPStatus(false);
                                }

                            }else {
                                //todo: set dcp status for posting
                                loConfig.setDCPStatus(false);
                            }

                        }else {
                            //todo: set dcp status for posting
                            loConfig.setDCPStatus(false);
                        }

                    }else {
                        //todo: set dcp status for posting
                        loConfig.setDCPStatus(false);
                    }
                }else {
                    //todo: set dcp status for posting
                    loConfig.setDCPStatus(false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(poNetRecvr);
        Log.e(TAG, "Internet status receiver has been unregistered.");
        AppConfigPreference.getInstance(Activity_Main.this).setIsMainActive(false);
        mViewModel.ResetRaffleStatus();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            loMessage.initDialog();
            loMessage.setPositiveButton("Yes", (view, dialog) -> {
                dialog.dismiss();
                finish();
            });
            loMessage.setNegativeButton("No", (view, dialog) -> dialog.dismiss());
            loMessage.setTitle("Guanzon Circle");
            loMessage.setMessage("Exit Guanzon Circle app?");
            loMessage.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            startActivity(new Intent(Activity_Main.this, Activity_Settings.class));
            overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
        } else if(item.getItemId() == R.id.action_logout){
            loMessage.initDialog();
            loMessage.setNegativeButton("No", (view, dialog) -> dialog.dismiss());
            loMessage.setPositiveButton("Yes", (view, dialog) -> {
                dialog.dismiss();
                finish();
                new EmployeeMaster(getApplication()).LogoutUserSession();
                AppConfigPreference.getInstance(Activity_Main.this).setIsAppFirstLaunch(false);
                startActivity(new Intent(Activity_Main.this, Activity_SplashScreen.class));
            });
            loMessage.setTitle("Account Session");
            loMessage.setMessage("Are you sure you want to end session/logout?");
            loMessage.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("request code:", String.valueOf(requestCode));
        Log.e("result code:", String.valueOf(resultCode));

        if(requestCode == AppConstants.INTENT_SELFIE_LOGIN && resultCode == RESULT_OK){
            Intent intent = new Intent(Activity_Main.this, Activity_Application.class);
            intent.putExtra("app", AppConstants.INTENT_SELFIE_LOGIN);
            startActivity(intent);
        }else if(requestCode == AppConstants.INTENT_DCP_LOG && resultCode == RESULT_OK){
            Intent  intent = new Intent(Activity_Main.this, Activity_LogCollection.class);
            startActivity(intent);
        }else if(requestCode == AppConstants.INTENT_DCP_LIST && resultCode == RESULT_OK){
            Intent  intent = new Intent(Activity_Main.this, Activity_CollectionList.class);
            startActivity(intent);
        }
    }
}