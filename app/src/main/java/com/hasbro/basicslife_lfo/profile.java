package com.hasbro.basicslife_lfo;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

//import com.github.dhaval2404.imagepicker.ImagePicker;

public class profile extends AppCompatActivity {
    CardView card1, card2;
    String username, phone, empcode,tempempcode, email, dob,doj, address, aadhar;
    TextView user, phonenumber, emp_code, emailid, dobb,dojj, addr, aadh;


    @SuppressLint({"WrongConstant", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_profile);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);


        user = findViewById(R.id.user);
        phonenumber = findViewById(R.id.phone);
        emp_code = findViewById(R.id.empid);
        emailid = findViewById(R.id.email);
        dobb = findViewById(R.id.dob);
        addr = findViewById(R.id.address);
        dojj = findViewById(R.id.doj);
        aadh = findViewById(R.id.aadhar);
        card1 = findViewById(R.id.card1);


        username = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("mobile");
        empcode = getIntent().getStringExtra("emp_code");
        email = getIntent().getStringExtra("email");
        dob = getIntent().getStringExtra("dob");
        doj = getIntent().getStringExtra("doj");
        tempempcode = getIntent().getStringExtra("temp_empcode");
        aadhar = getIntent().getStringExtra("aadharno");
        address = getIntent().getStringExtra("street");


        user.setText(username);
        phonenumber.setText("+91-" + phone);
        if(empcode.equalsIgnoreCase("-")){
            emp_code.setText(tempempcode);
        }else {
            emp_code.setText(empcode);
        }
        if(email.equalsIgnoreCase("")){
            emailid.setText("-");
        }else {
            emailid.setText(email);
        }
        if(doj.equalsIgnoreCase("")){
            dojj.setText("-");
        }else {
            dojj.setText(doj);
        }
        if(dob.equalsIgnoreCase("")){
            dobb.setText("-");
        }else {
            dobb.setText(dob);
        }
        if(aadhar.equalsIgnoreCase("")){
            aadh.setText("-");
        }else {
            aadh.setText(aadhar);
        }if(address.equalsIgnoreCase("")){
            addr.setText("-");
        }else {
            addr.setText(address);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity

                    finish();

            }
        });
    }


    }












