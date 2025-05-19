package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchPerformance;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.PerformancePeriod;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.Adapter_Area_Performance_Monitoring;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Model.PeriodicPerformance;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchPerformanceMonitor;

import java.util.ArrayList;
import java.util.List;

public class Activity_BranchPerformanceMonitoring extends AppCompatActivity {

    private VMBranchPerformanceMonitor mViewModel;
    private String BranchCD,BranchNM;
    private LineChart linechart;
    private PieChart piechart;
    private MaterialTextView lblBranch;
    private TabLayout tabLayout;
    private ArrayList<Entry> poActual, poGoalxx;
    private int width, height;
    private RecyclerView rvAreaPerformance, rvBranchPerformance;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.BranchCD = getIntent().getStringExtra("brnCD");
        this.BranchNM = getIntent().getStringExtra("brnNM");
        this.mViewModel = new ViewModelProvider(Activity_BranchPerformanceMonitoring.this).get(VMBranchPerformanceMonitor.class);

        setContentView(R.layout.activity_branch_performance_monitoring);

        Log.e("pinasa ko galing area", String.valueOf(getIntent().getStringExtra("brnCD")));

        Toolbar toolbar = findViewById(R.id.toolbar_monitoring);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lblBranch = findViewById(R.id.lbl_AreaCde);

        width = metrics.widthPixels;
        height = metrics.heightPixels;
        piechart = findViewById(R.id.pie_chart);
        linechart = findViewById(R.id.line_chart);
        rvBranchPerformance = findViewById(R.id.rvBranchPerformance);

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("MC Sales"));
        tabLayout.addTab(tabLayout.newTab().setText("SP Sales"));
        tabLayout.addTab(tabLayout.newTab().setText("Job Order"));

        lblBranch.setText(BranchNM);

        initMCSales();
        initTablayout();
    }

    private void initTablayout(){

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override

            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    initMCSales();
                } else if (tab.getPosition() == 1) {
                    initSPSales();
                } else{
                    initJOSales();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void initMCSales(){
        Log.e("sample branch",String.valueOf(BranchCD));
        mViewModel.GetMCSalesPeriodicPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this,  BranchPerforamancebyMC -> {
            try{
                InitializeBranchList(BranchPerforamancebyMC);
                InitializeBranch(BranchPerforamancebyMC);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        mViewModel.GetCurrentMCSalesPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this, BranchPerforamancebyMC ->{
            try{
                InitializePieChart(BranchPerforamancebyMC);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void initSPSales(){
        mViewModel.GetSPSalesPeriodicPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this,  BranchPerforamancebySP -> {
            try{
                InitializeBranchList(BranchPerforamancebySP);
                InitializeBranch(BranchPerforamancebySP);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        mViewModel.GetCurrentSPSalesPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this, BranchPerforamancebySP ->{
            try{
                InitializePieChart(BranchPerforamancebySP);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }
    public void initJOSales(){
        mViewModel.GetJobOrderPeriodicPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this,  BranchPerforamancebyJO -> {
            try{
                InitializeBranchList(BranchPerforamancebyJO);
                InitializeBranch(BranchPerforamancebyJO);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        mViewModel.GetJobOrderPerformance(BranchCD).observe(Activity_BranchPerformanceMonitoring.this, BranchPerforamancebyJO ->{
            try{
                InitializePieChart(BranchPerforamancebyJO);
                Log.e("value of ",BranchPerforamancebyJO);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
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

    private void InitializeBranchList(List<DBranchPerformance.PeriodicalPerformance> list){
        poActual = new ArrayList<>();
        poGoalxx = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            poActual.add(new Entry(x, Float.valueOf(list.get(x).Actual)));
            poGoalxx.add(new Entry(x, Float.valueOf(list.get(x).Goal)));
        }

        LineDataSet loActual = new LineDataSet(poActual, "Actual");
        LineDataSet loGoalxx = new LineDataSet(poGoalxx, "Goal");

        loActual.setAxisDependency(YAxis.AxisDependency.RIGHT);
        loActual.setLineWidth(2);
        loActual.setColors(getResources().getColor(R.color.guanzon_orange));

        loGoalxx.setAxisDependency(YAxis.AxisDependency.RIGHT);
        loGoalxx.setLineWidth(2);
        loGoalxx.setColors(getResources().getColor(R.color.check_green));

        ArrayList<ILineDataSet> loDataSet = new ArrayList<>();
        loDataSet.add(loActual);
        loDataSet.add(loGoalxx);

        LineData data = new LineData(loDataSet);
        linechart.setData(data);
        int color = AppConstants.getThemeTextColor(Activity_BranchPerformanceMonitoring.this);
        linechart.getData().setValueTextColor(color);
        linechart.getData().setValueTextColor(color);
        linechart.getXAxis().setTextColor(color);
        linechart.getAxisLeft().setTextColor(color);
        linechart.getAxisRight().setTextColor(color);
        linechart.getLegend().setTextColor(color);
        linechart.setDescription(null);
        linechart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(PerformancePeriod.parseBranchPeriodicPerformance(list)));
        linechart.setDoubleTapToZoomEnabled(false);
        linechart.getXAxis().setTextSize(10f);
        linechart.setExtraOffsets(0, 0, 10f, 18f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (height / 3));
        linechart.setLayoutParams(params);
        linechart.getLegend().setEnabled(false);
        linechart.setDrawGridBackground(false);
        linechart.setDrawBorders(true);
        linechart.setBorderWidth(1);
        linechart.setBorderColor(getResources().getColor(R.color.color_dadada));
        linechart.animateX(500);
        XAxis xAxis = linechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        linechart.invalidate();
    }

    private void InitializePieChart(String args){

        try{
            ArrayList<PieEntry> loEntries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            Log.e("anu ito",String.valueOf(args));
            if(String.valueOf(args).contains("/")){
                String[] rat = args.split("/");
                float lnActual = (float) Double.parseDouble(rat[0]);
                float lnGoalxx = (float) Double.parseDouble(rat[1]);

                if(lnGoalxx > lnActual){
                    float lnRemain = lnGoalxx - lnActual;
                    loEntries.add(new PieEntry((float) Double.parseDouble(rat[0]), "Actual")); //Set actual performance
                    loEntries.add(new PieEntry(lnRemain , "Remaining Goal"));

                    colors.add(Color.parseColor("#F47422"));
                    colors.add(Color.parseColor("#1ED760"));
                } else {
                    float lnExcess = lnActual - lnGoalxx;
                    loEntries.add(new PieEntry(lnExcess , "Overdue"));
                    loEntries.add(new PieEntry((float) Double.parseDouble(rat[0]), "Actual")); //Set actual performance

                    colors.add(Color.parseColor("#ff3333"));
                    colors.add(Color.parseColor("#F47422"));
                }
            }

            PieDataSet pieDataSet = new PieDataSet(loEntries, "");
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setColors(colors);
            PieData pieData = new PieData(pieDataSet);
            pieData.setDrawValues(true);
            piechart.getLegend().setEnabled(false);
            piechart.setData(pieData);
            piechart.invalidate();
            piechart.setDescription(null);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void InitializeBranch(List<DBranchPerformance.PeriodicalPerformance> BranchPerformanceList) {
        List<PeriodicPerformance> loList = new ArrayList<>();
        for(int x = 0; x < BranchPerformanceList.size(); x++){
            String lsPeriod = BranchPerformanceList.get(x).Period;
            String lnActual = BranchPerformanceList.get(x).Actual;
            String lnGoalxx = BranchPerformanceList.get(x).Goal;

            loList.add(new PeriodicPerformance(lsPeriod, lnActual, lnGoalxx));
        }
        Adapter_Area_Performance_Monitoring loAdapter = new Adapter_Area_Performance_Monitoring(loList, new Adapter_Area_Performance_Monitoring.OnAreasClickListener() {
            @Override
            public void OnClick(String sBranchCd, String sBranchnm) {
                Intent loIntent = new Intent(Activity_BranchPerformanceMonitoring.this, Activity_BranchPerformanceMonitoring.class);
                loIntent.putExtra("brnCD", sBranchCd);
                loIntent.putExtra("brnNM", sBranchnm);
                startActivity(loIntent);
            }
        });
        LinearLayoutManager loManager = new LinearLayoutManager(Activity_BranchPerformanceMonitoring.this);
        loManager.setOrientation(RecyclerView.VERTICAL);
        loManager.setReverseLayout(true);
        rvBranchPerformance.setLayoutManager(loManager);
        rvBranchPerformance.setAdapter(loAdapter);
        rvBranchPerformance.setVisibility(View.VISIBLE);
    }

}