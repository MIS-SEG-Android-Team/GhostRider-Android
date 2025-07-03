package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EItinerary;

import java.util.List;

@Dao
public interface DItinerary {

    @Insert
    void Save(EItinerary foVal);

    @Query("SELECT COUNT (*) FROM Employee_Itinerary")
    int GetRowsCountForID();

    @Query("UPDATE Employee_Itinerary SET cSendStat = 1, sTransNox =:TransNo1 WHERE sTransNox =:TransNox")
    void UpdateSentItinerary(String TransNox, String TransNo1);

    @Query("UPDATE Employee_Itinerary SET cSendStat = 2 WHERE sTransNox =:TransNox")
    void UpdateItineraryStatus(String TransNox);

    @Query("SELECT * FROM Employee_Itinerary WHERE sTransNox =:TransNox")
    EItinerary GetItineraryForUpload(String TransNox);

    @Query("SELECT * FROM Employee_Itinerary " +
            "WHERE sEmployID = (SELECT sEmployID FROM User_Info_Master) " +
            "AND strftime('%Y-%m-%d', datetime('now', 'localtime')) = dTransact " +
            "ORDER BY dTransact DESC, dTimeFrom DESC")
    LiveData<List<EItinerary>> GetItineraryListForCurrentDay();

    @Query("SELECT * FROM Employee_Itinerary " +
            "WHERE sEmployID = (SELECT sEmployID FROM User_Info_Master) " +
            "AND cSendStat == '0' " +
            "ORDER BY dTransact DESC")
    List<EItinerary> GetUnsentItinerary();

    @Query("SELECT * FROM Employee_Itinerary " +
            "WHERE sEmployID = (SELECT sEmployID FROM User_Info_Master) " +
            "AND dTransact >= strftime('%Y-%m-%d', :dDateFrom) AND dTransact <= strftime('%Y-%m-%d', :dDateThru) " +
            "ORDER BY dTransact DESC, dTimeFrom DESC")
    LiveData<List<EItinerary>> GetItineraryListForFilteredDate(String dDateFrom, String dDateThru);
}
