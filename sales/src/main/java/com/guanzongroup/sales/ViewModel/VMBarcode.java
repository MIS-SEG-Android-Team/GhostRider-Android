package com.guanzongroup.sales.ViewModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.Sales.Barcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBarcode extends AndroidViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Barcode poBarcode;

    public VMBarcode(@NonNull Application application) {
        super(application);

        this.context = application;
        this.poBarcode = new Barcode(context);
    }

    public void CheckPermission(onCheckPermission callback){

        TaskExecutor.Execute(context, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onChecking("Initializing Camera");
            }

            @Override
            public Object DoInBackground(Object args) {

                Context instance = (Context) args;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (instance.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    if (ContextCompat.checkSelfPermission(instance, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        return true;
                    }else {
                        return false;
                    }
                }
            }

            @Override
            public void OnPostExecute(Object object) {

                if ((boolean) object){
                    callback.onPermissionGranted();
                }else {
                    callback.onPermissionDenied("Camera Permission Denied. Please enable from settings.");
                }

            }
        });
    }

    public void saveBarcode(EBarcode barcode){
        poBarcode.saveBarcode(barcode);
    }

    public int countBarcode(){
        return poBarcode.countBarcode();
    }

    public void deleteBarcode(String barCodeID){
        poBarcode.deleteBarcode(barCodeID);
    }

    public LiveData<List<EBarcode>> observeBarcodeList(){
        return poBarcode.getBarcodeList();
    }

    public interface onCheckPermission{
        void onChecking(String message);
        void onPermissionGranted();
        void onPermissionDenied(String message);
    }
}
