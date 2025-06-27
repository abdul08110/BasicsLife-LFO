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
import com.hasbro.basicslife_lfo.intro.step1;


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

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        // Initialize Network Check
        new CheckNetworkConnection(getApplicationContext()).registerDefaultNetworkCallback();

        // Initialize UI Elements
        initializeUI();

        // Retrieve Intent Data
        phoneno = getIntent().getStringExtra("mobile");
        username = getIntent().getStringExtra("name");
        bio = getIntent().getStringExtra("bio");
        tmcode = getIntent().getStringExtra("tmcode");

        // Update UI
        updateUI();

        // Set up Biometric Authentication
        setupBiometric();

        // Handle Button Clicks
        handleButtonClicks();

        // Schedule Notifications
        scheduleNotificationAlarm();


        // First Run Check
        checkFirstRun();

        // Handle Back Press
        handleBackPress();

       // Check Biometric Supported
        checkBioMetricSupported();
        //setup Hyperlink
        setupHyperlink();
    }
    private void initializeUI() {
        uid = findViewById(R.id.uid);
        pass = findViewById(R.id.pass);
        welcome = findViewById(R.id.textwelcome);
        forgetpass = findViewById(R.id.frgtpass);
        textInputPassword = findViewById(R.id.textInputPassword);
        biotext = findViewById(R.id.biotext);
        enablebio = findViewById(R.id.enablebiometric);
        sign = findViewById(R.id.anotheraccount);
        launchAuthentication = findViewById(R.id.launchAuthentication);
        biometric = findViewById(R.id.biometric);
        myDialog = new Dialog(this);
    }
    private void updateUI() {
        welcome.setText(String.format("Welcome Back, %s", username));
        uid.setText(phoneno);

        if ("false".equalsIgnoreCase(bio)) {
            enablebio.setVisibility(View.VISIBLE);
            biotext.setVisibility(View.GONE);
            biometric.setVisibility(View.GONE);
        } else {
            enablebio.setVisibility(View.GONE);
            biotext.setVisibility(View.VISIBLE);
            biometric.setVisibility(View.VISIBLE);
        }
    }
    private void setupBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        showToast("Authentication error: " + errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        loginwithbiometric();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        showToast("Authentication failed");
                    }
                }
        );

        biometric.setOnClickListener(v -> {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock BasicsLife LFO Portal")
                    .setSubtitle("Confirm your screen lock pattern, PIN, or password")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .setDeviceCredentialAllowed(true)
                    .build();

            biometricPrompt.authenticate(promptInfo);
        });
    }
    private void handleButtonClicks() {
        enablebio.setOnClickListener(v -> ShowPopup(phoneno));

        launchAuthentication.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            v.startAnimation(bounce);

            hideKeyboard(v);
            Login_validate();
        });

        forgetpass.setOnClickListener(v -> {
            Intent intent = new Intent(this, forgetPassword.class);
            intent.putExtra("mobile", phoneno);
            startActivity(intent);
        });

        sign.setOnClickListener(v -> startActivity(new Intent(this, freshLoginPage.class)));
    }
    private void scheduleNotificationAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = SystemClock.elapsedRealtime() + 20 * 1000;
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
    }
    private void checkFirstRun() {
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        if (settings.getBoolean("firstRun", true)) {
            settings.edit().putBoolean("firstRun", false).apply();
            Log.d("TAG1", "First run detected.");
        }
    }
    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!progressflag) {
                    exiterror();
                }
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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




    private void Go_loginbio() {

        String mobileno= uid.getText().toString();

        String URL = retrofit.baseUrl()+"loginbio?mobile=" + mobileno + " ";
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

                        Intent intent = new Intent(getApplicationContext(), step1.class);
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

        String URL = retrofit.baseUrl()+"loginpass?mobile=" + mobileno + "&password=" + passw + "";
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

            Intent intent = new Intent(getApplicationContext(), step1.class);
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
        String URL = retrofit.baseUrl()+"loginpass?mobile=" + mobileno + "";
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

                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
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

            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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