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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchOpenMonitor;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationRecipient;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationUser;

@Dao
public interface DNotificationReceiver {

    @Insert
    void insert(ENotificationMaster notificationMaster);

    @Insert
    void insert(ENotificationRecipient notificationRecipient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ENotificationUser notificationUser);

    @Update
    void update(ENotificationRecipient notificationRecipient);

    @Query("UPDATE Notification_Info_Recepient SET cMesgStat =:status WHERE sTransNox =:MessageID")
    void updateNotificationStatusFromOtherDevice(String MessageID, String status);

    @Query("SELECT COUNT(*) FROM Notification_Info_Master WHERE sMesgIDxx=:TransNox")
    int CheckNotificationIfExist(String TransNox);

    @Query("SELECT * FROM Notification_Info_Recepient WHERE sTransNox =:MessageID")
    ENotificationRecipient GetNotification(String MessageID);
    @Query("SELECT * FROM Notification_User WHERE sUserIDxx=:fsVal")
    ENotificationUser CheckIfUserExist(String fsVal);

    @Query("SELECT COUNT(*) FROM Notification_Info_Master")
    int GetNotificationCountForID();

    @Query("SELECT sUserIDxx FROM User_Info_Master")
    String GetUserID();

    @Query("UPDATE Notification_Info_Recepient SET " +
            "dLastUpdt =:dateTime, " +
            "dReceived =:dateTime, " +
            "cMesgStat =:Status, " +
            "cStatSent = '1' " +
            "WHERE sTransNox =:MessageID")
    void UpdateSentResponseStatus(String MessageID, String Status, String dateTime);

    @Query("SELECT * FROM Notification_Info_Master WHERE sMesgIDxx=:fsVal")
    ENotificationMaster CheckIfMasterExist(String fsVal);

    @Insert
    void SaveBranchOpening(EBranchOpenMonitor foVal);

    @RawQuery
    String ExecuteTableUpdateQuery(SupportSQLiteQuery query);
}


