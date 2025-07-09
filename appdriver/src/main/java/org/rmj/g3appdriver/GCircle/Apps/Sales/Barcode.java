package org.rmj.g3appdriver.GCircle.Apps.Sales;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcodeDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcodeDetail;
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
    private DBarcodeDetail barcodeDetailDao;
    private GCircleApi poApi;
    private HttpHeaders poHeaders;

    private String message;

    public Barcode(Application context){
        this.barcodeDao = GGC_GCircleDB.getInstance(context).barcodeDao();
        this.barcodeDetailDao = GGC_GCircleDB.getInstance(context).barcodeDetailDao();
        this.poApi = new GCircleApi(context);
        this.poHeaders = HttpHeaders.getInstance(context);
    }

    public void saveBarcode(String barcode){

        EBarcode foVal = new EBarcode();
        foVal.setBarcodeIdxx(generateTransNox());
        foVal.setBarcode(barcode);
        foVal.setChecked(0);

        barcodeDao.save(foVal);
    }

    public void selectBarcode(String bcodeIDxx, Integer status){
        barcodeDao.index(bcodeIDxx, status);
    }

    public void deleteBarcode(String bcodeID){
        barcodeDao.deleteBarcode(bcodeID);
    }

    private String getCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    private String generateTransNox(){

        String barcodeid = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            barcodeid = "MX01" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                    barcodeDao.getBarcodeCount() + 1;
        }else {
            barcodeid = "MX01" +
                    new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    barcodeDao.getBarcodeCount() + 1;
        }

        return barcodeid;
    }

    public LiveData<List<EBarcode>> getBarcodeList(){
        return barcodeDao.getBarcodes();
    }

    public LiveData<List<EBarcode>> observeCheckedBarcodeList(){
        return barcodeDao.observeCheckedBarcodes();
    }

    public List<EBarcode> getCheckedBarcodeList(){
        return barcodeDao.getCheckedBarcodes();
    }

    public LiveData<List<EBarcodeDetail>> getBarcodeITems(String id){
        return barcodeDetailDao.GetBarcodeItems(id);
    }

    public String getMessage(){
        return message;
    }

    public Boolean downloadBundles(String sBundleIdxx){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sBundleIdxx", sBundleIdxx);

            String lsResponse = WebClient.sendRequest(poApi.getUrlDownloadBundles(), loParams.toString(), poHeaders.getHeaders());

           if (lsResponse == null || lsResponse.isEmpty()){
                message = "No response from server";
                return false;
            }

            Log.d("Barcode", lsResponse);

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if (lsResult.equalsIgnoreCase("error")) {

                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONObject loData = loResponse.getJSONObject("detail");
            JSONArray loArray = loData.getJSONArray("payload");

            final String lsTransNox = generateTransNox();

            for (int i = 0; i < loArray.length(); i++){

                JSONObject loItem = loArray.getJSONObject(i);

                EBarcodeDetail barcodeDetail = new EBarcodeDetail();
                barcodeDetail.setBarcode_id(lsTransNox);
                barcodeDetail.setnEntryNox(loItem.getInt("nEntryNox"));
                barcodeDetail.setsDescript(loItem.getString("sSerialID"));

                barcodeDetailDao.insert(barcodeDetail);
            }

            EBarcode barcode = new EBarcode();
            barcode.setBarcodeIdxx(lsTransNox);
            barcode.setBarcode(loData.getString("bundleIDxx"));
            barcode.setsDescriptxx(loData.getString("sDescriptxx"));
            barcode.setChecked(0);

            barcodeDao.save(barcode);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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

            String lsTransnox = loResponse.getString("sTransNox");
            message = lsTransnox.substring(lsTransnox.length() - 6);

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
