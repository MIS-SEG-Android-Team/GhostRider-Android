/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.approvalCode
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;

import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;

import java.util.List;

public class VMApprovalSelection extends AndroidViewModel {
    public static final String TAG = VMApprovalSelection.class.getSimpleName();
    private final ApprovalCode poSys;

    public VMApprovalSelection(@NonNull Application application) {
        super(application);
        this.poSys = new ApprovalCode(application);
    }

    public LiveData<List<ESCA_Request>> getReferenceAuthList(String Type){
        return poSys.getAuthorizedFeatures(Type);
    }

    public String getLatestStamp(){
        return poSys.getLatestStamp();
    }
}
