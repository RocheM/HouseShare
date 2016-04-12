package itt.matthew.houseshare.Events;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import itt.matthew.houseshare.Activities.FBLogin;
import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Fragments.FBLoginFragment;
import itt.matthew.houseshare.R;

public class MyHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 0;


    @Override
    public void onRegistered(Context context,  final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                try {
                    NewCost.mClient.getPush().register(gcmRegistrationId);
                    Log.d("HOUSE SHARE", "REGISTER");
                    return null;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    @Override
    public void onReceive(Context context, Bundle bundle) {
        String msg = bundle.getString("message");

        Log.d("HOUSE SHARE", "NOTIFICATION");


        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, // requestCode
                new Intent(context, NewCost.class),
                0); // flags

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_add_24dp)
                .setContentTitle("Notification Hub Demo")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();



        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}