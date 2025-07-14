package org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo;

import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.CHANNEL_DESC;
import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.CHANNEL_NAME;
import static org.rmj.guanzongroup.ghostrider.notifications.Notifications.Panalo.PanaloNotification.NotificationID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.guanzongroup.ghostrider.notifications.R;

public class RewardNotification implements PnlNotification{
    private static final String TAG = RewardNotification.class.getSimpleName();

    private final Context mContext;
    private final ENotificationMaster poMessage;
    private NotificationManager loManager;

    public RewardNotification(Context mContext, ENotificationMaster message) {
        this.mContext = mContext;
        this.poMessage = message;
    }

    @Override
    public void CreateNotification() {
        try{
            // this portion of code generates random channel id for notifications needed to show separately
//            int lnChannelID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            int lnChannelID = 123;

            Intent loIntent = new Intent(mContext, Class.forName("org.rmj.guanzongroup.ghostrider.epacss.Activity.Activity_Main"));

            String lsDataxx = poMessage.getDataSndx();
            JSONObject loJson = new JSONObject(lsDataxx);
            JSONObject loPromo = loJson.getJSONObject("data");
            String lsUrlLinkx = loPromo.getString("sReferNox");
//            String lsImageUrl = loPromo.getString("sImageUrl");

            loIntent.putExtra("notification", "promo");
            loIntent.putExtra("args", "1");
            loIntent.putExtra("url_link", lsUrlLinkx);

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

            //Retrieve a resource drawable and convert it to bitmap.
            //retrieving a resource drawable in panalo notification must be base on
            //which type of panalo notification must be displayed
            //Panalo Notifications
            // 1. WIN
            // 2. CLAIM
            // 3. REDEEMED
            // 4. WARNING

            String lsPanaloTp = loJson.getString("panalo");

            Bitmap icon;

//            switch (lsPanaloTp){
//                case "reward":
//                    icon = BitmapFactory.decodeResource(mContext.getResources(),
//                            R.drawable.sample_listing_product);
//                    break;
//                case "claim":
//                    icon = BitmapFactory.decodeResource(mContext.getResources(),
//                            R.drawable.sample_listing_product);
//                    break;
//                case "redeemed":
//                    icon = BitmapFactory.decodeResource(mContext.getResources(),
//                            R.drawable.sample_listing_product);
//                    break;
//                default:
//                    icon = BitmapFactory.decodeResource(mContext.getResources(),
//                            R.drawable.sample_listing_product);
//                    break;
//            }

            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(mContext, String.valueOf(lnChannelID))
                            .setContentIntent(notifyPendingIntent)
                            .setAutoCancel(true)
                            .setChannelId(NotificationID)
//                        .setLargeIcon(icon)
//                        .setStyle(new NotificationCompat.BigPictureStyle()
//                            .bigPicture(icon)
//                            .bigLargeIcon(null))
                            .setSmallIcon(R.drawable.ic_guanzon_circle)
                            .setContentTitle(lsTitlexx)
                            .setContentText(lsMessage)
                            .setColor(Color.rgb(255, 209, 26));

            loManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            loManager.notify(lnChannelID, notification.build());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
