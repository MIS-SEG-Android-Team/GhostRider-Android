package org.rmj.guanzongroup.ghostrider.notifications.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.TaskStackBuilder;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.guanzongroup.ghostrider.notifications.Etc.iNotificationUI;
import org.rmj.guanzongroup.ghostrider.notifications.R;

import java.util.Date;

public class PromoNotification implements iNotificationUI {
    private static final String TAG = PromoNotification.class.getSimpleName();

    private final Context mContext;
    private final ENotificationMaster poMessage;
    private final NotificationManager loManager;

    public static final String NotificationID = "org.rmj.guanconnect.guanzonpromos";
    private static final String CHANNEL_NAME = "Guanzon Promotions";
    private static final String CHANNEL_DESC = "Notify Guanzon connect users for Guanzon mobitek, motorcycle, and other promotions";

    public PromoNotification(Context mContext, ENotificationMaster message) {
        this.mContext = mContext;
        this.poMessage = message;
        this.loManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void CreateNotification() {
        try{
            // this portion of code generates random channel id for notifications needed to show separately

            Intent loIntent = new Intent(mContext, Class.forName("org.rmj.guanzongroup.guanzonapp.Activity.Activity_SplashScreen"));

            String lsDataxx = poMessage.getDataSndx();
            JSONObject loJson = new JSONObject(lsDataxx);
            JSONObject loPromo = loJson.getJSONObject("data");
            String lsUrlLinkx = loPromo.getString("sPromoUrl");
            String lsImageUrl = loPromo.getString("sImageUrl");

            loIntent.putExtra("notification", "promo");
            loIntent.putExtra("args", "1");
            loIntent.putExtra("url_link", lsUrlLinkx);

            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addNextIntentWithParentStack(loIntent);
            // Get the PendingIntent containing the entire back stack

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
                        mContext, 0, loIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            } else {
                notifyPendingIntent = PendingIntent.getActivity(
                        mContext, 0, loIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            String lsTitlexx = poMessage.getMsgTitle();
            String lsMessage = poMessage.getMessagex();

            Notification.Builder loBuilder = new Notification.Builder(mContext)
                    .setContentIntent(notifyPendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_guanzon_logo)
                    .setContentTitle(lsTitlexx)
                    .setContentText(lsMessage);

            int lnChannelID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            loManager.notify(lnChannelID, loBuilder.build());

//            Glide.with(mContext)
//                    .asBitmap()
//                    .load(lsImageUrl)
//                    .into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap icon, @Nullable Transition<? super Bitmap> transition) {
//                            NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, String.valueOf(lnChannelID))
//                                    .setContentIntent(notifyPendingIntent)
//                                    .setPriority(Notification.PRIORITY_HIGH)
//                                    .setAutoCancel(true)
//                                    .setChannelId(NotificationID)
//                                    .setLargeIcon(icon)
//                                    .setStyle(new NotificationCompat.BigPictureStyle()
//                                            .bigPicture(icon)
//                                            .bigLargeIcon(null))
//                                    .setSmallIcon(R.drawable.ic_guanzon_logo)
//                                    .setContentTitle(lsTitlexx)
//                                    .setContentText(lsMessage);
//
//                            loManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                            loManager.notify(lnChannelID, notification.build());
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                        }
//                    });

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
