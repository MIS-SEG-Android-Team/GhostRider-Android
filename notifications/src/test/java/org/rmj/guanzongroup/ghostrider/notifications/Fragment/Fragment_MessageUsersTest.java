/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.notifications
 * Electronic Personnel Access Control Security System
 * project file created : 5/18/21 8:28 AM
 * project file last modified : 5/18/21 8:28 AM
 */

package org.rmj.guanzongroup.ghostrider.notifications.Fragment;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class Fragment_MessageUsersTest {
    private static final String URL_QUICK_SEARCH = "https://apps.guanzongroup.com.ph/integsys/paramqry/cash_count_rqst_officer.php";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void newInstance() throws Exception{
        Calendar calendar = Calendar.getInstance();
        Map<String, String> headers =
                new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", "gRider");
        headers.put("g-api-imei", "356060072281722");
        headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
        headers.put("g-api-hash", SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key")));
        headers.put("g-api-user", "GAP021002961");
        headers.put("g-api-mobile", "09270359402");
        headers.put("g-api-model", "PAR-LX9");
        headers.put("g-api-client", "GGC_BM001");
        headers.put("g-api-log", "GAP021018252");
        headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

        JSONObject object = new JSONObject();
        object.put("reqstdnm", "Garcia, Michael");
        object.put("bsearch", true);

        String lsResult = WebClient.sendRequest(URL_QUICK_SEARCH, object.toString(), (HashMap) headers);
        JSONObject loResult = new JSONObject(lsResult);
        String result = loResult.getString("result");
        assertEquals("success", result);
    }
}