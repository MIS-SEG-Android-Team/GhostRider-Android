package org.rmj.guanzongroup.ghostrider.ahmonitoring;

import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class CashCountAPITest {
    private static final String LOCAL_LOGIN = "http://192.168.10.141/security/mlogin.php";
    public static String IMPORT_FOR_EVALUATION = "http://192.168.10.141/integsys/evaluator/import_for_evaluations.php";

    private static Map<String, String> headers = new HashMap<>();
    private static boolean isSuccess = false;

    private static String sClientID = "";
    private static String sLogNoxxx = "";
    private static String sUserIDxx = "";

    @Before
    public void setup() throws Exception{
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
                String lsMessage = loError.getString("message");
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }

    @Test
    public void test03DownloadForEvaluator() throws Exception {
        JSONObject params = new JSONObject();
        params.put("sEmployID", "M00119001131");
        String lsResponse = WebClient.sendRequest(IMPORT_FOR_EVALUATION,
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
                String lsMessage = loError.getString("message");
                isSuccess = false;
            }
        }
        assertTrue(isSuccess);
    }
}
