package org.rmj.g3appdriver.lib.Notifications.Obj.Receiver;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DNotificationReceiver;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchOpenMonitor;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationRecipient;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationUser;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Notifications.NOTIFICATION_STATUS;
import org.rmj.g3appdriver.lib.Notifications.RemoteMessageParser;
import org.rmj.g3appdriver.lib.Notifications.model.iNotification;
import org.rmj.g3appdriver.lib.Notifications.pojo.NotificationItemList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NMM_Regular implements iNotification {
    private static final String TAG = NMM_Regular.class.getSimpleName();

    private final DNotificationReceiver poDao;
    protected final HttpHeaders poHeaders;
    protected final GCircleApi poApi;

    protected String message;

    public NMM_Regular(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).ntfReceiverDao();
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poApi = new GCircleApi(instance);
    }

    @Override
    public String Save(RemoteMessage foVal) {
        try{
            RemoteMessageParser loParser = new RemoteMessageParser(foVal);
            String lsMesgIDx = loParser.getValueOf("transno");

            if(poDao.CheckNotificationIfExist(lsMesgIDx) >= 1){
                String lsStatus = loParser.getValueOf("status");
                poDao.updateNotificationStatusFromOtherDevice(lsMesgIDx, lsStatus);
            } else {
                ENotificationMaster loMaster = new ENotificationMaster();
                loMaster.setTransNox(CreateUniqueID());
                loMaster.setMesgIDxx(loParser.getValueOf("transno"));
                loMaster.setParentxx(loParser.getValueOf("parent"));
                loMaster.setCreatedx(loParser.getValueOf("stamp"));
                loMaster.setAppSrcex(loParser.getValueOf("appsrce"));
                loMaster.setCreatrID(loParser.getValueOf("srceid"));
                loMaster.setCreatrNm(loParser.getValueOf("srcenm"));
                loMaster.setDataSndx(loParser.getValueOf("infox"));
                loMaster.setMsgTitle(loParser.getDataValueOf("title"));
                loMaster.setMessagex(loParser.getDataValueOf("message"));
                loMaster.setMsgTypex(loParser.getValueOf("msgmon"));

                ENotificationRecipient loRecpnt = new ENotificationRecipient();
                loRecpnt.setTransNox(loParser.getValueOf("transno"));
                loRecpnt.setAppRcptx(loParser.getValueOf("apprcpt"));
                loRecpnt.setRecpntID(loParser.getValueOf("rcptid"));
                loRecpnt.setRecpntNm(loParser.getValueOf("rcptnm"));
                loRecpnt.setMesgStat(loParser.getValueOf("status"));
                loRecpnt.setReceived(new AppConstants().DATE_MODIFIED);
                loRecpnt.setTimeStmp(new AppConstants().DATE_MODIFIED);

                poDao.insert(loMaster);
                poDao.insert(loRecpnt);

                if(!"SYSTEM".equalsIgnoreCase(loParser.getValueOf("srceid"))) {
                    ENotificationUser loUser = new ENotificationUser();
                    loUser.setUserIDxx(loParser.getValueOf("srceid"));
                    loUser.setUserName(loParser.getValueOf("srcenm"));

                    if (poDao.CheckIfUserExist(loParser.getValueOf("srceid")) == null) {
                        poDao.insert(loUser);
                    }
                }

                String lsData = loParser.getValueOf("infox");

                if(lsData == null){
                    return lsMesgIDx;
                }

                if(lsData.isEmpty()){
                    return lsMesgIDx;
                }

                JSONObject loJson = new JSONObject(lsData);

                String lsModule = loJson.getString("module");

                switch (lsModule){
                    case "00001":
//                        SaveTableUpdate(lsData);
                        Log.e(TAG, "No corresponding function for module 00001");
                        break;
                    case "00002":
                        SaveBranchOpening(lsData);
                        break;
                    default:
                        break;
                }
            }
            return lsMesgIDx;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public ENotificationMaster SendResponse(String mesgID, NOTIFICATION_STATUS status) {
        try{
            ENotificationRecipient loDetail = poDao.GetNotification(mesgID);

            if(loDetail == null){
                message = "Unable to find notification for update.";
                return null;
            }

            if(loDetail.getMesgStat().equalsIgnoreCase("3")){
                message = "Message is already read.";
                return null;
            }

            String lsTranStat = "";

            switch (status){
                case OPEN:
                    lsTranStat = "0";
                    break;
                case DELIVERED:
                    lsTranStat = "1";
                    break;
                case RECEIVED:
                    lsTranStat = "2";
                    break;
                case READ:
                    lsTranStat = "3";
                    break;
                case DELETED:
                    lsTranStat = "4";
                    break;
            }

            loDetail.setMesgStat(lsTranStat);
            loDetail.setReadxxxx(new AppConstants().DATE_MODIFIED);
            poDao.update(loDetail);

            JSONObject params = new JSONObject();
            params.put("transno", mesgID);
            params.put("status", lsTranStat);
            params.put("stamp", new AppConstants().DATE_MODIFIED);
            params.put("infox", "");

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSendResponse(),
                    params.toString(),
                    poHeaders.getHeaders());
            if(lsResponse == null){
                message = "Server no response while sending response.";
                return null;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (!lsResult.equalsIgnoreCase("success")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return null;
            }

            loDetail.setStatSent("1");
            poDao.update(loDetail);
            return poDao.CheckIfMasterExist(mesgID);
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public boolean CreateNotification(String title, String message) {
        try{

            return true;
        } catch (Exception e){
            e.printStackTrace();
            this.message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public LiveData<List<NotificationItemList>> GetNotificationList() {
        return null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    protected String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetNotificationCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return lsUniqIDx;
    }

    private boolean SaveBranchOpening(String args){
        try{
            JSONObject loJson = new JSONObject(args);
            JSONObject loData = loJson.getJSONObject("data");
            EBranchOpenMonitor loDetail = new EBranchOpenMonitor();
            loDetail.setBranchCD(loData.getString("sBranchCD"));
            loDetail.setTransact(loData.getString("dTransact"));
            loDetail.setTimeOpen(loData.getString("sTimeOpen"));
            loDetail.setOpenNowx(loData.getString("sOpenNowx"));
            poDao.SaveBranchOpening(loDetail);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }
}
