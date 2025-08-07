package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.SafeClikcListener;
import com.hasbro.basicslife_lfo.databinding.StepfourBinding;
import com.hasbro.basicslife_lfo.geturl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;

public class stepFour extends AppCompatActivity {

    private StepfourBinding binding;
    private List<String> monthList = new ArrayList<>();
    private List<String> staffNameList = new ArrayList<>();
    private JSONArray staffJSONArray;
    private String selectedStoreId;
    private String strName, strCode, grName, carpetArea, tmcode, phone, username, bio;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;
    private ArrayList<String> hygieneResponses;

    Retrofit retrofit = geturl.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StepfourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get all intent extras from stepThree
        InitializeIntentData();

        parseStaffList(staffListJson);
        setupMonthSpinner();
        setupStaffSpinner();

        binding.spinnerMonth.setOnItemClickListener((parent, view, position, id) -> loadAttendanceData());
       binding.spinnerStaff.setOnItemClickListener((parent, view, position, id) -> loadAttendanceData());

        binding.btnNextStep.setOnClickListener(v -> proceedToStepFive());

        binding.btnBackStep.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException, IOException {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void InitializeIntentData() {
        selectedStoreId = getIntent().getStringExtra("strId");
        strName = getIntent().getStringExtra("strname");
        strCode = getIntent().getStringExtra("strcode");
        grName = getIntent().getStringExtra("grname");
        carpetArea = getIntent().getStringExtra("carea");
        tmcode = getIntent().getStringExtra("tmcode");
        phone = getIntent().getStringExtra("mobile");
        username = getIntent().getStringExtra("name");
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

        staffListJson = getIntent().getStringExtra("staff_list");
        hygieneResponses = getIntent().getStringArrayListExtra("hygiene_csv");
    }

    private void proceedToStepFive() {
        Intent intent = new Intent(this, stepFive.class);

        intent.putExtra("strId", selectedStoreId);
        intent.putExtra("strname", strName);
        intent.putExtra("strcode", strCode);
        intent.putExtra("grname", grName);
        intent.putExtra("carea", carpetArea);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phone);
        intent.putExtra("name", username);
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
        intent.putStringArrayListExtra("hygiene_csv", hygieneResponses); // pass hygiene data to step 5

        startActivity(intent);
    }

    private void parseStaffList(String staffJson) {
        staffNameList.clear();
        try {
            staffJSONArray = new JSONArray(staffJson);
            for (int i = 0; i < staffJSONArray.length(); i++) {
                JSONObject obj = staffJSONArray.getJSONObject(i);
                String name = obj.optString("emp_name", "").trim();
                if (!name.isEmpty()) {
                    staffNameList.add(name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupMonthSpinner() {
        monthList.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 6; i++) {
            monthList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -1);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monthList);
        binding.spinnerMonth.setAdapter(monthAdapter);
    }

    private void setupStaffSpinner() {
        ArrayAdapter<String> staffAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, staffNameList);
        binding.spinnerStaff.setAdapter(staffAdapter);
    }

    private void loadAttendanceData() {

            Log.d("DEBUG", "loadAttendanceData() called"); // Add this at the very start

            // Rest of your method...

        // 1. Validate month selection
        String selectedMonthText = binding.spinnerMonth.getText().toString().trim();


        if (selectedMonthText.isEmpty()) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validate staff selection
        String selectedStaffName = binding.spinnerStaff.getText().toString().trim();
        if (selectedStaffName.isEmpty()) {
            Toast.makeText(this, "Please select a staff member", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Parse month and year from selection
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(selectedMonthText));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing month selection", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedMonth = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based
        int selectedYear = calendar.get(Calendar.YEAR);

        Log.d("DEBUG", "Selected Month: " + selectedMonth + ", Year: " + selectedYear);


        // 4. Find the selected staff's ID
        String selectedEmpId = "";
        try {
            if (staffJSONArray != null) {
                for (int i = 0; i < staffJSONArray.length(); i++) {
                    JSONObject obj = staffJSONArray.getJSONObject(i);
                    String staffName = obj.optString("emp_name", "").trim();
                    if (staffName.equals(selectedStaffName)) {
                        selectedEmpId = obj.optString("emp_id", "");
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing staff data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedEmpId.isEmpty()) {
            Toast.makeText(this, "Could not find ID for selected staff", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DEBUG", "Selected Staff Name: " + selectedStaffName + ", Emp ID: " + selectedEmpId);

        // 5. Build the request URL
        String url = retrofit.baseUrl() + "getAttendanceWithNsvQty?str_id=" + selectedStoreId +
                "&emp_id=" + selectedEmpId +
                "&month=" + selectedMonth +
                "&year=" + selectedYear;
        Log.d("DEBUG", "Final URL: " + url);

        // 6. Make the network request
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> handleAttendanceResponse(response),
                error -> handleAttendanceError(error)
        );

        queue.add(request);
    }

    private void handleAttendanceResponse(JSONArray response) {
        binding.attendanceContainer.removeAllViews();
        Log.d("DEBUG", "Response Length: " + response.length());
        Log.d("DEBUG", "Response: " + response.toString());
        if (response.length() == 0) {
            Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Calculate summary stats
            int presentDays = 0;
            int weekOffDays = 0;
            int absentDays = 0;
            int earnedLeaveDays = 0;
            int halfDays = 0;

            double totalQty = 0;
            double totalNsv = 0;

            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                String date = obj.optString("date", "");
                String day = obj.optString("dayname", "");
                String qtyStr = obj.optString("qty", "0");
                String nsvStr = obj.optString("nsv", "0");
                String attendance = obj.optString("attendance", "");

                // Parse numbers safely
                double qty = 0;
                double nsv = 0;
                try {
                    qty = Double.parseDouble(qtyStr);
                    nsv = Double.parseDouble(nsvStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // Update summary stats
                totalQty += qty;
                totalNsv += nsv;

                switch (attendance.toUpperCase()) {
                    case "P":
                        presentDays++;
                        break;
                    case "W":
                        weekOffDays++;
                        break;
                    case "A":
                        absentDays++;
                        break;
                    case "E":
                        earnedLeaveDays++;
                        break;
                    case "H":
                        halfDays++;
                        break;
                }


                // Inflate and populate row
                View row = getLayoutInflater().inflate(R.layout.attendance_row, binding.attendanceContainer, false);


                TextView txtDate = row.findViewById(R.id.txtDate);
                TextView txtDay = row.findViewById(R.id.txtDay);
                TextView txtQty = row.findViewById(R.id.txtQty);
                TextView txtNsv = row.findViewById(R.id.txtNsv);
                TextView txtPA = row.findViewById(R.id.txtPA);

                txtDate.setText(date);
                txtDay.setText(day);
                txtQty.setText(String.format(Locale.ENGLISH, "%.0f", qty));
                txtNsv.setText(String.format(Locale.ENGLISH, "%.2f", nsv));
                txtPA.setText(attendance);

                // Set color based on attendance status
                switch (attendance.toUpperCase()) {
                    case "P":
                        txtPA.setBackgroundResource(R.drawable.status_present);
                        txtPA.setTextColor(ContextCompat.getColor(this, R.color.green_dark));
                        break;
                    case "W":
                        txtPA.setBackgroundResource(R.drawable.status_weekoff);
                        txtPA.setTextColor(ContextCompat.getColor(this, R.color.blue_grey_dark));
                        break;
                    case "A":
                        txtPA.setBackgroundResource(R.drawable.status_absent);
                        txtPA.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
                        break;
                    case "E":
                        txtPA.setBackgroundResource(R.drawable.status_earned_leave);
                        txtPA.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
                        break;
                    case "H":
                        txtPA.setBackgroundResource(R.drawable.status_halfday);
                        txtPA.setTextColor(ContextCompat.getColor(this, R.color.brown));
                        break;
                }


                binding.attendanceContainer.addView(row);
            }

            // Update summary UI
            binding.txtPresentDays.setText(String.valueOf(presentDays));
            binding.txtAbsentDays.setText(String.valueOf(absentDays));
            binding.txtEarnedLeaveDays.setText(String.valueOf(earnedLeaveDays));
            binding.txtWeekOffDays.setText(String.valueOf(weekOffDays));
            binding.txtHalfDay.setText(String.valueOf(halfDays));

            // Update totals
            binding.txtTotalQty.setText(String.format(Locale.ENGLISH, "%.0f", totalQty));
            binding.txtTotalNsv.setText(String.format(Locale.ENGLISH, "%.2f", totalNsv));


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing attendance data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAttendanceError(VolleyError error) {
        error.printStackTrace();
        if (error.networkResponse != null) {
            Toast.makeText(this,
                    "Server error: " + error.networkResponse.statusCode,
                    Toast.LENGTH_SHORT).show();
        } else if (error.getMessage() != null) {
            Toast.makeText(this,
                    "Network error: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Unknown error loading attendance data",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
