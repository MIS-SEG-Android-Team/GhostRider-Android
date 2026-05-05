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

public class TestSubmitBranchVisit {

    private static Map<String, String> headers = new HashMap<>();

    private Application instance;
    private DBranchVisitMaster daoMaster;
    private DBranchVisitDetail daoDetail;
    private  EBranchVisitMaster foMaster;
    private  List<DBranchVisitDetail.BranchVisitDetail> faDetail;

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
        daoMaster = GGC_GCircleDB.getInstance(instance).branchVisitMasterDao();
        daoDetail = GGC_GCircleDB.getInstance(instance).branchVisitDetailDao();

        //initialize parameters
        foMaster = new EBranchVisitMaster();
        faDetail = new ArrayList<>();

        foMaster.setsTransNox("M0162026050214022601");
        foMaster.setsBranchCd("M001");
        foMaster.setdTransact("2026-05-02");
        foMaster.setcTranStat("0");
        foMaster.setsUserIDxx("GAP023000374");

        DBranchVisitDetail.BranchVisitDetail loItem1 = new DBranchVisitDetail.BranchVisitDetail();
        loItem1.sCategrID = "001";
        loItem1.sRemarksx = "test remarks 1";
        loItem1.sDescript = "Corporate Wall Groupie";

        DBranchVisitDetail.BranchVisitDetail loItem2 = new DBranchVisitDetail.BranchVisitDetail();
        loItem2.sCategrID = "002";
        loItem2.sRemarksx = "test remarks 2";
        loItem2.sDescript = "Showroom";

        faDetail.add(loItem1);
        faDetail.add(loItem2);
    }

    @Test
    public void TestSubmit(){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", foMaster.getsTransNox());
            loParams.put("sBranchCd", foMaster.getsBranchCd());
            loParams.put("cTranStat", foMaster.getcTranStat());

            JSONArray laDetails = new JSONArray();
            for (DBranchVisitDetail.BranchVisitDetail loDetail : faDetail){

                JSONObject loParam = new JSONObject();
                loParam.put("sCategrID", loDetail.sCategrID);
                loParam.put("sRemarksx", loDetail.sRemarksx);

                laDetails.put(loParam);
            }
            loParams.put("sPayloadxx", laDetails);

            String lsResult = WebClient.sendRequest("http://192.165.29.233/GMC%20SEG%20Folder%20-%20PHP/eclipse-workspace/apps/gcircle/branchmonitoring/submit_branch_visit.php",
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
