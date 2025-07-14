package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core;

import static org.junit.Assert.*;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class DownloadPostDCPWithoutRemCodeTest {

    private static final String LOCAL_LOGIN = "http://192.168.10.141/security/mlogin.php";

    private Application instance;

    private AppConfigPreference poConfig;
    private EmployeeSession poSession;
    private HttpHeaders poHeaders;
    private DcpManager poDcp;

    private EmployeeMaster poUser;
    private Telephony poTlphny;

    private boolean isSuccess = false;

    @Before
    public void setUp() throws Exception {
        instance = ApplicationProvider.getApplicationContext();
        poConfig = AppConfigPreference.getInstance(instance);
        poHeaders = HttpHeaders.getInstance(instance);
        poSession = EmployeeSession.getInstance(instance);
        poDcp = new DcpManager(instance);
        poUser = new EmployeeMaster(instance);
        poTlphny = new Telephony(instance);
        poConfig.setTestCase(true);
        poConfig.setMobileNo("09171870011");
        poConfig.setProductID("gRider");
        poConfig.setAppToken("f7qNSw8TRPWHSCga0g8YFF:APA91bG3i_lBPPWv9bbRasNzRH1XX1y0vzp6Ct8S_a-yMPDvSmud8FEVPMr26zZtBPHq2CmaIw9Rx0MZmf3sbuK44q3vQemUBoPPS4Meybw8pnTpcs3p0VbiTuoLHJtdncC6BgirJxt3");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test01LoginAccount() throws Exception{
        //Login Account for Authorization
        JSONObject params = new JSONObject();
        params.put("user", "mikegarcia8748@gmail.com");
        params.put("pswd", "123456");
        String lsResponse = WebClient.sendRequest(LOCAL_LOGIN,
                params.toString(), poHeaders.getHeaders());
        if(lsResponse == null){
            isSuccess = false;
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("success")) {
                EEmployeeInfo employeeInfo = new EEmployeeInfo();
                employeeInfo.setClientID(loResponse.getString("sClientID"));
                employeeInfo.setBranchCD(loResponse.getString("sBranchCD"));
                employeeInfo.setBranchNm(loResponse.getString("sBranchNm"));
                employeeInfo.setLogNoxxx(loResponse.getString("sLogNoxxx"));
                employeeInfo.setUserIDxx(loResponse.getString("sUserIDxx"));
                employeeInfo.setEmailAdd(loResponse.getString("sEmailAdd"));
                employeeInfo.setUserName(loResponse.getString("sUserName"));
                employeeInfo.setUserLevl(loResponse.getString("nUserLevl"));
                employeeInfo.setDeptIDxx(loResponse.getString("sDeptIDxx"));
                employeeInfo.setPositnID(loResponse.getString("sPositnID"));
                employeeInfo.setEmpLevID(loResponse.getInt("sEmpLevID"));
                employeeInfo.setAllowUpd(loResponse.getString("cAllowUpd"));
                employeeInfo.setEmployID(loResponse.getString("sEmployID"));
                employeeInfo.setDeviceID(poTlphny.getDeviceID());
                employeeInfo.setModelIDx(Build.MODEL);
                employeeInfo.setMobileNo(poConfig.getMobileNo());
                employeeInfo.setLoginxxx(new AppConstants().DATE_MODIFIED);
                employeeInfo.setSessionx(AppConstants.CURRENT_DATE());
//                poUser.insertEmployee(employeeInfo);

                String lsClientx = loResponse.getString("sClientID");
                String lsUserIDx = loResponse.getString("sUserIDxx");
                String lsUserNme = loResponse.getString("sUserName");
                String lsLogNoxx = loResponse.getString("sLogNoxxx");
                String lsBranchx = loResponse.getString("sBranchCD");
                String lsBranchN = loResponse.getString("sBranchNm");
                String lsDeptIDx = loResponse.getString("sDeptIDxx");
                String lsEmpIDxx = loResponse.getString("sEmployID");
                String lsPostIDx = loResponse.getString("sPositnID");
                String lsEmpLvlx = loResponse.getString("sEmpLevID");
                isSuccess = true;

                poSession.initUserSession(lsUserIDx, lsUserNme,lsClientx, lsLogNoxx, lsBranchx, lsBranchN, lsDeptIDx, lsEmpIDxx, lsPostIDx, lsEmpLvlx, "1");
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = getErrorMessage(loError);
                isSuccess = false;
            }
        }
    }

    @Test
    public void test04PostLRDCPCollectionWithoutRemCode() throws Exception{
        poDcp.PostLRDCPCollection(new DcpManager.OnActionCallback() {
            @Override
            public void OnSuccess(String args) {
                isSuccess = true;
            }

            @Override
            public void OnFailed(String message) {
                isSuccess = false;
            }
        });
        assertTrue(isSuccess);
    }

    @Test
    public void test05PostDcpMaster() throws Exception{
        poDcp.PostDcpMaster(new DcpManager.OnActionCallback() {
            @Override
            public void OnSuccess(String args) {
                isSuccess = true;
            }

            @Override
            public void OnFailed(String message) {
                isSuccess = false;
            }
        });
        assertTrue(isSuccess);
    }
}