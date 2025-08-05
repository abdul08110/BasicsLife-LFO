package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.FreshLoginPage.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class compSaleRecordSaved extends AppCompatActivity {

    String phoneno,user_name,bio,tmcode;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
        network.registerDefaultNetworkCallback();
        setContentView(R.layout.comp_sale_record_saved);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // login=findViewById(R.id.launchAuthentication);
                user_name = getIntent().getStringExtra("name");
                phoneno = getIntent().getStringExtra("mobile");
                bio = getIntent().getStringExtra("bio");
                tmcode = getIntent().getStringExtra("tmcode");

                Intent intent=new Intent(compSaleRecordSaved.this,Welcome.class);
                intent.putExtra("mobile",phoneno);
                intent.putExtra("name",user_name);
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