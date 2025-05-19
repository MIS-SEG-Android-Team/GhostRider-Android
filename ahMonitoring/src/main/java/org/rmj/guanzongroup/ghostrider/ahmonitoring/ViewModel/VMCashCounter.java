/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 6/7/21 2:35 PM
 * project file last modified : 6/7/21 2:35 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CashCount.CashCount;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMCashCounter extends AndroidViewModel {
    private static final String TAG = VMCashCounter.class.getSimpleName();

    private final CashCount poSys;

    private final MutableLiveData<String> psBranch = new MutableLiveData<>();

    private final MutableLiveData<Double> p1000 = new MutableLiveData<>();
    private final MutableLiveData<Double> p500 = new MutableLiveData<>();
    private final MutableLiveData<Double> p200 = new MutableLiveData<>();
    private final MutableLiveData<Double> p100 = new MutableLiveData<>();
    private final MutableLiveData<Double> p50 = new MutableLiveData<>();
    private final MutableLiveData<Double> p20 = new MutableLiveData<>();

    private final MutableLiveData<Double> p10 = new MutableLiveData<>();
    private final MutableLiveData<Double> p5 = new MutableLiveData<>();
    private final MutableLiveData<Double> p1 = new MutableLiveData<>();
    private final MutableLiveData<Double> c1 = new MutableLiveData<>();
    private final MutableLiveData<Double> c25 = new MutableLiveData<>();
    private final MutableLiveData<Double> c10 = new MutableLiveData<>();
    private final MutableLiveData<Double> c5 = new MutableLiveData<>();

    private final MutableLiveData<Integer> qtyp1000 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp500 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp200 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp100 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp50 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp20 = new MutableLiveData<>();

    private final MutableLiveData<Integer> qtyp10 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp5 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyp1 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyc50 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyc25 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyc10 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyc5 = new MutableLiveData<>();
    private final MutableLiveData<Integer> qtyc1 = new MutableLiveData<>();

    private final MutableLiveData<Double> pnTotalx = new MutableLiveData<>();
    private final MutableLiveData<Double> cnTotalx = new MutableLiveData<>();
    private final MutableLiveData<Double> grandTotalx = new MutableLiveData<>();
    private final MutableLiveData<JSONObject> jsonData = new MutableLiveData<>();
    private String message;
    public VMCashCounter(@NonNull Application application) {
        super(application);
        this.poSys = new CashCount(application);
        this.p1000.setValue((double) 0);
        this.p500.setValue((double) 0);
        this.p200.setValue((double) 0);
        this.p100.setValue((double) 0);
        this.p50.setValue((double) 0);
        this.p20.setValue((double) 0);

        this.p10.setValue((double) 0);
        this.p5.setValue((double) 0);
        this.p1.setValue((double) 0);
        this.c1.setValue((double) 0);
        this.c25.setValue((double) 0);
        this.c10.setValue((double) 0);
        this.c5.setValue((double) 0);

        this.qtyp1000.setValue(0);
        this.qtyp500.setValue(0);
        this.qtyp200.setValue(0);
        this.qtyp100.setValue(0);
        this.qtyp50.setValue(0);
        this.qtyp20.setValue(0);

        this.qtyp10.setValue(0);
        this.qtyp5.setValue(0);
        this.qtyp1.setValue(0);
        this.qtyc50.setValue(0);
        this.qtyc25.setValue(0);
        this.qtyc10.setValue(0);
        this.qtyc5.setValue(0);
        this.qtyc1.setValue(0);

        this.pnTotalx.setValue((double) 0);
        this.cnTotalx.setValue((double) 0);
        this.grandTotalx.setValue((double) 0);
        this.psBranch.setValue("");
    }

    public void setBranchCd(String val) {
        this.psBranch.setValue(val);
    }

    public LiveData<String> GetBranchCd() {
        return psBranch;
    }

    public LiveData<EBranchInfo> GetBranchForCashCount(String args) {
        return poSys.GetBranchForCashCount(args);
    }

    //Added by Jonathan 2021/06/08
    //Computation for all peso bill
    private void calculatePesoTotal() {
        double d1000 = p1000.getValue();
        double d500 = p500.getValue();
        double d200 = p200.getValue();
        double d100 = p100.getValue();
        double d50 = p50.getValue();
        double d20 = p20.getValue();
        double lnTotal = d1000 + d500 + d200 + d100 + d50 + d20;
        pnTotalx.setValue(lnTotal);
        calc_grandTotal();
    }

    //Added by Jonathan 2021/04/13
    //set  peso bill value
    public void setP1000(Double fnAmount, int qty) {
        this.p1000.setValue(fnAmount);
        this.qtyp1000.setValue(qty);
        calculatePesoTotal();
    }

    public void setP500(Double fnAmount, int qty) {
        this.p500.setValue(fnAmount);
        this.qtyp500.setValue(qty);
        calculatePesoTotal();
    }

    public void setP200(Double fnAmount, int qty) {
        this.p200.setValue(fnAmount);
        this.qtyp200.setValue(qty);
        calculatePesoTotal();
    }

    public void setP100(Double fnAmount, int qty) {
        this.p100.setValue(fnAmount);
        this.qtyp100.setValue(qty);
        calculatePesoTotal();
    }

    public void setP50(Double fnAmount, int qty) {
        this.p50.setValue(fnAmount);
        this.qtyp50.setValue(qty);
        calculatePesoTotal();
    }

    public void setP20(Double fnAmount, int qty) {
        this.p20.setValue(fnAmount);
        this.qtyp20.setValue(qty);
        calculatePesoTotal();
    }

    public LiveData<Double> getPesoTotalAmount() {
        return pnTotalx;
    }

    //Added by Jonathan 2021/06/08
    //Computation for all peso bill
    private void calculateCoinsTotal() {
        double d10 = p10.getValue();
        double d5 = p5.getValue();
        double d1 = p1.getValue();
        double dc25 = c25.getValue();
        double dc10 = c10.getValue();
        double dc5 = c5.getValue();
        double dc1 = c1.getValue();
        double lnCTotal = d10 + d5 + d1 + dc1 + dc25 + dc10 + dc5;
        cnTotalx.setValue(lnCTotal);
        calc_grandTotal();
    }

    //Added by Jonathan 2021/04/13
    //set coins bill value
    public void setP10(Double fnAmount, int qty) {
        this.p10.setValue(fnAmount);
        this.qtyp10.setValue(qty);
        calculateCoinsTotal();
    }

    public void setP5(Double fnAmount, int qty) {
        this.p5.setValue(fnAmount);
        this.qtyp5.setValue(qty);
        calculateCoinsTotal();
    }

    public void setP1(Double fnAmount, int qty) {
        this.p1.setValue(fnAmount);
        this.qtyp1.setValue(qty);
        calculateCoinsTotal();
    }

    public void setC1(Double fnAmount, int qty) {
        this.c1.setValue(fnAmount);
        this.qtyc1.setValue(qty);
        calculateCoinsTotal();
    }

    public void setC25(Double fnAmount, int qty) {
        this.c25.setValue(fnAmount);
        this.qtyc25.setValue(qty);
        calculateCoinsTotal();
    }

    public void setC10(Double fnAmount, int qty) {
        this.c10.setValue(fnAmount);
        this.qtyc10.setValue(qty);
        calculateCoinsTotal();
    }

    public void setC5(Double fnAmount, int qty) {
        this.c5.setValue(fnAmount);
        this.qtyc5.setValue(qty);
        calculateCoinsTotal();
    }

    public LiveData<Double> getCoinsTotalAmount() {
        return cnTotalx;
    }

    public void calc_grandTotal() {
        double pesoTotal = pnTotalx.getValue();
        double coinTotal = cnTotalx.getValue();
        double total = pesoTotal + coinTotal;
        grandTotalx.setValue(total);
        createJSONParameters();
    }

    public LiveData<Double> getGrandTotalAmount() {
        return grandTotalx;
    }

    public LiveData<JSONObject> getJsonData() {
        createJSONParameters();
        return jsonData;
    }

    private JSONObject createJSONParameters() {
        JSONObject param = new JSONObject();
        try {
            param.put("nCn0001cx", qtyc1.getValue());
            param.put("nCn0005cx", qtyc5.getValue());
            param.put("nCn0010cx", qtyc10.getValue());
            param.put("nCn0025cx", qtyc25.getValue());
            param.put("nCn0050cx", qtyc50.getValue());
            param.put("nCn0001px", qtyp1.getValue());
            param.put("nCn0005px", qtyp5.getValue());
            param.put("nCn0010px", qtyp10.getValue());
            param.put("nNte0020p", qtyp20.getValue());
            param.put("nNte0050p", qtyp50.getValue());
            param.put("nNte0100p", qtyp100.getValue());
            param.put("nNte0200p", qtyp200.getValue());
            param.put("nNte0500p", qtyp500.getValue());
            param.put("nNte1000p", qtyp1000.getValue());
            param.put("nTotSales", grandTotalx.getValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonData.setValue(param);
        return param;
    }

    public interface OnGetBranchesList {
        void OnLoad();

        void OnRetrieve(List<EBranchInfo> list);

        void OnFailed(String message);
    }

    public void GetBranchesList(OnGetBranchesList listener) {
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                List<EBranchInfo> loList = poSys.GetBranchesForCashCount();
                if (loList == null) {
                    message = poSys.getMessage();
                    return null;
                }
                return loList;
            }

            @Override
            public void OnPostExecute(Object object) {
                List<EBranchInfo> loResult = (List<EBranchInfo>) object;
                if(loResult == null){
                    listener.OnFailed(message);
                    return;
                }

                listener.OnRetrieve(loResult);
            }
        });
    }

}