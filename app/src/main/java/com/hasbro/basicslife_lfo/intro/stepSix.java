package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.databinding.StepsixBinding;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class stepSix extends AppCompatActivity {

    private StepsixBinding binding;
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private ArrayList<String> hygieneResponses;
    private boolean entryPhotoCaptured;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StepsixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive data from StepFive
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

        hygieneResponses = intent.getStringArrayListExtra("hygiene_csv");
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
        Intent intent = new Intent(stepSix.this, stepSeven.class);

        // Basic info
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

        // Staff JSON
        intent.putExtra("staff_list", staffListJson);

        // Hygiene & Photo
        intent.putStringArrayListExtra("hygiene_csv", hygieneResponses);
        intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);

        // On-Floor Data
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
