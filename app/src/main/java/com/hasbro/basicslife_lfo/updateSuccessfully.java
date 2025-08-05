package com.hasbro.basicslife_lfo;


import static com.hasbro.basicslife_lfo.FreshLoginPage.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class updateSuccessfully extends AppCompatActivity {
    TextView login;
    String phoneno,name,bio,tmcode;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_update_successfully);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // login=findViewById(R.id.launchAuthentication);
                phoneno = getIntent().getExtras().getString("mobile");
                name = getIntent().getExtras().getString("name");
                bio = getIntent().getExtras().getString("bio");
                tmcode= getIntent().getExtras().getString("tmcode");
                //System.out.println("phoneno: " + phoneno);
               // System.out.println("name update: " + name);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("hasLoggedIn", true);
                editor.putString("mobile",phoneno);
                editor.putString("name",name);
                editor.putString("bio",bio);
                editor.putString("tmcode",tmcode);
                editor.commit();
                Intent intent=new Intent(updateSuccessfully.this,MainActivity.class);
                intent.putExtra("mobile",phoneno);
                intent.putExtra("name",name);
                intent.putExtra("bio",bio);
                intent.putExtra("tmcode",tmcode);
                startActivity(intent);
                finish();
            }
        },2000);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity

            }
        });

    }

}