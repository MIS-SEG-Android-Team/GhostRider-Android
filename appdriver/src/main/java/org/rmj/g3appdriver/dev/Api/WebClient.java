/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.dev.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class WebClient {

    public static String sendRequest(String sURL, String sJSon, HashMap<String, String> headers) throws IOException {
        try{
            if (sURL.substring(0, 5).equalsIgnoreCase("https")){
                HttpsURLConnection conn = null;
                StringBuilder lsResponse = new StringBuilder();
                URL url = null;

                //Open network IO
                url = new URL(sURL);

                //opens a connection, then sends POST & set HTTP header nicely
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");

                if(headers != null){
                    Set<Map.Entry<String, String>> entrySet = headers.entrySet();

                    for(Map.Entry<String, String> entry : entrySet) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }

                }

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
                bw.write(sJSon);
                bw.flush();
                bw.close();

                if (!(conn.getResponseCode() == HttpsURLConnection.HTTP_CREATED ||
                        conn.getResponseCode() == HttpsURLConnection.HTTP_OK)) {
                    System.setProperty("store.error.info", String.valueOf(conn.getResponseCode()));
                    System.out.println(lsResponse);
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    lsResponse.append(output);
                }
                conn.disconnect();

                return lsResponse.toString();
            } else {
                return httpPostJSon(sURL, sJSon, headers);
            }
        }catch (SSLPeerUnverifiedException e){
            return "{\"result\" : \"error\", \"error\" : {\"code\" : \"40020\"}}";
        }
    }

    private static String httpPostJSon(String sURL, String sJSon, HashMap<String, String> headers) throws IOException {
        try {

            HttpURLConnection conn = null;
            StringBuilder lsResponse = new StringBuilder();
            URL url = null;

            //Open network IO
            url = new URL(sURL);

            //opens a connection, then sends POST & set HTTP header nicely
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/json");

            if(headers != null){
                Set<Map.Entry<String, String>> entrySet = headers.entrySet();

                for(Map.Entry<String, String> entry : entrySet) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }

            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
            bw.write(sJSon);
            bw.flush();
            bw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                lsResponse.append(output);
            }
            conn.disconnect();

            return lsResponse.toString();

        }catch (SSLPeerUnverifiedException e){
            return "{\"result\" : \"error\", \"error\" : {\"code\" : \"40020\"}}";
        }
    }
}
