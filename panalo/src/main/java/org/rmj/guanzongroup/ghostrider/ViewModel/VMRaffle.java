package org.rmj.guanzongroup.ghostrider.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleStatus;
import org.rmj.g3appdriver.lib.Panalo.Obj.GPanalo;
import org.rmj.g3appdriver.lib.Panalo.Obj.ILOVEMYJOB;
import org.rmj.g3appdriver.lib.Panalo.model.PanaloRewards;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMRaffle extends AndroidViewModel {

    private final GPanalo poSys;
    private final ILOVEMYJOB poPanalo;

    public interface OnRetrieveRaffleListener{
        void OnLoad(String title, String message);
        void OnSuccess(List<PanaloRewards> args);
        void OnFailed(String message);
    }



    public VMRaffle(@NonNull Application application) {
        super(application);
        this.poSys = new GPanalo(application);
        this.poPanalo = new ILOVEMYJOB(application);
    }

    public LiveData<ERaffleStatus> GetRaffleStatus(){
        return poPanalo.GetRaffleStatus();
    }

    public void GetRewards(int fnArgs, VMRaffle.OnRetrieveRaffleListener listener){
        new VMRaffle.GetRewardsTask(listener).execute(fnArgs);
    }

    private class GetRewardsTask{
        private final VMRaffle.OnRetrieveRaffleListener listener;
        private String message;

        public GetRewardsTask(VMRaffle.OnRetrieveRaffleListener listener) {
            this.listener = listener;
        }
        public void execute(int fnArgs){
            TaskExecutor.Execute(fnArgs, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    listener.OnLoad("Panalo Rewards", "Checking rewards. Please wait...");
                }

                @Override
                public Object DoInBackground(Object args) {
                    String lsType = String.valueOf(args);
                    List<PanaloRewards> loResult = poSys.GetRewards(lsType);
                    if(loResult == null){
                        message = poSys.getMessage();

                        return null;
                    }
                    return loResult;
                }

                @Override
                public void OnPostExecute(Object object) {
                    if(object == null){
                        listener.OnFailed(message);
                    } else {
                        listener.OnSuccess((List<PanaloRewards>) object);
                    }
                }
            });
        }
    }
}
