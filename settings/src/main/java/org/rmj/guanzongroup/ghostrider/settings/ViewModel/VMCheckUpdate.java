/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/28/21 11:12 AM
 * project file last modified : 6/28/21 11:12 AM
 */

package org.rmj.guanzongroup.ghostrider.settings.ViewModel;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

public class VMCheckUpdate extends AndroidViewModel {

    private final Application instance;
    private final AppConfigPreference poConfig;
    private final MutableLiveData<String> psVersion = new MutableLiveData<>();

    private final ConnectionUtil poConn;
    private final HttpHeaders poHeaders;
    private final String PATH;
    private final GCircleApi poApi;
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public VMCheckUpdate(@NonNull Application application) {
        super(application);

        this.instance = application;
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.psVersion.setValue(poConfig.getVersionInfo());

        this.poConn = new ConnectionUtil(application);
        this.poHeaders = HttpHeaders.getInstance(application);
        this.poApi = new GCircleApi(application);

        PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
    }

    public interface SystemUpateCallback{
        void OnDownloadUpdate(String title, String message);
        void OnProgressUpdate(int progress);
        void OnFinishDownload(Intent intent);
        void OnFailedDownload(String message);
    }

    public LiveData<String> getVersionInfo(){
        return psVersion;
    }

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct){
        return base.divide(pct, 2, RoundingMode.HALF_UP);
    }

    public void DownloadUpdate(SystemUpateCallback callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnDownloadUpdate("System Update", "Downloading updates. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {

                String lsResult;

                try {

                    if(poConn.isWifiConnected()) {

                        if (poConn.isDeviceConnected()) {

                            URL url = new URL(poApi.getUrlDownloadUpdate());
                            HttpURLConnection c = (HttpURLConnection) url.openConnection();
                            c.setRequestMethod("GET");
                            c.setDoOutput(true);
                            c.connect();

                            File file = new File(PATH);
                            file.mkdirs();
                            File outputFile = new File(file, "gRider.apk");
                            if (outputFile.exists()) {
                                outputFile.delete();
                            }

                            FileOutputStream fos = new FileOutputStream(outputFile);
                            BigDecimal fileLength = BigDecimal.valueOf(c.getContentLength());
                            InputStream is = c.getInputStream();

                            byte[] buffer = new byte[4096];
                            BigDecimal total = BigDecimal.valueOf(4096);
                            int len1;

                            while ((len1 = is.read(buffer)) != -1) {

                                total = total.add(BigDecimal.valueOf(len1));

                                if (fileLength.intValue() > 0) {
                                    BigDecimal percentage = percentage(total, fileLength).multiply(ONE_HUNDRED);
                                    callback.OnProgressUpdate(percentage.intValue());
                                }

                                fos.write(buffer, 0, len1);
                            }

                            fos.close();
                            is.close();
                            lsResult = AppConstants.APPROVAL_CODE_GENERATED("Download Complete. Ready to install.");

                        } else {
                            lsResult = AppConstants.NO_INTERNET();
                        }
                    } else {
                        lsResult = AppConstants.LOCAL_EXCEPTION_ERROR("You need wifi connection in order to download updates.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lsResult = AppConstants.LOCAL_EXCEPTION_ERROR(e.getMessage());
                }
                return lsResult;

            }

            @Override
            public void OnPostExecute(Object object) {

                try {

                    JSONObject loJson = new JSONObject(object.toString());
                    String lsResult = loJson.getString("result");

                    if (lsResult.equalsIgnoreCase("success")) {

                        String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/gRider.apk";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        Uri loUriFile = FileProvider.getUriForFile(instance, "org.rmj.guanzongroup.ghostrider.epacss" + ".provider", new File(PATH));
                        intent.setDataAndType(loUriFile, "application/vnd.android" + ".package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        callback.OnFinishDownload(intent);

                    } else {

                        JSONObject loError = loJson.getJSONObject("error");
                        callback.OnFailedDownload(getErrorMessage(loError));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
}
