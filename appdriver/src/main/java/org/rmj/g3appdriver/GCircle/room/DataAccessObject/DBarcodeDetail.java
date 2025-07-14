package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcodeDetail;

import java.util.List;

@Dao
public interface DBarcodeDetail {

    @Upsert
    void insert(EBarcodeDetail poBarcodeDetail);

    @Query("DELETE FROM Barcode_Detail")
    void clear();

    @Query("SELECT * FROM Barcode_Detail WHERE barcode_id = :barcode_id ORDER BY nEntryNox ASC")
    LiveData<List<EBarcodeDetail>> GetBarcodeItems(String barcode_id);
}
