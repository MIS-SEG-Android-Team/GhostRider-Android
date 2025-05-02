package org.rmj.g3appdriver.GCircle.Apps.Sales;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.util.List;

;

public class Barcode {

    private DBarcode barcodeDao;

    public Barcode(Context context){
        this.barcodeDao = GGC_GCircleDB.getInstance(context).barcodeDao();
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

    public Bitmap generateQR(JSONObject loData) throws Exception{

        //todo: initialize barcode encoder
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

        //todo: generate image details for bitmap
        BitMatrix bitMatrix = new MultiFormatWriter().encode(loData.toString(), BarcodeFormat.QR_CODE, 700, 700);
        return barcodeEncoder.createBitmap(bitMatrix); //todo: return bitmap

    }

}
