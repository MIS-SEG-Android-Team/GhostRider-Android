package org.guanzongroup.com.creditevaluation.APITest;

import static org.junit.Assert.assertTrue;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class SubmitCIResult {

    private GCircleApi poApis;

    private static final String LIVE_LOGIN = "https://apps.guanzongroup.com.ph/security/mlogin.php";
    private static final String LOCAL_LOGIN = "http://192.168.10.141/security/mlogin.php";

    private static Map<String, String> headers = new HashMap<>();

    private static boolean isSuccess = false;

    private static String sClientID = "";
    private static String sLogNoxxx = "";
    private static String sUserIDxx = "";

    @Before
    public void setup() throws Exception{
//        poApis = new GCircleApi(true);

        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", "gRider");
        headers.put("g-api-client", sClientID);
        headers.put("g-api-imei", "e2be38386f093d59");
        headers.put("g-api-model", "sdk_gphone_x86");
        headers.put("g-api-mobile", "09171870011");
        headers.put("g-api-token", "dVIHzAJrQWSzzkxU4i-zUk:APA91bHs72uhiwuGtazaHhXmB44MuHXyQwyDZDHlDJaCtMQzqlph_hj6gwZpoIX1iyYHaA8UWDXke2ixvUJIH--hjHGnrM1UKecRF9H7haVvpAGc6D-JEAGD93G2sd1YeyUTAIqUAZB5");
        headers.put("g-api-user", sUserIDxx);
        headers.put("g-api-log", sLogNoxxx);
        headers.put("Accept", "application/json");
        headers.put("g-api-key", SQLUtil.dateFormat(Calendar.getInstance().getTime(), "yyyyMMddHHmmss"));
        String hash_toLower = SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key"));
        hash_toLower = hash_toLower.toLowerCase();
        headers.put("g-api-hash", hash_toLower);
    }

    @Test
    public void test01Login() throws Exception{
        JSONObject params = new JSONObject();
        params.put("user", "mikegarcia8748@gmail.com");
        params.put("pswd", "123456");
        String lsResponse = WebClient.sendRequest(LOCAL_LOGIN,
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                sClientID = loResponse.getString("sClientID");
                sLogNoxxx = loResponse.getString("sLogNoxxx");
                sUserIDxx = loResponse.getString("sUserIDxx");
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

//    @Test
    public void test02SubmitEvaluation() throws Exception {
        JSONObject params = new JSONObject();
        String lsAddressx = "{\"present_address\":{\"cAddrType\":null,\"sAddressx\":\"1\",\"sAddrImge\":null,\"nLatitude\":0.0,\"nLongitud\":0.0},\"primary_address\":{\"cAddrType\":null,\"sAddressx\":\"1\",\"sAddrImge\":null,\"nLatitude\":0.0,\"nLongitud\":0.0}}";
        String lsAssetsxx = "{\"sProprty1\":null,\"sProprty2\":null,\"sProprty3\":null,\"cWith4Whl\":null,\"cWith3Whl\":null,\"cWith2Whl\":null,\"cWithRefx\":\"1\",\"cWithTVxx\":\"1\",\"cWithACxx\":null}";
        String lsIncomexx = "{\"employed\":{\"sEmployer\":null,\"sWrkAddrx\":\"1\",\"sPosition\":null,\"nLenServc\":0.0,\"nSalaryxx\":-1.0},\"self_employed\":{\"sBusiness\":null,\"sBusAddrx\":null,\"nBusLenxx\":0.0,\"nBusIncom\":0.0,\"nMonExpns\":0.0},\"financed\":{\"sFinancer\":null,\"sReltnDsc\":null,\"sCntryNme\":null,\"nEstIncme\":0.0},\"pensioner\":{\"sPensionx\":null,\"nPensionx\":0.0}}";
        params.put("sTransNox", "CI4K52200070");
        params.put("sAddrFndg", new JSONObject(lsAddressx));
        params.put("sAsstFndg", new JSONObject(lsAssetsxx));
        params.put("sIncmFndg", new JSONObject(lsIncomexx));
        params.put("cHasRecrd", "0");
        params.put("sRecrdRem", "Barangay blotter due to unable to pay credits");
        params.put("sPrsnBrgy", "sample");
        params.put("sPrsnPstn", "sample");
        params.put("sPrsnNmbr", "09171870011");
        params.put("sNeighBr1", "sample neighbor 1");
        params.put("sNeighBr2", "sample neighbor 2");
        params.put("sNeighBr3", "sample neighbor 3");
        params.put("cTranStat", "2");
        params.put("sApproved", "M00117000702");
        params.put("dApproved", AppConstants.CURRENT_DATE());

        String lsResponse = WebClient.sendRequest(poApis.getUrlSubmitCIResult(),
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

//    @Test
    public void test03DownloadCIList() throws Exception{
        JSONObject params = new JSONObject();
        params.put("sEmployID", "M00117000702");

        String lsResponse = WebClient.sendRequest(poApis.getUrlDownloadBhPreview(),
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

//    @Test
    public void test04PostEvaluation() throws Exception{
        JSONObject params = new JSONObject();
        params.put("sTransNox", "CI4K52200069");
        params.put("dRcmdRcv1", new AppConstants().DATE_MODIFIED);
        params.put("dRcmdtnx1", new AppConstants().DATE_MODIFIED);
        params.put("cRcmdtnx1", "1");
        params.put("sRcmdtnx1", "sample");

        String lsResponse = WebClient.sendRequest(poApis.getUrlPostCiApproval(),
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

    @Test
    public void test05DownloadBH() throws Exception{
        JSONObject params = new JSONObject();
        params.put("sEmployID", "sEmployID");

        String lsResponse = WebClient.sendRequest(poApis.getUrlDownloadBhPreview(),
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

//    @Test
    public void test05PostBHEvaluation() throws Exception{
        JSONObject params = new JSONObject();
        params.put("sTransNox", "CI4K52200069");
        params.put("dRcmdRcv2", new AppConstants().DATE_MODIFIED);
        params.put("dRcmdtnx2", new AppConstants().DATE_MODIFIED);
        params.put("cRcmdtnx2", "1");
        params.put("sRcmdtnx2", "sample");

        String lsResponse = WebClient.sendRequest(poApis.getUrlPostBhApproval(),
                params.toString(), (HashMap<String, String>) headers);
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                isSuccess = true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }
}
