package org.guanzongroup.com.itinerary.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.GCircle.room.Entities.EItinerary;
import org.rmj.g3appdriver.GCircle.Apps.Itinerary.Obj.EmployeeItinerary;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMUsersItineraries extends AndroidViewModel {
    private static final String TAG = VMUsersItineraries.class.getSimpleName();

    private final EmployeeItinerary poSys;
    private String message;

    public VMUsersItineraries(@NonNull Application application) {
        super(application);
        this.poSys = new EmployeeItinerary(application);
    }

    public interface OnDownloadUserEntriesListener{
        void OnImport(String title, String message);
        void OnSuccess(List<EItinerary> args);
        void OnFailed(String message);
    }

    public void DownloadItineraryForUser(String args, String args1, String args2, OnDownloadUserEntriesListener listener){

        String[] params = {args, args1, args2};
        TaskExecutor.Execute(params, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnImport("Employee Itinerary", "Importing entries. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {

                String[] params = (String[]) args;

                String param1 = params[0];
                String param2 = params[1];
                String param3 = params[2];

                List<EItinerary> loList = poSys.GetItineraryListForEmployee(param1, param2, param3);
                if(loList == null){
                    message = poSys.getMessage();
                    return null;
                }

                return loList;

            }

            @Override
            public void OnPostExecute(Object object) {

                List<EItinerary> list = (List<EItinerary>) object;

                if(list == null){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess(list);
                }

            }
        });

    }

}
