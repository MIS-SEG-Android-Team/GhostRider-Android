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

    @ColumnInfo(name = "checked")
    private Integer checked = 0;

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

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }
}
