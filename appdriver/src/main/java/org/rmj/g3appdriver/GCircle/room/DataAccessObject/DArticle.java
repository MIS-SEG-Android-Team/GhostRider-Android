package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EArticleDetails;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;

@Dao
public interface DArticle {

    @Upsert
    void saveArticleHead(EArticleHead articleHead);

    @Upsert
    void saveArticleDetails(EArticleDetails articleDetails);
}
