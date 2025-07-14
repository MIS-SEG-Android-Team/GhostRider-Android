package org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.SQLUtil;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pacita {
    private static final String TAG = Pacita.class.getSimpleName();

    private final DPacita poDao;
    private final HttpHeaders poHeaders;
    private final GCircleApi poApi;

    private String message;

    public Pacita(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).pacitaDao();
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poApi = new GCircleApi(instance);
    }

    public String getMessage() {
        return message;
    }

    public LiveData<List<EBranchInfo>> GetBranchesList(){
        return poDao.GetBranchList();
    }

    public LiveData<List<DPacita.BranchRecords>> GetBranchRecords(String BranchCD){
        return poDao.GetBranchRecords(BranchCD);
    }

    public LiveData<List<DPacita.RecentRecords>> GetRecentRecords(){
        return poDao.GetRecentRecords();
    }

    /**
     *
     * @return true if the operation for importing pacita rules is successful else false if failed.
     * call getMessage() method to identify the error message
     */
    public boolean ImportPacitaRules(){
        try{
            JSONObject params = new JSONObject();
            String lsTimeStmp = poDao.GetLatestRecordTimeStamp();
            if(lsTimeStmp != null){
                params.put("dTimeStmp", lsTimeStmp);
            }

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlGetPacitaRules(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                Log.e(TAG, message);
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("payload");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);

                int lnEntryNo = loJson.getInt("nEntryNox");

                EPacitaRule loDetail = poDao.GetPacitaRule(lnEntryNo);

                if(loDetail == null){

                    EPacitaRule loInfo = new EPacitaRule();
                    loInfo.setEntryNox(loJson.getInt("nEntryNox"));
                    loInfo.setFieldNmx(loJson.getString("sFieldNmx"));
                    loInfo.setMaxValue(loJson.getDouble("nMaxValue"));
                    loInfo.setRecdStat(loJson.getString("cRecdStat"));
//                    loInfo.setParentxx(loJson.getString("cParentxx"));
                    loInfo.setModified(loJson.getString("dModified"));
                    loInfo.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Save(loInfo);
                    Log.d(TAG, "Pacita rule info has been saved.");
                } else {

                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)) {
                        loDetail.setEntryNox(loJson.getInt("nEntryNox"));
                        loDetail.setFieldNmx(loJson.getString("sFieldNmx"));
                        loDetail.setMaxValue(loJson.getDouble("nMaxValue"));
                        loDetail.setRecdStat(loJson.getString("cRecdStat"));
                        loDetail.setParentxx(loJson.getString("cParentxx"));
                        loDetail.setModified(loJson.getString("dModified"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.Update(loDetail);
                        Log.d(TAG, "Pacita rule info has been updated.");
                    }
                }
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public LiveData<List<EPacitaRule>> GetPacitaRules(){
        return poDao.GetPacitaRules();
    }

    /**
     *
     * @param BranchCD pass the branch code in order to identify which record of
     *                 evaluations should be retrieve from server and local.
     * @return true if import operation succeeds else false if failed.
     * Call the getMessage() method to identify the error message.
     */
    public boolean ImportPacitaEvaluations(String BranchCD){
        try{
            JSONObject params = new JSONObject();
            params.put("sBranchCD", BranchCD);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlGetPacitaEvaluations(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                Log.e(TAG, message);
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("payload");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);

                String lsTransNox = loJson.getString("sTransNox");
                EPacitaEvaluation loDetail = poDao.CheckEvaulationRecord(lsTransNox);

                if(loDetail == null){

                    EPacitaEvaluation loInfo = new EPacitaEvaluation();
                    loInfo.setTransNox(loJson.getString("sTransNox"));
                    loInfo.setTransact(loJson.getString("dTransact"));
                    loInfo.setUserIDxx(poDao.GetUserID());
                    loInfo.setBranchCD(BranchCD);
                    loInfo.setPayloadx(loJson.getString("sPayloadx"));
                    loInfo.setRatingxx(loJson.getDouble("nRatingxx"));
                    loInfo.setModified(loJson.getString("dModified"));
                    loInfo.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Save(loInfo);
                    Log.d(TAG, "Pacita evaluation info has been saved.");
                } else {

                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)) {
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setUserIDxx(poDao.GetUserID());
                        loDetail.setBranchCD(BranchCD);
                        loDetail.setPayloadx(loJson.getString("sPayloadx"));
                        loDetail.setRatingxx(loJson.getDouble("nRatingxx"));
                        loDetail.setModified(loJson.getString("dModified"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.Update(loDetail);
                        Log.d(TAG, "Pacita evaluation info has been updated.");
                    }
                }
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean UpdateBranchRate(String TransNox, String loPayloadx){
        try{
            EPacitaEvaluation loDetail = poDao.CheckEvaulationRecord(TransNox);

            if(loDetail == null){
                message = "Unable to rate branch. No record found to update. Please try restarting the app.";
                return false;
            }

            /*String lsPayload = loDetail.getPayloadx();

            JSONArray laJson = new JSONArray(lsPayload);

            for(int x = 0; x < laJson.length(); x++){

                int lnEntryNo = laJson.getJSONObject(x).getInt("nEntryNox");

                if(EntryNox == lnEntryNo){

                    laJson.getJSONObject(x).put("xRatingxx", Result);
                    Log.d(TAG, "Entry No. " + EntryNox + ", has been updated!");
                    break;
                }
            }*/

            loDetail.setPayloadx(loPayloadx);
            poDao.Update(loDetail);

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    /**
     *
     * @param TransNox pass the Transaction No. of the evaluation record to identify which record will be retrieve and upload to server.
     * @return true if the operation is successful else false if failed.
     * call the getMessage() method to identify the error message.
     */
    public boolean SaveBranchRatings(String TransNox){
        try{
            EPacitaEvaluation loDetail = poDao.GetEvaluationForPosting(TransNox);

            if(loDetail == null){
                message = "Unable to find record for posting.";
                return false;
            }

            if(loDetail.getTranStat().equalsIgnoreCase("1")){
                message = "Evaluation record is already posted.";
                return false;
            }

            String lsPayload = loDetail.getPayloadx();
            JSONArray laJson = new JSONArray(lsPayload);
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);
                if(loJson.getString("xRatingxx").isEmpty()){
                    message = "Please finish rating before saving.";
                    return false;
                }
            }

            JSONObject params = new JSONObject();
            params.put("sBranchCD", loDetail.getBranchCD());
            params.put("dTransact", loDetail.getTransact());

            JSONObject loPayload = new JSONObject();
            loPayload.put("sEvalType", loDetail.getEvalType());

            JSONArray laPayload = new JSONArray(loDetail.getPayloadx());
            loPayload.put("sPayloadx", laPayload);

            params.put("sPayloadxx", loPayload);

            Log.d(TAG, params.toString());
            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSubmitPacitaResult(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                Log.e(TAG, message);
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            String lsTransNo = loResponse.getString("sTransNox");
            Log.d(TAG, "Server transaction no.: " + lsTransNo);
            poDao.UpdatePosted(loDetail.getTransNox(), lsTransNo);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    /**
     *
     * @param BranchCD pass the branch code on parameter to determine which branch is needed for evaluation
     * @return returns Transaction No. of the evaluation record else null if something went wrong.
     * Call the getMessage() method to identify the error message.
     *
     * Check the getTranStat value if the value is equal to 1 means the evaluation is no longer available to be update by the user.
     * else if zero evaluation is still able to be update by the user.
     *
     *
     */
    public String InitializePacitaEvaluation(String BranchCD){
        try{
            EPacitaEvaluation loDetail = poDao.GetEvaluationForInitialization(BranchCD, AppConstants.CURRENT_DATE());

            if(loDetail == null){
                return CreateNewEvaluation(BranchCD);
            }

            if(loDetail.getTranStat().equalsIgnoreCase("1")){
                message = "Evaluation for this branch was already posted.";
                return null;
            }

            if(!loDetail.getTransact().equalsIgnoreCase(AppConstants.CURRENT_DATE())){
                poDao.ResetPacitaRewardForBranch(loDetail.getTransNox());
                Log.e(TAG, "Evaluation record has been reset.");
                return CreateNewEvaluation(BranchCD);
            }

            return loDetail.getTransNox();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public LiveData<EPacitaEvaluation> GetEvaluationRecord(String TransNox){
        return poDao.GetPacitaEvaluation(TransNox);
    }

    private String CreateNewEvaluation(String BranchCD){
        try{
            //Create a JSONObject for all the rules on pacita_rule
            JSONArray laJson = new JSONArray();
            List<Integer> loFields = poDao.GetPacitaRulesEntryNo();

            if(loFields == null){
                message = "Unable to find pacita rules.";
                return null;
            }

            if(loFields.size() == 0){
                message = "Unable to find pacita rules.";
                return null;
            }

            for (int x = 0; x < loFields.size(); x++){
                JSONObject loJson = new JSONObject();
                loJson.put("nEntryNox", loFields.get(x));
                loJson.put("xRatingxx", "");
                laJson.put(loJson);
            }

            EPacitaEvaluation loInfo = new EPacitaEvaluation();
            String lsTransNo = CreateUniqueID();
            String lsDeptIDxx = poDao.GetDepartmentID();

            Log.d(TAG, "Department ID: " + lsDeptIDxx);

            loInfo.setTransNox(lsTransNo);
            loInfo.setTransact(AppConstants.CURRENT_DATE());
            loInfo.setPayloadx(laJson.toString());
            loInfo.setRatingxx(0.0);
            loInfo.setBranchCD(BranchCD);
            loInfo.setUserIDxx(poDao.GetUserID());
            loInfo.setEvalType(lsDeptIDxx);
            loInfo.setTimeStmp(new AppConstants().DATE_MODIFIED);
            poDao.Save(loInfo);
            return loInfo.getTransNox();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    private String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }
}
