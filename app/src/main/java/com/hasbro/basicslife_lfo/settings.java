package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.FreshLoginPage.PREFS_NAME;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.SQLException;
import java.text.ParseException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class settings extends AppCompatActivity {
    RelativeLayout changempin,biometric;
    String mobile,mpin,name,bio;
    private final int flag=0;
    customProgressBar progressDialog = new customProgressBar();
    Dialog myDialog;
    boolean progressflag=false;

    SwitchCompat switchCompat;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    Retrofit retrofit   = geturl.getClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_settings);
        myDialog = new Dialog(this);
        changempin=findViewById(R.id.r1);
        biometric=findViewById(R.id.r2);
        switchCompat=(SwitchCompat)findViewById(R.id.switchbio);
        mobile= getIntent().getStringExtra("phoneno");
        mpin= getIntent().getStringExtra("mpin");
        name= getIntent().getStringExtra("name");
        bio = getIntent().getStringExtra("bio");
        switchCompat.setChecked(bio.equalsIgnoreCase("true"));

        changempin.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                Intent set = new Intent(settings.this, changeMpin.class);
                set.putExtra("phoneno",mobile);
                set.putExtra("mpin",mpin);
                set.putExtra("name",name);
                startActivity(set);
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    ShowPopupenable();
                } else {
                    ShowPopupdisable();
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("bio", bio);
                editor.apply();
                finish();
            }
        });
    }
    private void ShowPopupenable() {

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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                myDialog.dismiss();
                                enablebioswitch();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                } else {
                    interneterror();
                }


            }
        });
        txtclose.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v)  {

                myDialog.dismiss();

            }
        });
        skip.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }



    private void ShowPopupdisable() {

        TextView txtclose,skip;
        Button disablebio;

        myDialog.setContentView(R.layout.disable_biometric_popup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        skip =(TextView) myDialog.findViewById(R.id.skip);
        txtclose.setText("X");
        disablebio = (Button) myDialog.findViewById(R.id.enablebio);
        disablebio.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                disablebio.setVisibility(View.GONE);

                if (GlobalVars.isNetworkConnected) {

                    progressDialog.show(getSupportFragmentManager(), "tag");
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressflag = true;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                myDialog.dismiss();
                                disablebioswitch();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                } else {
                    interneterror();
                }

            }
        });
        txtclose.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v)  {

                myDialog.dismiss();

            }
        });
        skip.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v)  {

                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void enablebioswitch() {
        final String[] returnbio = {""};
        RequestQueue queue = Volley.newRequestQueue(this);


        String URL = ""+retrofit.baseUrl()+"loginpass?mobile=" + mobile + "";
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

                            new SweetAlertDialog(settings.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Basics Life LFO Portal")
                                    .setContentText("Biometric Enabled")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            progressflag = false;
                                            progressDialog.dismiss();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            bio="true";
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("hasLoggedIn", true);
                                            editor.putString("bio",bio);
                                            editor.commit();

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

    private void disablebioswitch() {
        final String[] returnbio = {""};

        String URL = ""+retrofit.baseUrl()+"disablebio?mobile=" + mobile + "";
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

                            new SweetAlertDialog(settings.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Basics Life LFO Portal")
                                    .setContentText("Biometric Disabled")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            progressflag = false;
                                            progressDialog.dismiss();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            bio="false";
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("hasLoggedIn", true);
                                            editor.putString("bio",bio);
                                            editor.commit();
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

    private void commonError() {
        new SweetAlertDialog(settings.this, SweetAlertDialog.ERROR_TYPE)
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

        new SweetAlertDialog(settings.this, SweetAlertDialog.WARNING_TYPE)
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