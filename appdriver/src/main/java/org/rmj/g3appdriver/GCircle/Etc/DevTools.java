package org.rmj.g3appdriver.GCircle.Etc;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DevTool;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;

public class DevTools {
    private static final String TAG = DevTools.class.getSimpleName();

    private final Application instance;

    private final DevTool poDao;
    private final EmployeeSession poSession;
    private final AppConfigPreference poConfig;
    private final EmployeeMaster poUser;

    private String message;

    public DevTools(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GCircleDB.getInstance(instance).devTool();
        this.poSession = EmployeeSession.getInstance(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poUser = new EmployeeMaster(instance);
    }

    public String getMessage() {
        return message;
    }

    public LiveData<EEmployeeInfo> GetUserInfo(){
        return poDao.GetUserInfo();
    }

    public boolean Update(EEmployeeInfo args){
        try{
            poDao.Update(args);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean SetDefault(){
        try{
            EEmployeeInfo loDetail = poDao.GetUser();
            loDetail.setPositnID(poSession.getPositionID());
            loDetail.setEmpLevID(Integer.parseInt(poSession.getEmployeeLevel()));
            loDetail.setDeptIDxx(poSession.getDeptID());
            poDao.Update(loDetail);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

}
