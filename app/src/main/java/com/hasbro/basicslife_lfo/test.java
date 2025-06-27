package com.hasbro.basicslife_lfo;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static com.hasbro.basicslife_lfo.freshLoginPage.PREFS_NAME;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricManager.Authenticators.*;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class test extends AppCompatActivity {

    EditText pass,uid;
    TextView sign,forgetpass,hyper,copyR,welcome;
    ImageView biometric;
    TextView launchAuthentication,biotext,enablebio;
    TextInputLayout textInputPassword;
    String login_status="",tmcode="",name="", passwordd="";

    String sessionflag="0";
    private String username;

    boolean progressflag=false;
    Dialog myDialog;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    SharedPreferences settings;
    JSONArray imageurl =new JSONArray();
    JSONArray login =new JSONArray();
    SharedPreferences.Editor editor;
    customProgressBar progressDialog = new customProgressBar();
    String phoneno,bio;
    Retrofit retrofit   = geturl.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
        network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scheduleNotificationAlarm();
            }
        }, 10000);

        // Enqueue the work request
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        //Create a ProgressBar object....

        //Create a TextInputEditText object....
        phoneno = getIntent().getExtras().getString("mobile");
        username = getIntent().getExtras().getString("name");
        bio = getIntent().getExtras().getString("bio");
        tmcode = getIntent().getExtras().getString("tmcode");



        myDialog = new Dialog(this);
        uid=findViewById(R.id.uid);
        pass  = findViewById(R.id.pass);
        welcome = findViewById(R.id.textwelcome);
        forgetpass=findViewById(R.id.frgtpass);
        textInputPassword=findViewById(R.id.textInputPassword);
        biotext=findViewById(R.id.biotext);
        enablebio=findViewById(R.id.enablebiometric);



        sign = findViewById(R.id.anotheraccount);
        launchAuthentication =(Button) findViewById(R.id.launchAuthentication);
        hyper = findViewById(R.id.hyper);
        copyR = findViewById(R.id.copyR);
        biometric = findViewById(R.id.biometric);
        welcome.setText("Welcome Back " +username);
        uid.setText(phoneno);

        if(bio.equalsIgnoreCase("false") ){
            enablebio.setVisibility(View.VISIBLE);
            biotext.setVisibility(View.GONE);
            biometric.setVisibility(View.GONE);
        }
        else{
            enablebio.setVisibility(View.GONE);
            biotext.setVisibility(View.VISIBLE);
            biometric.setVisibility(View.VISIBLE);
        }

        settings = getSharedPreferences("prefs", 0);
        editor = settings.edit();
        editor.putBoolean("firstRun", false);
        editor.commit();

        boolean firstRun = settings.getBoolean("firstRun", true);
        Log.d("TAG1", "firstRun: " + Boolean.valueOf(firstRun).toString());

        enablebio.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                ShowPopup(phoneno);
            }
        });



        launchAuthentication.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                if (v == launchAuthentication) {
                    launchAuthentication.startAnimation(bounce);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);

                }
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("activity_executed", true);
                edt.apply();


                if (v.getId() == R.id.launchAuthentication) {
                    hideKeybaord(v);
                }
                Login_validate();
            }
            private void hideKeybaord(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
            }
        });


        //onClick view event method call ...

        forgetpass.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                Intent frgtp= new Intent(getApplicationContext(),forgetPassword.class);
                frgtp.putExtra("mobile",phoneno);
                startActivity(frgtp);
            }
        });
        sign.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                Intent signup= new Intent(getApplicationContext(),freshLoginPage.class);
                startActivity(signup);
            }
        });
        checkBioMetricSupported();
        setupHyperlink();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(test.this,
                executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            // this method will automatically call when it is succeed verify fingerprint
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                loginwithbiometric();
            }

            // this method will automatically call when it is failed verify fingerprint
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //attempt not regconized fingerprint
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
        biometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(test.this, "helloo", Toast.LENGTH_SHORT).show();
                BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
                promptInfo.setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                promptInfo.setDeviceCredentialAllowed(true);
                //activate callback if it succeed
                biometricPrompt.authenticate(promptInfo.build());
            }

            private BiometricPrompt.PromptInfo.Builder dialogMetric() {
                return new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Unlock BasicsLife LFO Portal")
                        .setSubtitle("Confirm your screen lock pattern,PIN or password");
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // Back is pressed... Finishing the activity
                if (!progressflag) {
                    exiterror();
                }
            }
        });



        // NotificationHelper.fetchNotification(this);
    }
    public void loginwithbiometric() {

        if (GlobalVars.isNetworkConnected) {

            progressDialog.show(getSupportFragmentManager(), "tag");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressflag=true;

            new Thread(() -> {
                try {
                    // This method call MySQL database perform sql query...
                    Go_loginbio();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

        } else {
            interneterror();
        }
    }

    private void scheduleNotificationAlarm() {
        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule exact alarm for 20 seconds later
        long triggerTime = SystemClock.elapsedRealtime() + 20 * 1000;
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
        );
    }
    private void Go_loginbio() {

        String mobileno= uid.getText().toString();

        String URL = ""+retrofit.baseUrl()+"loginbio?mobile=" + mobileno + " ";
        //RequestQueue initialized

        mRequestQueue = Volley.newRequestQueue(this);
        HashMap<String, List> mappingimgurl =new HashMap<String,List>();

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, URL, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.length() > 0){
                    login = obj.getJSONArray("login");
                    imageurl = obj.getJSONArray("imageurl");
                    String chk = String.valueOf(obj.get("login"));
                    if (!chk.equalsIgnoreCase("[null]")) {

                        login_status = (String) login.get(2);

                        if (login_status.equalsIgnoreCase("1")){
                            ArrayList<String> tempOneList = new ArrayList<String>();
                            for (int i = 0; i < imageurl.length(); i++) {
                                String img = (String) imageurl.get(i);
                                tempOneList.add(img);

                            }
                            mappingimgurl.put("imgurl", tempOneList);
                            System.out.println("mappingimgurl "+mappingimgurl);

                            //getting the json object of the particular index inside the array
                            name = (String) login.get(0);
                            passwordd = (String) login.get(1);
                            tmcode= (String) login.get(4);

                            runOnUiThread(() -> {
                                //message.setText("Welcome : "+ finalUser_name);

                                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                Bundle bundle = new Bundle();
                                intent.putExtra("mobile", phoneno);
                                intent.putExtra("mpin", passwordd);
                                intent.putExtra("name", name);
                                intent.putExtra("map", mappingimgurl);
                                intent.putExtra("bio", bio);
                                intent.putExtra("tmcode", tmcode);
                                intent.putExtra("sessionflag", sessionflag);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void run() {
                                    notactive();
                                    progressflag = false;
                                    progressDialog.dismiss();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mobilenumdoesntexist();
                                progressflag=false;
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            invalidLoginError();
                            progressflag = false;
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                        }
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }, error -> System.out.println("Error :" + error.toString()));

        mRequestQueue.add(mStringRequest);
    }
    private void ShowPopup(String phoneno) {

        TextView txtclose,skip;
        Button enablebio;

        myDialog.setContentView(R.layout.enable_biometric_popup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        skip =(TextView) myDialog.findViewById(R.id.skip);
        txtclose.setText("X");
        enablebio = (Button) myDialog.findViewById(R.id.enablebio);
        enablebio.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                enablebio.setVisibility(View.GONE);

                if (GlobalVars.isNetworkConnected) {


                    progressDialog.show(getSupportFragmentManager(), "tag");

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressflag = true;

                    new Thread(() -> {


                        try {

                            // This method call MySQL database perform sql query...
                            // checkdatabio(phoneno);
                            myDialog.dismiss();
                            changestausbio();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }).start();

                } else {
                    interneterror();
                }


                //  Go_Check(user_mpin,phonenoo, flag[0]);
            }
        });
        txtclose.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {

                myDialog.dismiss();
                // Go_Check(user_mpin,phonenoo, flag[0]);
            }
        });
        skip.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                //  flag[0][0] =2;
                myDialog.dismiss();
                // Go_Check(user_mpin,phonenoo, flag[0]);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void checkBioMetricSupported() {
        BiometricManager manager = BiometricManager.from(this);
        String info="";
        switch (manager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG))
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "App can authenticate using biometrics.";
                enableButton(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on this device.";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable.";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Need register at least one finger print";
                enableButton(false,true);
                break;
            default:
                info= "Unknown cause";
                enableButton(false);
        }

        //set message to text view so we can see what happen with sensor device
        // TextView txinfo =  findViewById(R.id.tx_info);
        Toast.makeText(this, ""+info, Toast.LENGTH_SHORT).show();
    }

    private void enableButton(boolean enable) {
        biometric.setEnabled(enable);

    }
    void enableButton(boolean enable,boolean enroll)
    {
        enableButton(enable);

        if(!enroll) return;
        // Prompts the user to create credentials that your app accepts.
        //Open settings to set credential fingerprint or PIN
        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        startActivity(enrollIntent);
    }




    private void setupHyperlink() {
        TextView linkTextView = findViewById(R.id.hyper);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(Color.BLACK);
    }

    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event);
            return true;
        }
        if(key_code == KeyEvent.KEYCODE_HOME)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void Login_validate() {

        //get value from TextView object....
        String password = pass.getText().toString().trim();

        if (pass.getText().toString().trim().equalsIgnoreCase("")) {
            pass.requestFocus();
            textInputPassword.setError("Please enter password.");
        } else {
            textInputPassword.setError("");

            if (GlobalVars.isNetworkConnected) {

                progressDialog.show(getSupportFragmentManager(), "tag");

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressflag = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {

                            // This method call MySQL database perform sql query...
                            logincheck();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            } else {
                interneterror();
            }

        }
    }
    private void logincheck() {
        String mobileno= uid.getText().toString();
        String passw= pass.getText().toString();

        String URL = ""+retrofit.baseUrl()+"loginpass?mobile=" + mobileno + "&password=" + passw + "";
        //RequestQueue initialized

        mRequestQueue = Volley.newRequestQueue(this);
        HashMap<String, List> mappingimgurl =new HashMap<String,List>();

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, URL, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.length() > 0){
                    login = obj.getJSONArray("login");
                    imageurl = obj.getJSONArray("imageurl");
                    String chk = String.valueOf(obj.get("login"));
                    if (!chk.equalsIgnoreCase("[null]")) {
                        login_status = (String) login.get(2);

                        if (login_status.equalsIgnoreCase("1")){

                            for (int i = 0; i < imageurl.length(); i++) {
                                ArrayList<String> tempOneList = new ArrayList<String>();
                                String img = (String) imageurl.get(i);
                                tempOneList.add(img);
                                mappingimgurl.put("imgurl", tempOneList);
                            }

                            //getting the json object of the particular index inside the array

                            name = (String) login.get(0);
                            passwordd = (String) login.get(1);
                            tmcode = (String) login.get(4);



                            runOnUiThread(() -> {
                                //message.setText("Welcome : "+ finalUser_name);

                                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                Bundle bundle = new Bundle();
                                intent.putExtra("mobile", phoneno);
                                intent.putExtra("mpin", passwordd);
                                intent.putExtra("name", name);
                                intent.putExtra("map", mappingimgurl);
                                intent.putExtra("bio", bio);
                                intent.putExtra("tmcode", tmcode);
                                intent.putExtra("sessionflag", sessionflag);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void run() {
                                    notactive();
                                    progressflag = false;
                                    progressDialog.dismiss();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mobilenumdoesntexist();
                                progressflag=false;
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            invalidLoginError();
                            progressflag = false;
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                        }
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }, error -> System.out.println("Error :" + error.toString()));

        mRequestQueue.add(mStringRequest);
    }

    private void changestausbio() {
        final String[] returnbio = {""};
        RequestQueue queue = Volley.newRequestQueue(this);
        String mobileno= uid.getText().toString();
        System.out.println(mobileno);
        String URL = ""+retrofit.baseUrl()+"loginpass?mobile=" + mobileno + "";
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                returnbio[0] = response;
                if(returnbio[0].equalsIgnoreCase("true")){

                    runOnUiThread(new Runnable() {

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void run() {

                            new SweetAlertDialog(test.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Basics Life LFO Portal")
                                    .setContentText("Biometric Enabled")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            progressflag = false;
                                            progressDialog.dismiss();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            enablebio.setVisibility(View.GONE);
                                            bio="true";
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("hasLoggedIn", true);
                                            editor.putString("bio",bio);
                                            editor.commit();
                                            biotext.setVisibility(View.VISIBLE);
                                            biometric.setVisibility(View.VISIBLE);
                                            sDialog.dismissWithAnimation();
                                        }
                                    }).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void run() {
                            commonError();
                            progressflag=false;
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }
                    });
                }
            }

        }, error -> System.out.println("Error :" + error.toString()));

        mRequestQueue.add(mStringRequest);
    }


    private void mobilenumdoesntexist() {
        new SweetAlertDialog(test.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Mobile Number Not Exist")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }
    private void notactive() {
        new SweetAlertDialog(test.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("You Are Not Active Employee")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void commonError() {
        new SweetAlertDialog(test.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Something Went Wrong...!")
                .setConfirmText("Try Again")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }


    private void invalidLoginError() {
        new SweetAlertDialog(test.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Mobile/Password is Invalid (or) Not Active Employee")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }


    private void interneterror() {

        new SweetAlertDialog(test.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Turn On Internet")
                .setConfirmText("Settings")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    private void exiterror() {
        new SweetAlertDialog(test.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("We Miss You...")
                .setConfirmText("Stay")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Me Too", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        finishAffinity();
                        System.exit(0);
                    }
                })
                .show();

    }

}