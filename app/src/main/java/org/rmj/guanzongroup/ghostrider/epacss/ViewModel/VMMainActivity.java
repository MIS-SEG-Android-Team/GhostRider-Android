/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.ViewModel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLRDcp;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeRole;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.lib.Panalo.Obj.ILOVEMYJOB;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.epacss.Service.DataSyncService;
import org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard.Fragment_AHDashboard;
import org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard.Fragment_BHDashboard;
import org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard.Fragment_Dashboard;
import org.rmj.guanzongroup.ghostrider.epacss.ui.Dashboard.Fragment_Eng_Dashboard;

import java.net.URL;
import java.util.List;

public class VMMainActivity extends AndroidViewModel {
    private static final String TAG = "GRider Main Activity";
    private final Application app;
    private final DataSyncService poNetRecvr;
    private final EmployeeMaster poUser;
    private final ILOVEMYJOB poPanalo;
    private Bitmap bmp;
    private String message;
    private LRDcp poDCP;

    public VMMainActivity(@NonNull Application application) {
        super(application);
        this.app = application;
        this.poNetRecvr = new DataSyncService(app);
        this.poUser = new EmployeeMaster(app);
        this.poPanalo = new ILOVEMYJOB(app);
        this.poDCP = new LRDcp(application);
    }

    public DataSyncService getInternetReceiver() {
        return poNetRecvr;
    }

    public LiveData<List<EEmployeeRole>> getEmployeeRole() {
        return poUser.getEmployeeRoles();
    }

    public LiveData<List<EEmployeeRole>> getChildRoles() {
        return poUser.getChildRoles();
    }

    public LiveData<EEmployeeInfo> getEmployeeInfo() {
        return poUser.GetEmployeeInfo();
    }

    public LiveData<EDCPCollectionMaster> getLatestPostedDCP(){
        return poDCP.GetPostedDCP();
    }

    public Fragment GetUserFragments(EEmployeeInfo args) {
        Fragment userLevel;
        switch (args.getEmpLevID()) {
            case 3:
                userLevel = new Fragment_BHDashboard();
                break;
            case 4:
                userLevel = new Fragment_AHDashboard();
                break;
            default:
                switch (args.getDeptIDxx()) {
                    case "032":
                        userLevel = new Fragment_Eng_Dashboard();
                        break;
                    default:
                        userLevel = new Fragment_Dashboard();
                        break;
                }
        }
        return userLevel;
    }

    public void ResetRaffleStatus() {
        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                if (!poPanalo.ResetRaffleStatus()) {
                    message = poPanalo.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

            }
        });
    }

    public void DisplayURLImage(String urlink, onDownload callback){
        TaskExecutor.Execute(urlink, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onLoad("Employee QR", "Downloading QR");
            }
            @Override
            public Object DoInBackground(Object args) {
                try {
                    URL url = new URL(args.toString());
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    if (bmp != null){
                        return true;
                    }else {
                        message = "No generated QR for employee";
                        return false;
                    }

                }catch (Exception e){
                    message = "No generated QR for employee";
                    e.printStackTrace();
                    return false;
                }
            }
            @Override
            public void OnPostExecute(Object object) {
                Boolean aBoolean = (Boolean) object;
                if(aBoolean){
                    callback.onDisplay(bmp);
                }else {
                    callback.onFailed(message);
                }
            }
        });
    }
    public interface onDownload{
        void onLoad(String title, String message);
        void onDisplay(Bitmap loImg);
        void onFailed(String message);
    }
}
