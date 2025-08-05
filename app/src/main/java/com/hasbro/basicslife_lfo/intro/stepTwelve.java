package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasbro.basicslife_lfo.adapter.GRNAdapter;
import com.hasbro.basicslife_lfo.databinding.SteptwelveBinding;
import com.hasbro.basicslife_lfo.pojo.GRNModel;

import java.util.ArrayList;
import java.util.HashMap;

public class stepTwelve extends AppCompatActivity {

    private SteptwelveBinding binding;

    // Intent data
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;
    private boolean entryPhotoCaptured;
    private ArrayList<String> hygieneResponses;
    private HashMap<String, String> onFloorData;

    private ArrayList<GRNModel> grnList;
    private GRNAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SteptwelveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeIntentData();
        setupRecyclerView();
        setupButtons();
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

    private void setupRecyclerView() {
        // Sample dummy data
        grnList = new ArrayList<>();
        grnList.add(new GRNModel("PO123", "INV001", "10", "LR01", "8", "2025-08-01", "GRN001"));
        grnList.add(new GRNModel("PO456", "INV002", "20", "LR02", "18", "2025-08-02", "GRN002"));
        grnList.add(new GRNModel("PO789", "INV003", "15", "LR03", "14", "2025-08-03", "GRN003"));

        adapter = new GRNAdapter(grnList);
        binding.recyclerGrnList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerGrnList.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnBackStep.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnNextStep.setOnClickListener(v -> {
            // Collect updated GRN data
            ArrayList<GRNModel> updatedGrnList = new ArrayList<>(adapter.getUpdatedGRNList());

            // Pass everything to stepThirteen
            Intent intent = new Intent(stepTwelve.this, stepThirteen.class);

            // Send previous data
            intent.putExtra("strId", strid);
            intent.putExtra("strname", storname);
            intent.putExtra("strcode", strcode);
            intent.putExtra("grname", grname);
            intent.putExtra("carea", carea);
            intent.putExtra("tmcode", tmcode);
            intent.putExtra("mobile", phoneno);
            intent.putExtra("name", user_name);
            intent.putExtra("bio", bio);
            intent.putExtra("sm_name", smName);
            intent.putExtra("sm_contact", smContact);
            intent.putExtra("sm_email", smEmail);
            intent.putExtra("dm_name", dmName);
            intent.putExtra("dm_contact", dmContact);
            intent.putExtra("dm_email", dmEmail);
            intent.putExtra("wh_name", whName);
            intent.putExtra("wh_contact", whContact);
            intent.putExtra("wh_email", whEmail);
            intent.putExtra("staff_list", staffListJson);
            intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);
            intent.putExtra("hygiene_csv", hygieneResponses);
            intent.putExtra("onFloorData", onFloorData);

            // Pass GRN list
            intent.putExtra("grnList", updatedGrnList);  // <-- Make sure GRNModel implements Serializable or Parcelable

            startActivity(intent);
        });
    }

}
