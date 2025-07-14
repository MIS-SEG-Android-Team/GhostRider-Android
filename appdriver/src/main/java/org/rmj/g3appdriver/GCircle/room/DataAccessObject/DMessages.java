package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DMessages {

    @Query("SELECT a.*, " +
            "(SELECT sMessagex FROM Notification_Info_Master WHERE sCreatrID = a.sUserIDxx " +
            "AND sMsgTypex == '00000' " +
            "AND sCreatrID <> 'SYSTEM' ORDER BY dCreatedx DESC LIMIT 1) AS sMessagex, " +
            "(SELECT dReceived FROM Notification_Info_Recepient WHERE sTransNox = " +
            "(SELECT sMesgIDxx FROM Notification_Info_Master WHERE sCreatrID = a.sUserIDxx ORDER BY dCreatedx DESC LIMIT 1) ORDER BY dReceived DESC LIMIT 1) AS dReceived " +
            "FROM Notification_User a")
    LiveData<List<MessageUsers>> GetMessageUsers();

    @Query("SELECT sUserName FROM Notification_User WHERE sUserIDxx =:args")
    LiveData<String> GetSenderName(String args);

    @Query("SELECT " +
            "a.sMessagex," +
            "b.dReceived " +
            "FROM Notification_Info_Master a " +
            "LEFT JOIN Notification_Info_Recepient b " +
            "ON a.sMesgIDxx = b.sTransNox " +
            "WHERE a.sCreatrID =:args " +
            "ORDER BY b.dReceived DESC")
    LiveData<List<UserMessages>> GetUserMessages(String args);

    /**
     *
     * @param args pass the user id of the sender
     * @return returns message id of unread messages or unsent read response
     */
    @Query("SELECT a.sMesgIDxx " +
            "FROM Notification_Info_Master a " +
            "LEFT JOIN Notification_Info_Recepient b " +
            "ON a.sMesgIDxx = b.sTransNox " +
            "WHERE a.sCreatrID =:args " +
            "AND b.cMesgStat == '2' " +
            "OR a.sCreatrID =:args " +
            "AND b.cMesgStat == '3' " +
            "AND b.cStatSent == 0")
    List<String> GetUnreadMessagesID(String args);

    @Query("SELECT COUNT(a.sMesgIDxx) " +
            "FROM Notification_Info_Master a " +
            "LEFT JOIN Notification_Info_Recepient b " +
            "ON a.sMesgIDxx = b.sTransNox " +
            "WHERE b.sRecpntID = (SELECT sUserIDxx FROM User_Info_Master) " +
            "AND a.sMsgTypex == '00000' " +
            "AND a.sMsgTitle NOT LIKE '%PAYSLIP%'" +
            "AND a.sCreatrID <> 'SYSTEM' " +
            "AND b.cMesgStat == '2'")
    LiveData<Integer> GetUnreadMessagesCount();

    class MessageUsers {
        public String sUserIDxx;
        public String sUserName;
        public String sMessagex;
        public String dReceived;
    }

    class UserMessages{
        public String sMessagex;
        public String dReceived;
    }
}
