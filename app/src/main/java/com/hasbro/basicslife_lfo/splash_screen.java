package com.hasbro.basicslife_lfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.hasbro.basicslife_lfo.freshLoginPage.PREFS_NAME;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the splash screen layout
        setContentView(R.layout.activity_splash_screen);

        // Configure window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Register network callback (ensure `CheckNetworkConnection` works on Android 10+)
        CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
        network.registerDefaultNetworkCallback();

        // Retrieve shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        String phoneno = settings.getString("mobile", null);
        String name = settings.getString("name", null);
        String bio = settings.getString("bio", null);

        // Debugging logs (can be removed later)
        System.out.println("User logged in: " + hasLoggedIn);
        System.out.println("Bio: " + bio);

        // Navigate after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (hasLoggedIn) {
                if (phoneno != null && !phoneno.isEmpty()) {
                    // User is logged in and phone number exists
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("mobile", phoneno);
                    intent.putExtra("name", name);
                    intent.putExtra("bio", bio);
                    startActivity(intent);
                    finish();
                } else {
                    // Missing phone number, suggest reinstall
                    Toast.makeText(getApplicationContext(), "Error: Please reinstall the app.", Toast.LENGTH_LONG).show();
                }
            } else {
                // Navigate to login screen
                Intent intent = new Intent(getApplicationContext(), freshLoginPage.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
