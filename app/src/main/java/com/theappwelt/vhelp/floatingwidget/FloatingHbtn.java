package com.theappwelt.vhelp.floatingwidget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.theappwelt.vhelp.AppWidget;
import com.theappwelt.vhelp.MainActivity;
import com.theappwelt.vhelp.R;
import com.theappwelt.vhelp.model.Contact;
import com.theappwelt.vhelp.utilities.SharedPref;
import com.theappwelt.vhelp.utilities.UpdateWidgetService;
import com.theappwelt.vhelp.utilities.Utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FloatingHbtn extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    TextView tvWidget;
    float height,weight;
    int i=0;
    private CountDownTimer countDownTimer;
    private long timeLeft = 10000;
    private int Counter = 10;
    private static final int REQUEST_PHONE_CALL = 10;

    private static final String TAG = FloatingHbtn.class.getSimpleName();
    private Context context;
    //private static FloatingHbtn floatingHbtn = null;
    private static FloatingHbtn floatingHbtnInstance = null;
    private final static Object ServiceLock = new Object();

    ArrayList<Contact> contactArrayList=new ArrayList<>();
    String latLong = "";


    /**
     * Task Executor: Every 2 min check mConnection == null || mConnection.isConnected == false
     * If any of this is true the socket is closed and we use SocketStart to start the Connection thread.
     */
    ScheduledExecutorService scheduleTaskExecutor;

    public static FloatingHbtn getInstance() {

        synchronized (ServiceLock) {
            if (floatingHbtnInstance == null) { //if there is no instance available... create new one
                floatingHbtnInstance = new FloatingHbtn();
            }

            return floatingHbtnInstance;
        }
    }
    /**
     * Service Constructor set the BaseContext to service context
     */
    public FloatingHbtn() {
        this.context = getBaseContext();
    }

    public void SetContext(Context context)
    {
        this.context = context;
    }


    /**
     * Initiate the service and socket thread for conneciton.
     */
    public void ServiceInitiate() {

//        LoadFloatingButton();
    }

    public void setContactList(ArrayList<Contact> cArrayList)
    {
        contactArrayList   = cArrayList;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void LoadFloatingButton()
    {

        AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(context,
                AppWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);


        Log.w("LOG", "isBusy : "+ SharedPref.getString(context,"isBusy"));
        Log.w("LOG", "appMsg : "+SharedPref.getString(context,"appMsg"));

        if (SharedPref.getString(context,"isBusy").equals("busy")){

        }else if (SharedPref.getString(context,"appMsg").equals("1")){
            // There may be multiple widgets active, so update all of them
//            for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
                Log.w("LOG", "onUpdate method called");


                new CountDownTimer(10000, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {
                        SharedPref.putString(context.getApplicationContext(),"isBusy","busy");
                        String rs = String.valueOf(millisUntilFinished / 1000);

                        // Build the intent to call the service
                        Intent intent = new Intent(context.getApplicationContext(),
                                UpdateWidgetService.class);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
                        intent.putExtra("msg", rs);

                        // Update the widgets via the service
                        context.startService(intent);
                    }

                    public void onFinish() {
                        SharedPref.putString(context.getApplicationContext(),"isBusy","free");
                        // Build the intent to call the service
                        Intent intent = new Intent(context.getApplicationContext(),
                                UpdateWidgetService.class);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
                        intent.putExtra("msg", "V Help");
                        // Update the widgets via the service
                        context.startService(intent);


                        List<Contact> contactArrayList = SharedPref.getList(context,"contactArrayList");
                        if (contactArrayList != null){
                            if (contactArrayList.size() > 0){
                                setFinishTast(context);
                            }else{
                                Toast.makeText(context, "Please Add Contact first", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, "Please Add Contact first", Toast.LENGTH_SHORT).show();
                        }


                    }
                }.start();
//
//            }
        }else if(SharedPref.getString(context,"isBusy").equals("free")){
            // Build the intent to call the service
            Intent intent = new Intent(context.getApplicationContext(),
                    UpdateWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            intent.putExtra("msg", "V Help!");
            // Update the widgets via the service
            context.startService(intent);
        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//
//        mFloatingView = LayoutInflater.from(context).inflate(R.layout.floating_help,null);
//
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        layoutParams.gravity = Gravity.TOP|Gravity.RIGHT;
//        layoutParams.x = 0;
//        layoutParams.y = 100;
//
//        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140,
//                140,LAYOUT_FLAG,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
//        imageParams.y = 100;
//
//        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//        imageClose = new ImageView(context);
//        imageClose.setImageResource(R.drawable.ic_close);
//        imageClose.setVisibility(View.INVISIBLE);
//        windowManager.addView(imageClose,imageParams);
//        windowManager.addView(mFloatingView,layoutParams);
//        mFloatingView.setVisibility(View.VISIBLE);
//
//        height = windowManager.getDefaultDisplay().getHeight();
//        weight = windowManager.getDefaultDisplay().getWidth();
//
//        tvWidget = (TextView) mFloatingView.findViewById(R.id.text_widget);
//        tvWidget.setTextSize(20);
//
//        mFloatingView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if(i == 0)
//                {
//                    startTimer();
//                    i++;
//                }
//                else if (i == 1)
//                {
//                    stopTimer();
//                    i = 0;
//                }
//
//            }
//
//        });
//
//        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
//            int initialX,initialY;
//            float initialTouchX,initialTouchY;
//            long startClickTime;
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                switch (motionEvent.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//
//                        initialX = layoutParams.x;
//                        initialY = layoutParams.y;
//
//                        initialTouchX = motionEvent.getRawX();
//                        initialTouchY = motionEvent.getRawY();
//
//                        break;
//                    //return true;
//                    case MotionEvent.ACTION_UP:
//
//                        imageClose.setVisibility(View.GONE);
//                        layoutParams.x = initialX+(int)(initialTouchX-motionEvent.getRawX());
//                        layoutParams.y = initialY+(int)(motionEvent.getRawY()-initialTouchY);
//
////                        if (layoutParams.y>(height*0.6))
////                        {
////                            stopSelf();
////                        }
//
//                        break;
//                    //return true;
//                    case MotionEvent.ACTION_MOVE:
//
//                        layoutParams.x = initialX+(int)(initialTouchX-motionEvent.getRawX());
//                        layoutParams.y = initialY+(int)(motionEvent.getRawY()-initialTouchY);
//
//                       if (mFloatingView.isAttachedToWindow())
//                            windowManager.updateViewLayout(mFloatingView,layoutParams);
//
//                        if (layoutParams.y>(height*0.6)){
//                            imageClose.setImageResource(R.drawable.ic_close1);
//                        } else {
//                            imageClose.setImageResource(R.drawable.ic_close);
//                        }
//                        //return true;
//                        break;
//                }
//                return false;
//            }
//        });
//
//        SocketTrackerStart();
    }

    private void setFinishTast(Context context) {

        String latLong = SharedPref.getString(context,"latitude")+","+SharedPref.getString(context,"longitude");
        Log.i("TAG", "location " + latLong);
        List<Contact> contactArrayList = SharedPref.getList(context,"contactArrayList");

        if (contactArrayList != null){
            if (contactArrayList.size() > 0){
                Log.d("Array List Size ", "" + contactArrayList.size());
//                for (int i = 0; i < contactArrayList.size(); i++) {
//                    if (contactArrayList.get(i).isSms()) {
//                        String strMessage = "Specified by User.";
//                        if (contactArrayList.get(i).getMessage().equals("DEFAULT"))
//                            strMessage = "VHelp - Test: " + getUsername(context) + " need help at " + latLong;
//
//
//                        String strMobileNo = contactArrayList.get(i).getMobile();
////                sendMessage(contactArrayList.get(i).getMobile(), Message);
//                        try {
//                            SmsManager smsManager = SmsManager.getDefault();
//                            smsManager.sendTextMessage(strMobileNo, null, strMessage, null, null);
//                            Log.d("SMS sent ", strMobileNo + " " + strMessage);
//                            Toast.makeText(context.getApplicationContext(), "Your Message Sent",Toast.LENGTH_LONG).show();
//                        } catch (Exception ex) {
//                            Toast.makeText(context.getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }
            }else{
                Log.i("TAG", "contact list is empty");
            }
        }else{
            Log.i("TAG", "contact list is empty");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent myIntent, int flags, int startId) {

        if (myIntent !=null && myIntent.getExtras()!=null){
            contactArrayList = myIntent.getExtras().getParcelableArrayList("key");
            latLong = myIntent.getExtras().getString("KEY_LAT_LONG");
            //Toast.makeText(this, listFromActivity1.get(0).getName(), Toast.LENGTH_SHORT).show();
        }

        LoadFloatingButton();

        return START_STICKY;
    }

    /**
     *  onCreate for service override
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ServiceStart();
    }


    /**
     *  Used to start the service in foreground mode as per the OS version
     *  Push the notification if the SDK version is greater than 26
     */
    public void ServiceStart()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    /**
     * Scheduled task executor for service to check and start the socket thread
     * It check the connection object and  Socket state and then start socket or reset socket.
     * It does the check after every 2 mins and the task is fixed for this interval.
     */
    private void SocketTrackerStart()
    {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                synchronized (ServiceLock) {
                    LoadFloatingButton();
                }
            }
        }, 1, 2, TimeUnit.MINUTES);
    }

    /**
     * Service onTaskRemove for Service Override
     * Standard parameters to the start command
     * It start the broadcast intetnt to so that the app receive the SocketRestarterBroadcastRecevier.
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent broadcastIntent = new Intent(context, ServiceRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    /**
     * Service Notification for the SDK greater than 26
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "VHelp.service";
        String channelName = "V Help Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("V Help app service is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }

    /**
     * Not in Use
     * can be used to send the notificaiton on alert/error received
     * @param strMessage String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void NotificationShow(String strMessage)
    {
        try {
            String NOTIFICATION_CHANNEL_ID = "VHelp.service";
            String channelName = "V Help App Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle(strMessage)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(3, notification);

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }

    }


    public void startTimer(){
        updateTimer();
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                Counter--;
                updateTimer();
                tvWidget.setBackgroundResource(R.drawable.round_drawable_normal);
            }

            @Override
            public void onFinish() {

                new Runnable() {
                    @Override
                    public void run() {
                        tvWidget.setText("V Help"); //context.getResources().getString(R.string.app_name)
                        //tvWidget.setBackgroundResource(R.drawable.round_drawable_red);
                    }
                };

                Log.d("Array List Size ", "" + contactArrayList.size());
                for (int i = 0; i < contactArrayList.size(); i++) {
                    if (contactArrayList.get(i).isSms()) {
                        String Message = "Specified by User.";
                        if (contactArrayList.get(i).getMessage().equals("DEFAULT"))
                            Message = "VHelp - Test: " + getUsername() + " need help at " + latLong;

                        sendMessage(contactArrayList.get(i).getMobile(), Message);
                    }

//                    if (contactArrayList.get(i).isCall()) {
//                        doCall(contactArrayList.get(i).getMobile());
//                    }
                }
            }
        }.start();

    }


    private void doCall(String mobile) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobile));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        startActivity(callIntent);
    }


    public void sendMessage(String strMobileNo, String strMessage) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strMobileNo, null, strMessage, null, null);
            Log.d("SMS sent ", strMobileNo + " " + strMessage);
            //Toast.makeText(getApplicationContext(), "Your Message Sent",Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private String getUsername(){
        String name = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
           name = sharedPreferences.getString("username", "username");
        return name;
    }


    public void stopTimer(){
        countDownTimer.cancel();
        tvWidget.setText(context.getResources().getString(R.string.app_name));
        timeLeft = 10000;
        Counter = 10;
    }

    public void updateTimer(){
        int sec = Counter; //(int)(timeLeft % 60000 / 1000);

        final String tleft;
        tleft = "" + sec;
        if (tleft.equals("0")) {
            tvWidget.setText(context.getResources().getString(R.string.app_name));
            for (int i = 0; i < contactArrayList.size(); i++) {
                if (contactArrayList.get(i).isSms()) {
                    String Message = "Specified by User.";

                    getLocation();

                    if (contactArrayList.get(i).getMessage().equals("DEFAULT"))
                        Message = "VHelp - Test: URGENCY, " + getUsername() + " need help: " + latLong;

                    sendMessage(contactArrayList.get(i).getMobile(), Message);
                }

//                    if (contactArrayList.get(i).isCall()) {
//                        doCall(contactArrayList.get(i).getMobile());
//                    }
            }

        }
        else
            tvWidget.setText(tleft);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeIcon();
    }

     void removeIcon(){
        if (mFloatingView!=null){
            windowManager.removeView(mFloatingView);
        }

        if (imageClose!=null){
            windowManager.removeView(imageClose);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
           // ActivityCompat.requestPermissions(context, new String[]
             //       {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //enableGPS();
                latLong = " GPS Disabled.";
            }
            else
            {
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.
                        GPS_PROVIDER);
                Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.
                        NETWORK_PROVIDER);
                Location locationPassive = locationManager.getLastKnownLocation(LocationManager.
                        PASSIVE_PROVIDER);
                if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();
                    //latitude = getRoundOffValue(lat);
                    //longitude = getRoundOffValue(longi);
                    latLong = getRoundOffValue(lat) + ", " + getRoundOffValue(longi);
                    //Log.d("TAG 1 ",""+getRoundOffValue(lat)+" "+getRoundOffValue(longi));
                } else if (locationNetwork != null) {
                    double lat = locationNetwork.getLatitude();
                    double longi = locationNetwork.getLongitude();
                    //latitude = getRoundOffValue(lat);
                    //longitude = getRoundOffValue(longi);
                    //Log.d("TAG 2 ",latitude+" "+longitude);
                    latLong = getRoundOffValue(lat) + ", " + getRoundOffValue(longi);
                } else if (locationPassive != null) {
                    double lat = locationPassive.getLatitude();
                    double longi = locationPassive.getLongitude();
                    //latitude = getRoundOffValue(lat);
                    //longitude = getRoundOffValue(longi);
                    //Log.d("TAG 3 ",latitude+" "+longitude);
                    latLong = getRoundOffValue(lat) + ", " + getRoundOffValue(longi);
                } else {
                    Toast.makeText(this, "Not Getting Location!!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRoundOffValue(Double latitude){
        String lat = "";
        DecimalFormat df = new DecimalFormat("##.######");
        df.setRoundingMode(RoundingMode.CEILING);
        lat = df.format(latitude);
        //System.out.println(df.format(lat));

        return lat;
    }

}
