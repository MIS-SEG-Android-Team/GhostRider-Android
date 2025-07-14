package org.rmj.guanzongroup.ghostrider.notifications.Notifications.Regular;

import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.CHANNEL_DESC;
import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.CHANNEL_NAME;
import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.NotificationID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.guanzongroup.ghostrider.notifications.R;

import java.util.Date;

public class RegularSysNotification implements RglNotification{

    private final Context mContext;

    private final ENotificationMaster poMessage;
    private NotificationManager loManager;

    private static final int SYSTEM_SUMMARY_NOTIFICATION = 141;
    private static final String SYSTEM_GROUP_NOTIFICATION = "org.rmj.guanzongroup.ghostrider.sysgroupnotification";

    public RegularSysNotification(Context mContext, ENotificationMaster message) {
        this.mContext = mContext;
        this.poMessage = message;
    }

    @Override
    public void CreateNotification() {
        try{
            // this portion of code generates random channel id for notifications needed to show separately
            int lnChannelID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

//            int lnChannelID = 123;

            Intent loIntent = new Intent(mContext, Class.forName("org.rmj.guanzongroup.ghostrider.epacss.Activity.Activity_Main"));

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

            String lsTitlexx = poMessage.getMsgTitle();
            String lsMessage = poMessage.getMessagex();

            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(mContext, String.valueOf(lnChannelID))
                            .setContentIntent(notifyPendingIntent)
                            .setAutoCancel(true)
                            .setChannelId(NotificationID)
                            .setSmallIcon(R.drawable.ic_guanzon_circle)
                            .setContentTitle(lsTitlexx)
                            .setContentText(lsMessage);

            loManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            loManager.notify(lnChannelID, notification.build());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
