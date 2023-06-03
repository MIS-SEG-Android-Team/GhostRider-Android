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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicationDocuments;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Repositories.RBranchLoanApplication;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCreditApplication;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCreditApplicationDocument;
import org.rmj.g3appdriver.GCircle.room.Repositories.RDailyCollectionPlan;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.GCircle.Apps.SelfieLog.SelfieLog;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMDataManager extends AndroidViewModel {

    private final SelfieLog poLog;
    private final RImageInfo poImage;
    private final RDailyCollectionPlan poDcp;
    private final RCreditApplication poCreditApp;
    private final RBranchLoanApplication poLoan;
    private final RCreditApplicationDocument poDocs;

    public interface OnDataFetchListener{
        void OnCheck();
        void OnCheckProgress(int table);
        void OnCheckLocalData(boolean hasPendingData);
    }

    public VMDataManager(@NonNull Application instance) {
        super(instance);
        this.poImage = new RImageInfo(instance);
        this.poDcp = new RDailyCollectionPlan(instance);
        this.poLog = new SelfieLog(instance);
        this.poCreditApp = new RCreditApplication(instance);
        this.poLoan = new RBranchLoanApplication(instance);
        this.poDocs = new RCreditApplicationDocument(instance);
    }

    public void checkData(OnDataFetchListener listener){
        new CheckDataTask(listener).execute();
    }

    /*@SuppressLint("StaticFieldLeak")
    private class CheckDataTask extends AsyncTask<String, Integer, Boolean>{

        OnDataFetchListener mListener;

        public CheckDataTask(OnDataFetchListener mListener) {
            this.mListener = mListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListener.OnCheck();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean hasData = false;
            try{
                List<EImageInfo> loginImageInfo = poImage.getUnsentSelfieLogImageList();
                if(loginImageInfo.size() > 0){
                    hasData = true;
                }
                publishProgress(1);

                List<EDCPCollectionDetail> collectionDetails = poDcp.getUnsentPaidCollection();
                if(collectionDetails.size() > 0){
                    hasData = true;
                }
                publishProgress(1);

                List<ECreditApplication> loanApplications = poCreditApp.getUnsentLoanApplication();
                if(loanApplications.size() > 0){
                    hasData = true;
                }
                publishProgress(1);

                List<ECreditApplicationDocuments> docsFile = poDocs.getUnsentApplicationDocumentss();
                if(docsFile.size() > 0){
                    hasData = true;
                }
                publishProgress(1);

            } catch (Exception e){
                e.printStackTrace();
            }
            return hasData;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mListener.OnCheckLocalData(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mListener.OnCheckProgress(values[0]);
        }
    }*/
    private class CheckDataTask{
        OnDataFetchListener mListener;
        public CheckDataTask(OnDataFetchListener mListener) {
            this.mListener = mListener;
        }
        public void execute(){
            TaskExecutor.Execute(null, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    mListener.OnCheck();
                }

                @Override
                public Object DoInBackground(Object args) {
                    boolean hasData = false;
                    try{
                        List<EImageInfo> loginImageInfo = poImage.getUnsentSelfieLogImageList();
                        if(loginImageInfo.size() > 0){
                            hasData = true;
                        }
                        TaskExecutor.ShowProgress(() -> mListener.OnCheckProgress(1));

                        List<EDCPCollectionDetail> collectionDetails = poDcp.getUnsentPaidCollection();
                        if(collectionDetails.size() > 0){
                            hasData = true;
                        }
                        TaskExecutor.ShowProgress(() -> mListener.OnCheckProgress(1));

                        List<ECreditApplication> loanApplications = poCreditApp.getUnsentLoanApplication();
                        if(loanApplications.size() > 0){
                            hasData = true;
                        }
                        TaskExecutor.ShowProgress(() -> mListener.OnCheckProgress(1));

                        List<ECreditApplicationDocuments> docsFile = poDocs.getUnsentApplicationDocumentss();
                        if(docsFile.size() > 0){
                            hasData = true;
                        }
                        TaskExecutor.ShowProgress(() -> mListener.OnCheckProgress(1));

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return hasData;
                }

                @Override
                public void OnPostExecute(Object object) {
                    Boolean aBoolean = (Boolean) object;
                    mListener.OnCheckLocalData(aBoolean);
                }
            });
        }
    }

}
