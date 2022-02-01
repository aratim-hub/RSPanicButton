package com.theappwelt.vhelp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.theappwelt.vhelp.utilities.SharedPref;

public class SplashScreenActivity extends AppCompatActivity {

    private int loadingTime= 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //<<<<<<<<<
        SharedPref.putString(getApplicationContext(),"appMsg","0");
        SharedPref.putString(getApplicationContext(),"isBusy","free");

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String status = sharedPreferences.getString("status", "Logged Out");
        if (!status.equals("Logged In")) {
            loadingTime= 3000;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppWidgetManager mAppWidgetManager = getSystemService(AppWidgetManager.class);

                ComponentName myProvider = new ComponentName(SplashScreenActivity.this, AppWidget.class);

                Bundle b = new Bundle();
                b.putString("ggg", "ggg");
                if (mAppWidgetManager.isRequestPinAppWidgetSupported()) {

                    Intent pinnedWidgetCallbackIntent = new Intent(SplashScreenActivity.this, AppWidget.class);
                    pinnedWidgetCallbackIntent.setAction("ACTION_REFRESH");

                    PendingIntent successCallback = PendingIntent.getBroadcast(SplashScreenActivity.this, 0,
                            pinnedWidgetCallbackIntent, 0);

                    mAppWidgetManager.requestPinAppWidget(myProvider, b, successCallback);
                }
            }

        }
        //>>>>>>>>>>>

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (status.equals("Logged In")) {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, loadingTime);
    }
}
