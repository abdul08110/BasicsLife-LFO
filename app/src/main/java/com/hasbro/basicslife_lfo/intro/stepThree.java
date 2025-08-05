package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.MainActivity;
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.SafeClikcListener;
import com.hasbro.basicslife_lfo.databinding.StepthreeBinding;
import com.hasbro.basicslife_lfo.databinding.HygieneCheckRowBinding;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class stepThree extends AppCompatActivity {

    private StepthreeBinding binding;
    private List<String> hygieneResponses = new ArrayList<>();

    private String selectedStoreName, selectedStoreCode, selectedGroupName, selectedStrId, selectedCarpetarea;
    private String tmcode, phoneno, user_name, bio, staff_list;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;

    private final String[] hygieneChecks = {
            "UNIFORM", "GROOMING", "DSR/SOH BOOK",
            "ALTERNATE BOOK", "SALES UPDATE", "STOCK UPDATE",
            "ATT. UPDATE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StepthreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent Extras from stepTwo
        InitializeIntentData();

        loadHygieneChecks();

        binding.btnNextStep.setOnClickListener(v -> proceedToStepFour());

        binding.btnBackStep.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException, IOException {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void InitializeIntentData() {
        selectedStoreName = getIntent().getStringExtra("strname");
        selectedStoreCode = getIntent().getStringExtra("strcode");
        selectedGroupName = getIntent().getStringExtra("grname");
        selectedStrId = getIntent().getStringExtra("strId");
        selectedCarpetarea = getIntent().getStringExtra("carea");
        staff_list = getIntent().getStringExtra("staff_list");
        tmcode = getIntent().getStringExtra("tmcode");
        phoneno = getIntent().getStringExtra("mobile");
        user_name = getIntent().getStringExtra("name");
        bio = getIntent().getStringExtra("bio");

        smName = getIntent().getStringExtra("sm_name");
        smContact = getIntent().getStringExtra("sm_contact");
        smEmail = getIntent().getStringExtra("sm_email");

        dmName = getIntent().getStringExtra("dm_name");
        dmContact = getIntent().getStringExtra("dm_contact");
        dmEmail = getIntent().getStringExtra("dm_email");

        whName = getIntent().getStringExtra("wh_name");
        whContact = getIntent().getStringExtra("wh_contact");
        whEmail = getIntent().getStringExtra("wh_email");
    }

    private void loadHygieneChecks() {
        LayoutInflater inflater = LayoutInflater.from(this);
        hygieneResponses.clear();

        for (String check : hygieneChecks) {
            HygieneCheckRowBinding rowBinding = HygieneCheckRowBinding.inflate(inflater, binding.hygieneContainer, false);
            rowBinding.txtHygieneLabel.setText(check);
            binding.hygieneContainer.addView(rowBinding.getRoot());

            hygieneResponses.add("");
            int index = hygieneResponses.size() - 1;

            rowBinding.radioYes.setOnClickListener(v -> {
                rowBinding.radioYes.setChecked(true);
                rowBinding.radioNo.setChecked(false);
                rowBinding.radioNA.setChecked(false);
                hygieneResponses.set(index, "YES");
            });

            rowBinding.radioNo.setOnClickListener(v -> {
                rowBinding.radioYes.setChecked(false);
                rowBinding.radioNo.setChecked(true);
                rowBinding.radioNA.setChecked(false);
                hygieneResponses.set(index, "NO");
            });

            rowBinding.radioNA.setOnClickListener(v -> {
                rowBinding.radioYes.setChecked(false);
                rowBinding.radioNo.setChecked(false);
                rowBinding.radioNA.setChecked(true);
                hygieneResponses.set(index, "NA");
            });
        }
    }

    private void proceedToStepFour() {
        boolean allAnswered = true;
        for (String response : hygieneResponses) {
            if (response == null || response.trim().isEmpty()) {
                allAnswered = false;
                break;
            }
        }

        if (!allAnswered) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Incomplete")
                    .setContentText("Please answer all hygiene checks before proceeding.")
                    .setConfirmText("OK")
                    .show();
            return;
        }

        Intent intent = new Intent(this, stepFour.class);
        intent.putExtra("strname", selectedStoreName);
        intent.putExtra("strcode", selectedStoreCode);
        intent.putExtra("grname", selectedGroupName);
        intent.putExtra("strId", selectedStrId);
        intent.putExtra("carea", selectedCarpetarea);
        intent.putExtra("staff_list", staff_list);
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

        intent.putStringArrayListExtra("hygiene_csv", new ArrayList<>(hygieneResponses));
        startActivity(intent);
    }
}
