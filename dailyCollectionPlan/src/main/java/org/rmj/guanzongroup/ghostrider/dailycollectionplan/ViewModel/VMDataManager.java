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
import org.rmj.g3appdriver.GCircle.room.Repositories.RCreditApplication;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCreditApplicationDocument;
import org.rmj.g3appdriver.GCircle.room.Repositories.RDailyCollectionPlan;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.GCircle.Apps.SelfieLog.SelfieLog;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMDataManager extends AndroidViewModel {

    private final SelfieLog poLog;
    private final RImageInfo poImage;
    private final RDailyCollectionPlan poDcp;
    private final RCreditApplication poCreditApp;
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
        this.poDocs = new RCreditApplicationDocument(instance);
    }

    public void checkData(OnDataFetchListener listener){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnCheck();
            }

            @Override
            public Object DoInBackground(Object args) {
                boolean hasData = false;
                try{
                    List<EImageInfo> loginImageInfo = poImage.getUnsentSelfieLogImageList();
                    if(loginImageInfo.size() > 0){
                        hasData = true;
                    }
                    TaskExecutor.ShowProgress(() -> listener.OnCheckProgress(1));

                    List<EDCPCollectionDetail> collectionDetails = poDcp.getUnsentPaidCollection();
                    if(collectionDetails.size() > 0){
                        hasData = true;
                    }
                    TaskExecutor.ShowProgress(() -> listener.OnCheckProgress(1));

                    List<ECreditApplication> loanApplications = poCreditApp.getUnsentLoanApplication();
                    if(loanApplications.size() > 0){
                        hasData = true;
                    }
                    TaskExecutor.ShowProgress(() -> listener.OnCheckProgress(1));

                    List<ECreditApplicationDocuments> docsFile = poDocs.getUnsentApplicationDocumentss();
                    if(docsFile.size() > 0){
                        hasData = true;
                    }
                    TaskExecutor.ShowProgress(() -> listener.OnCheckProgress(1));

                } catch (Exception e){
                    e.printStackTrace();
                }
                return hasData;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean aBoolean = (Boolean) object;
                listener.OnCheckLocalData(aBoolean);
            }
        });
    }
}
