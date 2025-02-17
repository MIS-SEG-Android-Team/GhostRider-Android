package org.guanzongroup.com.creditevaluation.ViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditOnlineApplicationCI;
import org.rmj.g3appdriver.GCircle.room.Entities.EOccupationInfo;
import org.rmj.g3appdriver.GCircle.room.Repositories.ROccupation;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.BarangayRecord;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.CIImage;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.Obj.CITagging;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMEvaluation extends AndroidViewModel {
    private static final String TAG = VMEvaluation.class.getSimpleName();

    private final Application instance;

    private final CITagging poSys;
    private final ROccupation poJob;

    private final ConnectionUtil poConn;

    private String message;

    public interface OnValidateTaggingResult{
        void OnValid();
        void OnInvalid(String message);
    }

    public interface OnSaveCIResultListener {
        void OnSuccess();
        void OnError(String message);
    }

    public interface OnSaveAddressResult {
        void OnSuccess(String args, String args1, String args2);
        void OnError(String message);
    }

    public interface OnUpdateOtherDetail{
        void OnSuccess();
        void OnError(String message);
    }

    public interface OnUploadResultListener{
        void OnUpload();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnPostRecommendationCallback{
        void OnPost();
        void OnSuccess();
        void OnFailed(String message);
    }

    public VMEvaluation(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new CITagging(application);
        this.poJob = new ROccupation(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poSys.GetUserInfo();
    }

    public LiveData<ECreditOnlineApplicationCI> GetApplicationDetail(String fsVal){
        return poSys.GetApplicationDetail(fsVal);
    }

    public LiveData<List<EOccupationInfo>> GetOccupationList(){
        return poJob.getAllOccupationInfo();
    }

    public void ValidateTagging(String TransNox, String fsKeyxx, List<String> foList, OnValidateTaggingResult listener){
        TaskExecutor.Execute(foList, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.ValidateTagging(TransNox, fsKeyxx, (List<String>) args)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnInvalid(message);
                } else {
                    listener.OnValid();
                }
            }
        });
    }

    /*private class ValidateTaggingTask extends AsyncTask<List<String>, Void, Boolean>{

        private final OnValidateTaggingResult mListener;
        private final String TransNox;
        private final String fsKeyxx;

        private String message;

        public ValidateTaggingTask(String TransNox, String fsKeyxx, OnValidateTaggingResult listener) {
            this.TransNox = TransNox;
            this.fsKeyxx = fsKeyxx;
            this.mListener = listener;
        }

        @Override
        protected Boolean doInBackground(List<String>... lists) {

            if(!poSys.ValidateTagging(TransNox, fsKeyxx, lists[0])){
                message = poSys.getMessage();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                mListener.OnInvalid(message);
            } else {
                mListener.OnValid();
            }
        }
    }*/

    public void SaveCIResult(String args, String fsPar, String fsKey, String fsRes, List<String> foList, OnSaveCIResultListener listener){
        String[] params = {args, fsPar, fsKey, fsRes};
        TaskExecutor.Execute(params, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try{
                    String[] array = (String[]) args;
                    String TransNox = array[0];
                    String Parentxx = array[1];
                    String KeyNamex = array[2];
                    String Resultxx = array[3];


                    if (!poSys.SaveCIResult(TransNox, Parentxx, KeyNamex, Resultxx)) {
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnError(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    /*private class SaveCIResult extends AsyncTask<String, Void, Boolean>{

        private final OnSaveCIResultListener listener;

        private String message;

        public SaveCIResult(List<String> foList, OnSaveCIResultListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                String TransNox = strings[0];
                String Parentxx = strings[1];
                String KeyNamex = strings[2];
                String Resultxx = strings[3];


                if (!poSys.SaveCIResult(TransNox, Parentxx, KeyNamex, Resultxx)) {
                    message = poSys.getMessage();
                    return false;
                }

                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                listener.OnError(message);
            } else {
                listener.OnSuccess();
            }
        }
    }*/

    public void SaveBarangayRecord(BarangayRecord foVal, OnUpdateOtherDetail callback){
        TaskExecutor.Execute(foVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poSys.UpdateBarangayRecord((BarangayRecord) args)){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnError(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    /*private class SaveBarangayRecordCallback extends AsyncTask<BarangayRecord, Void, Boolean>{

        private final OnUpdateOtherDetail callback;

        private String message;

        public SaveBarangayRecordCallback(OnUpdateOtherDetail callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(BarangayRecord... record) {
            try{
                if(!poSys.UpdateBarangayRecord(record[0])){
                    message = poSys.getMessage();
                    return false;
                }

                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                callback.OnError(message);
            } else {
                callback.OnSuccess();
            }
        }
    }*/

    public void UpdateOtherDetail(String args, String args1, String args2, OnUpdateOtherDetail callback){
        String[] array = {args, args1, args2};
        TaskExecutor.Execute(array, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try {
                    String[] vals = (String[]) args;

                    String lsTransN = vals[0];
                    String lsUpdate = vals[1];
                    String lsValuex = vals[2];

                    switch (lsUpdate) {
                        case "n1":
                            poSys.UpdateNeighbor1(lsTransN, lsValuex);
                            break;
                        case "n2":
                            poSys.UpdateNeighbor2(lsTransN, lsValuex);
                            break;
                        case "n3":
                            poSys.UpdateNeighbor3(lsTransN, lsValuex);
                            break;
                    }
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnError(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    /*private class UpdateOtherDetailTask extends AsyncTask<String, Void, Boolean>{

        private final OnUpdateOtherDetail callback;

        private String message;

        public UpdateOtherDetailTask(OnUpdateOtherDetail callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                String lsTransN = args[0];
                String lsUpdate = args[1];
                String lsValuex = args[2];
                switch (lsUpdate) {
                    case "n1":
                        poSys.UpdateNeighbor1(lsTransN, lsValuex);
                        break;
                    case "n2":
                        poSys.UpdateNeighbor2(lsTransN, lsValuex);
                        break;
                    case "n3":
                        poSys.UpdateNeighbor3(lsTransN, lsValuex);
                        break;
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                callback.OnError(message);
            } else {
                callback.OnSuccess();
            }
        }
    }*/

    public void UploadResult(String args, OnUploadResultListener listener){
        TaskExecutor.Execute(args, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnUpload();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.UploadEvaluationResult((String) args)){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    /*private class UploadResultTask extends AsyncTask<String, Void, Boolean>{

        private final OnUploadResultListener listener;

        private String message;

        public UploadResultTask(OnUploadResultListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.OnUpload();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                if(!poSys.UploadEvaluationResult(strings[0])){
                    message = poSys.getMessage();
                    return false;
                }

                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                listener.OnFailed(message);
            } else {
                listener.OnSuccess();
            }
        }
    }*/

    public void SaveRecommendation(String TransNox, String cApprove, String sRemarks, OnPostRecommendationCallback callback){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnPost();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poSys.SaveCIApproval(TransNox, cApprove, sRemarks)){
                        message = poSys.getMessage();
                        return false;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.PostCIApproval(TransNox)){
                        message = poSys.getMessage();
                        return false;
                    }
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnFailed(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    /*private class PostRecommendationTask extends AsyncTask<String, Void, Boolean>{

        private final OnPostRecommendationCallback callback;

        private String message;

        public PostRecommendationTask(OnPostRecommendationCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnPost();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try{
                String TransNox = args[0];
                String cApprove = args[1];
                String sRemarks = args[2];
                if(!poSys.SaveCIApproval(TransNox, cApprove, sRemarks)){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                if(!poSys.PostCIApproval(args[0])){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                callback.OnFailed(message);
            } else {
                callback.OnSuccess();
            }
        }
    }*/

    public void InitCameraLaunch(Activity activity, String TransNox, OnInitializeCameraCallback callback){
        ImageFileCreator loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_CI_ADDRESS, TransNox);
        String argsList[] = new String[4];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!loImage.IsFileCreated(false)){
                    message = loImage.getMessage();
                    return false;
                } else {
                    LocationRetriever loLrt = new LocationRetriever(instance, activity);
                    if(loLrt.HasLocation()){
                        argsList[0] = loImage.getFilePath();
                        argsList[1] = loImage.getFileName();
                        argsList[2] = loLrt.getLatitude();
                        argsList[3] = loLrt.getLongitude();
                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);
                        return loIntent;
                    } else {
                        argsList[0] = loImage.getFilePath();
                        argsList[1] = loImage.getFileName();
                        argsList[2] = loLrt.getLatitude();
                        argsList[3] = loLrt.getLongitude();
                        message = loLrt.getMessage();
                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", false);
                        return loIntent;
                    }
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Intent loResult = (Intent) object;
                if(loResult.getBooleanExtra("result", false)){
                    callback.OnFailed(message, loResult, argsList);
                    return;
                }
                callback.OnSuccess(loResult, argsList);
            }
        });
    }

  /*  private static class InitializeCameraTask extends AsyncTask<String, Void, Boolean>{

        private final Activity activity;
        private final Application instance;
        private final OnInitializeCameraCallback callback;
        private final ImageFileCreator loImage;

        private Intent loIntent;
        private String[] args = new String[4];
        private String message;

        public InitializeCameraTask(Activity activity, String TransNox, Application instance, OnInitializeCameraCallback callback){
            this.activity = activity;
            this.instance = instance;
            this.callback = callback;
            this.loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_CI_ADDRESS, TransNox);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnInit();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if(!loImage.IsFileCreated(false)){
                message = loImage.getMessage();
                return false;
            } else {
                LocationRetriever loLrt = new LocationRetriever(instance, activity);
                if(loLrt.HasLocation()){
                    args[0] = loImage.getFilePath();
                    args[1] = loImage.getFileName();
                    args[2] = loLrt.getLatitude();
                    args[3] = loLrt.getLongitude();
                    loIntent = loImage.getCameraIntent();
                    return true;
                } else {
                    args[0] = loImage.getFilePath();
                    args[1] = loImage.getFileName();
                    args[2] = loLrt.getLatitude();
                    args[3] = loLrt.getLongitude();
                    loIntent = loImage.getCameraIntent();
                    message = loLrt.getMessage();
                    return false;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(isSuccess){
                callback.OnSuccess(loIntent, args);
            } else {
                callback.OnFailed(message, loIntent, args);
            }
        }
    } */

    public void SaveAddressImage(CIImage foVal, OnSaveAddressResult listener){
        final String[] argsVal = new String[1];
        final String[] args1 = new String[1];
        final String[] args2 = new String[1];
        TaskExecutor.Execute(foVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                CIImage foval = (CIImage) args;
                try{
                    if(!poSys.SaveImageInfo((foval))){
                        message = poSys.getMessage();
                        return false;
                    }
                    argsVal[0] = foval.getsParentxx();
                    args1[0] = foval.getsKeyNamex();
                    args2[0] = foval.getcResultxx();
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnError(message);
                } else {
                    listener.OnSuccess(argsVal[0], args1[0], args2[0]);
                }
            }
        });
    }

    /*private class SaveAddressImageTask extends AsyncTask<CIImage, Void, Boolean>{

        private final OnSaveAddressResult listener;

        private String message;

        private String args, args1, args2;

        public SaveAddressImageTask(OnSaveAddressResult listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(CIImage... ciImages) {
            try{
                if(!poSys.SaveImageInfo(ciImages[0])){
                    message = poSys.getMessage();
                    return false;
                }
                args = ciImages[0].getsParentxx();
                args1 = ciImages[0].getsKeyNamex();
                args2 = ciImages[0].getcResultxx();
                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                listener.OnError(message);
            } else {
                listener.OnSuccess(args, args1, args2);
            }
        }
    }*/
}
