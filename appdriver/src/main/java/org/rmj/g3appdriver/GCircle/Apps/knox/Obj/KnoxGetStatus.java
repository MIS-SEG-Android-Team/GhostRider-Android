package org.rmj.g3appdriver.GCircle.Apps.knox.Obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.knox.KnoxResult;
import org.rmj.g3appdriver.GCircle.Apps.knox.model.SamsungKnox;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConstants;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class KnoxGetStatus extends SamsungKnox {
    private static final String TAG = KnoxUnlock.class.getSimpleName();

    private String psDeviceID,
                    psStatusxx,
                    psDetailsx,
                    psdUpdatex;

    public KnoxGetStatus(Application instance) {
        super(instance);
    }

    @Override
    public String GetResult(String DeviceID) {
        try{
            JSONObject loJSon = new JSONObject();
            JSONObject loParam = new JSONObject();
            loJSon.put("deviceUid", DeviceID);
            loJSon.put("pageNum", 0);
            loJSon.put("pageSize", 30);
            loParam.put("request", AppConstants.GET_DEVICE_LOG_REQUEST);
            loParam.put("param", loJSon.toString());

            String lsResponse = WebClient.sendRequest(
                                        poApi.getUrlKnox(),
                                        loParam.toString(),
                                        poHeaders.getHeaders());


            Log.d(TAG, lsResponse);

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return null;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("FAIL") ||
                    lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = KnoxResult.getMessage(loError);
                return null;
            }

            JSONArray jsonArray = loResponse.getJSONArray("deviceLogs");
            long maxValue = (long) jsonArray.getJSONObject(jsonArray.length() - 1).get("time");

            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject loJson = new JSONObject(jsonArray.getString(x));

                long CurrMax = (long) loJson.get("time");
                if (CurrMax > maxValue) {
                    maxValue = CurrMax;
                    psDeviceID = DeviceID;
                    psStatusxx = loJson.getString("deviceStatus");
                    psDetailsx = loJson.getString("details");
                    psdUpdatex = getReadableDateFormat(loJson.getLong("time"));

                    if (x == 0){
                        if (psStatusxx.equalsIgnoreCase("Activating")){

                            if (new JSONObject(jsonArray.getString(x + 1)).get("deviceStatus").toString()
                                    .equalsIgnoreCase("Active")){

                                psStatusxx = loJson.getString("deviceStatus");
                                break;
                            }
                        }
                    }
                }
            }

            return "Device status has been retrieve.";
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String GetResult(String DeviceID, String Remarks) {
        return null;
    }

    public String getDeviceID() {
        return psDeviceID;
    }

    public String getStatus() {
        return psStatusxx;
    }

    public String getDetails() {
        return psDetailsx;
    }

    public String getLastUpdate() {
        return psdUpdatex;
    }

    private String getReadableDateFormat(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH);
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        return months[month] + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR);
    }
}
