package com.hasbro.basicslife_lfo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.sql.SQLException;
import java.text.ParseException;

public class contactSupport extends AppCompatActivity {
RelativeLayout connum,email;
    private static final int MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555;

    private static final String LOG_TAG = "AndroidExample";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.contact_support);

        connum=findViewById(R.id.r1);
        email=findViewById(R.id.r2);

        connum.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "18004194270"));
                try {
                    startActivity(callIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
              //  callIntent.setData(Uri.parse("tel:18004194270"));

                if (ActivityCompat.checkSelfPermission(contactSupport.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        email.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {

                try {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "support@basicslife.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                    intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                    startActivity(intent);
                } catch (ActivityNotFoundException e){
                    System.out.println(e);
                }


            }
        });
    }

}