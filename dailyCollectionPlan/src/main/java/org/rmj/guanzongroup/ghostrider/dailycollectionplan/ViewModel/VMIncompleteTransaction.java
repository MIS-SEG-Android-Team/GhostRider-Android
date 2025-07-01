/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.OTH;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Location.GmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.HmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.OtherRemCode;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMIncompleteTransaction extends AndroidViewModel {
    private static final String TAG = VMIncompleteTransaction.class.getSimpleName();

    private final Application instance;

    private final OTH poSys;

    private String message;

    public VMIncompleteTransaction(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new OTH(application);
    }

    public LiveData<EDCPCollectionDetail> GetCollectionDetail(String TransNox, String AccountNo, String EntryNox){
        return poSys.GetAccountDetailForTransaction(TransNox, AccountNo, EntryNox);
    }

    public void UpdateCollection(EDCPCollectionDetail foVal) {
        poSys.UpdateCollection(foVal);
    }

    public EDCPCollectionDetail GetCollectionForTransaction(String TransNox, String AccountNo, String EntryNox) {
        return poSys.GetCollectionForTransaction(TransNox, AccountNo, EntryNox);
    }

    public void InitCameraLaunch(Activity activity, String TransNox, OnInitializeCameraCallback callback){

        ImageFileCreator loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_SELFIE_LOG, TransNox);
        String[] lsResult = new String[4];

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!loImage.IsFileCreated(true)){
                    message = loImage.getMessage();
                    return null;
                }

                lsResult[0] = loImage.getFilePath();
                lsResult[1] = loImage.getFileName();

                String lsCompx = android.os.Build.MANUFACTURER.toLowerCase();
                LocationRetriever.iLocationRetriever location;

                if (!lsCompx.equalsIgnoreCase("huawei")) {
                    location = new GmsLocationRetriever();
                } else {
                    location = new HmsLocationRetriever();
                }

                location.GetLocation(instance, new LocationRetriever.OnRetrieveLocationListener() {
                    @Override
                    public void OnRetrieve(String latitude, String longitude) {

                        lsResult[2] = latitude;
                        lsResult[3] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);

                        callback.OnSuccess(loIntent, lsResult);
                    }

                    @Override
                    public void OnFailed(String message, String latitude, String longitude) {

                        lsResult[2] = latitude;
                        lsResult[3] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", false);

                        callback.OnFailed(message, loIntent, lsResult);
                    }
                });

                return null;
            }

            @Override
            public void OnPostExecute(Object object) {
            }
        });
    }

    public void SaveTransaction(OtherRemCode foVal, ViewModelCallback callback){

        TaskExecutor.Execute(foVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.SaveTransaction(args)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnFailedResult(message);
                } else {
                    callback.OnSuccessResult();
                }
            }
        });
    }
}