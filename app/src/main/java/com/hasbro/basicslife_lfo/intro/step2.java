package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.MainActivity;
import com.hasbro.basicslife_lfo.R;


import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class step2 extends AppCompatActivity {

    private LinearLayout hygieneContainer, attendanceContainer;
    private Button btnProceed;

    private String selectedStoreName, selectedStoreCode, selectedGroupName, selectedStrId, selectedCarpetarea;
    private String tmcode, phoneno, user_name, bio;

    private final String[] hygieneChecks = {
            "UNIFORM", "GROOMING", "DSR/SOH BOOK",
            "ALTERNATE BOOK", "SALES UPDATE", "STOCK UPDATE",
            "ATT. UPDATE"
    };

    private final String[] pastMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"}; // Placeholder
    private final String[] leavePercent = {"5%", "3%", "6%", "4%", "7%", "2%"};    // Dummy values
    private final String[] attendancePercent = {"95%", "97%", "94%", "96%", "93%", "98%"};

    private List<String> hygieneResponses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step2);

        hygieneContainer = findViewById(R.id.hygieneContainer);
        attendanceContainer = findViewById(R.id.attendanceContainer);
        btnProceed = findViewById(R.id.btnProceedStep3);

        // Get Intent Extras from step1
        selectedStoreName = getIntent().getStringExtra("strname");
        selectedStoreCode = getIntent().getStringExtra("strcode");
        selectedGroupName = getIntent().getStringExtra("grname");
        selectedStrId = getIntent().getStringExtra("strid");
        selectedCarpetarea = getIntent().getStringExtra("carea");
        tmcode = getIntent().getStringExtra("tmcode");
        phoneno = getIntent().getStringExtra("mobile");
        user_name = getIntent().getStringExtra("name");
        bio = getIntent().getStringExtra("bio");
        //hygieneResponses = getIntent().getStringArrayListExtra("hygiene_csv");

        loadHygieneChecks();
        loadAttendanceStats();

        btnProceed.setOnClickListener(v -> proceedToStep3());
        // Handle Back Press
        setupBackPressedHandler();
    }

    private void loadHygieneChecks() {
        LayoutInflater inflater = LayoutInflater.from(this);
        hygieneResponses.clear();  // Reset list

        for (String check : hygieneChecks) {
            View row = inflater.inflate(R.layout.hygiene_check_row, hygieneContainer, false);

            TextView label = row.findViewById(R.id.txtHygieneLabel);
            RadioButton radioYes = row.findViewById(R.id.radioYes);
            RadioButton radioNo = row.findViewById(R.id.radioNo);
            RadioButton radioNA = row.findViewById(R.id.radioNA);

            label.setText(check);
            hygieneContainer.addView(row);
            hygieneResponses.add("NA"); // default

            int index = hygieneResponses.size() - 1;

            // Manually handle exclusive selection
            radioYes.setOnClickListener(v -> {
                radioYes.setChecked(true);
                radioNo.setChecked(false);
                radioNA.setChecked(false);
                hygieneResponses.set(index, "YES");
            });

            radioNo.setOnClickListener(v -> {
                radioYes.setChecked(false);
                radioNo.setChecked(true);
                radioNA.setChecked(false);
                hygieneResponses.set(index, "NO");
            });

            radioNA.setOnClickListener(v -> {
                radioYes.setChecked(false);
                radioNo.setChecked(false);
                radioNA.setChecked(true);
                hygieneResponses.set(index, "NA");
            });
        }
    }


    private void loadAttendanceStats() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < pastMonths.length; i++) {
            View row = inflater.inflate(R.layout.attendance_row, attendanceContainer, false);
            TextView month = row.findViewById(R.id.txtMonth);
            TextView leave = row.findViewById(R.id.txtLeave);
            TextView attend = row.findViewById(R.id.txtAttendance);

            month.setText(pastMonths[i]);
            leave.setText(leavePercent[i]);
            attend.setText(attendancePercent[i]);

            attendanceContainer.addView(row);
        }
    }

    private void proceedToStep3() {
        boolean allAnswered = true;
        for (String response : hygieneResponses) {
            if (response.equals("NA")) {
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
        Intent intent = new Intent(this, step3.class);
        intent.putExtra("strname", selectedStoreName);
        intent.putExtra("strcode", selectedStoreCode);
        intent.putExtra("grname", selectedGroupName);
        intent.putExtra("strid", selectedStrId);
        intent.putExtra("carea", selectedCarpetarea);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", user_name);
        intent.putExtra("bio", bio);

        // Pass hygiene responses as CSV string
        intent.putStringArrayListExtra("hygiene_csv", new ArrayList<>(hygieneResponses));

        startActivity(intent);
    }
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                logout();
            }
        });
    }
    private void logout() {

        new SweetAlertDialog(step2.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Sure You Want To Logout...")
                .setConfirmText("Stay")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent it = new Intent(step2.this, MainActivity.class);
                        it.putExtra("mobile", phoneno);
                        it.putExtra("name", user_name);
                        it.putExtra("bio", bio);
                        startActivity(it);
                    }
                })
                .show();
    }
}