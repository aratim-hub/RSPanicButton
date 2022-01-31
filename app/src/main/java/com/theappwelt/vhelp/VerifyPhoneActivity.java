package com.theappwelt.vhelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theappwelt.vhelp.model.User;
import com.theappwelt.vhelp.model.UserViewModel;
import com.theappwelt.vhelp.utilities.NW_URL;
import com.theappwelt.vhelp.utilities.ServiceHandler;
import com.theappwelt.vhelp.utilities.TransparentProgressDialog;
import com.theappwelt.vhelp.utilities.Utils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class VerifyPhoneActivity extends AppCompatActivity {

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;
    private Button buttonVerifyOTP;
    private TextView lblMobileNo;
    private String strMobileNo, strOTP;

    private String jsonStr, responseSuccess, responseMsg;
    private JSONObject jsonData;
    private TransparentProgressDialog progressDialog;

    private EditText et_otp_var1, et_otp_var2, et_otp_var3, et_otp_var4;

    DatabaseReference mDatabase;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        //initializing objects
        lblMobileNo = findViewById(R.id.lblMobileNo);
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP);
        editTextCode = findViewById(R.id.editTextName);
        et_otp_var1 = findViewById(R.id.et_otp_var1);
        et_otp_var2 = findViewById(R.id.et_otp_var2);
        et_otp_var3 = findViewById(R.id.et_otp_var3);
        et_otp_var4 = findViewById(R.id.et_otp_var4);

        progressDialog = new TransparentProgressDialog(this);
        progressDialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        getIntentData();

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
//        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String code = et_otp_var1.getText().toString().trim();
//                if (code.isEmpty()) {
//                    Toast.makeText(VerifyPhoneActivity.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //verifying the code entered manually
//                //verifyVerificationCode(code);
//                Intent intent = new Intent(VerifyPhoneActivity.this,
//                        MainActivity.class);
//                //intent.putExtra("KEY_EMAIL",email);
//                //intent.putExtra("KEY_MOBILE",mobile);
//                SharedPreferences sharedPreferences = PreferenceManager.
//                        getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("status", "Logged In");
//                editor.apply();
//                startActivity(intent);
//                finish();
//            }
//        });

        //call text change listener on edit text
        et_otp_var1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    et_otp_var1.clearFocus();
                    et_otp_var2.requestFocus();
                    et_otp_var2.setCursorVisible(true);
                }
            }
        });
        et_otp_var2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    et_otp_var2.clearFocus();
                    et_otp_var3.requestFocus();
                    et_otp_var3.setCursorVisible(true);
                }
            }
        });
        et_otp_var3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    et_otp_var3.clearFocus();
                    et_otp_var4.requestFocus();
                    et_otp_var4.setCursorVisible(true);
                }
            }
        });

        buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOTP = et_otp_var1.getText().toString() + et_otp_var2.getText().toString() + et_otp_var3.getText().toString() + et_otp_var4.getText().toString();
                new VerifyOTP().execute(strMobileNo, strOTP );
            }
        });
    }


    private void gotoMainActivity(){
        Intent intent = new Intent(VerifyPhoneActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getIntentData()
    {
        Intent intent = getIntent();
        strMobileNo = intent.getStringExtra("mobileno");
        lblMobileNo.setText("+91 " + strMobileNo);
    }

    private class ResendOTP extends AsyncTask<String, Void, String> {

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
                ServiceHandler shh = new ServiceHandler(VerifyPhoneActivity.this);
                String mobileno = args[0];

                RequestBody values = new FormBody.Builder()
                        .add("user_ContactNo", mobileno)
                        .build();

                jsonStr = shh.makeServiceCall(NW_URL.RequestOTP(), ServiceHandler.POST, values);

                Log.d("Response: ", "> " + jsonStr);


            } catch (final Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showDialog(VerifyPhoneActivity.this, e.toString(), false, false);
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
                        Utils.showDialog(VerifyPhoneActivity.this, responseMsg, false, false);
                    }
                    else
                    {
                        responseMsg = jsonData.getString("message_text");
                        Utils.showDialog(VerifyPhoneActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class VerifyOTP extends AsyncTask<String, Void, String> {

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
                ServiceHandler shh = new ServiceHandler(VerifyPhoneActivity.this);
                String mobileno = args[0];
                String otp = args[1];

                RequestBody values = new FormBody.Builder()
                        .add("user_ContactNo", mobileno)
                        .add("user_OTP", otp)
                        .build();

                jsonStr = shh.makeServiceCall(NW_URL.VerifyOTP(), ServiceHandler.POST, values);

                Log.d("Response: ", "> " + jsonStr);


            } catch (final Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showDialog(VerifyPhoneActivity.this, e.toString(), false, false);
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
                        int id = obj.getInt("user_Id");
                        String mobile = obj.getString("user_ContactNo");
                        String name = obj.getString("user_Name");
                        String selectedOccupation = obj.getString("user_Occupation");

                        userViewModel.insertProduct(new User(id, mobile, name, selectedOccupation));
                        saveToDatabase(id, mobile,name,selectedOccupation);
                        saveToLocal(name, id);

                        gotoMainActivity();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        responseMsg = jsonData.getString("message_text");
                        progressDialog.dismiss();
                        Utils.showDialog(VerifyPhoneActivity.this, responseMsg, false, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void saveToLocal(String name, int id){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", name);
        editor.putString("userid", String.valueOf(id));
        editor.putString("status", "Logged In");
        editor.apply();
        Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveToDatabase(int id, String mobile,String username,String occupation){
        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = String.valueOf(id); mDatabase.push().getKey();
        User user = new User(id, mobile,username,occupation);
        mDatabase.child(userId).setValue(user);
    }



}

