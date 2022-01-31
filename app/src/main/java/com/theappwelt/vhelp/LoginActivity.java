package com.theappwelt.vhelp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import com.theappwelt.vhelp.utilities.*;

public class LoginActivity extends AppCompatActivity {

    String mobile;
    @BindView(R.id.editTextMobileLogin) EditText editTextMobile;
    @BindView(R.id.layoutMobileLogin) TextInputLayout layoutMobile;

    private String IMEI_CODE = "";
    private String jsonStr, responseSuccess, responseMsg;
    private JSONObject jsonData;
    private TransparentProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        finish();
        //Get Firebase auth instance
        initView();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        String statusLogin = sharedPreferences.getString("status", "Logged Out");

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        GetIMEI();
        if (statusLogin.equals("Logged In")) {
            gotoMainActivity();
        }
    }

    private void initView()
    {
        progressDialog = new TransparentProgressDialog(this);
        progressDialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

    }

    @OnClick(R.id.buttonLogin)
    protected void login() {
        mobile = editTextMobile.getText().toString();

        if (mobile.isEmpty()) {
            layoutMobile.setError("Provide Mobile No.");
        } else if (mobile.length() < 10) {
            layoutMobile.setError("Enter valid 10 digit Mobile No.");
        } else {
           gotoAhead(mobile);
        }
    }

    @OnClick(R.id.textViewRegister)
    protected void register() {
        gotoRegisterActivity();
    }

    void gotoAhead(String mobile) {

        APILoginProcess();

// boolean isAvailable = false;
//                if (existingMobile.equals(mobile)) {
//                    isAvailable = true;
//                    //Toast.makeText(this, "Existing User", Toast.LENGTH_SHORT).show();
//                    //saveToLocal();
//                    doConclusion(isAvailable);
//                    return;
//                } else {
//                    isAvailable = false;
//                    //userViewModel.insertProduct(new User(mobile));
//                    //Toast.makeText(this, "New User!! Register here", Toast.LENGTH_SHORT).show();
//                    //gotoRegisterActivity();
//                }


//        } else {
//            //userViewModel.insertProduct(new User(mobile));
//            //Toast.makeText(this, "New User!! Register here", Toast.LENGTH_SHORT).show();
//            gotoRegisterActivity();
//        }
    }

    private void doConclusion(boolean isAvailable){

        if (isAvailable){
            saveToLocal();
        }else {
            gotoRegisterActivity();
        }
    }

    private void gotoOtpActivity(){
        Intent intent = new Intent(LoginActivity.this,
                VerifyPhoneActivity.class);
        intent.putExtra("mobileno", editTextMobile.getText().toString());
        startActivity(intent);
        finish();
    }

    private void gotoRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,
                RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveToLocal(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("status", "Logged In");
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            return;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    private void APILoginProcess() {
        if (Validation.isPhoneNumber(editTextMobile, true)) {
            if (Utils.isConnectedToInternet(LoginActivity.this)) {
                new RequestOTP().execute(editTextMobile.getText().toString(), IMEI_CODE);
            }
            else
                editTextMobile.setError("Please check your internet connection.");
        }
        else
            editTextMobile.setError("Phone number invalid.");
    }

    private void GetIMEI()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
            // use this checkSelfPermission method to check whether this permission is already granted or not
            int phoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
                // sms permission is not granted then invoke the user to allow this permission
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else if (phoneStatePermission == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                if (tm != null) {
                    IMEI_CODE = tm.getDeviceId();
                    if (IMEI_CODE == null) {
                        IMEI_CODE = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    }

                }

            }
        } else if (android.os.Build.VERSION.SDK_INT == 29) {
            int phoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
                // sms permission is not granted then invoke the user to allow this permission
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else if (phoneStatePermission == PackageManager.PERMISSION_GRANTED) {
                IMEI_CODE = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            }

    } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                try {
                    IMEI_CODE = tm.getDeviceId();
                }
                catch (Exception e)
                {
                    IMEI_CODE = "1234567890";
                }

            }
        }

    }

    private class RequestOTP extends AsyncTask<String, Void, String> {
        private String imeiCode;

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
                ServiceHandler shh = new ServiceHandler(LoginActivity.this);
                String mobileno = args[0];
                imeiCode = args[1];

                RequestBody values = new FormBody.Builder()
                        .add("user_ContactNo", mobileno)
                        .add("user_imei", imeiCode)
                        .build();

                jsonStr = shh.makeServiceCall(NW_URL.RequestOTP(), ServiceHandler.POST, values);

                Log.d("Response: ", "> " + jsonStr);


            } catch (final Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showDialog(LoginActivity.this, e.toString(), false, false);
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
                        gotoOtpActivity();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        responseMsg = jsonData.getString("message_text");
                        progressDialog.dismiss();
                        Utils.showDialog(LoginActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
