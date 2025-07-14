package org.rmj.guanzongroup.ghostrider.notifications.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.guanzongroup.ghostrider.notifications.Etc.iNotificationUI;
import org.rmj.guanzongroup.ghostrider.notifications.R;

import java.util.Date;

public class TableUpdateNotification implements iNotificationUI {
    private static final String TAG = TableUpdateNotification.class.getSimpleName();

    private final Context mContext;
    private final ENotificationMaster poMessage;
    private final NotificationManager loManager;

    public static final String NotificationID = "org.rmj.guanconnect.table_update";
    private static final String CHANNEL_NAME = "Data Configuration";
    private static final String CHANNEL_DESC = "Guanzon Circle app reconfigure notice";

    public TableUpdateNotification(Context mContext, ENotificationMaster message) {
        this.mContext = mContext;
        this.poMessage = message;
        this.loManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void CreateNotification() {
        try{
            Intent loIntent = new Intent();

            String lsDataxx = poMessage.getDataSndx();
            JSONObject loJson = new JSONObject(lsDataxx);
            JSONObject loData = loJson.getJSONObject("data");

            String lsModule = loJson.getString("module");

            String lsTitlexx = "";
            String lsMessage = "";

            switch (lsModule){
                case "00001":
                    lsTitlexx = "System Configuration";
                    lsMessage = "GRider Circle configuration has been updated.";
                    break;
                case "00002":
                    lsTitlexx = "Branch Opening Monitor";
                    lsMessage = poMessage.getMessagex();
                    break;
                default:
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(NotificationID, CHANNEL_NAME, importance);
                channel.setDescription(CHANNEL_DESC);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            PendingIntent notifyPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notifyPendingIntent = PendingIntent.getActivity(
                        mContext, 0, loIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                notifyPendingIntent = PendingIntent.getActivity(
                        mContext, 0, loIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Notification.Builder loBuilder = new Notification.Builder(mContext)
                    .setContentIntent(notifyPendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_guanzon_logo)
                    .setContentTitle(lsTitlexx)
                    .setContentText(lsMessage);

            int lnChannelID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            loManager.notify(lnChannelID, loBuilder.build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
