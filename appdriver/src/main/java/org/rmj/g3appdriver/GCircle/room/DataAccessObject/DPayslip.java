package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationRecipient;

import java.util.List;

@Dao
public interface DPayslip {

    @Insert
    void insert(ENotificationMaster notificationMaster);

    @Insert
    void insert(ENotificationRecipient notificationRecipient);

    @Update
    void Update(ENotificationRecipient foVal);

    @Query("SELECT b.sTransNox, " +
            "a.sMesgIDxx, " +
            "a.sMsgTitle, " +
            "a.sMessagex, " +
            "b.cMesgStat, " +
            "b.dReceived " +
            "FROM Notification_Info_Master a " +
            "LEFT JOIN Notification_Info_Recepient b ON a.sMesgIDxx = b.sTransNox " +
            "WHERE a.sMsgTypex = '00000' " +
            "AND a.sAppSrcex IN ('gRider', 'IntegSys') " +
            "AND a.sMsgTitle LIKE 'PAYSLIP%' " +
            "AND b.sRecpntID = (SELECT sUserIDxx FROM User_Info_Master) " +
            "ORDER BY dCreatedx ASC")
    LiveData<List<Payslip>> GetPaySlipList();

    @Query("SELECT COUNT(b.sTransNox) " +
            "FROM Notification_Info_Master a " +
            "LEFT JOIN Notification_Info_Recepient b ON a.sMesgIDxx = b.sTransNox " +
            "WHERE a.sMsgTypex = '00000' " +
            "AND b.cMesgStat == '2'" +
            "AND a.sAppSrcex = 'IntegSys' " +
            "AND a.sMsgTitle LIKE 'PAYSLIP%' " +
            "AND b.sRecpntID = (SELECT sUserIDxx FROM User_Info_Master)")
    LiveData<Integer> GetUnreadPayslipCount();

    @Query("SELECT * FROM Notification_Info_Master WHERE sMesgIDxx=:MesgID")
    ENotificationMaster CheckIfExist(String MesgID);

    class Payslip{
        public String sTransNox;
        public String sMesgIDxx;
        public String sMsgTitle;
        public String sMessagex;
        public String cMesgStat;
        public String dReceived;
    }
}
