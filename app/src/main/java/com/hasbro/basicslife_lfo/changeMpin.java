package com.hasbro.basicslife_lfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class changeMpin extends AppCompatActivity {
    String mobile,mpin,name;
    EditText oldM,newM,confirmM;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    TextInputLayout oldmpin,newmpin,conmpin;
    private final int flag=0;
    customProgressBar progressDialog = new customProgressBar();
    boolean progressflag=false;
    String returnupdate="";
    Retrofit retrofit   = geturl.getClient();
Button changempin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.change_mpin);

        mobile= getIntent().getStringExtra("phoneno");
        mpin= getIntent().getStringExtra("mpin");
        name= getIntent().getStringExtra("name");

        System.out.println("mpin111 "+mpin);
        oldM=findViewById(R.id.oldmpin);
        newM=findViewById(R.id.newmpin);
        confirmM=findViewById(R.id.confirmmpin);

        oldmpin=findViewById(R.id.textInputold);
        newmpin=findViewById(R.id.textInputnew);
        conmpin=findViewById(R.id.textInputconfirm);

        changempin=findViewById(R.id.change);

        changempin.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                if (v == changempin) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmM.getWindowToken(), 0);

                }


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

    }
    public void Login_validate() {

        //get value from TextView object....
        String oldpin = oldM.getText().toString().trim();
        String newpin = newM.getText().toString().trim();
        String conpin = confirmM.getText().toString().trim();

        if (oldM.getText().toString().trim().equalsIgnoreCase("")) {
            oldM.requestFocus();
            newmpin.setError("");
            conmpin.setError("");
            oldmpin.setError("Enter Old Mpin.");
        }else if(newM.getText().toString().trim().equalsIgnoreCase("")){
            newM.requestFocus();
            oldmpin.setError("");
            conmpin.setError("");
            newmpin.setError("Enter New Mpin.");
        }else if(confirmM.getText().toString().trim().equalsIgnoreCase("")){
            confirmM.requestFocus();
            oldmpin.setError("");
            newmpin.setError("");
            conmpin.setError("Enter Confirm Mpin.");
        }else if(!oldM.getText().toString().trim().equalsIgnoreCase(mpin)){
            oldM.requestFocus();
            newmpin.setError("");
            conmpin.setError("");
            oldmpin.setError("Invalid Old Mpin");
        }
        else if (!newM.getText().toString().matches("[0-9]{6}")) {
            newM.requestFocus();
            oldmpin.setError("");
            conmpin.setError("");
            newmpin.setError("Enter 6 Digit MPin.");
        }else if(!newpin.equalsIgnoreCase(conpin) || !conpin.equalsIgnoreCase(newpin)) {
            newM.setText("");
            confirmM.setText("");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
            new SweetAlertDialog(changeMpin.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Basics Life LFO Portal")
                    .setContentText("Mpin Doesn't Match")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();
                }
            });
        }
        else {
            oldmpin.setError("");
            newmpin.setError("");
            conmpin.setError("");
            Ckeck_data(newpin, mobile);
        }
    }

   public void Ckeck_data(String newpin, String mobile) {

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
                        Go_Check(newpin,mobile);


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progressflag = false;
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });

                    }

                }
            }).start();

        } else {
            interneterror();
        }
    }

    private void Go_Check(String newpin, String mobile){

        try {
            // Setup the connection with the DB
            String updateURL = ""+retrofit.baseUrl()+"generateMpinupdate";
            mRequestQueue = Volley.newRequestQueue(changeMpin.this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("mobile", mobile);
            jsonBody.put("password", newpin);
            final String mRequestBody = jsonBody.toString();

            mStringRequest = new StringRequest(Request.Method.PUT, updateURL, response1 -> {
                returnupdate= response1;

                System.out.println("returnupdate "+returnupdate);
                if(returnupdate.equalsIgnoreCase("true")) {
                    Intent intent = new Intent(getApplicationContext(), updateSuccessfully.class);
                    //System.out.println("name: " + query1);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("name", name);
                    intent.putExtra("bio", "false");
                    startActivity(intent);
                    finish();
                }
            }, error -> Log.e("LOG_RESPONSE", error.toString())) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return mRequestBody == null ? null : mRequestBody.getBytes(StandardCharsets.UTF_8);
                }


            };

            mRequestQueue.add(mStringRequest);

        }

        catch(Exception ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());

            // Toast.makeText(getApplicationContext(),"Can't Connect Server ",Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {

                @SuppressLint("RestrictedApi")
                @Override
                public void run() {
                    commonError();
                    progressflag = false;
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

        }

        finally {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressflag = false;
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

        }
    }
    private void commonError() {
        new SweetAlertDialog(changeMpin.this, SweetAlertDialog.ERROR_TYPE)
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

    private void interneterror() {
        new SweetAlertDialog(changeMpin.this, SweetAlertDialog.WARNING_TYPE)
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
}