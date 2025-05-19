package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;

import java.util.List;

@Dao
public interface DBarcode {

    @Upsert
    void save(EBarcode barcode);

    @Query("UPDATE Barcode SET checked = :status WHERE barcode_id = :bcodeID")
    void index(String bcodeID, Integer status);

    @Query("DELETE FROM Barcode WHERE barcode_id = :bcodeID")
    void deleteBarcode(String bcodeID);

    @Query("SELECT * FROM Barcode")
    LiveData<List<EBarcode>> getBarcodes();

    @Query("SELECT * FROM Barcode WHERE checked = 1")
    LiveData<List<EBarcode>> getCheckedBarcodes();

    @Query("SELECT COUNT(*) FROM Barcode")
    int getBarcodeCount();

}
