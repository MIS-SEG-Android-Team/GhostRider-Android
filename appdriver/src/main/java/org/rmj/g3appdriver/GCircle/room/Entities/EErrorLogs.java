package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Error_Logs", primaryKeys = {"nErrorLogID"})
public class EErrorLogs {

    @NonNull
    @ColumnInfo(name = "nErrorLogID")
    public int nErrorLogID;

    @ColumnInfo(name = "sSourceTransNo")
    public String sSourceTransNo;

    @ColumnInfo(name = "sMessagex")
    public String sMessagex;

    @ColumnInfo(name = "dLogDate")
    public String dLogDate;

    @ColumnInfo(name = "cRead")
    public String cRead;

    public int getnErrorLogID() {
        return nErrorLogID;
    }

    public void setnErrorLogID(int nErrorLogID) {
        this.nErrorLogID = nErrorLogID;
    }

    public String getsSourceTransNo() {
        return sSourceTransNo;
    }

    public void setsSourceTransNo(String sSourceTransNo) {
        this.sSourceTransNo = sSourceTransNo;
    }

    public String getsMessagex() {
        return sMessagex;
    }

    public void setsMessagex(String sMessagex) {
        this.sMessagex = sMessagex;
    }

    public String getdLogDate() {
        return dLogDate;
    }

    public void setdLogDate(String dLogDate) {
        this.dLogDate = dLogDate;
    }

    public String getcRead() {
        return cRead;
    }

    public void setcRead(String cRead) {
        this.cRead = cRead;
    }
}
