package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.databinding.Step4Binding;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class step4 extends AppCompatActivity {

    private Step4Binding binding;
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private ArrayList<String> hygieneResponses;
    private boolean entryPhotoCaptured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Step4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive data from Step3
        initializeIntentData();

        // Button: Next → Step5
        binding.btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                goToStep5();
            }
        });

        // Button: ← Back
        binding.btnBackStep.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        strcode = intent.getStringExtra("strcode");
        grname = intent.getStringExtra("grname");
        storname = intent.getStringExtra("strname");
        strid = intent.getStringExtra("strid");
        tmcode = intent.getStringExtra("tmcode");
        phoneno = intent.getStringExtra("mobile");
        user_name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        carea = intent.getStringExtra("carea");
        hygieneResponses = intent.getStringArrayListExtra("hygieneResponses");
        entryPhotoCaptured = intent.getBooleanExtra("entryPhotoCaptured", false);
    }

    private boolean validateInputs() {
        EditText[] fields = {
                binding.onFloorBSH, binding.onFloorBTR, binding.onFloorBTS,
                binding.onFloorBJN, binding.onFloorPRO, binding.onFloorOTH
        };

        for (EditText field : fields) {
            if (TextUtils.isEmpty(field.getText().toString().trim())) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("On-Floor Required")
                        .setContentText("Please Fill All On-Floor Values")
                        .setConfirmText("OK")
                        .show();
                return false;
            }
        }
        return true;
    }

    private void goToStep5() {
        Intent intent = new Intent(step4.this, step5.class);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("strname", storname);
        intent.putExtra("strid", strid);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", user_name);
        intent.putExtra("bio", bio);
        intent.putExtra("carea", carea);
        intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);
        intent.putStringArrayListExtra("hygieneResponses", hygieneResponses);

        // Bundle on-floor data into HashMap
        HashMap<String, String> onFloorData = new HashMap<>();
        onFloorData.put("onFloorBSH", binding.onFloorBSH.getText().toString().trim());
        onFloorData.put("onFloorBTR", binding.onFloorBTR.getText().toString().trim());
        onFloorData.put("onFloorBTS", binding.onFloorBTS.getText().toString().trim());
        onFloorData.put("onFloorBJN", binding.onFloorBJN.getText().toString().trim());
        onFloorData.put("onFloorPRO", binding.onFloorPRO.getText().toString().trim());
        onFloorData.put("onFloorOTH", binding.onFloorOTH.getText().toString().trim());
        intent.putExtra("onFloorData", onFloorData);

        startActivity(intent);
    }
}
