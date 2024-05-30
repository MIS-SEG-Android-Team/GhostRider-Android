package org.rmj.guanzongroup.petmanager.ViewModel;

import android.app.Application;
import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;

import java.util.List;

public class VMEmployeeLoanEntry extends AndroidViewModel{
    private static final String TAG = VMEmployeeLoanEntry.class.getSimpleName();
    private Context context;
    private EmployeeLoan poLoan;

    public VMEmployeeLoanEntry(@NonNull Application application) {
        super(application);

        this.context = application;
        this.poLoan = new EmployeeLoan(application);
    }

    public LiveData<List<ELoanTypes>> GetLoanTypes(){
        return poLoan.GetLoanTypes();
    }
    public ArrayAdapter<String> GetTerms(){
        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, poLoan.GetTermConstants());
        return termAdapter;
    }

}
