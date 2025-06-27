package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.adapter.EmployeeAdapter;
import com.hasbro.basicslife_lfo.databinding.LfoStaffViewBinding;
import com.hasbro.basicslife_lfo.pojo.Employee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class lfo_staff_view extends AppCompatActivity {
    private LfoStaffViewBinding binding;
    private String strcode,grname,storname,strid,tmcode,phoneno,user_name,bio,carea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoStaffViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize data
        initializeIntentData();
        // Fetch data using Volley
        fetchEmployeeData();

        setHeaderText();
    }
    private void fetchEmployeeData() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = ""+retrofit.baseUrl()+"getStaffDetail?strid=" + strid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        // Parse the JSON response and populate RecyclerView
                        List<Employee> employees = parseEmployeeData(response);
                        setupRecyclerView(employees);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching employee data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private List<Employee> parseEmployeeData(JSONObject response) throws JSONException {
        List<Employee> employees = new ArrayList<>();
        if (response.length() > 0) {
            try {

                Iterator<String> keys = response.keys();
               // System.out.println("keys" + keys);
                // Parse JSON Arrays
                while (keys.hasNext()) {
                    String key = keys.next(); // Get the current key

                    // Get the JSONArray corresponding to the key
                    JSONArray employeeArray = response.getJSONArray(key);

                    // Access elements in the JSONArray
                    String firstName = employeeArray.getString(0);
                    String empCode = employeeArray.getString(2);
                    employees.add(new Employee(firstName, empCode, employeeArray));
                    // String firstName = detailsArray.getString(0);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }else{
            norecordfound();
        }

//        JSONArray employeeArray = response.getJSONArray("employees"); // Ensure API returns an array
//        for (int i = 0; i < employeeArray.length(); i++) {
//            JSONObject employee = employeeArray.getJSONObject(i);
//            String firstName = employee.getString("first_name");
//            String empCode = employee.getString("empcode");
//
//            employees.add(new Employee(firstName, empCode, employee)); // Pass full object for details
//        }
        return employees;
    }
    private void setupRecyclerView(List<Employee> employees) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EmployeeAdapter adapter = new EmployeeAdapter(employees, this);
        recyclerView.setAdapter(adapter);
    }
    private void initializeIntentData() {
        // Fetch Intent Data

        strcode = getIntent().getStringExtra("strcode");
        grname = getIntent().getStringExtra("grname");
        storname = getIntent().getStringExtra("strname");
        strid = getIntent().getStringExtra("strid");
        tmcode = getIntent().getStringExtra("tmcode");
        user_name = getIntent().getStringExtra("name");
        phoneno = getIntent().getStringExtra("mobile");
        bio = getIntent().getStringExtra("bio");
        carea= getIntent().getStringExtra("carea");

    }
    private void setHeaderText() {

        SpannableString headerText = new SpannableString("STAFF DETAILS - " +storname);
        int startIndex = headerText.toString().indexOf(storname);
        int endIndex = startIndex + storname.length();
        headerText.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.headerTitle.setText(headerText);
    }
        private void norecordfound() {
        new SweetAlertDialog(lfo_staff_view.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("No Record Found")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        intent.putExtra("tmcode", tmcode);
                        intent.putExtra("mobile",phoneno);
                        intent.putExtra("name",user_name);
                        intent.putExtra("bio",bio);
                        startActivity(intent);
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }

}