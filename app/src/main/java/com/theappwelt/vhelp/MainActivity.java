package com.theappwelt.vhelp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bsk.floatingbubblelib.FloatingBubbleTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.theappwelt.vhelp.floatingwidget.FloatingHbtn;
import com.theappwelt.vhelp.model.Contact;
import com.theappwelt.vhelp.model.MainViewModel;
import com.theappwelt.vhelp.model.User;
import com.theappwelt.vhelp.model.UserViewModel;
import com.theappwelt.vhelp.utilities.NW_URL;
import com.theappwelt.vhelp.utilities.ServiceHandler;
import com.theappwelt.vhelp.utilities.SharedPref;
import com.theappwelt.vhelp.utilities.TransparentProgressDialog;
import com.theappwelt.vhelp.utilities.Utils;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    String latitude, longitude;
    private static final String TAG = "MainActivity";

    private final int ACTIVITY_REQUEST_CODE = 1;
    ProductListAdapter productListAdapter;
    private RecyclerView coursesRV;

    private MainViewModel mViewModel;
    UserViewModel userViewModel;
    ArrayList<Contact> contactArrayList = new ArrayList<>();
    //Button buttonAdd;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 7;
    private static final int REQUEST_PHONE_CALL = 10;

    SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static final int SMS_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    FloatingBubbleTouchListener bubbleTouchListener;

    private String jsonStr, responseSuccess, responseMsg;
    private JSONObject jsonData;
    private TransparentProgressDialog progressDialog;

    private FloatingActionButton buttonFloatingAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

//        //<<<<<<<<<
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            AppWidgetManager mAppWidgetManager = getSystemService(AppWidgetManager.class);
//
//            ComponentName myProvider = new ComponentName(MainActivity.this, AppWidget.class);
//
//            Bundle b = new Bundle();
//            b.putString("ggg", "ggg");
//            if (mAppWidgetManager.isRequestPinAppWidgetSupported()) {
//                Intent pinnedWidgetCallbackIntent = new Intent(MainActivity.this, AppWidget.class);
//                PendingIntent successCallback = PendingIntent.getBroadcast(MainActivity.this, 0,
//                        pinnedWidgetCallbackIntent, 0);
//
//                mAppWidgetManager.requestPinAppWidget(myProvider, b, successCallback);
//            }
//        }
//        //////>>>>>>>>>

        initializeView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},
//                    REQUEST_PHONE_CALL);
//        }

        checkAddContactsPermission();

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        productListAdapter = new ProductListAdapter(R.layout.item_contact);
        coursesRV = findViewById(R.id.contactList);
        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);
        coursesRV.setAdapter(productListAdapter);


//        for (int i = 0; i < temp.getValue().size(); i++) {
//            if (contactArrayList.get(i).getMobile().equals(temp.getValue().get(i).getMobile())) {
//                //Toast.makeText(MainActivity.this, "Contact already added in list.", Toast.LENGTH_SHORT).show();
//                Contact model = new Contact();
//                model.setCall(false);
//                model.setSms(true);
//                model.setName(temp.getValue().get(i).getName());
//                model.setMobile(temp.getValue().get(i).getMobile());
//                model.setRelationship(temp.getValue().get(i).getRelationship());
//                contactArrayList.add(model);
//            }
//        }

        observerSetup();
        productListAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact model, int position) {
                PreferenceselectionDialog(model, false);
            }
        });

        // initialize add contact button
        buttonFloatingAdd = findViewById(R.id.buttonFloatingAdd);
        buttonFloatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
        } else {
            // Pre-Marshmallow
        }

        mViewModel.getAllProducts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> products) {
                productListAdapter.setProductList(products);
                contactArrayList.clear();
                contactArrayList.addAll(products);
                //enableDisableSOS(contactArrayList);
                SOS();
                Log.i(TAG, "onCreate: "+products.size());
                SharedPref.saveList(getApplicationContext(),"contactArrayList",products);
            }
        });



        observerSetup();

        //LiveData<List<Contact>> temp = mViewModel.getAllProducts();

        //enableDisableSOS(contactArrayList);

//        if (FloatingHbtn.getInstance() == null)
//        {
//            this.startService(new Intent(this, FloatingHbtn.class));
//        }
//        FloatingHbtn.getInstance().SetContext(this);
//        FloatingHbtn.getInstance().ServiceInitiate();

    }

    private void observerSetup() {
        if (contactArrayList.size()>0)
        {
            if (FloatingHbtn.getInstance() == null)
            {
                FloatingHbtn.getInstance().startService(new Intent(this, FloatingHbtn.class));
            }
            FloatingHbtn.getInstance().SetContext(this);
            FloatingHbtn.getInstance().ServiceInitiate();
            FloatingHbtn.getInstance().setContactList(contactArrayList);
        }
    }

    protected void SOS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPS();
        }
        getLocation();
        observerSetup();
//        Log.d("Array List Size ", "" + contactArrayList.size());
        //for (int i = 0; i < contactArrayList.size(); i++) {
        //    if (contactArrayList.get(i).isSms()) {
        //        sendMessage(contactArrayList.get(i).getMobile(), "URGENCY, " + contactArrayList.get(i).name + " need help at " + latitude + " , " + longitude);
        //    }
            //if (contactArrayList.get(i).isCall()) {
                //doCall(contactArrayList.get(i).getMobile());
           // }
        //}
        //activateSOS();
    }

    private void enableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activate GPS?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
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
                Toast.makeText(this, "Not Getting Location!!",
                        Toast.LENGTH_SHORT).show();
            }

            SharedPref.putString(getApplicationContext(),"latitude",latitude);
            SharedPref.putString(getApplicationContext(),"longitude",longitude);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (ACTIVITY_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {

                    // successfully selected contact to add
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        // add item to storage and update contact list
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contact = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Contact model = new Contact();
                        model.setCall(false);
                        model.setSms(true);
                        model.setName(name);
                        model.setMobile(contact);
                        model.setRelationship("None");
//                        PreferenceselectionDialog(model, true);
  //                      setDefaultPreference(name, contact, model);

                        new InsertContact().execute(name, contact, "None", "false", "true", "DEFAULT");
                    }
                }
                break;
            case (CODE_DRAW_OVER_OTHER_APP_PERMISSION):{
                if (reqCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION){
                    if (resultCode == RESULT_OK){
                        initializeView();
                    } else {
                        Toast.makeText(this,"Draw Over other App Permission is Not Available. Closing the Application",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    super.onActivityResult(reqCode, resultCode, data);
                }
            }
        }
    }

    private void initializeView() {

        progressDialog = new TransparentProgressDialog(this);
        progressDialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        checkSmsPermission();
        //checkCallPhonePermission();

//        buttonSOS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SOS();
//                checkSmsPermission();
//                checkCallPhonePermission();
//
//                stopService(new Intent(MainActivity.this, FloatingHbtn.class));
//                Intent mIntent = new Intent(MainActivity.this, FloatingHbtn.class);
//                Bundle bundle = new Bundle();
//                mIntent.putParcelableArrayListExtra("key", contactArrayList);
//                mIntent.putExtra("KEY_LAT_LONG",latitude+" , "+longitude);
//                mIntent.putExtras(bundle);
//                startService(mIntent);
//                finish();
//            }
//        });
    }


    private void checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need SMS Permission");
                builder.setMessage("This app needs SMS permission to send Messages.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CONSTANT);
                        observerSetup();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            //else if (permissionStatus.getBoolean(Manifest.permission.SEND_SMS, false)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Need SMS Permission");
//                builder.setMessage("This app needs SMS permission to send Messages.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        sentToSettings = true;
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(),
//                                "Go to Permissions to Grant SMS permissions", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}
                        , SMS_PERMISSION_CONSTANT);
            }

//            SharedPreferences.Editor editor = permissionStatus.edit();
//            editor.putBoolean(Manifest.permission.SEND_SMS, true);
//            editor.commit();

        }
    }

    public void sendMessage(String strMobileNo, String strMessage) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strMobileNo, null, strMessage, null, null);
            Log.d("SMS sent ", strMobileNo + " " + strMessage);
            //Toast.makeText(getApplicationContext(), "Your Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

//    private void checkCallPhonePermission() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 7);
//        }
//    }

    private void checkAddContactsPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 10);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, 11);
        }
    }

//    private void doCall(String strMobileNo) {
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobileNo));
//        startActivity(intent);
//    }

    private void onDeleteContact(String name){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Delete?");
        builder1.setMessage("Do you want to delete this contact?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mViewModel.deleteProduct();
                        userViewModel.deleteProduct();
                        productListAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

//    private void gotoLoginActivity(){
//        mViewModel.deleteProduct();
//        userViewModel.deleteProduct();
//        Intent intent = new Intent(MainActivity.this,
//                LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private void setDefaultPreference(String name, String contact, Contact model){

        for (int i = 0; i < contactArrayList.size(); i++) {
            if (contactArrayList.get(i).getMobile().equals(model.getMobile())) {
                Toast.makeText(MainActivity.this, "Contact already added in list.", Toast.LENGTH_SHORT).show();
                model = contactArrayList.get(i);
                //IsNew = false;
                return;
            }
        }

//        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
//        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.alert_layout_preferences, null);
//        dialogBuilder.setView(dialogView);
//        android.app.AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.setCancelable(true);

        String[] country = {"None", "Mother", "Father", "Son", "Daughter", "Brother", "Sister", "Cousin","Friend","Husband", "Wife", "Fieance","Grandmother","Grandfather","Aunty","Uncle"};
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        Spinner spnRelationship = (Spinner) dialogView.findViewById(R.id.spinnerKeluhan);
        ArrayAdapter aa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRelationship.setAdapter(aa);

        CheckBox chkCall = (CheckBox) dialogView.findViewById(R.id.chkCall);
        chkCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        CheckBox chkSms = (CheckBox) dialogView.findViewById(R.id.chkSms);
        chkSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        chkSms.setChecked(true);

        chkCall.setVisibility(View.GONE);
        chkSms.setVisibility(View.GONE);

        TextView lblPreferences = (TextView) dialogView.findViewById(R.id.lblPreferences);
        lblPreferences.setVisibility(View.GONE);

        RelativeLayout deleteLayout = (RelativeLayout) dialogView.findViewById(R.id.deleteLayout);
        deleteLayout.setVisibility(View.GONE);

        TextInputEditText editTextName = (TextInputEditText) dialogView.findViewById(R.id.editTextName);
        editTextName.setText(name);

        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Message = "";
                String edname = editTextName.getText().toString();
                String selectedRelationship = spnRelationship.getSelectedItem().toString();
                boolean isSMs = chkSms.isChecked();
                boolean isCall = chkCall.isChecked();
                if (!isCall && !isSMs) {
                    Toast.makeText(MainActivity.this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
                } else {

                    lblPreferences.setText("Set Preferences for " + edname);
                    Message = "DEFAULT";
                    alertDialog.dismiss();

                    new InsertContact().execute(edname, contact, selectedRelationship, String.valueOf(isCall), String.valueOf(isSMs), Message);
                }
            }
        });
        alertDialog.show();

    }

    private void PreferenceselectionDialog(Contact model, boolean IsNew)
    {
//        for (int i = 0; i < contactArrayList.size(); i++) {
//            if (contactArrayList.get(i).getMobile().equals(model.getMobile())) {
//                Toast.makeText(MainActivity.this, "Contact already added in list.", Toast.LENGTH_SHORT).show();
//                model = contactArrayList.get(i);
//                IsNew = false;
//                return;
//            }
//        }

        final Contact innmodel = model;

        String[] country = {"None", "Mother", "Father", "Son", "Daughter", "Brother", "Sister", "Cousin","Friend","Husband","Wife","Fieance","Grandmother","Grandfather","Aunty","Uncle"};
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);

        Spinner spnRelationship = (Spinner) dialogView.findViewById(R.id.spinnerKeluhan);
        ArrayAdapter aa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRelationship.setAdapter(aa);

        CheckBox chkCall = (CheckBox) dialogView.findViewById(R.id.chkCall);
        //chkCall.setVisibility(View.GONE);
//        chkCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!innmodel.isCall()){
//                    for (int i = 0; i < contactArrayList.size(); i++) {
//                        if (contactArrayList.get(i).isCall()) {
//                            chkCall.setChecked(false);
//                            Toast.makeText(MainActivity.this, "Another contact is already selected for call. Only one contact is valid for call.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
//                }
//            }
//        });
        CheckBox chkSms = (CheckBox) dialogView.findViewById(R.id.chkSms);
        //chkSms.setVisibility(View.GONE);
        chkSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setText("Update To List");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedRelationship = spnRelationship.getSelectedItem().toString();
                boolean isSMs = chkSms.isChecked();
                boolean isCall = chkCall.isChecked();
                final String name = innmodel.getName();
                final String contact = innmodel.getMobile();
                final String message = innmodel.getMessage();
                //Log.d("name ", name);
                //Log.d("contact ", contact);

                if (!isCall && !isSMs) {
                    Toast.makeText(MainActivity.this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    mViewModel.updateProduct(new Contact(name, contact, selectedRelationship, isCall, isSMs, message));
                    observerSetup();
                }
            }
        });
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        RelativeLayout imgDelete = (RelativeLayout) dialogView.findViewById(R.id.deleteLayout);
        if (IsNew == true)
            imgDelete.setVisibility(View.GONE);
        else {
            imgDelete.setVisibility(View.VISIBLE);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String contact = innmodel.getMobile();
                    onDeleteContact(contact);
                    alertDialog.dismiss();
                }
            });
        }


        TextInputEditText editTextName = (TextInputEditText) dialogView.findViewById(R.id.editTextName);
        editTextName.setText(model.getName());

        chkCall.setChecked(model.isCall());
        chkSms.setChecked(model.isSms());
        spnRelationship.setSelection(aa.getPosition(model.getRelationship()));
        alertDialog.show();
    }


    private class InsertContact extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog.show();
            //spinProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            try {
                ServiceHandler shh = new ServiceHandler(MainActivity.this);
                String mobile = args[1];
                String name = args[0];
                String relationship = args[2];
                String strCall = args[3];
                String strSMS = args[4];
                String message = args[5];

                if (strSMS.equals("true"))
                    strSMS = "1";
                else
                    strSMS = "0";

                if (strCall.equals("true"))
                    strCall = "1";
                else
                    strCall = "0";

                mobile = mobile.replace(" ", "");
                mobile = mobile.replace("+91", "");

                SharedPreferences sharedPreferences = PreferenceManager.
                        getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String user_id = sharedPreferences.getString("userid", "");

                RequestBody values = new FormBody.Builder()
                        .add("User_Id", user_id)
                        .add("Contact_Number", mobile)
                        .add("Contact_Name", name)
                        .add("Contact_SMS_Message", message)
                        .add("Contact_CALL", strCall)
                        .add("Contact_SMS", strSMS)
                        .add("Contact_Relation", relationship)
                        .build();

                jsonStr = shh.makeServiceCall(NW_URL.InsertContact(), ServiceHandler.POST, values);

                Log.d("Response: ", "> " + jsonStr);


            } catch (final Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showDialog(MainActivity.this, e.toString(), false, false);
                    }
                });
                // workerThread();
                Log.e("ServiceHandler", e.toString());
            }


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            // TODO Auto-generated method stub
            super.onPostExecute(jsonStr);
            try {
                if (jsonStr != null) {
                    jsonData = new JSONObject(jsonStr);
                    responseSuccess = String.valueOf(jsonData.getInt("message_code"));
                    Log.d("isSuccess", "" + responseSuccess);

                    if (responseSuccess.equals("1000")) {
                        responseMsg = jsonData.getString("message_text");
                        JSONObject obj = jsonData.getJSONObject("message_data");

                        String mobile = obj.getString("Mobile");
                        String name = obj.getString("Name");
                        String Message = obj.getString("Message");
                        String strisCall = obj.getString("isCall");
                        String strisSMS = obj.getString("isSMS");
                        String relationship = obj.getString("Relationship");

                        boolean isCall = false;
                        boolean isSMS = true;

                        if (strisCall.equals("1"))
                            isCall = true;
                        if (strisSMS.equals("0"))
                            isSMS = false;

                        Contact contact1 = new Contact(name, mobile, relationship, isCall, isSMS, Message);
                        mViewModel.insertProduct(contact1);

                        progressDialog.dismiss();
                    }
                    else
                    {
                        responseMsg = jsonData.getString("message_text");
                        progressDialog.dismiss();
                        Utils.showDialog(MainActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
