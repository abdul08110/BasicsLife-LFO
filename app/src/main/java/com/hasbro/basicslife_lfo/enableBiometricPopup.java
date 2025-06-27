package com.hasbro.basicslife_lfo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class enableBiometricPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.enable_biometric_popup);
    }
}