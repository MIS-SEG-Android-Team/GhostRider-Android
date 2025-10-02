package org.rmj.g3appdriver.lib.Notifications;

import static org.junit.Assert.assertTrue;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.utils.SQLUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PanaloNotificationSender {

    /**
     *
     * @param app set recipient product id
     * @param userid set recipient user id
     * @param title notification title
     * @param message notification message
     * type of Panalo notification 0 = raffle, 1 = reward, 2 = claim, 3 = redeemed, 4 = warning
     * @param status status of panalo raffle 0 = No Status, 1 = Starting Soon, 2 = Started, 3 = Ended
     * @return returns true if process has been successfully executed.
     */
    public static boolean SendSystemPanaloRaffleNotification(String app,
                                                       String userid,
                                                       String title,
                                                       String message,
                                                       int status){
        try{
            String sURL = "https://apps.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);

            JSONObject loInfo = new JSONObject();
            loInfo.put("module", "002");
            loInfo.put("panalo", "0");

            JSONObject loData = new JSONObject();
            loData.put("status", status);

            loInfo.put("data", loData);

            String lsInfoxx = loInfo.toJSONString();

            JSONObject param = new JSONObject();
            param.put("type", "00008");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", lsInfoxx);

            String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     *
     * @param app set recipient product id
     * @param userid set recipient user id
     * @param title notification title
     * @param message notification message
     * @return returns true if process has been successfully executed.
     */
    public static boolean SendSystemPanaloRewardNotification(String app,
                                                             String userid,
                                                             String title,
                                                             String message){
        try{
            String sURL = "https://apps.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);

            JSONObject loInfo = new JSONObject();
            loInfo.put("module", "001");
            loInfo.put("panalo", "1");

            JSONObject loData = new JSONObject();
            loData.put("sReferNox", "MX01123456789");
            loData.put("cTranStat", "0");

            loInfo.put("data", loData);

            String lsInfoxx = loInfo.toJSONString();

            JSONObject param = new JSONObject();
            param.put("type", "00008");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", lsInfoxx);

            String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     *
     * @param app set recipient product id
     * @param userid set recipient user id
     * @param title notification title
     * @param message notification message
     * @return returns true if process has been successfully executed.
     */
    public static boolean SendSystemBranchOpeningNotification(String app,
                                                             String userid,
                                                             String title,
                                                             String message){
        try{
            String sURL = "https://apps.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);

            JSONObject loInfo = new JSONObject();
            loInfo.put("module", "00002");

            String lsTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault()).format(Calendar.getInstance().getTime());

            JSONObject loData = new JSONObject();
            loData.put("dTransact", AppConstants.CURRENT_DATE());
            loData.put("sBranchCD", "M174");
            loData.put("sTimeOpen", "08:30:00 AM");
            loData.put("sOpenNowx", lsTime);

            loInfo.put("data", loData);

            String lsInfoxx = loInfo.toJSONString();

            JSONObject param = new JSONObject();
            param.put("type", "00000");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", lsInfoxx);

            String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean SendRegularSystemNotification(String app,
                                                        String userid,
                                                        String title,
                                                        String message){
        try{
            String sURL = "https://apps.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);

//            rcpt = new JSONObject();
//            rcpt.put("app", app);
//            rcpt.put("user", "GAP0190004");
//            rcpts.add(rcpt);
//
//            rcpt = new JSONObject();
//            rcpt.put("app", app);
//            rcpt.put("user", "GAP021002985");
//            rcpts.add(rcpt);

            JSONObject param = new JSONObject();
            param.put("type", "00000");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", null);

            String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
