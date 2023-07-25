/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 9/2/21, 2:53 PM
 * project file last modified : 9/2/21, 2:53 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.Object;

import android.content.Context;
import android.content.Intent;

import org.guanzongroup.com.creditevaluation.Activity.Activity_CIEvaluationList;
import org.guanzongroup.com.creditevaluation.Activity.Activity_EvaluationCIHistory;
import org.guanzongroup.com.itinerary.Activities.Activity_ItineraryEntry;
import org.guanzongroup.com.itinerary.Activities.Activity_ItineraryLog;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.guanzongroup.documentscanner.Activity_DocumentScan;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_AreaPerformanceMonitoring;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_CashCountLog;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_CashCounter;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_Inventory;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_ApprovalCode;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_ApprovalSelection;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_CollectionList;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_LogCollection;
import org.rmj.guanzongroup.ghostrider.samsungknox.Activity_Knox;
import org.rmj.guanzongroup.onlinecreditapplication.Activities.Activity_BranchApplications;
import org.rmj.guanzongroup.onlinecreditapplication.Activities.Activity_CreditApplications;
import org.rmj.guanzongroup.onlinecreditapplication.Activities.Activity_IntroductoryQuestion;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.Activity.Activity_EmployeeLoanEntry;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Employee_Applications;

public class ChildObject {

    private final String psObjName;

    public ChildObject(String psObjName) {
        this.psObjName = psObjName;
    }

    public String getChildMenuName() {
        return psObjName;
    }

    public Intent getIntent(Context context){
        Intent loIntent;
        String menu = psObjName.toLowerCase();
        switch (menu){
            case "collection list":
                loIntent = new Intent(context, Activity_CollectionList.class);
                loIntent.putExtra("syscode", "2");
                break;
            case "transaction log":
                loIntent = new Intent(context, Activity_LogCollection.class);
                loIntent.putExtra("syscode", "2");
                break;
            case "loan application":
                loIntent = new Intent(context, Activity_IntroductoryQuestion.class);
//                loIntent = new Intent(context, Activity_CreditAppHome.class);
                break;
            case "user application list":
                loIntent = new Intent(context, Activity_CreditApplications.class);
//                loIntent.putExtra("app", AppConstants.INTENT_OB_APPLICATION);
                break;

            case "branch application list":
                loIntent = new Intent(context, Activity_BranchApplications.class);
                break;
            case "document scan":
                loIntent = new Intent(context, Activity_DocumentScan.class);
                break;
//            case "":
//                loIntent = new Intent(context, DCPPhotoCapture.class);
//                break;

            case "ci evaluation list":
                loIntent = new Intent(context, Activity_CIEvaluationList.class);
                break;

            case "ci evaluation history":
                loIntent = new Intent(context, Activity_EvaluationCIHistory.class);
                loIntent.putExtra("cPreviewx", true);
                break;

            case "bh approval":
                loIntent = new Intent(context, Activity_EvaluationCIHistory.class);
                loIntent.putExtra("cPreviewx", false);
                break;

            case "leave application":
                loIntent = new Intent(context, Activity_Application.class);
                loIntent.putExtra("app", AppConstants.INTENT_LEAVE_APPLICATION);
                break;

            case "business trip":
                loIntent = new Intent(context, Activity_Application.class);
                loIntent.putExtra("app", AppConstants.INTENT_OB_APPLICATION);
                break;

            case "selfie log":
                loIntent = new Intent(context, Activity_Application.class);
                loIntent.putExtra("app", AppConstants.INTENT_SELFIE_LOGIN);
                break;

            case "application approval":
                loIntent = new Intent(context, Activity_Employee_Applications.class);
//                loIntent.putExtra("app", AppConstants.INTENT_APPLICATION_APPROVAL);
                break;

            case "application history":
                loIntent = new Intent(context, Activity_Employee_Applications.class);
                loIntent.putExtra("type", true);
                break;

            case "unlock":
                loIntent = new Intent(context, Activity_Knox.class);
                loIntent.putExtra("knox", 2);
                break;

            case "get pin":
                loIntent = new Intent(context, Activity_Knox.class);
                loIntent.putExtra("knox", 3);
                break;

            case "get offline pin":
                loIntent = new Intent(context, Activity_Knox.class);
                loIntent.putExtra("knox", 4);
                break;

            case "activate":
                loIntent = new Intent(context, Activity_Knox.class);
                loIntent.putExtra("knox", 1);
                break;

            case "check status":
                loIntent = new Intent(context, Activity_Knox.class);
                loIntent.putExtra("knox", 5);
                break;

            case "by reference":
                loIntent = new Intent(context, Activity_ApprovalSelection.class);
                loIntent.putExtra("sysCode", "1");
                break;

            case "by name":
                loIntent = new Intent(context, Activity_ApprovalSelection.class);
                loIntent.putExtra("sysCode", "2");
                break;

            case "manual log":
                loIntent = new Intent(context, Activity_ApprovalCode.class);
                loIntent.putExtra("sysCode", "2");
                break;

            case "random stock inventory":
                loIntent = new Intent(context, Activity_Inventory.class);
                break;
            case "branches performance info":
                loIntent = new Intent(context, Activity_AreaPerformanceMonitoring.class);
                break;
            case "cash count":
                loIntent = new Intent(context, Activity_CashCounter.class);
                break;
            case "cash count log":
                loIntent = new Intent(context, Activity_CashCountLog.class);
                break;
            case "itinerary logs":
                loIntent = new Intent(context, Activity_ItineraryLog.class);
                break;
            case "itinerary entry":
                loIntent = new Intent(context, Activity_ItineraryEntry.class);
                break;
            case "employee loan":
                loIntent = new Intent(context, Activity_EmployeeLoanEntry.class);
                break;
            default:
                loIntent = null;
        }
        return loIntent;
    }
}
