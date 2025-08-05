package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.databinding.SteptenBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class stepTen extends AppCompatActivity {

    private SteptenBinding binding;

    // Intent data
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;
    private boolean entryPhotoCaptured;
    private ArrayList<String> hygieneResponses;
    private HashMap<String, String> onFloorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SteptenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeIntentData();
        setupButtonListeners();
    }

    private void initializeIntentData() {
        Intent intent = getIntent();

        strid = intent.getStringExtra("strId");
        storname = intent.getStringExtra("strname");
        strcode = intent.getStringExtra("strcode");
        grname = intent.getStringExtra("grname");
        carea = intent.getStringExtra("carea");
        tmcode = intent.getStringExtra("tmcode");
        phoneno = intent.getStringExtra("mobile");
        user_name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");

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

    private void setupButtonListeners() {
        binding.btnBackStep.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnNextStep.setOnClickListener(v -> goToStepEleven());
    }

    private void goToStepEleven() {
        Intent intent = new Intent(stepTen.this, stepEleven.class);

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

        // Staff and Assessment
        intent.putExtra("staff_list", staffListJson);
        intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);
        intent.putExtra("hygiene_csv", hygieneResponses);
        intent.putExtra("onFloorData", onFloorData);

        startActivity(intent);
    }
}
