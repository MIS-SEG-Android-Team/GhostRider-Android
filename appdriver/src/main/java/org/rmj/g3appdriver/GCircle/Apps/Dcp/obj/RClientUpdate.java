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

package org.rmj.g3appdriver.GCircle.Apps.Dcp.obj;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.LoanUnit;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DClientUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RClientUpdate {
    private static final String TAG = RClientUpdate.class.getSimpleName();

    private final DClientUpdate poDao;

    private String message;

    public RClientUpdate(Application application){
        this.poDao = GGC_GCircleDB.getInstance(application).ClientUpdateDao();
    }

    public String getMessage() {
        return message;
    }

    public EClientUpdate getClientUpdateInfoForPosting(String TransNox, String AcctNox){
        return poDao.getClientUpdateInfoForPosting(TransNox, AcctNox);
    }

    public String SaveClientUpdate(LoanUnit foVal){
        try {
            EClientUpdate loClient = new EClientUpdate();
            String lsTransNo = CreateUniqueID();
            loClient.setSourceNo(lsTransNo);
            loClient.setDtlSrcNo(foVal.getAccntNox());
            loClient.setFrstName(foVal.getFrstName());
            loClient.setLastName(foVal.getLastName());
            loClient.setMiddName(foVal.getMiddName());
            loClient.setSuffixNm(foVal.getSuffixxx());
            loClient.setAddressx(foVal.getStreetxx());
            loClient.setBirthDte(foVal.getBirthDte());
            loClient.setBirthPlc(foVal.getBirthPlc());
            loClient.setEmailAdd(foVal.getEmailAdd());
            loClient.setHouseNox(foVal.getHouseNox());
            loClient.setImageNme(foVal.getFileName());
            loClient.setLandline(foVal.getPhoneNox());
            loClient.setMobileNo(foVal.getMobileNo());
            loClient.setTownIDxx(foVal.getTownIDxx());
            loClient.setBarangay(foVal.getBrgyIDxx());
            loClient.setGenderxx(foVal.getGenderxx());
            loClient.setCivlStat(foVal.getCvilStat());
            loClient.setModified(AppConstants.DATE_MODIFIED());
            loClient.setSendStat("0");
            loClient.setSourceCd("DCPa");
            poDao.SaveClientUpdate(loClient);

            Log.d(TAG, "Client update request details has been save.");
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    private String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }
}
