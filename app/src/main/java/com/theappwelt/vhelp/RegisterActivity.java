package com.theappwelt.vhelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.theappwelt.vhelp.utilities.NW_URL;
import com.theappwelt.vhelp.utilities.ServiceHandler;
import com.theappwelt.vhelp.utilities.TransparentProgressDialog;
import com.theappwelt.vhelp.utilities.Utils;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextMobile)
    EditText editTextMobile;
    @BindView(R.id.layoutName)
    TextInputLayout layoutName;
    @BindView(R.id.layoutMobile)
    TextInputLayout layoutMobile;
    Spinner spinnerOccupation;
    //UserViewModel userViewModel;
    //List<User> arrayListUser = new ArrayList<>();

    private String IMEI_CODE = "";
    private TransparentProgressDialog progressDialog;
    private String jsonStr, responseSuccess, responseMsg;
    private JSONObject jsonData;

    private static FirebaseUser currentUser;
   // DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initview();

       // userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        spinnerOccupation = (Spinner)findViewById(R.id.spinnerOccupation);
        //observerSetup();

//        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        GetIMEI();
    }

    private void initview()
    {
        progressDialog = new TransparentProgressDialog(this);
        progressDialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
    }
    @OnClick(R.id.buttonRegister)
    protected void register() {
        String name = editTextName.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String selectedOccupation = String.valueOf(spinnerOccupation.getSelectedItemPosition()); //spinnerOccupation.getSelectedItem().toString();


        if (name.isEmpty()) {
            layoutName.setError("Enter name");
        }else{
            layoutName.setError("");
        }

        if (mobile.isEmpty()) {
            layoutMobile.setError("Enter mobile number");
        } else if (mobile.length() < 10) {
            layoutMobile.setError("Enter 10 digit mobile number");
        }

        if (selectedOccupation.equals("0"))
            layoutMobile.setError("Please select your work profile.");
        else {
            //observerSetup();

//            if (arrayListUser.size() > 0) {
//                for (int i = 0; i < arrayListUser.size(); i++) {
//                    String existingMobile = arrayListUser.get(i).getMobile();
//                    if (existingMobile.equals(mobile)) {
//                        Toast.makeText(this, "Existing User", Toast.LENGTH_SHORT).show();
//                        saveToLocal();
//                        gotoMainActivity();
//                    } else {

                        //userViewModel.insertProduct(new User(mobile, name, selectedOccupation));
                        //saveToDatabase(mobile,name,selectedOccupation);
                        //saveToLocal(name);

                        new RegisterUser().execute(mobile, name, selectedOccupation);

                        //gotoOtpActivity();
//                    }
//                }
//            } else {
//                userViewModel.insertProduct(new User(mobile,name,selectedOccupation));
//                saveToDatabase(mobile,name,selectedOccupation);
//                saveToLocal(name);
//                Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show();
//                gotoOtpActivity();
//            }
        }

    }

//    private void saveToLocal(String name){
//        SharedPreferences sharedPreferences = PreferenceManager.
//                getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("username", name);
//        editor.putString("status", "Logged In");
//        editor.apply();
//        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void saveToDatabase(String mobile,String username,String occupation){
//        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = mDatabase.push().getKey();
//        User user = new User(mobile,username,occupation);
//        mDatabase.child(userId).setValue(user);
//    }

//    private void observerSetup() {
//        userViewModel.getAllProducts().observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(@Nullable final List<User> products) {
//                arrayListUser.clear();
//                arrayListUser.addAll(products);
//            }
//        });
//    }

    private void gotoOtpActivity(){
        Intent intent = new Intent(RegisterActivity.this,
                VerifyPhoneActivity.class);
        intent.putExtra("mobileno", editTextMobile.getText().toString());
        startActivity(intent);
        finish();
    }

//    private void gotoRegisterActivity(){
//        Intent intent = new Intent(RegisterActivity.this,
//                RegisterActivity.class);
//        startActivity(intent);
//        finish();
//    }

//    private void gotoMainActivity(){
//        Intent intent = new Intent(RegisterActivity.this,
//                MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private class RegisterUser extends AsyncTask<String, Void, String> {
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
                ServiceHandler shh = new ServiceHandler(RegisterActivity.this);
                String mobileno = args[0];
                String name = args[1];
                String selectedOccupation = args[2];

                imeiCode = args[1];

                RequestBody values = new FormBody.Builder()
                        .add("user_FirstName", name)
                        .add("user_ContactNo", mobileno)
                        .add("user_Occupation", selectedOccupation)
                        .add("user_LastName", "-")
                        .add("user_Email", "-")
                        .add("user_imei", imeiCode)
                        .build();

                jsonStr = shh.makeServiceCall(NW_URL.RegisterUser(), ServiceHandler.POST, values);

                Log.d("Response: ", "> " + jsonStr);


            } catch (final Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showDialog(RegisterActivity.this, e.toString(), false, false);
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
                        //Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show();



                        //gotoOtpActivity();
                        new RequestOTP().execute(editTextMobile.getText().toString(), IMEI_CODE);

                        progressDialog.dismiss();
                    }
                    else
                    {
                        responseMsg = jsonData.getString("message_text");
                        progressDialog.dismiss();
                        Utils.showDialog(RegisterActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                ServiceHandler shh = new ServiceHandler(RegisterActivity.this);
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
                        Utils.showDialog(RegisterActivity.this, e.toString(), false, false);
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
                        Utils.showDialog(RegisterActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



}
