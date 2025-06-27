package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.databinding.Step5Binding;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class step5 extends AppCompatActivity {

    private Step5Binding binding;
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private ArrayList<String> hygieneResponses;
    private HashMap<String, String> onFloorData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Step5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive Step4 data
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
        onFloorData = (HashMap<String, String>) intent.getSerializableExtra("onFloorData");

        // Buttons
        binding.btnBackStep.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                goToStep6();
            }
        });
    }

    private boolean validateInputs() {
        EditText[] fields = {
                binding.onFloorCHK, binding.onFloorSOL, binding.onFloorPRI, binding.onFloorSTR,
                binding.onFloorTAP, binding.onFloorSKI
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

    private void goToStep6() {
        // Add Step5 inputs to onFloorData
        onFloorData.put("onFloorCHK", binding.onFloorCHK.getText().toString().trim());
        onFloorData.put("onFloorSOL", binding.onFloorSOL.getText().toString().trim());
        onFloorData.put("onFloorPRI", binding.onFloorPRI.getText().toString().trim());
        onFloorData.put("onFloorSTR", binding.onFloorSTR.getText().toString().trim());
        onFloorData.put("onFloorTAP", binding.onFloorTAP.getText().toString().trim());
        onFloorData.put("onFloorSKI", binding.onFloorSKI.getText().toString().trim());

        Intent step6Intent = new Intent(step5.this, step6.class);
        step6Intent.putExtra("strcode", strcode);
        step6Intent.putExtra("grname", grname);
        step6Intent.putExtra("strname", storname);
        step6Intent.putExtra("strid", strid);
        step6Intent.putExtra("tmcode", tmcode);
        step6Intent.putExtra("mobile", phoneno);
        step6Intent.putExtra("name", user_name);
        step6Intent.putExtra("bio", bio);
        step6Intent.putExtra("carea", carea);
        step6Intent.putExtra("hygieneResponses", hygieneResponses);
        step6Intent.putExtra("onFloorData", onFloorData);

        startActivity(step6Intent);
    }
}
