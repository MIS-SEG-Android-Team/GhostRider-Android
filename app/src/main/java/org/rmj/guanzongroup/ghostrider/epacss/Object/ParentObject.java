/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 9/2/21, 2:20 PM
 * project file last modified : 9/2/21, 2:20 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.Object;

import android.content.Context;
import android.content.Intent;

import org.rmj.guanzongroup.ganado.Activities.Activity_CategorySelection;
import org.rmj.guanzongroup.ganado.Activities.Activity_ProductInquiry;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_Browser;
import org.rmj.guanzongroup.ghostrider.dataChecker.Activity.Activity_DB_Explorer;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.pacitareward.Activity.Activity_BranchList;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_DigitalGcard;

public class ParentObject {

    private final String psObjName;
    private final String pcParentx;

    public ParentObject(String psObjName, String pcParentx) {
        this.psObjName = psObjName;
        this.pcParentx = pcParentx;
    }

    public String getMenuName(){
        return psObjName;
    }

    public int getMenuIcon(){
        int lnIcon = 0;
        switch (psObjName.toLowerCase()){
            case "samsung knox":
                lnIcon = R.drawable.ic_menu_knox;
                break;
            case "credit application":
                lnIcon = R.drawable.ic_menu_creditapp;
                break;
            case "daily collection plan":
                lnIcon = R.drawable.ic_menu_dcp;
                break;
            case "pet manager":
                lnIcon = R.drawable.ic_approval_biometric;
                break;
            case "health checklist":
                lnIcon = R.drawable.ic_checklist;
                break;
            case "ah monitoring":
                lnIcon = R.drawable.ic_menu_monitoring;
                break;
            case "ci evaluation":
                lnIcon = R.drawable.ic_menu_credit_investigate;
                break;
            case "approval code":
                lnIcon = R.drawable.ic_menu_approval;
                break;
            case "gcard info":
                lnIcon = R.drawable.ic_scan_qr_code;
                break;
            case "db explorer":
                lnIcon = R.drawable.ic_settings_localdata;
                break;
            case "employee itinerary":
                lnIcon = R.drawable.ic_baseline_checklist_24;
                break;
            case "pacita reward":
                lnIcon = R.drawable.ic_rating_24;
                break;
            case "product inquiry":
                lnIcon = R.drawable.ic_product_inquiry;
                break;
        }
        return lnIcon;
    }

    public boolean isParent(){
        return pcParentx.equalsIgnoreCase("1");
    }

    public Intent getIntent(Context context){
        Intent loIntent;
        switch (psObjName.toLowerCase()){
            case "health checklist":
                loIntent = new Intent(context, Activity_Browser.class);
                loIntent.putExtra("url_link", "https://www.google.com/webhp?hl=ceb&sa=X&ved=0ahUKEwj0ne24tI7xAhV8yIsBHbQ7Az0QPAgI");
                loIntent.putExtra("syscode", "2");
                break;

            case "gcard info":
                loIntent = new Intent(context, Activity_DigitalGcard.class);
                break;

            case "db explorer":
                loIntent = new Intent(context, Activity_DB_Explorer.class);
                break;

            case "branch evaluation":
                loIntent = new Intent(context, Activity_BranchList.class);
                break;

            default:
                loIntent = null;
        }
        return loIntent;
    }
}
