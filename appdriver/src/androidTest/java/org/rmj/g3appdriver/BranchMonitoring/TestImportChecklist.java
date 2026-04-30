package org.rmj.g3appdriver.BranchMonitoring;

/**
 * Test By: Guillier
 * Test Date: 04/30/2026**/

import android.app.Application;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class TestImportChecklist {

    private Application instance;
    private DBranchVisitChecklist dao;
    private static Map<String, String> headers = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        instance = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        dao = GGC_GCircleDB.getInstance(instance).branchChecklistDao();

        //initialize headers
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", "gRider");
        headers.put("g-api-client", "");
        headers.put("g-api-imei", "e2be38386f093d59");
        headers.put("g-api-model", "sdk_gphone_x86");
        headers.put("g-api-mobile", "09171870011");
        headers.put("g-api-token", "dVIHzAJrQWSzzkxU4i-zUk:APA91bHs72uhiwuGtazaHhXmB44MuHXyQwyDZDHlDJaCtMQzqlph_hj6gwZpoIX1iyYHaA8UWDXke2ixvUJIH--hjHGnrM1UKecRF9H7haVvpAGc6D-JEAGD93G2sd1YeyUTAIqUAZB5");
        headers.put("g-api-user", "");
        headers.put("g-api-log", "");
        headers.put("Accept", "application/json");
        headers.put("g-api-key", SQLUtil.dateFormat(Calendar.getInstance().getTime(), "yyyyMMddHHmmss"));

        String hash_toLower = SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key"));
        hash_toLower = hash_toLower.toLowerCase();
        headers.put("g-api-hash", hash_toLower);
    }

    @Test
    public void TestDownloadChecklist() throws IOException, JSONException {

        String lsResult = WebClient.sendRequest("http://192.165.10.175/GMC%20SEG%20Folder%20-%20PHP/eclipse-workspace/apps/gcircle/branchmonitoring/download_checklist.php",
                new JSONObject().toString(), (HashMap<String, String>) headers);

        if (lsResult == null || lsResult.isEmpty()){
            return;
        }

        JSONObject loResult = new JSONObject(lsResult);
        if (loResult.getString("result").equalsIgnoreCase("error")){
            return;
        }

        JSONArray laChecklist= loResult.getJSONArray("detail");
        for (int i = 0; i < laChecklist.length(); i++){
            JSONObject loChecklist = laChecklist.getJSONObject(i);

            loChecklist.getString("sCategrID");
            loChecklist.getString("sDescript");
            loChecklist.getString("cRecdStat");

            EBranchVisitChecklist loEntity = new EBranchVisitChecklist();
            loEntity.setsCategrID(loChecklist.getString("sCategrID"));
            loEntity.setsDescript(loChecklist.getString("sDescript"));
            loEntity.setcRecdStat(loChecklist.getString("cRecdStat"));

            dao.Save(loEntity);
        }

        for (EBranchVisitChecklist loEntity : dao.GetChecklistForTest()){
            System.out.println(loEntity.getsDescript());
        }

    }
}
