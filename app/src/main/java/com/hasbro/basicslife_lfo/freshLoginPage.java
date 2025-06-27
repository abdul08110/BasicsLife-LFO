package com.hasbro.basicslife_lfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class freshLoginPage extends AppCompatActivity {
    // UI Elements
    private TextInputEditText uid;
    private TextInputLayout textInputMobile;
    private TextView hyper, copyR;

    // Constants
    public static final String PREFS_NAME = "MyLoginPrefsFile";

    // Variables
    private String randString = "";
    private String name = "", loginStatus = "", tmcode = "";
    private JSONArray freshLogin = new JSONArray();

    // Objects
    private customProgressBar progressDialog = new customProgressBar();
    private RequestQueue requestQueue;
    private Retrofit retrofit = geturl.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fresh_login_page);

        // Initialize Network Check
        CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
        network.registerDefaultNetworkCallback();

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Initialize UI elements
        uid = findViewById(R.id.uid);
        textInputMobile = findViewById(R.id.textInputmobile);
        hyper = findViewById(R.id.hyper);
        copyR = findViewById(R.id.copyR);

        // Setup Hyperlink
        setupHyperlink();

        // Generate Random String for OTP
        randGenerator();

        // Handle Back Button Press
        handleBackPress();

        // Set up authentication button listener
        findViewById(R.id.launchAuthentication).setOnClickListener(v -> {
            hideKeyboard(v);
            validateLogin();
        });
    }

    // Setup hyperlink for TextView
    private void setupHyperlink() {
        hyper.setMovementMethod(LinkMovementMethod.getInstance());
        hyper.setLinkTextColor(Color.BLACK);
    }

    // Generate random string for OTP
    private void randGenerator() {
        int range = 6;
        String block = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        try {
            for (int i = 0; i < range; i++) {
                sb.append(block.charAt(random.nextInt(block.length())));
            }
            randString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hide keyboard
    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    // Handle back button press using OnBackPressedDispatcher
    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
                if (hasLoggedIn) {
                    navigateToMainActivity(settings);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void navigateToMainActivity(SharedPreferences settings) {
        String phoneno = settings.getString("mobile", null);
        String name = settings.getString("name", null);
        String bio = settings.getString("bio", null);
        String tmcode = settings.getString("tmcode", null);

        if (phoneno != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("mobile", phoneno);
            intent.putExtra("name", name);
            intent.putExtra("bio", bio);
            intent.putExtra("tmcode", tmcode);
            startActivity(intent);
        }
    }

    // Validate login
    private void validateLogin() {
        if (uid.getText().toString().trim().isEmpty()) {
            uid.requestFocus();
            textInputMobile.setError("Please enter Mobile Number.");
        } else if (!uid.getText().toString().matches("[0-9]{10}")) {
            uid.requestFocus();
            textInputMobile.setError("Enter 10 Digit Mobile Number.");
        } else {
            textInputMobile.setError(null);
            checkData();
        }
    }

    // Check data and handle response
    private void checkData() {
        if (GlobalVars.isNetworkConnected) {
            progressDialog.show(getSupportFragmentManager(), "tag");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            new Thread(this::fetchLoginData).start();
        } else {

            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showInternetError();
        }
    }

    // Fetch login data
    private void fetchLoginData() {
        String mobileNo = uid.getText().toString();
        String url = retrofit.baseUrl() + "freshlogin?mobile=" + mobileNo;

        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                handleLoginResponse(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Volley Error", error.toString()));

        requestQueue.add(request);
    }

    private void handleLoginResponse(JSONObject obj) throws JSONException {
        if (obj.length() > 0 && obj.getJSONArray("freshlogin").length() > 0) {
            freshLogin = obj.getJSONArray("freshlogin");
            String loginStatus = freshLogin.getString(1);
            if ("1".equals(loginStatus)) {

                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                navigateToOTPPage();
            } else {

                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showNotActiveError();
            }
        } else {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showMobileNumberDoesNotExist();
        }
    }

    private void navigateToOTPPage() {
        Intent intent = new Intent(getApplicationContext(), verificationOTP.class);
        intent.putExtra("otp", randString);
        intent.putExtra("mobile", uid.getText().toString());
        startActivity(intent);
    }

    private void showMobileNumberDoesNotExist() {
        runOnUiThread(() -> new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Mobile Number Does Not Exist")
                .setConfirmText("OK")
                .show());
    }

    private void showNotActiveError() {
        runOnUiThread(() -> new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("You Are Not an Active Employee")
                .setConfirmText("OK")
                .show());
    }

    private void showInternetError() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Error")
                .setContentText("Turn On Internet")
                .setConfirmText("Settings")
                .setConfirmClickListener(dialog -> startActivity(new Intent(Settings.ACTION_SETTINGS)))
                .show();
    }
}
