package org.rmj.g3appdriver.GCircle.Apps.BullsEye.obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.PerformancePeriod;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DAreaPerformance;
import org.rmj.g3appdriver.GCircle.room.Entities.EAreaPerformance;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchPerformance;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.ABPM;

import java.util.ArrayList;
import java.util.List;

public class AreaPerformance extends ABPM {
    private static final String TAG = AreaPerformance.class.getSimpleName();

    private final DAreaPerformance poDao;

    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public AreaPerformance(Application instance) {
        super(instance);
        this.poDao = GGC_GCircleDB.getInstance(instance).AreaPerformanceDao();
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean ImportData() {
        try{
            int lsUserLvl = poDao.GetUserLevel();
            String lsDeptIDx = poDao.GetUserDepartment();

            if(!lsDeptIDx.equalsIgnoreCase(DeptCode.MANAGEMENT_INFORMATION_SYSTEM)) {
                if(lsUserLvl < DeptCode.LEVEL_BRANCH_HEAD) {
                    message = "User is not authorize to download area/branch performance";
                    return false;
                }
            }

            String lsAreaCode = poDao.GetAreaCode();

            if(lsAreaCode == null){
                message = "Unable to retrieve area code. Please re-login account.";
                return false;
            }

            ArrayList<String> lsPeriod = PerformancePeriod.getList();
            for(int i = 0; i < lsPeriod.size(); i++){
                JSONObject params = new JSONObject();
                params.put("period", lsPeriod.get(i));
                params.put("areacd", lsAreaCode);

                String lsRespones = WebClient.sendRequest(
                        poApi.getImportAreaPerformance(),
                        params.toString(),
                        poHeaders.getHeaders());

                if(lsRespones == null){
                    message = SERVER_NO_RESPONSE;
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    continue;
                }

                JSONObject loResponse = new JSONObject(lsRespones);
                String lsResult = loResponse.getString("result");
                if (lsResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResponse.getJSONObject("error");
                    message = getErrorMessage(loError);
                    Thread.sleep(1000);
                    continue;
                }

                JSONArray laJson = loResponse.getJSONArray("detail");
                for(int x = 0; x < laJson.length(); x++){
                    JSONObject loJson = laJson.getJSONObject(x);
                    EAreaPerformance loDetail = poDao.GetAreaPerformance(
                            loJson.getString("sPeriodxx"),
                            loJson.getString("sAreaCode"));

                    if(loDetail == null){

                        EAreaPerformance loArea = new EAreaPerformance();
                        loArea.setPeriodxx(loJson.getString("sPeriodxx"));
                        loArea.setAreaCode(loJson.getString("sAreaCode"));
                        loArea.setAreaDesc(loJson.getString("sAreaDesc"));
                        loArea.setMCGoalxx(Integer.parseInt(loJson.getString("nMCGoalxx")));
                        loArea.setSPGoalxx(Float.parseFloat(loJson.getString("nSPGoalxx")));
                        loArea.setJOGoalxx(Integer.parseInt(loJson.getString("nJOGoalxx")));
                        loArea.setLRGoalxx(Float.parseFloat(loJson.getString("nLRGoalxx")));
                        loArea.setMCActual(Integer.parseInt(loJson.getString("nMCActual")));
                        loArea.setSPActual(Float.parseFloat(loJson.getString("nSPActual")));
                        loArea.setLRActual(Float.parseFloat(loJson.getString("nLRActual")));
                        poDao.insert(loArea);
                        Log.d(TAG, "Area performance for " + lsAreaCode + " has been saved.");

                    } else {

                    }
                }

                Thread.sleep(1000);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public LiveData<List<EAreaPerformance>> GetPeriodicAreaPerformance() {
        return poDao.getAllAreaPerformanceInfo();
    }

    @Override
    public LiveData<String> GetCurrentMCSalesPerformance() {
        return poDao.GetMCSalesPerformance();
    }

    @Override
    public LiveData<String> GetCurentSPSalesPerformance() {
        return poDao.GetSPSalesPerformance();
    }

    @Override
    public LiveData<String> GetJobOrderPerformance() {
        return poDao.GetJobOrderPerformance();
    }

    public LiveData<String> getAreaDescription(){
        return poDao.getAreaDescription();
    }

    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForMCSales(){
        return poDao.GetTopBranchPerformerForMCSales();
    }

    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForSPSales(){
        return poDao.GetTopBranchPerformerForSPSales();
    }

    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForJobOrder(){
        return poDao.GetTopBranchPerformerForJobOrder();
    }

    public LiveData<List<DAreaPerformance.BranchPerformance>> GetMCSalesBranchesPerformance(){
        return poDao.GetMCSalesBranchesPerformance();
    }

    public LiveData<List<DAreaPerformance.BranchPerformance>> GetSPSalesBranchesPerformance(){
        return poDao.GetSPSalesBranchesPerformance();
    }

    public LiveData<List<DAreaPerformance.BranchPerformance>> GetJobOrderBranchesPerformance(){
        return poDao.GetJobOrderBranchesPerformance();
    }

    public LiveData<List<DAreaPerformance.PeriodicPerformance>>GetMCSalesPeriodicPerformance(){
        return poDao.GetMCSalesPeriodicPerformance();
    }

    public LiveData<List<DAreaPerformance.PeriodicPerformance>>GetSPSalesPeriodicPerformance(){
        return poDao.GetSPSalesPeriodicPerformance();
    }

    public LiveData<List<DAreaPerformance.PeriodicPerformance>>GetJobOrderPeriodicPerformance(){
        return poDao.GetJobOrderPeriodicPerformance();
    }

}
