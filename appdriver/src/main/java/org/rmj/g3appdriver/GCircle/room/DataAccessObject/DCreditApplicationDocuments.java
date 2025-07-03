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

import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicationDocuments;

import java.util.List;

@Dao
    public interface DCreditApplicationDocuments {

    @Query("SELECT * FROM Credit_Online_Application_Documents WHERE sTransNox=:args")
    ECreditApplicationDocuments GetCreditAppDocs(String args);

    @Query("UPDATE Credit_Online_Application_Documents " +
            "SET sFileLoct = (SELECT sFileLoct FROM Image_Information WHERE sSourceNo =:TransNox AND sFileCode=:sFileCD), " +
            "sImageNme = (SELECT sImageNme FROM Image_Information WHERE sSourceNo =:TransNox AND sFileCode=:sFileCD) " +
            "WHERE sTransNox =:TransNox " +
            "AND sFileCode =:sFileCD")
    void updateDocumentsInfo(String TransNox, String sFileCD);

    @Query("UPDATE Credit_Online_Application_Documents " +
            "SET sFileLoct = (SELECT sFileLoct FROM Image_Information WHERE sSourceNo =:TransNox AND sFileCode = Credit_Online_Application_Documents.sFileCode), " +
            "sImageNme = (SELECT sImageNme FROM Image_Information WHERE sSourceNo =:TransNox AND sFileCode = Credit_Online_Application_Documents.sFileCode) " +
            "WHERE sTransNox =:TransNox ")
    void updateDocumentsInfos(String TransNox);

    @Query("INSERT INTO Credit_Online_Application_Documents (sTransNox, sFileCode, nEntryNox) " +
            "SELECT a.sTransNox, b.sFileCode, b.nEntryNox FROM Credit_Online_Application_List a LEFT JOIN EDocSys_File b " +
            "WHERE a.sTransNox =:TransNox AND b.sFileCode !='0021' AND b.sFileCode !='0020' ")
        void insertDocumentByTransNox(String TransNox);

    @Query("SELECT a.sTransNox, " +
            "a.sFileCode, " +
            "b.sBriefDsc, " +
            "a.sImageNme, " +
            "a.sFileLoct, " +
            "a.cSendStat " +
            "FROM Credit_Online_Application_Documents a " +
            "LEFT JOIN EDocSys_File b " +
            "ON a.sFileCode = b.sFileCode " +
            "WHERE a.sTransNox =:TransNox " +
            "ORDER BY a.nEntryNox ASC")
    LiveData<List<ApplicationDocument>> GetCreditAppDocuments(String TransNox);

    @Query("SELECT * FROM Credit_Online_Application_Documents WHERE cSendStat != '1' GROUP BY sTransNox")
    List<ECreditApplicationDocuments> getUnsentApplicationDocumentss();

    class ApplicationDocument{
        public String sTransNox;
        public String sFileCode;
        public String sBriefDsc;
        public String sImageNme;
        public String sFileLoct;
        public String cSendStat;
    }
}
