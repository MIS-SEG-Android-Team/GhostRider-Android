/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 6/23/21 2:25 PM
 * project file last modified : 6/23/21 2:25 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.ViewModel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DRawDao;
import org.rmj.g3appdriver.GCircle.room.Repositories.RRawData;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportBarangay;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportBranch;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportBrand;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportBrandModel;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportCategory;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportCountry;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportFileCode;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportInstance;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportMcModelPrice;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportMcTermCategory;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportProvinces;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.ImportTown;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_BankList;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_Occupations;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_Relation;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_SCARequest;
import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_SysConfig;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.settings.Objects.LocalData;
import org.rmj.guanzongroup.ghostrider.settings.utils.DatabaseExport;

import java.util.ArrayList;
import java.util.List;

public class VMLocalData extends AndroidViewModel {

    private final Application instance;
    private final RRawData poData;
    private List<LocalData> dataList = new ArrayList<>();
    private final GCircleApi poApi;
    private final AppConfigPreference loConfig;
    private final MutableLiveData<List<LocalData>> loDataList = new MutableLiveData<>();

    private DRawDao.AppLocalData poAppData = new DRawDao.AppLocalData();

    public interface OnRefreshDataCallback{
        void OnLoad(String Title, String Message);
        void OnSuccess();
        void OnFailed();
    }

    public interface OnClearDataCallBack{
        void OnClear();
        void OnFailed();
    }

    public VMLocalData(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poData = new RRawData(application);
        this.loConfig = AppConfigPreference.getInstance(instance);
        this.poApi = new GCircleApi(application);
    }

    public LiveData<DRawDao.AppLocalData> getAppLocalData(){
        return poData.getAppLocalData();
    }

    public void setAppLocalData(DRawDao.AppLocalData localData){
        poAppData = localData;
        dataList.clear();
        loDataList.setValue(dataList);
        dataList.add(new LocalData("Approval Code Data", poAppData.Approval_Code, poApi.getUrlScaRequest()));
        dataList.add(new LocalData("Branch Data", poAppData.Branch_Data, poApi.getUrlImportBranches()));
        dataList.add(new LocalData("Mc Brand", poAppData.Mc_Brand, poApi.getUrlImportBrand()));
        dataList.add(new LocalData("Mc Model", poAppData.Mc_Model, poApi.getUrlImportMcModel()));
        dataList.add(new LocalData("Mc Category", poAppData.Mc_Category, poApi.getUrlImportMcCategory()));
        dataList.add(new LocalData("Mc Model Price", poAppData.Mc_Model_Price, poApi.getUrlImportMcModelPrice()));
        dataList.add(new LocalData("Mc Term Category", poAppData.Mc_Term_Category, poApi.getUrlImportTermCategory()));
        dataList.add(new LocalData("Occupation Data", poAppData.Occupation_Data, poApi.getUrlImportOccupations()));
        dataList.add(new LocalData("Raffle Basis", poAppData.Raffle_Basis, poApi.getUrlImportRaffleBasis()));
        dataList.add(new LocalData("File Code", poAppData.File_Code, poApi.getUrlImportFileCode()));
        dataList.add(new LocalData("Bank Data", poAppData.Bank_Data, poApi.getUrlDownloadBankInfo()));
        dataList.add(new LocalData("Remittance Data", poAppData.Remittance_Data, poApi.getUrlBranchRemittanceAcc()));
        dataList.add(new LocalData("Barangay Data", poAppData.Barangay_Data, poApi.getUrlImportBarangay()));
        dataList.add(new LocalData("Town Data", poAppData.Town_Data, poApi.getUrlImportTown()));
        dataList.add(new LocalData("Province Data", poAppData.Province_Data, poApi.getUrlImportProvince()));
        dataList.add(new LocalData("Country Data", poAppData.Country_Data, poApi.getUrlImportCountry()));
        dataList.add(new LocalData("Relation Data", poAppData.Relation_Data, poApi.getUrlDownloadRelation()));
        loDataList.setValue(dataList);
    }

    public LiveData<List<LocalData>> getDataList(){
        return loDataList;
    }

    public void RefreshAllRecords(OnRefreshDataCallback callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad("Local Data", "Refreshing all local data. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                String result;

                try {

                    ImportInstance[] importInstances = new ImportInstance[]{
                            new Import_BankList(instance),
                            new ImportFileCode(instance),
                            new Import_Relation(instance),
                            new ImportBranch(instance),
                            new ImportBrand(instance),
                            new ImportBrandModel(instance),
                            new ImportCategory(instance),
                            new ImportProvinces(instance),
                            new ImportMcModelPrice(instance),
                            new ImportTown(instance),
                            new ImportBarangay(instance),
                            new ImportMcTermCategory(instance),
                            new ImportCountry(instance),
                            new Import_Occupations(instance),
                            new Import_SCARequest(instance),
                            new Import_SysConfig(instance)};

                    for (int x = 0; x < importInstances.length; x++) {
                        importInstances[x].ImportData(new ImportDataCallback() {
                            @Override
                            public void OnSuccessImportData() {

                            }

                            @Override
                            public void OnFailedImportData(String message) {

                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    result = "success";
                } catch (Exception e){
                    e.printStackTrace();
                    result = "error";
                }
                return result;
            }

            @Override
            public void OnPostExecute(Object object) {

                if(object.toString().equalsIgnoreCase("success")) {
                    callback.OnSuccess();
                } else {
                    callback.OnFailed();
                }

            }
        });
    }

    public void ExportDatabase(){
        DatabaseExport loExport = new DatabaseExport(instance);
        loExport.export();
    }

    public void ClearData(){
        try {
            // clearing app data
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear org.rmj.guanzongroup.ghostrider.epacss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void killProcessesAround(Activity activity) {
        try {
        ActivityManager am = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        String myProcessPrefix = activity.getApplicationInfo().processName;
        String myProcessName = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).processName;
        for (ActivityManager.RunningAppProcessInfo proc : am.getRunningAppProcesses()) {
            if (proc.processName.startsWith(myProcessPrefix) && !proc.processName.equals(myProcessName)) {
                android.os.Process.killProcess(proc.pid);
            }
        }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
