package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.databinding.StepsevenBinding;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class stepSeven extends AppCompatActivity {

    private StepsevenBinding binding;
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private ArrayList<String> hygieneResponses;
    private HashMap<String, String> onFloorData = new HashMap<>();
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;
    private boolean entryPhotoCaptured;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StepsevenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeIntentData();

        // Buttons
        binding.btnBackStep.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                goToStepEight();
            }
        });
    }

    private void initializeIntentData() {
        // Receive StepSix data
        Intent intent = getIntent();
        strcode = intent.getStringExtra("strcode");
        grname = intent.getStringExtra("grname");
        storname = intent.getStringExtra("strname");
        strid = intent.getStringExtra("strId");
        tmcode = intent.getStringExtra("tmcode");
        phoneno = intent.getStringExtra("mobile");
        user_name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        carea = intent.getStringExtra("carea");

        smName = intent.getStringExtra("sm_name");
        smContact = intent.getStringExtra("sm_contact");
        smEmail = intent.getStringExtra("sm_email");

        dmName = intent.getStringExtra("dm_name");
        dmContact = intent.getStringExtra("dm_contact");
        dmEmail = intent.getStringExtra("dm_email");

        whName = intent.getStringExtra("wh_name");
        whContact = intent.getStringExtra("wh_contact");
        whEmail = intent.getStringExtra("wh_email");

        staffListJson = intent.getStringExtra("staff_list");

        entryPhotoCaptured = intent.getBooleanExtra("entryPhotoCaptured", false);
        hygieneResponses = intent.getStringArrayListExtra("hygiene_csv");
        onFloorData = (HashMap<String, String>) intent.getSerializableExtra("onFloorData");
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

    private void goToStepEight() {
        // Add current inputs to onFloorData
        onFloorData.put("onFloorCHK", binding.onFloorCHK.getText().toString().trim());
        onFloorData.put("onFloorSOL", binding.onFloorSOL.getText().toString().trim());
        onFloorData.put("onFloorPRI", binding.onFloorPRI.getText().toString().trim());
        onFloorData.put("onFloorSTR", binding.onFloorSTR.getText().toString().trim());
        onFloorData.put("onFloorTAP", binding.onFloorTAP.getText().toString().trim());
        onFloorData.put("onFloorSKI", binding.onFloorSKI.getText().toString().trim());

        Intent intent = new Intent(stepSeven.this, stepEight.class);

        // Basic
        intent.putExtra("strId", strid);
        intent.putExtra("strname", storname);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("carea", carea);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", user_name);
        intent.putExtra("bio", bio);

        // SM
        intent.putExtra("sm_name", smName);
        intent.putExtra("sm_contact", smContact);
        intent.putExtra("sm_email", smEmail);

        // DM
        intent.putExtra("dm_name", dmName);
        intent.putExtra("dm_contact", dmContact);
        intent.putExtra("dm_email", dmEmail);

        // WH
        intent.putExtra("wh_name", whName);
        intent.putExtra("wh_contact", whContact);
        intent.putExtra("wh_email", whEmail);

        // Staff and assessment
        intent.putExtra("staff_list", staffListJson);
        intent.putExtra("hygiene_csv", hygieneResponses);
        intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);
        intent.putExtra("onFloorData", onFloorData);

        startActivity(intent);
    }

}
