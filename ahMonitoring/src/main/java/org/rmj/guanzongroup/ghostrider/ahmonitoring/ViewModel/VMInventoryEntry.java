package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EInventoryDetail;
import org.rmj.g3appdriver.GCircle.room.Repositories.RInventoryDetail;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Model.RandomItem;

public class VMInventoryEntry extends AndroidViewModel {
    private static final String TAG = VMInventoryEntry.class.getSimpleName();

    private final Application instance;
    private final RInventoryDetail poDetail;
    private String message;

    public interface OnInventoryUpdateCallBack {
        void OnUpdate(String message);

        void OnError(String message);
    }

    public VMInventoryEntry(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poDetail = new RInventoryDetail(instance);
    }

    public LiveData<EInventoryDetail> getInventoryItemDetail(String TransNox, String PartID, String BarCode) {
        return poDetail.getInventoryItemDetail(TransNox, PartID, BarCode);
    }

    public void saveInventoryUpdate(RandomItem foItem, OnInventoryUpdateCallBack callBack) {
//        new UpdateInventoryUpdateTask(instance, callBack).execute(foItem);
        TaskExecutor.Execute(foItem, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {

            }

            @Override
            public Object DoInBackground(Object args) {
                RandomItem lsRandomItems = (RandomItem) args;
                try {
                    RandomItem loItem = lsRandomItems;
                    poDetail.UpdateInventoryItem(loItem.getTransNox(),
                            loItem.getBarCodex(),
                            loItem.getPartIDxx(),
                            loItem.getItemQtyx(),
                            loItem.getRemarksx());
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if (lsSuccess) {
                    callBack.OnUpdate("Item updated.");
                } else {
                    callBack.OnError(message);
                }
            }
        });
    }
}
