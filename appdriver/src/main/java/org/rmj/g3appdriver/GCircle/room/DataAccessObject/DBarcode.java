package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcodeDetail;

import java.util.List;

@Dao
public interface DBarcode {

    @Upsert
    void save(EBarcode barcode);

    @Query("UPDATE Barcode SET checked = :status WHERE barcode_id = :bcodeID")
    void index(String bcodeID, Integer status);

    @Query("DELETE FROM Barcode WHERE barcode_id = :bcodeID")
    void deleteBarcode(String bcodeID);

    @Query("DELETE FROM Barcode")
    void clearBarcode();

    @Query("SELECT COUNT(*) FROM Barcode")
    int getBarcodeCount();

    @Query("SELECT * FROM Barcode")
    LiveData<List<EBarcode>> getBarcodes();

    @Query("SELECT b.* FROM Barcode a, Barcode_Detail b " +
            "WHERE a.barcode_id = b.barcode_id AND a.checked = 1 " +
            "ORDER BY nEntryNox ASC")
    LiveData<List<EBarcodeDetail>> observeCheckedBarcodes();

    @Query("SELECT * FROM Barcode WHERE checked = 1")
    List<EBarcode> getCheckedBarcodes();

}
