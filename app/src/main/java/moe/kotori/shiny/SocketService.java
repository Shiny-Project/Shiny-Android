package moe.kotori.shiny;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import moe.kotori.shiny.data.EventProvider;
import moe.kotori.shiny.data.InfoEventListener;
import moe.kotori.shiny.data.MessageItem;
import moe.kotori.shiny.data.PushEventListener;

public class SocketService extends Service {
    private Intent mIntent = new Intent("moe.kotori.shiny.RECEIVER");
    private NotificationManager nm;
    private int notificationId = 1;
    private Context context;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        EventProvider eventProvider = new EventProvider(context);
        eventProvider.Start(new PushEventListener() {
            @Override
            public void onData(String json) {
                mIntent.putExtra("type", 2);
                mIntent.putExtra("info", json);
                sendBroadcast(mIntent);
                doNotification(json);
            }
        }, new InfoEventListener() {
            @Override
            public void onInfo(String msg) {
                mIntent.putExtra("type", 1);
                mIntent.putExtra("info", msg);
                sendBroadcast(mIntent);
            }
        });

        return START_STICKY;
    }

    public void doNotification(String json){
        MessageItem msgItem = new MessageItem();
        msgItem.parseFromSocket(json);

        if(msgItem.getLevel() < 3){
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.noti_icon)
                .setContentText(msgItem.getContent())
                .setContentTitle(msgItem.getTitle())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pIntent);

        nm.notify(notificationId, mBuilder.build());
        notificationId++;
    }
}
