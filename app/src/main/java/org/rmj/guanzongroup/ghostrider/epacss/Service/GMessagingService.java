/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.Service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.GCircle.room.Repositories.AppTokenManager;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.lib.Notifications.model.NMM;
import org.rmj.g3appdriver.lib.Notifications.NOTIFICATION_STATUS;
import org.rmj.g3appdriver.lib.Notifications.model.iNotification;
import org.rmj.guanzongroup.ghostrider.notifications.Etc.NotificationUI;
import org.rmj.guanzongroup.ghostrider.notifications.Etc.iNotificationUI;

public class GMessagingService extends FirebaseMessagingService {
    private static final String TAG = GMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        AppTokenManager poToken = new AppTokenManager(getApplication());
        poToken.SaveFirebaseToken(s);
        AppConfigPreference.getInstance(GMessagingService.this).setAppToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "Message Received: " + message);

        iNotification loSys = new NMM(getApplication()).getInstance(message);

        String lsResult = loSys.Save(message);
        if(lsResult == null){
            Log.e(TAG, loSys.getMessage());
            return;
        }

        ENotificationMaster loMaster = loSys.SendResponse(lsResult, NOTIFICATION_STATUS.RECEIVED);

        if(loMaster == null){
            Log.e(TAG, loSys.getMessage());
            return;
        }

        iNotificationUI loUI = new NotificationUI(GMessagingService.this).getInstance(loMaster);
        loUI.CreateNotification();
    }
}
