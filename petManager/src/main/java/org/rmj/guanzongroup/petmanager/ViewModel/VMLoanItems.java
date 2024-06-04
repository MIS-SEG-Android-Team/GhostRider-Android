package org.rmj.guanzongroup.petmanager.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;

import java.util.List;

public class VMLoanItems extends AndroidViewModel {
    private EmployeeLoan poLoans;
    public VMLoanItems(@NonNull Application application) {
        super(application);

        this.poLoans = new EmployeeLoan(application);
    }

    public LiveData<List<EEmpLoan>> GetUserHistory(){
        return poLoans.GetUserEntries(poLoans.GetEmpID());
    }
    public LiveData<List<EEmpLoan>> GetForApproval(){
        return poLoans.GetForApproval();
    }
    public String GetUserLevel(){
        return poLoans.GetEmpLevel();
    }
}
