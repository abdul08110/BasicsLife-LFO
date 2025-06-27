package com.hasbro.basicslife_lfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class verificationOTP extends AppCompatActivity {

    // UI Elements
    private EditText inputotp1, inputotp2, inputotp3, inputotp4, inputotp5, inputotp6;
    private TextView launchAuthentication, timer, resendLabel, mobile, otpver;
    private String getotpbackend, phoneno, name, tmcode, randString = "";
    private int resendCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_otp);

        // Initialize UI elements
        initializeUI();

        // Animate UI elements
        animateUI();

        // Generate Random OTP
        randGenerator();

        // Handle OTP verification
        handleOTPVerification();

        // Handle Resend OTP
        handleResendOTP();

        // Restrict back button functionality
        handleBackPress();

        runThread();
    }

    private void runThread() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("00:" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resendLabel.setEnabled(true);
            }

        }.start();// Run on a background thread
    }

    private void initializeUI() {
        inputotp1 = findViewById(R.id.otp1);
        inputotp2 = findViewById(R.id.otp2);
        inputotp3 = findViewById(R.id.otp3);
        inputotp4 = findViewById(R.id.otp4);
        inputotp5 = findViewById(R.id.otp5);
        inputotp6 = findViewById(R.id.otp6);

        launchAuthentication = findViewById(R.id.launchAuthentication);
        timer = findViewById(R.id.tex_view);
        resendLabel = findViewById(R.id.resendotp);
        mobile = findViewById(R.id.mobileno);
        otpver = findViewById(R.id.otpver);

        phoneno = getIntent().getStringExtra("mobile");
        name = getIntent().getStringExtra("name");
        tmcode = getIntent().getStringExtra("tmcode");
        getotpbackend = getIntent().getStringExtra("otp");

        mobile.setText(phoneno);
        setupOTPFieldTransitions();
    }

    private void animateUI() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mobile.startAnimation(animation);
        otpver.startAnimation(animation);
        resendLabel.startAnimation(animation);
        launchAuthentication.startAnimation(animation);
    }

    private void handleOTPVerification() {
        launchAuthentication.setOnClickListener(v -> {
            String enteredOTP = inputotp1.getText().toString() +
                    inputotp2.getText().toString() +
                    inputotp3.getText().toString() +
                    inputotp4.getText().toString() +
                    inputotp5.getText().toString() +
                    inputotp6.getText().toString();

            if (enteredOTP.isEmpty() || enteredOTP.length() < 6) {
                Toast.makeText(this, "Enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(enteredOTP);
            }
        });
    }

    private void verifyCode(String code) {
        if (code.equals(getotpbackend) || code.equals(randString)) {
            Intent intent = new Intent(this, generateMpin.class);
            intent.putExtra("mobile", phoneno);
            intent.putExtra("name", name);
            intent.putExtra("tmcode", tmcode);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResendOTP() {
        if (resendCount < 2) {
            resendLabel.setOnClickListener(v -> {
                resendCount++;
                resendLabel.setEnabled(false);
                startResendTimer();
                randGenerator();
            });
        } else {
            showTooManyRequestsError();
        }
    }

    private void startResendTimer() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                resendLabel.setEnabled(true);
            }
        }.start();
    }

    private void showTooManyRequestsError() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Too Many OTP Requests")
                .setContentText("You have exceeded the maximum OTP resend attempts.")
                .setConfirmText("Go Back")
                .setConfirmClickListener(sDialog -> {
                    Intent intent = new Intent(this, freshLoginPage.class);
                    startActivity(intent);
                    finish();
                }).show();
    }

    private void sendOTP(String otp) {
        new Thread(() -> {
            try {
                String message = "Your OTP for login into BASICS LFO Portal is " + otp + " - Basics.";
                String fullURL = BuildConfig.OTP_URL + phoneno + "&sender=BSXLFE&message=" + message;

                URL url = new URL(fullURL);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);

                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                    writer.write(""); // Sending an empty request body if required
                    writer.flush();

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Debugging the response
                    Log.d("OTP Response", response.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("sendOTP", "Failed to send OTP", e);
            }
        }).start();
        // Run on a background thread
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Prevent default back behavior
            }
        });
    }

    private void randGenerator() {
        String block = "1234567890";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            otp.append(block.charAt(random.nextInt(block.length())));
        }

        randString = otp.toString();
        Log.d("Generated OTP", randString);

        // Send OTP on a background thread
        sendOTP(randString);
    }

    private void setupOTPFieldTransitions() {
        setupFieldTransition(inputotp1, inputotp2);
        setupFieldTransition(inputotp2, inputotp3);
        setupFieldTransition(inputotp3, inputotp4);
        setupFieldTransition(inputotp4, inputotp5);
        setupFieldTransition(inputotp5, inputotp6);
    }

    private void setupFieldTransition(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    current.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }
}
