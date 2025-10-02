package org.rmj.guanzongroup.ghostrider.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.lib.Panalo.Obj.GPanalo;
import org.rmj.g3appdriver.lib.Panalo.model.PanaloRewards;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import java.util.List;

public class VMPanaloRewards extends AndroidViewModel {
    private static final String TAG = VMPanaloRewards.class.getSimpleName();

    private final GPanalo poSys;

    public interface OnRetrieveRewardsListener{
        void OnLoad(String title, String message);
        void OnSuccess(List<PanaloRewards> earned, List<PanaloRewards> claimed);
        void OnFailed(String message);
    }



    public VMPanaloRewards(@NonNull Application application) {
        super(application);
       this.poSys = new GPanalo(application);
    }

    public void GetRewards(int fnArgs, OnRetrieveRewardsListener listener){
        new GetRewardsTask(listener).execute(fnArgs);
    }

    private class GetRewardsTask{
        private final OnRetrieveRewardsListener listener;
        private List<PanaloRewards> earned, claimed;
        private String message;

        public GetRewardsTask(OnRetrieveRewardsListener listener) {
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
                    try {
                        earned = poSys.GetRewards("0");
                        if (earned == null) {
                            message = poSys.getMessage();
                            Log.e(TAG, message);
                        }

                        Thread.sleep(1000);
                        claimed = poSys.GetRewards("1");
                        if (claimed == null) {
                            message = poSys.getMessage();
                            Log.e(TAG, message);
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
                        listener.OnSuccess(earned, claimed);
                    }
                }
            });
        }
    }

    public void RedeemReward(){

    }
}
