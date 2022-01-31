package com.theappwelt.vhelp.utilities;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.theappwelt.vhelp.AppWidget;
import com.theappwelt.vhelp.R;
import com.theappwelt.vhelp.SplashScreenActivity;
import com.theappwelt.vhelp.floatingwidget.ServiceRestarterBroadcastReceiver;

import java.util.Random;

public class UpdateWidgetService extends Service {
    private static final String LOG = "de.vogella.android.widget.example";
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.i("TAG", "run: Service is still running");
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }
    @Override
    public void onStart(Intent intent, int startId) {

        Log.i("TAG", "onStart: Service started by user.");

//        Toast.makeText(context, "Service Activated", Toast.LENGTH_SHORT).show();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());
        SharedPref.putString(this.getApplicationContext(),"appMsg","1");
        int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        String msg = intent.getStringExtra("msg");

        for (int widgetId : allWidgetIds) {
            // create some random data
//            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(this
                    .getApplicationContext().getPackageName(),
                    R.layout.app_widget);
//            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
            remoteViews.setTextViewText(R.id.appwidget_text,
                    msg);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(),
                    AppWidget.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();

        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("TAG", "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Log.i("TAG", "onDestroy: Service stopped");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent broadcastIntent = new Intent(context, ServiceRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }
}
