package org.rmj.g3appdriver.GCircle.Apps.Sales;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.lifecycle.LiveData;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

;

public class Barcode {

    private DBarcode barcodeDao;
    private GCircleApi poApi;
    private HttpHeaders poHeaders;

    private String message;

    public Barcode(Application context){
        this.barcodeDao = GGC_GCircleDB.getInstance(context).barcodeDao();
        this.poApi = new GCircleApi(context);
        this.poHeaders = HttpHeaders.getInstance(context);
    }

    private String getCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    public void saveBarcode(EBarcode barcode){
        barcodeDao.save(barcode);
    }

    public void selectBarcode(String bcodeIDxx, Integer status){
        barcodeDao.index(bcodeIDxx, status);
    }

    public int countBarcode(){
        return barcodeDao.getBarcodeCount();
    }

    public void deleteBarcode(String bcodeID){
        barcodeDao.deleteBarcode(bcodeID);
    }

    public LiveData<List<EBarcode>> getBarcodeList(){
        return barcodeDao.getBarcodes();
    }

    public LiveData<List<EBarcode>> getCheckedBarcodeList(){
        return barcodeDao.getCheckedBarcodes();
    }

    public String getMessage(){
        return message;
    }

    public Boolean submitBarcodes(JSONObject loData, String productType){

        try{

            JSONObject loParams = new JSONObject();
            loParams.put("dTransact", getCurrentDate());
            loParams.put("cDivision", productType);
            loParams.put("sPayloadx", loData);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSubmitBarcode(), loParams.toString(), poHeaders.getHeaders());
            if (lsResponse == null || lsResponse.isEmpty()){
                message = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Bitmap generateQR(JSONObject loData) throws Exception{

        //todo: initialize barcode encoder
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

        //todo: generate image details for bitmap
        BitMatrix bitMatrix = new MultiFormatWriter().encode(loData.toString(), BarcodeFormat.QR_CODE, 700, 700);
        return barcodeEncoder.createBitmap(bitMatrix); //todo: return bitmap

    }

}
