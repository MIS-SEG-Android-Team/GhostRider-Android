package org.rmj.g3appdriver.lib.Notifications;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteMessageParser {

    private final RemoteMessage dataMessage;

    public RemoteMessageParser(RemoteMessage Data){
        this.dataMessage = Data;
    }

    /**Parse the RemoteMessage receive from Firebase.
     * getValueof = this parses the remote message getting the JSONObject inside the remote message.
     * getDataValueOf = this parses the remote message getting the data inside the remote message itself.*/
    public String getValueOf(String JSONDataKeyValue){
        String msg_data = dataMessage.getData().get("msg_data");
        String value = "";

        Log.d("RemoteMessageParser", msg_data);

        try{
            JSONObject jsonData = new JSONObject(msg_data);
            value = jsonData.getString(JSONDataKeyValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getDataValueOf(String RemoteMessageKeyValue){
        return dataMessage.getData().get(RemoteMessageKeyValue);
    }
}
