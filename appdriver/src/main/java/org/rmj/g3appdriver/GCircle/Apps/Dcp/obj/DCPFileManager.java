package org.rmj.g3appdriver.GCircle.Apps.Dcp.obj;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class DCPFileManager {
    private static final String TAG = DCPFileManager.class.getSimpleName();
    private final Application instance;
    private String message;
    private DErrorLogs poError;

    private void SaveError(String source, String message){

        EErrorLogs logs = new EErrorLogs();
        logs.setnErrorLogID(poError.GetErrorLogCount() + 1);
        logs.setsMessagex(message);
        logs.setdLogDate(getCurrentDate());
        logs.setsSourceTransNo(source);
        logs.setcRead("0");

        poError.SaveErrorLogs(logs);
    }

    private String getCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    public DCPFileManager(Application instance) {
        this.instance = instance;
        this.poError = GGC_GCircleDB.getInstance(instance).errorLogsDao();
    }

    public String getMessage() {
        return message;
    }

    public String ReadToJson(Uri uri){
        try{
            StringBuffer builder = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(instance.getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();

            String lsResult = builder.toString();

            JSONObject laJson = new JSONObject(lsResult);
            return laJson.toString();
        }catch (JSONException je){
            je.printStackTrace();
            message = "Could not read the file properly";

            SaveError(TAG, getLocalMessage(je));
            return null;
        }  catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);

            SaveError(TAG, getLocalMessage(e));
            return null;
        }
    }
}
