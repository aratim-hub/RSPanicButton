package com.theappwelt.vhelp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theappwelt.vhelp.model.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPref {
    public static SharedPreferences prefs;

    private static Context context = null;

    public static void setApplicationContext(Context cxt) {
        if (context == null)
            context = cxt;
    }

    public static Context getApplicationContext()
    {
        return context;
    }


    public static void putBoolean(Context ctx, String key, boolean val) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, val).commit();

    }

    public static boolean getBoolean(Context ctx, String key, boolean val) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void putInt(Context ctx, String key, int score) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        prefs.edit().putInt(key, score).commit();

    }

    public static int getInt(Context ctx, String key) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);

        return prefs.getInt(key, 0);
    }

    public static void putString(Context ctx, String key, String score) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        prefs.edit().putString(key, score).commit();

    }

    public static String getString(Context ctx, String key) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
    public static void putString(Context ctx, String key) {
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        prefs.edit().putString(key,null).commit();

    }

    public static void saveList(Context ctx, String key, List<Contact> list){
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public static List<Contact> getList(Context ctx, String key){
        prefs = ctx.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<Contact>>() {}.getType();
        Log.i("TAG", "getList: "+json);
        return gson.fromJson(json, type);
    }

}
