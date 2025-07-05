package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Barcode_Detail", primaryKeys = {"barcode_id", "nEntryNox"})
public class EBarcodeDetail {

    @NonNull
    @ColumnInfo
    public String barcode_id;

    @NonNull
    @ColumnInfo
    public int nEntryNox;

    @ColumnInfo
    public String sDescript;

    @NonNull
    public String getBarcode_id() {
        return barcode_id;
    }

    public void setBarcode_id(@NonNull String barcode_id) {
        this.barcode_id = barcode_id;
    }

    @NonNull
    public int getnEntryNox() {
        return nEntryNox;
    }

    public void setnEntryNox(@NonNull int nEntryNox) {
        this.nEntryNox = nEntryNox;
    }

    public String getsDescript() {
        return sDescript;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }
}
