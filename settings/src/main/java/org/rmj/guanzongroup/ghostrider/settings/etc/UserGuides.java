package org.rmj.guanzongroup.ghostrider.settings.etc;

import android.app.Application;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGuides;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

public class UserGuides {

    private DGuides poDao;

    public UserGuides(Application context){
        this.poDao = GGC_GCircleDB.getInstance(context).userguideDao();
    }
}
