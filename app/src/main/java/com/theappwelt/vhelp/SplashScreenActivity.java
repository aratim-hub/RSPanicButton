package com.theappwelt.vhelp;

import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPref.putString(getApplicationContext(),"appMsg","0");
        SharedPref.putString(getApplicationContext(),"isBusy","free");

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String status = sharedPreferences.getString("status", "Logged Out");
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
