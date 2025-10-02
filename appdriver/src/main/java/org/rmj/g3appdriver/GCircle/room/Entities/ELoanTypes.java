package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Loan_Type")
public class ELoanTypes {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sLoanIDxx")
    public String sLoanIDxx;

    @ColumnInfo(name = "sLoanName")
    public String sLoanNmxx;

    @ColumnInfo(name = "cRecdStat")
    public String cRecdStat;

    @ColumnInfo(name = "dTimeStmp")
    public String dTimeStmp;

    public String getsLoanIDxx() {
        return sLoanIDxx;
    }

    public void setsLoanIDxx(String sLoanIDxx) {
        this.sLoanIDxx = sLoanIDxx;
    }

    public String getsLoanNmxx() {
        return sLoanNmxx;
    }

    public void setsLoanNmxx(String sLoanNmxx) {
        this.sLoanNmxx = sLoanNmxx;
    }

    public String getcRecdStat() {
        return cRecdStat;
    }

    public void setcRecdStat(String cRecdStat) {
        this.cRecdStat = cRecdStat;
    }

    public String getdTimeStmp() {
        return dTimeStmp;
    }

    public void setdTimeStmp(String dTimeStmp) {
        this.dTimeStmp = dTimeStmp;
    }
}
