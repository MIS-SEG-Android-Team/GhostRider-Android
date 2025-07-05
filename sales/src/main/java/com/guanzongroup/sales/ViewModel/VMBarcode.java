package com.guanzongroup.sales.ViewModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.Sales.Barcode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.lib.Etc.Town;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBarcode extends AndroidViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Barcode poBarcode;
    private final Town poTown;
    private final ConnectionUtil poConn;

    private String message;

    public VMBarcode(@NonNull Application application) {
        super(application);

        this.context = application;
        this.poTown = new Town(application);
        this.poBarcode = new Barcode(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<List<EBarcode>> observeBarcodeList(){
        return poBarcode.getBarcodeList();
    }

    public LiveData<List<EBarcode>> observeCheckedBarcodeList(){
        return poBarcode.getCheckedBarcodeList();
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

    public void saveBarcode(String barcode){
        poBarcode.saveBarcode(barcode);
    }

    public void selectBarcode(String bcodeIDxx, Integer status){
        poBarcode.selectBarcode(bcodeIDxx, status);
    }

    public void deleteBarcode(String barCodeID){
        poBarcode.deleteBarcode(barCodeID);
    }

    public void downloadBundles(String sBundleIdxx, OnDownloadBundles callback){

        TaskExecutor.Execute(sBundleIdxx, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad("Guanzon Circle", "Downloading bundles . .");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                if (!poBarcode.downloadBundles((String) args)){
                    message = poBarcode.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if (!(Boolean) object){
                    callback.OnFailed(message);
                }else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public void submitBarcodes(JSONObject loData, onSubmitBarcodes callback){

        TaskExecutor.Execute(loData, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onLoad("Guanzon Circle", "Sending barcodes . .");
            }

            @Override
            public Object DoInBackground(Object args) {

                JSONObject params = (JSONObject) args;
                Boolean response = poBarcode.submitBarcodes(params, "0");
                if (response){
                    message = poBarcode.getMessage();
                    return true;
                }else {
                    message = poBarcode.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {

                Boolean result = (Boolean) object;
                if (result){
                    callback.onSuccess(message);
                }else {
                    callback.onFailed(message);
                }
            }
        });
    }

    public void generateQR(JSONObject loData, onGenerateQR callback) throws Exception {

        TaskExecutor.Execute(loData, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onGenerating();
            }

            @Override
            public Object DoInBackground(Object args) {

                try {

                    JSONObject params = (JSONObject) args;

                    Log.d("BARCODE", params.toString());
                    if (poBarcode.generateQR(params) == null){
                        return null;
                    }else {
                        return poBarcode.generateQR(params);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                if (object != null){
                    callback.onQRGenerated((Bitmap) object);
                }else {
                    callback.onQRGenerationFailed("QR failed to generate.");
                }

            }
        });

    }

    public interface OnDownloadBundles{
        void OnLoad(String title, String message);
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface onSubmitBarcodes{
        void onLoad(String title, String message);
        void onSuccess(String transNox);
        void onFailed(String message);
    }

    public interface onGenerateQR{
        void onGenerating();
        void onQRGenerated(Bitmap bitmap);
        void onQRGenerationFailed(String message);
    }

    public interface onCheckPermission{
        void onChecking(String message);
        void onPermissionGranted();
        void onPermissionDenied(String message);
    }
}
