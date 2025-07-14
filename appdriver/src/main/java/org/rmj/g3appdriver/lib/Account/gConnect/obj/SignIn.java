package org.rmj.g3appdriver.lib.Account.gConnect.obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;
import org.rmj.g3appdriver.GConnect.Account.ClientSession;
import org.rmj.g3appdriver.GConnect.Api.GConnectApi;
import org.rmj.g3appdriver.GConnect.room.DataAccessObject.DClientInfo;
import org.rmj.g3appdriver.GConnect.room.Entities.EClientInfo;
import org.rmj.g3appdriver.GConnect.room.GGC_GConnectDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.lib.Account.Model.iAuth;
import org.rmj.g3appdriver.lib.Account.pojo.UserAuthInfo;

public class SignIn implements iAuth {
    private static final String TAG = SignIn.class.getSimpleName();

    private final Application instance;
    private final DClientInfo poDao;
    private final GConnectApi poApi;
    private final HttpHeaders poHeaders;
    private final AppConfigPreference poConfig;
    private final Telephony poDevID;
    private String message;

    public SignIn(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GConnectDB.getInstance(instance).EClientDao();
        this.poApi = new GConnectApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poDevID = new Telephony(instance);
    }

    @Override
    public int DoAction(Object args) {
        try{
            UserAuthInfo loInfo = (UserAuthInfo) args;
            if(!loInfo.isAuthInfoValid()){
                message = loInfo.getMessage();
                return 0;
            }

            JSONObject params = new JSONObject();
            params.put("user", loInfo.getEmail());
            params.put("pswd", loInfo.getPassword());

            String lsResponse = WebClient.sendRequest(
                    poApi.getSIGN_IN(),
                    params.toString(),
                    poHeaders.getHeaders());

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("error")) {

                JSONObject loError = loResponse.getJSONObject("error");
                String lsErrCode = loError.getString("code");
                if(lsErrCode.equalsIgnoreCase("40003")){
                    String lsOtp = loResponse.getString("otp");
                    String lsVfy = loResponse.getString("verify");
                    message = getErrorMessage(loError);
                    return 2;
                }

                message = getErrorMessage(loError);
                return 0;
            }

            ClientSession loAccount = ClientSession.getInstance(instance);
            loAccount.setUserID(loResponse.getString("sUserIDxx"));
            loAccount.setFullName(loResponse.getString("sUserName"));
            loAccount.setLoginStatus(true);

            EClientInfo loClient = new EClientInfo();

            loClient.setDateMmbr(loResponse.getString("dCreatedx"));
            loClient.setEmailAdd(loResponse.getString("sEmailAdd"));
            loClient.setUserName(loResponse.getString("sUserName"));
            loClient.setMobileNo(loResponse.getString("sMobileNo"));
            loClient.setUserIDxx(loResponse.getString("sUserIDxx"));
            poDao.RemoveSessions();
            poDao.insert(loClient);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return 0;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }
}
