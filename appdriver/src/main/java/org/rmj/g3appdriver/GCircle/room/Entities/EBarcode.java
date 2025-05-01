package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Barcode")
public class EBarcode {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "barcode_id")
    private String barcodeIdxx;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @NonNull
    public String getBarcodeIdxx() {
        return barcodeIdxx;
    }

    public void setBarcodeIdxx(@NonNull String barcodeIdxx) {
        this.barcodeIdxx = barcodeIdxx;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
