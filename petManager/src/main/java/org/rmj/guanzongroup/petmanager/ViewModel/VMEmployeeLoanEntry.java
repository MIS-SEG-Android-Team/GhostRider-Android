package org.rmj.guanzongroup.petmanager.ViewModel;

import android.app.Application;
import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import java.util.List;

public class VMEmployeeLoanEntry extends AndroidViewModel{
    private static final String TAG = VMEmployeeLoanEntry.class.getSimpleName();
    private Context context;
    private EmployeeLoan poLoan;
    private String message;
    private ConnectionUtil poConnect;

    public VMEmployeeLoanEntry(@NonNull Application application) {
        super(application);

        this.context = application;
        this.poLoan = new EmployeeLoan(application);
        this.poConnect = new ConnectionUtil(application);
    }

    public LiveData<List<ELoanTypes>> GetLoanTypes(){
        return poLoan.GetLoanTypes();
    }
    public ArrayAdapter<String> GetTerms(){
        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, poLoan.GetTermConstants());
        return termAdapter;
    }
    public String GenerateID(){
        return poLoan.CreateUniqueID();
    }
    public String CurrentDate(){
        return poLoan.CurrentDateTime();
    }
    public String GetEmpID(){
        return poLoan.GetEmpID();
    }
    public void SaveLoanEntry(EEmpLoan foVal, OnSaveEntry callback){
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onLoad("Employee Loan", "Saving entry . .");
            }
            @Override
            public Object DoInBackground(Object args) {
                EEmpLoan loVal = (EEmpLoan) args;

                if (!poLoan.ValidateEntry(loVal)){
                    message = poLoan.GetMessage();
                    return false;
                }else{
                    //todo: save to local first
                    poLoan.SaveLoan(loVal);

                    //todo: if connected to server, upload to server
                    if (poConnect.isDeviceConnected()){
                        if (!poLoan.UploadLoanEntry(loVal)){
                            message = poLoan.GetMessage();
                            return false;
                        }
                    }

                    return true;
                }
            }
            @Override
            public void OnPostExecute(Object object) {
                Boolean aBoolean = (Boolean) object;
                if (!aBoolean){
                    callback.onFailed(message);
                }else {
                    callback.onSuccess(message);
                }
            }
        });
    }
    public interface OnSaveEntry{
        void onLoad(String title, String message);
        void onSuccess(String message);
        void onFailed(String message);
    }

}
