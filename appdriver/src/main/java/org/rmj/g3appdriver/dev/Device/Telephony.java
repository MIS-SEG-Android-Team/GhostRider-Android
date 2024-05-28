/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.dev.Device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Telephony {
    private final Context mContext;
    public Telephony(Context context) {
        this.mContext = context;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceID() {
        return Settings.Secure.getString(
                mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    @SuppressLint("MissingPermission")
    public String getMobilNumbers() {
        try {
            List<SubscriptionInfo> subInfoList;
            ArrayList<String> Numbers = new ArrayList<>();
            SubscriptionManager mSubscriptionManager = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1 && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                mSubscriptionManager = SubscriptionManager.from(mContext);

                subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
                if (subInfoList.size() > 0) {
                    for (SubscriptionInfo subscriptionInfo : subInfoList) {
                        Numbers.add(subscriptionInfo.getNumber());
                    }

                    if(Numbers.get(0) != null){
                        String lsResult = "";
                        String MobileNo = Numbers.get(0);
                        if (!MobileNo.isEmpty()) {
                            if (MobileNo.substring(0, 3).equalsIgnoreCase("+63")) {
                                lsResult = MobileNo.replace("+63", "0");
                            } else if (MobileNo.substring(0, 1).equalsIgnoreCase("9")) {
                                lsResult = "0" + MobileNo;
                            } else if (MobileNo.substring(0, 2).equalsIgnoreCase("63")) {
                                lsResult = MobileNo.replace("63", "0");
                            } else if (MobileNo.substring(0, 2).equalsIgnoreCase("09")) {
                                lsResult = MobileNo;
                            } else {
                                MobileNo = MobileNo.replace("-", "");
                                MobileNo = MobileNo.replace("+", "");
                                String lsTarget = MobileNo.substring(0, 2);
                                lsResult = MobileNo.replace(lsTarget, "09");
                            }
                        }
                        return lsResult;
                    } else if(Numbers.get(1) != null){
                        String lsResult = "";
                        String MobileNo = Numbers.get(1);
                        if (!MobileNo.isEmpty()) {
                            if (MobileNo.substring(0, 3).equalsIgnoreCase("+63")) {
                                lsResult = MobileNo.replace("+63", "0");
                            } else if (MobileNo.substring(0, 1).equalsIgnoreCase("9")) {
                                lsResult = "0" + MobileNo;
                            } else if (MobileNo.substring(0, 2).equalsIgnoreCase("63")) {
                                lsResult = MobileNo.replace("63", "0");
                            } else if (MobileNo.substring(0, 2).equalsIgnoreCase("09")) {
                                lsResult = MobileNo;
                            } else {
                                //this method is use for emulator testing...
                                MobileNo = MobileNo.replace("-", "");
                                MobileNo = MobileNo.replace("+", "");
                                String lsTarget = MobileNo.substring(0, 2);
                                lsResult = MobileNo.replace(lsTarget, "09");
                            }
                        }
                        return lsResult;
                    }
                }
            }
            else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mSubscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                return mSubscriptionManager.getPhoneNumber(mSubscriptionManager.DEFAULT_SUBSCRIPTION_ID);
            }else {
                TelephonyManager poManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                return poManager.getLine1Number();
            }
            return "";
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String getFormattedMobileNo(String MobileNo){
        String result = "";
        if (!MobileNo.isEmpty()) {
            if (MobileNo.length() == 10){
                result = "0" + MobileNo;
            }else if(MobileNo.length() >= 11 && MobileNo.length() <= 13){
                if (MobileNo.substring(0, 3).equalsIgnoreCase("+63")) {
                    result = MobileNo.replace("+63", "0");
                } else if (MobileNo.substring(0, 2).equalsIgnoreCase("63")) {
                    result = MobileNo.replace("63", "0");
                } else if (MobileNo.substring(0, 2).equalsIgnoreCase("09")) {
                    result = MobileNo;
                }
            }else {
                result = "";
            }
        }else {
            result = "";
        }
        return result;
    }

}
