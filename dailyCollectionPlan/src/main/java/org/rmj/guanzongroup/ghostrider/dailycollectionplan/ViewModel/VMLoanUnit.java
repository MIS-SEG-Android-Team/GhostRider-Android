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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.LUN;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.lib.Etc.Barangay;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.lib.Etc.Province;
import org.rmj.g3appdriver.lib.Etc.Town;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Location.GmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.HmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.LoanUnit;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.g3appdriver.etc.ImageFileCreator;

import java.io.File;
import java.util.List;

public class VMLoanUnit extends AndroidViewModel {
    private static final String TAG = VMLoanUnit.class.getSimpleName();

    private final Application instance;
    private final LUN poSys;
    private final EmployeeMaster poUser;
    private final Branch poBranch;

    private final Province poProv;
    private final Town poTown;
    private final Barangay Brgy;

    private String message;

    public VMLoanUnit(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new LUN(application);
        this.poUser = new EmployeeMaster(application);
        this.poBranch = new Branch(application);
        poProv = new Province(application);
        poTown = new Town(application);
        Brgy = new Barangay(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<EDCPCollectionDetail> GetCollectionDetail(String TransNox, String AccountNo, String EntryNox){
        return poSys.GetAccountDetailForTransaction(TransNox, AccountNo, EntryNox);
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

    public void SaveTransaction(LoanUnit foVal, ViewModelCallback callback){

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

    public LiveData<List<DTownInfo.TownProvinceInfo>> getTownProvinceInfo(){
        return poTown.getTownProvinceInfo();
    }

    public LiveData<String[]> getBarangayNameList(String fsVal){
        return Brgy.getBarangayNamesFromTown(fsVal);
    }

    public LiveData<List<EBarangayInfo>> getBarangayInfoList(String fsVal){
        return Brgy.getAllBarangayFromTown(fsVal);
    }


    public LiveData<ArrayAdapter<String>> getSpnCivilStats(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_dropdown_item, DCP_Constants.CIVIL_STATUS)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    ((TextView) v).setTextColor(getApplication().getResources().getColorStateList(R.color.textColor_White));
                }else {
                    ((TextView) v).setTextColor(getApplication().getResources().getColorStateList(R.color.textColor_Black));
                }
                return v;
            }
        };
        MutableLiveData<ArrayAdapter<String>> liveData = new MutableLiveData<>();
        liveData.setValue(adapter);
        return liveData;
    }

    public String getRealPathFromURI (String contentUri) {
        File target = new File(contentUri);
        if (target.exists() && target.isFile() && target.canWrite()) {
            target.delete();
            Log.d("d_file", "" + target.getName());
        }
        return target.toString();
    }
}