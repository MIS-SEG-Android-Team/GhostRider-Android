package org.rmj.guanzongroup.evaluation.ViewModel.SSDD;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.SSDD.SSDDEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMSSDEvaluation extends AndroidViewModel {

    private String fsMessage;
    private SSDDEvaluation loEvaluation;

    public VMSSDEvaluation(@NonNull Application application) {
        super(application);

        this.loEvaluation = new SSDDEvaluation(application);
    }

    public LiveData<List<ESSDDepartments>> GetDepartments(){
        return loEvaluation.GetDepartments();
    }

    public LiveData<List<ESSDDCategories>> GetCategories(){
        return loEvaluation.GetCategories();
    }

    public ESSDDCategories GetCategory(String fsCategory){
        return loEvaluation.GetCategory(fsCategory);
    }

    public LiveData<List<ESSDDMaster>> GetMasterList(String dFrom, String dTo, String sDeptIDxx, String cTranStat){
        return loEvaluation.GetMasterList(dFrom, dTo, sDeptIDxx, cTranStat);
    }

    public LiveData<ESSDDMaster> GetMaster(String fsTransNox, String fsDeptIDxx) {
        return loEvaluation.GetMaster(fsTransNox, fsDeptIDxx);
    }

    public LiveData<List<ESSDDetail>> GetDetail(String fsTransNox) {
        return loEvaluation.GetDetail(fsTransNox);
    }

    public LiveData<ESSDDetail> GetCategoryDetail(String fsTransNox, String fsCategory){
        return loEvaluation.GetCategoryDetail(fsTransNox, fsCategory);
    }

    public LiveData<List<ESSDDImages>> GetCategoryImages(String fsTransNox, String fsCategory){
        return loEvaluation.GetCategoryImages(fsTransNox, fsCategory);
    }

    public LiveData<ESSDDImages> GetImage(String fsTransNox, String fsCategory){
        return loEvaluation.GetImage(fsTransNox, fsCategory);
    }

    public ESSDDepartments GetDepartment(String fsDeptIDxx){
        return loEvaluation.GetDepartment(fsDeptIDxx);
    }

    public interface OnDownloadCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess();
        void OnFailed(String fsMessage);
    }


    public String CreateEvaluation(String fsDeptID, List<ESSDDCategories> foCategories){
        return loEvaluation.CreateEvaluation(fsDeptID, foCategories);
    }

    public void DownloadSSDDepartments(OnDownloadCallback focallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Departments", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (loEvaluation.DownloadDepartments()){
                    return true;
                }else {
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {

                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void DownloadEvaluation(String fsDeptIDxx, String fsDfrom, String fsDto, OnDownloadCallback focallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Master", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (loEvaluation.DownloadMaster(fsDeptIDxx, fsDfrom, fsDto)){
                    return true;
                }else if (loEvaluation.DownloadCategories()){
                    return true;
                }else {
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }
}
