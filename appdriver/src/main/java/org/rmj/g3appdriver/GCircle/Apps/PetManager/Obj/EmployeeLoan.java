package org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLoanTypes;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EmployeeLoan {
    private Application context;
    private GCircleApi loApi;
    private HttpHeaders loHeaders;
    private DLoanTypes poTypes;
    public EmployeeLoan(Application context){
        this.context = context;
        this.loApi = new GCircleApi(context);
        this.loHeaders = HttpHeaders.getInstance(context);
        this.poTypes = GGC_GCircleDB.getInstance(context).loanTypesDao();
    }

    public String[] GetTermConstants(){
        String[] terms = {"36", "24", "18", "12", "6"};
        return terms;
    }
    public LiveData<List<ELoanTypes>> GetLoanTypes(){
        return poTypes.GetTypes();
    }
    public Boolean ImportLoanTypes(){
        try {
            JSONObject loParams =  new JSONObject();
            loParams.put("descript", "all");

            String lsResponse = WebClient.sendRequest(loApi.getDownloadLoanTypes(), loParams.toString(), loHeaders.getHeaders());
            if (lsResponse.isEmpty()){
                return false;
            }

            JSONObject loResult = new JSONObject(lsResponse);
            String lsResult = loResult.getString("result");

            if (lsResult.equalsIgnoreCase("error")){
                return false;
            }else {
                //{"result":"success","detail":[{"sLoanIDxx":"11003","sLoanName":"CONTG LOAN","cRecdStat":"1","dTimeStmp":"2024-05-29 16:17:12"},{"sLoanIDxx":"11007","sLoanName":"PAGIBIG-MP3","cRecdStat":"1","dTimeStmp":"2024-05-29 16:17:12"},{"sLoanIDxx":"11008","sLoanName":"PAGIBIG-CAL2","cRecdStat":"1","dTimeStmp":"2024-05-29 16:17:12"},{"sLoanIDxx":"11009","sLoanName":"SSS","cRecdStat":"1","dTimeStmp":"2024-05-29 16:17:12"}]}
                JSONArray loArray = loResult.getJSONArray("detail");
                for (int i = 0; i < loArray.length(); i++){
                    JSONObject loObj = loArray.getJSONObject(i);

                    ELoanTypes loTypes = new ELoanTypes();
                    loTypes.setsLoanIDxx(loObj.getString("sLoanIDxx"));
                    loTypes.setsLoanNmxx(loObj.getString("sLoanName"));
                    loTypes.setcRecdStat(loObj.getString("cRecdStat"));
                    loTypes.setdTimeStmp(loObj.getString("dTimeStmp"));

                    poTypes.SaveType(loTypes);
                }
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
