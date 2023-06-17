package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditApp;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditAppInstance;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.model.CoMakerResidence;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMComakerResidence extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMComakerResidence.class.getSimpleName();

    private final CreditApp poApp;
    private final CoMakerResidence poModel;

    private String TransNox;

    private String message;


    public VMComakerResidence(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application).getInstance(CreditAppInstance.CoMaker_Residence_Info);
        this.poModel = new CoMakerResidence();
    }

    public CoMakerResidence getModel() {
        return poModel;
    }

    @Override
    public void InitializeApplication(Intent params) {
        TransNox = params.getStringExtra("sTransNox");
    }

    @Override
    public LiveData<ECreditApplicantInfo> GetApplication() {
        return poApp.GetApplication(TransNox);
    }

    @Override
    public void ParseData(ECreditApplicantInfo args, OnParseListener listener) {
//        new ParseDataTask(listener).execute(args);
        TaskExecutor.Execute(args, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                ECreditApplicantInfo lsInfo = (ECreditApplicantInfo) args;
                try {
                    CoMakerResidence loDetail = (CoMakerResidence) poApp.Parse(lsInfo);
                    if (loDetail == null) {
                        message = poApp.getMessage();
                        return null;
                    }
                    return loDetail;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                CoMakerResidence lsResult = (CoMakerResidence) object;
                if (lsResult == null) {
                    Log.e(TAG, message);
                } else {
                    listener.OnParse(lsResult);
                }
            }
        });
    }

    @Override
    public void Validate(Object args) {

    }

    @Override
    public void SaveData(OnSaveInfoListener listener) {
//        new SaveDetailTask(listener).execute(poModel);
        TaskExecutor.Execute(poModel, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                CoMakerResidence lsInfo =(CoMakerResidence) args;
                int lnResult = poApp.Validate(lsInfo);

                if (lnResult != 1) {
                    message = poApp.getMessage();
                    return false;
                }

                String lsResult = poApp.Save(lsInfo);
                if (lsResult == null) {
                    message = poApp.getMessage();
                    return false;
                }

                TransNox = lsInfo.getTransNox();
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if (!lsSuccess) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSave(TransNox);
                }
            }
        });
    }
    
    public LiveData<List<DTownInfo.TownProvinceInfo>> GetTownProvinceList() {
        return poApp.GetTownProvinceList();
    }

    public LiveData<List<EBarangayInfo>> GetBarangayList(String args) {
        return poApp.GetBarangayList(args);
    }

    public LiveData<List<ECountryInfo>> GetCountryList() {
        return poApp.GetCountryList();
    }
}

//}
//private class ParseDataTask extends AsyncTask<ECreditApplicantInfo, Void, CoMakerResidence> {
//
//    private final OnParseListener listener;
//
//    public ParseDataTask(OnParseListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected CoMakerResidence doInBackground(ECreditApplicantInfo... app) {
//        try {
//            CoMakerResidence loDetail = (CoMakerResidence) poApp.Parse(app[0]);
//            if (loDetail == null) {
//                message = poApp.getMessage();
//                return null;
//            }
//            return loDetail;
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            message = getLocalMessage(e);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            message = getLocalMessage(e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(CoMakerResidence result) {
//        super.onPostExecute(result);
//        if (result == null) {
//            Log.e(TAG, message);
//        } else {
//            listener.OnParse(result);
//        }
//    }
//}
//
//private class SaveDetailTask extends AsyncTask<CoMakerResidence, Void, Boolean> {
//
//    private final OnSaveInfoListener listener;
//
//    public SaveDetailTask(OnSaveInfoListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected Boolean doInBackground(CoMakerResidence... info) {
//        int lnResult = poApp.Validate(info[0]);
//
//        if (lnResult != 1) {
//            message = poApp.getMessage();
//            return false;
//        }
//
//        String lsResult = poApp.Save(info[0]);
//        if (lsResult == null) {
//            message = poApp.getMessage();
//            return false;
//        }
//
//        TransNox = info[0].getTransNox();
//        return true;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean isSuccess) {
//        super.onPostExecute(isSuccess);
//        if (!isSuccess) {
//            listener.OnFailed(message);
//        } else {
//            listener.OnSave(TransNox);
//        }
//    }
//}