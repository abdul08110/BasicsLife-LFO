package com.hasbro.basicslife_lfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class passwordUpdated extends AppCompatActivity {
TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_password_updated);
        login=findViewById(R.id.launchAuthentication);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}