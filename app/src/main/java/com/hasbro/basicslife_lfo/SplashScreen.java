package com.hasbro.basicslife_lfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Register network callback
        CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
        network.registerDefaultNetworkCallback();

        // Retrieve shared preferences
        SharedPreferences settings = getSharedPreferences(FreshLoginPage.PREFS_NAME, MODE_PRIVATE);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        String mobile = settings.getString("mobile", null);
        String name = settings.getString("name", null);
        String bio = settings.getString("bio", null);

        // Navigate after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (hasLoggedIn && mobile != null) {
                intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("name", name);
                intent.putExtra("bio", bio);
            } else {
                intent = new Intent(SplashScreen.this, FreshLoginPage.class);
            }
            startActivity(intent);
            finish();

        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister network callback if applicable
    }
}
