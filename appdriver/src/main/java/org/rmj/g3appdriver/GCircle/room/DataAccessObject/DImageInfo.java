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
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;

import java.util.List;

@Dao
public interface DImageInfo {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveImageInfo(EImageInfo imageInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(EImageInfo imageInfo);

    @Query("UPDATE Image_Information SET sSourceNo = :fsSource WHERE sSourceNo = :fsOldTransNox")
    void UpdateTransNox(String fsSource, String fsOldTransNox);

    @Query("SELECT COUNT (*) FROM Image_Information")
    int GetRowsCountForID();

    @Query("SELECT COUNT(*) FROM Image_Information " +
            "WHERE sSourceNo = :fsSource " +
            "AND sFileCode = :fsCategrID " +
            "AND sSourceCD = 'SSDD'")
    int CountImagePerCategory(String fsSource, String fsCategrID);

    @Query("SELECT COUNT(*) FROM Image_Information " +
            "WHERE sSourceNo = :fsSource " +
            "AND sSourceCD = 'SSDD'")
    int GetCountPerTransaction(String fsSource);

    @Query("SELECT * FROM Image_Information WHERE sTransNox =:TransNox")
    EImageInfo GetImageInfo(String TransNox);

    /**
     *
     * @param TransNox pass the new transaction no which will be return by API after uploading image...
     * @param DateModifield call the AppConstants.DATE_MODIFIED to set the current date time for dTimeStmp
     * @param oldTransNox pass the old transaction no. of image info before uploading image to server...
     */
    @Query("UPDATE Image_Information " +
            "SET sTransNox =:TransNox, " +
            "cSendStat = '1', " +
            "dSendDate =:DateModifield " +
            "WHERE sTransNox =:oldTransNox")
    void updateImageInfo(String TransNox, String DateModifield, String oldTransNox);

    /**
     *
     * @return returns a LiveData List of all unsent DCP image info...
     */
    @Query("SELECT * FROM Image_Information " +
            "WHERE cSendStat = 0 " +
            "AND sSourceNo = (SELECT sTransNox " +
            "FROM LR_DCP_Collection_Master " +
            "ORDER BY dTransact DESC LIMIT 1) " +
            "AND sFileCode = (SELECT sFileCode FROM EDocSys_File WHERE sBarrcode = 'DCP001')")
    LiveData<List<EImageInfo>> getUnsentDCPImageInfoList();

    @Query("SELECT * FROM Image_Information " +
            "WHERE sSourceNo = :fsSource " +
            "AND sFileCode = :fsCategrID " +
            "AND sSourceCD = 'SSDD'")
    LiveData<List<EImageInfo>> GetSSDDImagesPerCategory(String fsSource, String fsCategrID);

    @Query("SELECT * FROM Image_Information " +
            "WHERE sDtlSrcNo = :sDtlSrcNo AND " +
            "sImageNme = :sImageNme")
    LiveData<EImageInfo> getImageLocation(String sDtlSrcNo, String sImageNme);

    @Query("SELECT sFileLoct FROM Image_Information WHERE sTransNox =:TrasNox")
    String GetImageFileLocation(String TrasNox);

    @Query("SELECT * FROM Image_Information WHERE sFileCode = '0021' AND cSendStat <> '1'")
    List<EImageInfo> getUnsentLoginImageInfo();

    @Query("SELECT * FROM Image_Information WHERE sSourceNo =:fsSource AND sDtlSrcNo =:fsDetail")
    EImageInfo CheckImageForCIExist(String fsSource, String fsDetail);

    @Query("SELECT * FROM Image_Information WHERE sSourceNo =:fsSource AND sFileCode =:FileCode")
    EImageInfo CheckCreditAppDocumentIfExist(String fsSource, String FileCode);

    @Query("SELECT * FROM Image_Information WHERE sSourceNo =:TransNox AND sDtlSrcNo=:AccntNo")
    EImageInfo getDCPImageInfoForPosting(String TransNox, String AccntNo);

}
