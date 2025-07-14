package org.rmj.g3appdriver.lib.Ganado.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.rmj.g3appdriver.GCircle.room.Entities.EMCColor;

import java.util.Collections;
import java.util.List;

public class GConstants {
    public static String[] CATEGORY = {
            "AUTO",
            "MOBILE PHONE",
            "MOTORCYCLE"};

    public static String[] MC_BRAND = {
            "HONDA",
            "KAWASAKI",
            "SUZUKI",
            "YAMAHA"};
    public static String getBrandID(String value){
        switch (value){
            case "0":
                return "M0W1001";
            case "1":
                return "M0W1009";
            case "2":
                return "M0W1002";
            case "3":
                return "M0W1003";
        }
        return "";
    }

    public static String[] INSTALLMENT_TERM = {
            "36 Months/3 Years",
            "24 Months/2 Years",
            "18 Months",
            "12 Months/1 Year",
            "6 Months"
    };
    public static String[] PAYMENT_FORM = {"Cash", "Installment"};

    public static String[] INQUIRY_STATUS = {
            "Pending",
            "Verified",
            "Unable to Verify",
            "Expired",
            "Bought",
            "Pending Inc. Release",
            "Incentive Released"};

    public static ArrayAdapter<String> getAdapter(Context mContext, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, data){
            @SuppressLint("ResourceAsColor")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                //change the color to which ever you want
                return view;
            }
        };
        return adapter;
    }

}
