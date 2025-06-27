package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class generateMpin extends AppCompatActivity {
    private static final String TAG = "generateMpin";

    private EditText mpin;
    private TextInputLayout textInputPassword;
    private TextView launchAuthentication;

    private String phoneno, name, tmcode, mpinn;
    private RequestQueue requestQueue;
    private customProgressBar progressDialog = new customProgressBar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_mpin);

        // Initialize UI elements
        mpin = findViewById(R.id.mpin);
        textInputPassword = findViewById(R.id.textInputPassword);
        launchAuthentication = findViewById(R.id.launchAuthentication);

        phoneno = getIntent().getStringExtra("mobile");
        name = getIntent().getStringExtra("name");

        Log.d(TAG, "onCreate: Mobile - " + phoneno);

        // Handle Authentication Button Click
        launchAuthentication.setOnClickListener(v -> {
            hideKeyboard(v);
            mpinn = mpin.getText().toString();
            validateMpin();
        });

        // Handle Back Button
        handleBackPress();
    }

    private void validateMpin() {
        if (mpinn.isEmpty()) {
            textInputPassword.setError("Please enter MPin");
        } else if (!mpinn.matches("[0-9]{6}")) {
            textInputPassword.setError("Enter a 6-digit MPin");
        } else {
            textInputPassword.setError(null);
            checkNetworkAndProceed();
        }
    }

    private void checkNetworkAndProceed() {
        if (GlobalVars.isNetworkConnected) {
            progressDialog.show(getSupportFragmentManager(), "tag");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            checkLoginStatus();
        } else {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showInternetError();
        }
    }

    private void checkLoginStatus() {
        String url = retrofit.baseUrl() + "loginbio?mobile=" + phoneno;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        handleLoginResponse(response);
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.e(TAG, "JSON Parsing Error", e);
                        showCommonError();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Log.e(TAG, "Volley Error", error);
                    showCommonError();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void handleLoginResponse(JSONObject response) throws JSONException {
        if (response.length() > 0) {
            JSONArray login = response.getJSONArray("login");
            name = login.getString(0);
            String loginStatus = login.getString(2);
            tmcode = login.getString(4);

            if ("1".equals(loginStatus)) {
                updateMpin();
            } else {
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showErrorDialog("You Are Not an Active Employee");
            }
        } else {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showErrorDialog("No Record Found");
        }
    }

    private void updateMpin() {
        String updateURL = retrofit.baseUrl() + "generateMpinupdate";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile", phoneno);
            jsonBody.put("password", mpinn);
        } catch (JSONException e) {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e(TAG, "JSON Error", e);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateURL,
                response -> {
                    if ("true".equalsIgnoreCase(response)) {
                        navigateToSuccessPage();
                    } else {
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        showCommonError();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Log.e(TAG, "Update Error", error);
                    showCommonError();
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(stringRequest);
    }

    private void navigateToSuccessPage() {
        Intent intent = new Intent(this, updateSuccessfully.class);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", name);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("bio", "false");
        startActivity(intent);
        finish();
    }

    private void showErrorDialog(String message) {
        runOnUiThread(() -> {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Basics Life LFO Portal")
                    .setContentText(message)
                    .setConfirmText("OK")
                    .setConfirmClickListener(dialog -> {
                        dialog.dismissWithAnimation();
                        navigateToLoginPage();
                    })
                    .show();
        });
    }

    private void showCommonError() {
        showErrorDialog("Something went wrong. Please try again.");
    }

    private void showInternetError() {
        runOnUiThread(() -> new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Internet Error")
                .setContentText("Please turn on your internet connection.")
                .setConfirmText("Settings")
                .setConfirmClickListener(dialog -> {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                })
                .show());
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(this, freshLoginPage.class);
        startActivity(intent);
        finish();
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back button functionality
            }
        });
    }
}
