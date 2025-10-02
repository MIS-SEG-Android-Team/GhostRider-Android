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

public class PanaloRaffleSender {

    private static boolean isSuccess = false;

    @Test
    public void test01SenderNotification() throws Exception{
        String sURL = "https://apps.guanzongroup.com.ph/notification/send_request.php";
        Calendar calendar = Calendar.getInstance();
        //Create the header section needed by the API
        Map<String, String> headers =
                new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", "GuanzonApp");
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
        param.put("type", "00000");
        param.put("parent", null);
        param.put("title", "Guanzon Panalo");
        param.put("message", "Congrats! libre kana");
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
        String sURL = "https://apps.guanzongroup.com.ph/notification/send_request.php";
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
            param.put("type", "00000");
            param.put("parent", null);
            param.put("title", "I LOVE MY JOB");
            param.put("message", "Employee raffle has ended!");
            param.put("rcpt", rcpts);
            param.put("infox", CreatePanaloNotification());


            JSONParser oParser = new JSONParser();
            JSONObject json_obj = null;

            String lsJson = param.toJSONString();
            System.out.println("Panalo JSON " + lsJson);

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

    /**
     *
     * @return Module 001 refers to Panalo notifications
     *          Module 002 refers to ILOVEMYJOB Notifications
     */
    private static String CreatePanaloNotification(){
        JSONObject loJSON = new JSONObject();

        loJSON.put("module", "002");
        loJSON.put("panalo", "raffle");
//        loJSON.put("panalo", "reward");
//        loJSON.put("panalo", "claim");
//        loJSON.put("panalo", "redeemed");
//        loJSON.put("panalo", "warning");

        JSONObject loPanalo = new JSONObject();
        loPanalo.put("cTranStat", "3");

        loJSON.put("data", loPanalo);

        return loJSON.toJSONString();
    }


    @Test
    public void test02PaySlipNotification() throws Exception{
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

        JSONObject param = new JSONObject();
        param.put("type", "00000");
        param.put("parent", null);
        param.put("title", "PAYSLIP (2023-02-16 - 2023-02-28)");
        param.put("message", "Good day! \n" +
                "\n" +
                " Attached is your payslip for the payroll period 2023-02-16 - 2023-02-28.\n" +
                "\n" +
                " [http://gts1.guanzongroup.com.ph:2007/repl/misc/download_ps.php?period=TTAwMTIzMTE=&client=TTAwMTE5MDAxMTMx]");
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
