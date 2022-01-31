package com.theappwelt.vhelp.floatingwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.theappwelt.vhelp.AppWidget;
import com.theappwelt.vhelp.utilities.UpdateWidgetService;

/**
 * This is a Braodcast receiver service for Aegis Service.
 * Its helps to get the background serbice started again if it get killed.
 */
public class ServiceRestarterBroadcastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(ServiceRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops!");

        Toast.makeText(context, "broadcast activated", Toast.LENGTH_SHORT).show();

        AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        Intent intt = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        intt.putExtra("msg", "VHelp!!");
        context.startService(intent);

//        context.startService(new Intent(context, FloatingHbtn.class));

//        FloatingHbtn.getInstance().SetContext(context);
//        FloatingHbtn.getInstance().ServiceInitiate();
    }

}
