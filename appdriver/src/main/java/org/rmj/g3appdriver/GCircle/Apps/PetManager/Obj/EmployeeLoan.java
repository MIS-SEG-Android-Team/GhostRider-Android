package org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj;

import android.app.Application;
import androidx.lifecycle.LiveData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmpLoan;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLoanTypes;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeLoan {
    private Application context;
    private GCircleApi loApi;
    private HttpHeaders loHeaders;
    private DLoanTypes poTypes;
    private DEmpLoan poEmpLoan;
    private EmployeeSession poSession;
    private String message;
    public EmployeeLoan(Application context){
        this.context = context;
        this.loApi = new GCircleApi(context);
        this.loHeaders = HttpHeaders.getInstance(context);
        this.poTypes = GGC_GCircleDB.getInstance(context).loanTypesDao();
        this.poEmpLoan = GGC_GCircleDB.getInstance(context).emploanDao();
        this.poSession = EmployeeSession.getInstance(context);
    }

    public String GetMessage(){
        return message;
    }
    public String CreateUniqueID() {
        String lsUniqIDx = "";
        try {
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());

            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poEmpLoan.GetRowsCountForID() + 1;

            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lsUniqIDx;
    }
    public String CurrentDateTime(){
        Date currDt = Calendar.getInstance().getTime();
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dtFormat.format(currDt);
    }
    public String GetEmpID(){
        return poSession.getEmployeeID();
    }
    public String GetEmpLevel(){
        return poSession.getEmployeeLevel();
    }
    public String GetEmpName(){
        return poSession.getUserName();
    }
    public String GetEmpDepartment(){
        return DeptCode.getDepartmentName(poSession.getDeptID());
    }
    public String GetLoanName(String sIDxx){
        return poTypes.GetLoanNm(sIDxx);
    }
    public String[] GetTermConstants(){
        String[] terms = {"36", "24", "18", "12", "6"};
        return terms;
    }
    public LiveData<List<ELoanTypes>> GetLoanTypes(){
        return poTypes.GetTypes();
    }
    public LiveData<EEmpLoan> GetLoanTransNox(String sTransNox){
        return poEmpLoan.GetLoanTransnox(sTransNox);
    }
    public LiveData<List<EEmpLoan>> GetUserEntries(String sEmpID){
        return poEmpLoan.GetUserEntries(sEmpID);
    }
    public LiveData<List<EEmpLoan>> GetForApproval(){
        return poEmpLoan.GetForApproval();
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
    public Boolean ValidateEntry(EEmpLoan empLoan){
        if (empLoan == null){
            message = "Entry not detected";
            return false;
        }

        if (empLoan.getsEmployID().isEmpty()){
            message = "Employee ID not detected";
            return false;
        }else if (empLoan.getdTransact().isEmpty()){
            message = "Date transaction not detected";
            return false;
        }else if (empLoan.getdLoanDate().isEmpty()){
            message = "Please select loan date";
            return false;
        }else if (empLoan.getsLoanIDxx().isEmpty()){
            message = "Please select valid loan type";
            return false;
        }else if (empLoan.getnLoanAmtxx() <= 0){
            message = "Please enter loan amount";
            return false;
        }else if (empLoan.getnPaymTerm() <= 0){
            message = "Please select loan terms";
            return false;
        }

        return true;
    }
    public Boolean SaveLoan(EEmpLoan empLoan){
        try {

            poEmpLoan.SaveLoan(empLoan);

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
                //todo update send status and transaction number
                poEmpLoan.UpdateSendStat(empLoan.getsTransNox(), loResult.getString("sTransNox"));
            }

            return true;
        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }
    public Boolean ApproveLoan(String sTransNox){
        poEmpLoan.ApproveLoanEntry(sTransNox);
        return true;
    }
}
