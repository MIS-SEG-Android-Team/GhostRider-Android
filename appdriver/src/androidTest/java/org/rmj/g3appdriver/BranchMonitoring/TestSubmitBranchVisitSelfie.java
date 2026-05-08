package org.rmj.g3appdriver.BranchMonitoring;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;

import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSubmitBranchVisitSelfie {

    private static Map<String, String> headers = new HashMap<>();

    private Application instance;
    private DImageInfo daoImage;

    @Before
    public void Setup(){

        //initialize headers
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", "gRider");
        headers.put("g-api-client", "");
        headers.put("g-api-imei", "e2be38386f093d59");
        headers.put("g-api-model", "sdk_gphone_x86");
        headers.put("g-api-mobile", "09171870011");
        headers.put("g-api-token", "dVIHzAJrQWSzzkxU4i-zUk:APA91bHs72uhiwuGtazaHhXmB44MuHXyQwyDZDHlDJaCtMQzqlph_hj6gwZpoIX1iyYHaA8UWDXke2ixvUJIH--hjHGnrM1UKecRF9H7haVvpAGc6D-JEAGD93G2sd1YeyUTAIqUAZB5");
        headers.put("g-api-user", "GAP023000374");
        headers.put("g-api-log", "");
        headers.put("Accept", "application/json");
        headers.put("g-api-key", SQLUtil.dateFormat(Calendar.getInstance().getTime(), "yyyyMMddHHmmss"));

        String hash_toLower = SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key"));
        hash_toLower = hash_toLower.toLowerCase();
        headers.put("g-api-hash", hash_toLower);

        instance = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        daoImage = GGC_GCircleDB.getInstance(instance).ImageInfoDao();
    }

    @Test
    public void TestSubmit(){

        try {

            JSONObject loParams = new JSONObject();

            //initialize other parameters
            loParams.put("sClientID", "GGC_BM016");
            loParams.put("sReferNox", "GAP023000374");
            loParams.put("sCategrID", "003");
            loParams.put("sScanndID", "");
            loParams.put("sImageNme", "GAP023000374_20260506_143705.png");
            loParams.put("sMD5Hashx", "603ed3f148b10e4db9b8bce82400b38f");
            loParams.put("sImagePth", "/storage/emulated/0/Android/data/org.rmj.guanzongroup.ghostrider.epacss/files/Branchvisit/003/MX0126000001/GAP023000374_20260506_143705.png");
            loParams.put("dImgeDate", "2026-05-06");

            String lsResult = WebClient.sendRequest("http://192.165.29.94/GMC%20SEG%20Folder%20-%20PHP/eclipse-workspace/apps/gcircle/branchmonitoring/submit_branch_visit_selfie.php",
                    loParams.toString(), (HashMap<String, String>) headers);

            System.out.println(lsResult);
            if (lsResult == null){
                System.out.println("Server no response");
                return;
            }

            JSONObject loResponse = new JSONObject(lsResult);
            String lsResponse = loResponse.getString("result");

            if(lsResponse.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                System.out.println(getErrorMessage(loError));
                return;
            }

            String lsTransNox = loResponse.getString("sTransNox");

            System.out.println(lsTransNox);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
