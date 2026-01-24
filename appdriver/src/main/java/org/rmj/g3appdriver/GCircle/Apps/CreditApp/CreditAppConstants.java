/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditApp
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.GCircle.Apps.CreditApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import org.rmj.g3appdriver.R;

import java.util.HashMap;


public class CreditAppConstants {

    /**
     *            <p>0 new Fragment_PersonalInfo(),</p>
     *            <p>1 new Fragment_ResidenceInfo(),</p>
     *            <p>2 new Fragment_MeansInfoSelection(),</p>
     *            <p>3 new Fragment_EmploymentInfo(),</p>
     *            <p>4 new Fragment_SelfEmployedInfo(),</p>
     *            <p>5 new Fragment_Finance(),</p>
     *            <p>6 new Fragment_PensionInfo(),</p>
     *            <p>7 new Fragment_SpouseInfo(),</p>
     *            <p>8 new Fragment_SpouseResidenceInfo(),</p>
     *            <p>9 new Fragment_SpouseEmploymentInfo(),</p>
     *            <p>10 new Fragment_SpouseSelfEmployedInfo(),</p>
     *            <p>11 new Fragment_SpousePensionInfo(),</p>
     *            <p>12 new Fragment_DisbursementInfo(),</p>
     *            <p>13 new Fragment_Dependent(),</p>
     *            <p>14 new Fragment_Properties(),</p>
     *            <p>15 new Fragment_OtherInfo(),</p>
     *            <p>16 new Fragment_CoMaker()</p>
     *            <p>17 new Fragment_CoMakerResidence()</p>
     * @param allLoanUnitNames
     */

    public static String[] APPLICATION_TYPE = {
            "Motorcycle",
            "Sidecar",
            "Others",
            "Mobile Phone",
            "Cars",
            "Services"};

    public static String[] CUSTOMER_TYPE = {
            "New",
            "Repeat"};

    public static String[] INSTALLMENT_TERM = {
            "36 Months/3 Years",
            "24 Months/2 Years",
            "18 Months",
            "12 Months/1 Year",
            "6 Months"
    };

    public static HashMap<String, String> TERMS_BY_CODE = new HashMap<>(){
        {
            put("36 Months/3 Years", "36");
            put("24 Months/2 Years", "24");
            put("18 Months", "18");
            put("12 Months/1 Year", "12");
            put("6 Months", "6");
        }
    };

    public static String[] MOBILE_NO_TYPE = {"Prepaid", "Postpaid"};

    public static String[] CIVIL_STATUS = {
            "Single",
            "Married",
            "Separated",
            "Widowed",
            "Single-Parent",
            "Single-Parent W/ Live-in Partner"};

    public static String[] HOUSEHOLDS ={
            "Living with Family(Spouse, Children)",
            "Living with Family(Father, Mother, Sibling)",
            "Living with Relatives"};

    public static String[] HOUSE_TYPE = {
            "Concrete",
            "Concrete and Wood",
            "Wood"};

    public static String[] LENGTH_OF_STAY = {"Month", "Year"};

    public static String[] BUSINESS_NATURE = {
            "BPO/Call Center",
            "Business Services",
            "Communication/Transportation",
            "Construction",
            "Financing Banking",
            "Health and Wellness",
            "Hotel/Resort",
            "Insurance",
            "Manufacturing",
            "Mining/Agriculture",
            "Pharmaceutical",
            "Real Estate",
            "Restaurants/Bars/Caffe",
            "Social Services",
            "utilities",
            "WholeSale/Retail",
            "Others"};

    public static String[] GOVERMENT_LEVEL = {
//            "Select Government Level (Required)",
            "LGU",
            "Provincial",
            "National"};

    public static String[] COMPANY_LEVEL = {
            "Local",
            "National",
            "Multi-National"};

    public static String[] EMPLOYEE_LEVEL = {
            "Rank and file",
            "Supervisory",
            "Managerial"};

    public static String[] OFW_CATEGORY = {
//            "Select Work Category (Required)",
            "Household Services",
            "Non-Technical",
            "Skilled/Professional"};

    public static String[] OFW_REGION = {
//            "Select Region (Required)",
            "America",
            "Europe",
            "Oceania",
            "Asia",
            "Others"
    };

    public static String[] YEARS_OF_EXPERIENCE = {
            "Select Yeas of Experience (Required)",
            "Below 1 Year",
            "1 Year",
            "2 Years",
            "3 Years",
            "4 Years",
            "5 Years",
            "6 Years",
            "7 Years",
            "8 Years",
            "9 Years",
            "10 Years",
            "More than 10 Years"};

    public static String[] SCHOOL_LEVEL = {
//            "Select School Level (Required)",
            "Primary",
            "High School",
            "Technical/Vocational",
            "College",
            "College Graduate"};

    public static String[] SCHOOL_TYPE = {
//            "Select School Type (Required)",
            "Public",
            "Private"};

    public static String[] EMPLOYMENT_STATUS = {
            "Regular",
            "Probationary",
            "Contractual",
            "Seasonal"};

    public static String[] BUSINESS_SIZE = {
            "Micro 1 (Less than 10,000 Income/Month)",
            "Micro 2 (Less than 50,000 Income/Month)",
            "Micro 3 (Less than 100,000 Income/Month)",
            "Small (Less than 300,000 Income/Month)",
            "Medium (Less than 1,000,000 Income/Month)",
            "Large (More than 1,000,000 Income/Month)"};

    public static String[] BUSINESS_TYPE = {
            "Sole",
            "Partnership",
            "Corporation"};

    public static String[] PENSION_SECTOR = {
            "Government",
            "Private"};

    public static String[] ACCOUNT_TYPE = {
            "Checkings",
            "Savings",
            "Payroll"};

    public static String[] EMPLOYMENT_SECTOR = {
//            "Select Employment Type",
            "Private",
            "Government",
            "OFW"};
    public static String[] STUDENT_YES_NO = {
            "Select student status",
            "Yes",
            "No"};
    public static String[] EMPLOYED_YES_NO = {
            "Select employment status",
            "Yes",
            "No"};


    public static String[] DEPENDENT_RELATIONSHIP = {
//            "Select Relationship",
            "Child",
            "Parents",
            "Siblings",
            "Relatives"};
//            "Others"};

    public static String[] UNIT_USER = {
            "Principal Customer",
            "Others"};

    public static String[] UNIT_USER_OTHERS = {
            "Children",
            "Parents",
            "Siblings",
            "Relatives",
            "Others",
            "Spouse"};

    public static String[] UNIT_USER_OTHERS_NO_SPOUSE = {
            "Children",
            "Parents",
            "Siblings",
            "Relatives",
            "Others"};

    public static String[] UNIT_PURPOSE = {
            "Business",
            "Personal Service",
            "Raffle",
            "Gift"};

    public static String[] UNIT_PAYER = {
            "Children",
            "Parents",
            "Siblings",
            "Relatives",
            "Spouse"};

    public static  String[] UNIT_PAYER_NO_SPOUSE = {
            "Children",
            "Parents",
            "Siblings",
            "Relatives"};

    public static String[] INTO_US = {
            "Facebook",
            "Branch",
            "Friends",
            "Relatives",
            "Walk-In",
            "Others"};

    public static String[] INTO_US_NO_SPOUSE= {
            "Facebook",
            "Branch",
            "Friends",
            "Relatives",
            "Walk-In",
            "Others"};

    public static String[] CO_MAKER_RELATIONSHIP= {
            "Children",
            "Parents",
            "Siblings",
            "Relatives",
            "Other",
            "Spouse"};

    public static String[] CO_MAKER_INCOME_SOURCE= {
            "Employed",
            "Self-Employed",
            "With-Financer",
            "Pensioner"};

    public static String[] FINANCE_SOURCE = {
            "Children",
            "Parents",
            "Siblings",
            "Relatives",
            "Others"};

    public static String[] APPLICATION_FILTER = {
            "User Applications",
            "Branch Applications"
    };
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