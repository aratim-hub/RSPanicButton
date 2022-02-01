package com.theappwelt.vhelp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.theappwelt.vhelp.model.Contact;
import com.theappwelt.vhelp.utilities.SharedPref;
import com.theappwelt.vhelp.utilities.UpdateWidgetService;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    public static final String ACTION_BUTTON_REFRESH = "ACTION_BUTTON_REFRESH";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.app_widget);

            Intent clickIntent = new Intent(context.getApplicationContext(), AppWidget.class)
                    .setAction(ACTION_BUTTON_REFRESH);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(ACTION_BUTTON_REFRESH)) {
//                Toast.makeText(context, "click event received", Toast.LENGTH_SHORT).show();
                ///<<<<<<
                AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context,
                        AppWidget.class);
                int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                for (int appWidgetId : allWidgetIds) {
                    if(SharedPref.getString(context,"isBusy").equals("free")){
                        new CountDownTimer(10000, 1000) { // adjust the milli seconds here

                            public void onTick(long millisUntilFinished) {
                               if (SharedPref.getString(context,"appMsg").equals("1")){
                                   cancel();

                                   SharedPref.putString(context.getApplicationContext(),"appMsg","0");
                                   SharedPref.putString(context.getApplicationContext(),"isBusy","free");
                                   RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.app_widget);

                                   // Set the text
                                   remoteViews.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.app_name));

                                   appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                               }else{
                                   SharedPref.putString(context.getApplicationContext(),"isBusy","busy");
                                   String rs = String.valueOf(millisUntilFinished / 1000);

                                   RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.app_widget);

                                   // Set the text
                                   remoteViews.setTextViewText(R.id.appwidget_text, rs);
                                   appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                               }

                            }

                            public void onFinish() {
                                SharedPref.putString(context.getApplicationContext(),"appMsg","0");
                                SharedPref.putString(context.getApplicationContext(),"isBusy","free");
                                RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.app_widget);

                                // Set the text
                                remoteViews.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.app_name));

                                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

                                List<Contact> contactArrayList = SharedPref.getList(context,"contactArrayList");
                                if (contactArrayList != null){
                                    if (contactArrayList.size() > 0){
                                        setFinishTask(context);
                                    }else{
                                        Toast.makeText(context, "Please Add Contact first", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Please Add Contact first", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }.start();
                    }else if (SharedPref.getString(context,"isBusy").equals("busy")){
                        SharedPref.putString(context,"appMsg","1");
                    }

                }
                ////>>>>>>>>>>>>>
            } else {
                super.onReceive(context, intent);
            }
        }
    }

    private void setFinishTask(Context context) {

        String latLong = getLocation(context);
//        String latLong = SharedPref.getString(context,"latitude")+","+SharedPref.getString(context,"longitude");
        Log.i("TAG", "location " + latLong);
        List<Contact> contactArrayList = SharedPref.getList(context,"contactArrayList");

        if (contactArrayList != null){
            if (contactArrayList.size() > 0){
                Log.d("Array List Size ", "" + contactArrayList.size());
                for (int i = 0; i < contactArrayList.size(); i++) {
                    if (contactArrayList.get(i).isSms()) {
                        String strMessage = "Specified by User.";
                        if (contactArrayList.get(i).getMessage().equals("DEFAULT"))
                            strMessage = "VHelp - Test: " + getUsername(context) + " need help at " + latLong;


                        String strMobileNo = contactArrayList.get(i).getMobile();
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(strMobileNo, null, strMessage, null, null);
                            Log.d("SMS sent ", strMobileNo + " " + strMessage);
                            Toast.makeText(context.getApplicationContext(), "Your Message Sent",Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(context.getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }else{
                Log.i("TAG", "contact list is empty");
            }
        }else{
            Log.i("TAG", "contact list is empty");
        }
    }

    private String getLocation(Context context) {
        String latitude = "0", longitude = "0";
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            LocationManager  locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableGPS(context);
            }
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.
                    GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.
                    NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.
                    PASSIVE_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = getRoundOffValue(lat);
                longitude = getRoundOffValue(longi);
                Log.d("TAG 1 ",""+getRoundOffValue(lat)+" "+getRoundOffValue(longi));
            } else if (locationNetwork != null) {
                double lat = locationNetwork.getLatitude();
                double longi = locationNetwork.getLongitude();
                latitude = getRoundOffValue(lat);
                longitude = getRoundOffValue(longi);
                Log.d("TAG 2 ",latitude+" "+longitude);
            } else if (locationPassive != null) {
                double lat = locationPassive.getLatitude();
                double longi = locationPassive.getLongitude();
                latitude = getRoundOffValue(lat);
                longitude = getRoundOffValue(longi);
                Log.d("TAG 3 ",latitude+" "+longitude);
            } else {
                Toast.makeText(context.getApplicationContext(), "Not Getting Location!!",
                        Toast.LENGTH_SHORT).show();
            }

            if (latitude.equals("0") && longitude.equals("0")){
                latitude = SharedPref.getString(context.getApplicationContext(),"latitude");
                longitude = SharedPref.getString(context.getApplicationContext(),"longitude");
            }else{
                SharedPref.putString(context.getApplicationContext(),"latitude",latitude);
                SharedPref.putString(context.getApplicationContext(),"longitude",longitude);
            }
        }
        String loc = latitude + ","+longitude;
        return loc;
    }

    private void enableGPS(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
        builder.setMessage("Activate GPS?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private String getRoundOffValue(Double latitude){
        String lat = "";
        DecimalFormat df = new DecimalFormat("##.######");
        df.setRoundingMode(RoundingMode.CEILING);
        lat = df.format(latitude);
        return lat;
    }

    private String getUsername(Context context){
        String name = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        name = sharedPreferences.getString("username", "username");
        return name;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}