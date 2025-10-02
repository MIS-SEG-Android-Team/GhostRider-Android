package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EArticleDetails;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;

import java.util.List;

@Dao
public interface DArticle {

    @Upsert
    void saveArticleHead(EArticleHead articleHead);

    @Upsert
    void saveArticleDetails(EArticleDetails articleDetails);

    @Query("SELECT * FROM Article_Head")
    LiveData<List<EArticleHead>> getArticleMenus();

    @Query("SELECT * FROM Article_Details WHERE sCodexx = :fsCodexx")
    LiveData<List<EArticleDetails>> getArticleDetails(String fsCodexx);
}
