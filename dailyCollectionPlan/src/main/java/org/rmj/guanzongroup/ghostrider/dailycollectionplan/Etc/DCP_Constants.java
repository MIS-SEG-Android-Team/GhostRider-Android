/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class DCP_Constants {


    /*
    * PAY - Paid
    * PTP - Promise to Pay
    * CNA - Customer Not Around
    * FLA - For Legal Action
    * Car - Carnap
    * UNC - Uncooperative
    * MCs - Missing Customer
    * MUn - Missing Unit
    * MCU - Missing Client and Unit
    * LUn - Loan Unit
    * TA - Transferred/Assumed
    * FO - False Ownership
    * DNP - Did Not Pay
    * NV - Not Visited
    * OTH - Others
    */
//FILE CODE
    public static Bitmap selectedImageBitmap;
    public static String FOLDER_NAME = "DCP";
    public static String TRANSACT_PAY = "PAY";
    public static String TRANSACT_PTP = "PTP";
    public static String TRANSACT_FLA = "FLA";
    public static String TRANSACT_Car = "Car";
    public static String TRANSACT_UNC = "UNC";
    public static String TRANSACT_MCs = "MCs";
    public static String TRANSACT_MUn = "MUn";
    public static String TRANSACT_LUn = "LUn";
    public static String TRANSACT_TA = "TA";
    public static String TRANSACT_FO = "FO";
    public static String TRANSACT_DNP = "DNP";
    public static String TRANSACT_NV = "NV";
    public static String TRANSACT_OTH = "OTH";
    public static double varLatitude;
    public static double varLongitude;
    public static boolean saveStorage=false;
    public static int collectionPos;

    public static String EXPORT_FOLDER = "/GRider Exports";

    public static String[] TRANSACTION_TYPE= {
            "Paid",
            "Promise to Pay",
            "Customer Not Around",
            "For Legal Action",
            "Car nap",
            "Uncooperative",
            "Missing Customer",
            "Missing Unit",
            "Missing Client and Unit",
            "Loan Unit",
            "Transferred/Assumed",
            "False Ownership",
            "Did not Pay",
            "Not Visited"};

    public static String[] PAYMENT_TYPE = {
            "Select Payment Type",
            "Monthly Payment",
            "Cash Balance",
            "Penalty Payment",
            "Registration",
            "Insurance",
            "Change Class",
            "Deed Of Sale",
            "Release",
            "Miscellaneous"};

    public static String GetPaymentTypeIndex(String PaymentType){
        for(int x = 0; x < PAYMENT_TYPE.length; x++){
            if(PaymentType.equalsIgnoreCase(PAYMENT_TYPE[x])){
                return String.valueOf(x);
            }
        }
        return "";
    }

    public static String[] CIVIL_STATUS = {
            "Single",
            "Married",
            "Separated",
            "Widowed"};

    public static String[] REQUEST_CODE = {
            "New",
            "Update",
            "Change"
    };


    public static String getRemarksDescription(String fsCode){
        if(fsCode.equalsIgnoreCase("PAY")){
            return "Paid";
        } else if(fsCode.equalsIgnoreCase("PTP")){
            return "Promise to Pay";
        } else if(fsCode.equalsIgnoreCase("CNA")){
            return "Customer Not Around";
        } else if(fsCode.equalsIgnoreCase("FLA")){
            return "For Legal Action";
        } else if(fsCode.equalsIgnoreCase("Car")){
            return "Car nap";
        } else if(fsCode.equalsIgnoreCase("UNC")){
            return "Uncooperative";
        } else if(fsCode.equalsIgnoreCase("MCs")){
            return "Missing Customer";
        } else if(fsCode.equalsIgnoreCase("MUn")){
            return "Missing Unit";
        } else if(fsCode.equalsIgnoreCase("MCU")){
            return "Missing Client and Unit";
        } else if(fsCode.equalsIgnoreCase("LUn")){
            return "Loan Unit";
        } else if(fsCode.equalsIgnoreCase("TA")){
            return "Transferred/Assumed";
        } else if(fsCode.equalsIgnoreCase("FO")){
            return "False Ownership";
        } else if(fsCode.equalsIgnoreCase("DNP")){
            return "Did Not Pay";
        } else if(fsCode.equalsIgnoreCase("NV")){
            return "Not Visited";
        } else {
            return "OTH";
        }
    }

    public static String getRemarksCode(String Description){
        if(Description.equalsIgnoreCase("Paid")){
            return "PAY";
        } else if(Description.equalsIgnoreCase("Promise to Pay")){
            return "PTP";
        } else if(Description.equalsIgnoreCase("Customer Not Around")){
            return "CNA";
        } else if(Description.equalsIgnoreCase("For Legal Action")){
            return "FLA";
        } else if(Description.equalsIgnoreCase("Car nap")){
            return "Car";
        } else if(Description.equalsIgnoreCase("Uncooperative")){
            return "UNC";
        } else if(Description.equalsIgnoreCase("Missing Customer")){
            return "MCs";
        } else if(Description.equalsIgnoreCase("Missing Unit")){
            return "MUn";
        } else if(Description.equalsIgnoreCase("Missing Client and Unit")){
            return "MCU";
        } else if(Description.equalsIgnoreCase("Loan Unit")){
            return "LUn";
        } else if(Description.equalsIgnoreCase("Transferred/Assumed")){
            return "TA";
        } else if(Description.equalsIgnoreCase("False Ownership")){
            return "FO";
        } else if(Description.equalsIgnoreCase("Did Not Pay")){
            return "DNP";
        } else if(Description.equalsIgnoreCase("Not Visited")){
            return "NV";
        } else {
            return "OTH";
        }
    }
    public static ArrayAdapter<String> getAdapter(Context mContext, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, data){
            @SuppressLint("ResourceAsColor")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                //change the color to which ever you want
//                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
//                    ((CheckedTextView) view).setTextColor(Color.WHITE);
//                }else{
//                    ((CheckedTextView) view).setTextColor(getContext().getResources().getColor(R.color.material_black));
//                }
                return view;
            }
        };
        return adapter;
    }
}



