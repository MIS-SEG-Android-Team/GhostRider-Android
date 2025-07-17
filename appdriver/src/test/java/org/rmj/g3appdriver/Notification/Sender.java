package org.rmj.g3appdriver.Notification;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.utils.SQLUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Sender {

    private static boolean isSuccess = false;

    @Test
    public void test01SenderNotification() throws Exception{
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
        rcpt.put("app", "gRider");
        rcpt.put("user", "GAP021002961");
        rcpts.add(rcpt);

        //Create the parameters needed by the API
        JSONObject param = new JSONObject();
        param.put("type", "00008");
        param.put("parent", null);
        param.put("title", "Guanzon Panalo");
        param.put("message", "Congratulations!");
        param.put("rcpt", rcpts);

        JSONParser oParser = new JSONParser();
        JSONObject json_obj = null;

        String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
        if(response == null){
            System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
        }
        //json_obj = (JSONObject) oParser.parse(response);
        //System.out.println(json_obj.toJSONString());
        isSuccess = true;
        System.out.println(response);
        assertTrue(isSuccess);
    }

    @Test
    public void test02SendDataNotification() throws Exception{
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
        rcpt.put("app", "gRider");
        rcpt.put("user", "GAP021002961");
        rcpts.add(rcpt);

        for(int x = 0; x < 1; x++){
            //Create the parameters needed by the API
            JSONObject param = new JSONObject();
            param.put("type", "00008");
            param.put("parent", null);
            param.put("title", "Guanzon Panalo");
            param.put("message", "Your panalo reward has been claimed at Guanzon Merchandising Corporation Dagupan City.");
            param.put("rcpt", rcpts);
            param.put("infox", CreatePanaloNotification());


            JSONParser oParser = new JSONParser();
            JSONObject json_obj = null;

            String response = WebClient.sendRequest(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }
            //json_obj = (JSONObject) oParser.parse(response);
            //System.out.println(json_obj.toJSONString());

            isSuccess = true;
            System.out.println(response);
            assertTrue(isSuccess);
        }
    }

    private static String CreatePanaloNotification(){
        JSONObject loJSON = new JSONObject();

//        loJSON.put("panalo", "reward");
        loJSON.put("panalo", "claim");
//        loJSON.put("panalo", "redeemed");
//        loJSON.put("panalo", "warning");

        JSONObject loPanalo = new JSONObject();
        loPanalo.put("sReferNox", "MX01123456789");

        loJSON.put("data", loPanalo);

        return loJSON.toJSONString();
    }
}
