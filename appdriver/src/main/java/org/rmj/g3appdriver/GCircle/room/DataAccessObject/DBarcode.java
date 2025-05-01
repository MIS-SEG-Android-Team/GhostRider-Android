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

    @Query("SELECT * FROM Barcode")
    LiveData<List<EBarcode>> getBarcodes();

    @Query("SELECT * FROM Barcode")
    List<EBarcode> getBarcodeList();

    @Query("SELECT COUNT(*) FROM Barcode")
    int getBarcodeCount();

    @Query("DELETE FROM Barcode WHERE barcode_id = :bcodeID")
    void deleteBarcode(String bcodeID);

}
