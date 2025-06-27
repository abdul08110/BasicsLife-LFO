package com.hasbro.basicslife_lfo;



import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class forgetPassword extends AppCompatActivity {
    TextView uid;
    TextView launchAuthentication,forgotpass,desc;
    TextInputLayout textInputmobile;
    customProgressBar progressDialog = new customProgressBar();
    ImageView image;
    Dialog dialog;
    private String randString = "";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    boolean progressflag=false;
    String phoneno;
    Retrofit retrofit   = geturl.getClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_forget_password);
        uid = findViewById(R.id.uid);
        image = findViewById(R.id.image);
        forgotpass = findViewById(R.id.forgotpass);
        desc = findViewById(R.id.desc);
        phoneno = getIntent().getExtras().getString("mobile");
        uid.setText(phoneno);
        randGenerator();
        launchAuthentication = (TextView) findViewById(R.id.launchAuthentication);
        launchAuthentication.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View arg0) {
                if (arg0.getId() == R.id.launchAuthentication) {
                    hideKeybaord(arg0);
                }
                Ckeck_data(uid.getText().toString());
            }

            private void hideKeybaord(View arg0) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(arg0.getApplicationWindowToken(), 0);
            }
        });


    }

    void Ckeck_data(final String  user_id){


        //Check internet connection with method ConnectivityManager CONNECTIVITY_SERVICE
        if (GlobalVars.isNetworkConnected) {
            progressDialog.show(getSupportFragmentManager(), "tag");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressflag=true;

            new Thread(new Runnable() {
                @Override
                public void run() {


                    try {

                        // This method call MySQL database perform sql query...
                        Go_Check(user_id);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {
            Internet_connection();
            //dialog.show();
        }
    }
    protected void Internet_connection() {

       interneterror();
        dialog.show();
    }
    private String randGenerator() {

        int range = 6;
        StringBuilder sb = new StringBuilder();
        String block = "1234567890";
        sb.append(block).append(block.toUpperCase()).append("012345");
        block = sb.toString();
        sb = new StringBuilder();
        Random random = new Random();

        try {
            for (int i = 0; i < range; i++) {
                sb.append(block.charAt(random.nextInt(block.length() - 1)));
            }
            randString = sb.toString();
            System.out.println("Random" + randString);

        } catch (Exception e) {
            System.out.println(e);
        }

        return randString;
    }
    protected void Go_Check(final String  user_id)  {




        try {

            String mobileno= uid.getText().toString();
            String URL =""+retrofit.baseUrl()+"loginbio?mobile=" + mobileno + " ";
            //RequestQueue initialized

            mRequestQueue = Volley.newRequestQueue(this);

            //String Request initialized
            mStringRequest = new StringRequest(Request.Method.GET, URL, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.length() > 0){

                        new Thread(() -> {
                            try {
                                String line;
                                String message="Your OTP for login into BASICS Employee Portal is "+randString+" - Basics.";
                                String fullURL = ""+BuildConfig.OTP_URL+"" + mobileno + "&sender=BSXLFE&message=" + message;
                                java.net.URL url = new URL(fullURL);
                                final StringBuilder stringBuffer = new StringBuilder();
                                final URLConnection connection = url.openConnection();
                                connection.setDoOutput(true);
                                String data = "";
                                final OutputStream outputStream = connection.getOutputStream();
                                final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                                outputStreamWriter.write(data);
                                outputStreamWriter.flush();
                                final BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while ((line = rd.readLine()) != null) {
                                    stringBuffer.append(line);
                                }
                                outputStreamWriter.close();
                                rd.close();
                                String count = stringBuffer.toString();
                                if (!"".equals(count)) {
                                    Intent intent = new Intent(getApplicationContext(), verificationOTP.class);
                                    intent.putExtra("otp", randString);
                                    intent.putExtra("mobile", uid.getText().toString());
                                    Log.d(TAG, "onCreate: " + intent);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(forgetPassword.this,"OTP not sent Try after sometime...!!!",Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }).start();
                    }else{
                        runOnUiThread(() -> {
                            mobilenumdoesntexist();
                            progressflag=false;
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }, error -> System.out.println("Error :" + error.toString()));

            mRequestQueue.add(mStringRequest);
            // ResultSet get the result of the SQL query


        } catch (Exception ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());

            runOnUiThread(() -> {
                somethingwentwrong();
                progressflag=false;
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            });


        } finally {



            runOnUiThread(() -> {
                progressflag=false;
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });


        }

    }
    private void mobilenumdoesntexist() {
        new SweetAlertDialog(forgetPassword.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Mobile Number Not Exist")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                       // Intent intent = new Intent(getApplicationContext(), customerCreateForm.class);
                       // startActivity(intent);
                      //  finish();
                    }
                })
                .show();
    }
    private void interneterror() {
        new SweetAlertDialog(forgetPassword.this, SweetAlertDialog.WARNING_TYPE)
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
    private void somethingwentwrong() {
        new SweetAlertDialog(forgetPassword.this, SweetAlertDialog.ERROR_TYPE)
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
}