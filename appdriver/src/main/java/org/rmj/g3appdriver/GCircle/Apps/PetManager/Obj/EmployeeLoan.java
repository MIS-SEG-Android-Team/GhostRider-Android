package org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmpLoan;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLoanTypes;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
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
    private DEmpLoan poEmpLoan;
    private String message;
    public EmployeeLoan(Application context){
        this.context = context;
        this.loApi = new GCircleApi(context);
        this.loHeaders = HttpHeaders.getInstance(context);
        this.poTypes = GGC_GCircleDB.getInstance(context).loanTypesDao();
        this.poEmpLoan = GGC_GCircleDB.getInstance(context).emploanDao();
    }

    public String GetMessage(){
        return message;
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
    public Boolean SaveLoan(EEmpLoan empLoan){
        try {
            JSONObject loParams = new JSONObject();
            loParams.put("sEmployID", empLoan.getsEmployID());
            loParams.put("dTransact", empLoan.getdTransact());
            loParams.put("dLoanDate", empLoan.getdLoanDate());
            loParams.put("sLoanIDxx", empLoan.getsLoanIDxx());
            loParams.put("nLoanAmtx", empLoan.getnLoanAmtxx());
            loParams.put("sPurposed", empLoan.getsPurposed());

            String lsResponse = WebClient.sendRequest(loApi.getSubmitLoanEntry(), loParams.toString(), loHeaders.getHeaders());
            if (lsResponse.isEmpty()){
                return false;
            }

            JSONObject loResult = new JSONObject(lsResponse);
            String lsResult = loResult.getString("result");

            if (lsResult.equalsIgnoreCase("error")){
                message = loResult.getString("message");
                return false;
            }else {
                poEmpLoan.SaveLoan(empLoan);
            }

            return true;
        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }
}
