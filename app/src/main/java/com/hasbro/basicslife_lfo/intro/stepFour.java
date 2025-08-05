package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        binding.spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateHeaderText();
                loadAttendanceData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerStaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateHeaderText();
                loadAttendanceData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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

    private void updateHeaderText() {
        String selectedMonth = binding.spinnerMonth.getSelectedItem().toString();
        binding.txtSubheading.setText("Attendance for the month of " + selectedMonth);
    }

    private void loadAttendanceData() {
        int monthIndex = binding.spinnerMonth.getSelectedItemPosition();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -monthIndex);
        int selectedMonth = calendar.get(Calendar.MONTH) + 1;
        int selectedYear = calendar.get(Calendar.YEAR);

        int selectedStaffIndex = binding.spinnerStaff.getSelectedItemPosition();
        String selectedEmpId = "";
        try {
            JSONObject obj = staffJSONArray.getJSONObject(selectedStaffIndex);
            selectedEmpId = obj.optString("emp_id", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = retrofit.baseUrl() + "getAttendanceWithNsvQty?str_id=" + selectedStoreId +
                "&emp_id=" + selectedEmpId +
                "&month=" + selectedMonth +
                "&year=" + selectedYear;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        binding.attendanceContainer.removeAllViews();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                String date = obj.optString("date", "");
                                String day = obj.optString("dayname", "");
                                String qty = obj.optString("qty", "0");
                                String nsv = obj.optString("nsv", "0");
                                String attendance = obj.optString("attendance", "");

                                View row = getLayoutInflater().inflate(R.layout.attendance_row, null);
                                TextView txtDate = row.findViewById(R.id.txtDate);
                                TextView txtDay = row.findViewById(R.id.txtDay);
                                TextView txtQty = row.findViewById(R.id.txtQty);
                                TextView txtNsv = row.findViewById(R.id.txtNsv);
                                TextView txtPA = row.findViewById(R.id.txtPA);

                                txtDate.setText(date);
                                txtDay.setText(day);
                                txtQty.setText(qty);
                                txtNsv.setText(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(nsv)));
                                txtPA.setText(attendance);

                                binding.attendanceContainer.addView(row);
                            }

                            if (response.length() == 0) {
                                Toast.makeText(stepFour.this, "No data found for selected month/staff", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(stepFour.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(stepFour.this, "Error loading data", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}
