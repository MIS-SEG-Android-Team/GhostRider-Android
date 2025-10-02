/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface DRawDao {

    @Query("SELECT (SELECT COUNT(*) FROM Branch_Info) AS Branch_Data," +
            "(SELECT COUNT(*) FROM Barangay_Info) AS Barangay_Data," +
            "(SELECT COUNT(*) FROM Town_Info) AS Town_Data," +
            "(SELECT COUNT(*) FROM Province_Info) AS Province_Data," +
            "(SELECT COUNT(*) FROM Country_Info) AS Country_Data," +
            "(SELECT COUNT(*) FROM MC_Brand) AS Mc_Brand," +
            "(SELECT COUNT(*) FROM Mc_Model) AS Mc_Model," +
            "(SELECT COUNT(*) FROM MC_Category) AS Mc_Category," +
            "(SELECT COUNT(*) FROM Mc_Model_Price) AS Mc_Model_Price," +
            "(SELECT COUNT(*) FROM MC_Term_Category) AS Mc_Term_Category," +
            "(SELECT COUNT(*) FROM Occupation_Info) AS Occupation_Data," +
            "(SELECT COUNT(*) FROM FB_Raffle_Transaction_Basis) AS Raffle_Basis," +
            "(SELECT COUNT(*) FROM EDocSys_File) AS File_Code," +
            "(SELECT COUNT(*) FROM Bank_Info) AS Bank_Data," +
            "(SELECT COUNT(*) FROM Collection_Account_Remittance) AS Remittance_Data," +
            "(SELECT COUNT(*) FROM Relation) AS Relation_Data, " +
            "(SELECT COUNT(*) FROM XXXSCA_REQUEST) AS Approval_Code")
    LiveData<AppLocalData> getAppLocalData();

    class AppLocalData{
        public int Branch_Data;
        public int Barangay_Data;
        public int Town_Data;
        public int Province_Data;
        public int Country_Data;
        public int Mc_Brand;
        public int Mc_Model;
        public int Mc_Category;
        public int Mc_Model_Price;
        public int Mc_Term_Category;
        public int Occupation_Data;
        public int Raffle_Basis;
        public int File_Code;
        public int Bank_Data;
        public int Remittance_Data;
        public int Relation_Data;
        public int Approval_Code;
    }
}
