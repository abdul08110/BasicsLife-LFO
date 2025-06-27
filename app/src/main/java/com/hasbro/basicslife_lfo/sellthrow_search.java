package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.databinding.ActivityLfoSalesViewBinding;
import com.hasbro.basicslife_lfo.databinding.LfoCounterPhotoBinding;
import com.hasbro.basicslife_lfo.databinding.SellthrowSearchBinding;

import org.json.JSONObject;

public class sellthrow_search extends AppCompatActivity {
    private SellthrowSearchBinding binding;
    private RequestQueue mRequestQueue;
    private String strcode,grname,storname,strid,tmcode,phoneno,user_name,bio,carea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SellthrowSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRequestQueue = Volley.newRequestQueue(this);
        // Fetch data from API
        initializeIntentData();
        fetchDropdownData();
        // Set Search Button Click Listener
        binding.buttonSearch.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException, IOException {
                validateAndSearch();
            }
        });


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
        // Pass fetched data to helper methods
    }

    private void fetchDropdownData() {
        String API_URL = retrofit.baseUrl() + "getDropDownSellThrough";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Populate spinners while storing both codes and names
                            populateSpinnerWithCode(binding.spinnerBrand, response.getJSONArray("brands"), brandMap);
                            populateSpinnerWithCode(binding.spinnerProduct, response.getJSONArray("products"), productMap);
                            populateSpinnerWithCode(binding.spinnerFit, response.getJSONArray("fits"), fitMap);

                            // For season and design (no code needed)
                            populateSpinner(binding.spinnerSeason, response.getJSONArray("seasons"));
                            populateSpinner(binding.spinnerDesign, response.getJSONArray("designs"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(sellthrow_search.this, "Data parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(sellthrow_search.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add request to queue
        mRequestQueue.add(request);
    }

    private void populateSpinner(Spinner spinner, JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        list.add("Select an Option"); // Default

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i)); // Only name
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
    }
    // Maps to store codes for each dropdown
    private HashMap<String, String> brandMap = new HashMap<>();
    private HashMap<String, String> productMap = new HashMap<>();
    private HashMap<String, String> fitMap = new HashMap<>();
    private void populateSpinnerWithCode(Spinner spinner, JSONArray jsonArray, HashMap<String, String> map) throws JSONException {
        List<String> nameList = new ArrayList<>();
        nameList.add("Select an Option"); // Default

        for (int i = 0; i < jsonArray.length(); i++) {
            String item = jsonArray.getString(i);
            String[] parts = item.split(":"); // Split "code:name"

            if (parts.length == 2) {
                String code = parts[0];
                String name = parts[1];

                nameList.add(name);  // Show only name in spinner
                map.put(name, code); // Store mapping
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nameList);
        spinner.setAdapter(adapter);
    }

    private void validateAndSearch() {
        String selectedSeason = binding.spinnerSeason.getSelectedItem().toString();

        if (selectedSeason.equals("Select an Option")) {
            Toast.makeText(this, "Season is required!", Toast.LENGTH_SHORT).show();
            return; // Stop execution if Season is not selected
        }

        // Get selected name from the spinners
        String selectedBrandName = binding.spinnerBrand.getSelectedItem().toString();
        String selectedProductName = binding.spinnerProduct.getSelectedItem().toString();
        String selectedFitName = binding.spinnerFit.getSelectedItem().toString();
        String selectedDesign = binding.spinnerDesign.getSelectedItem().toString();

        // Retrieve corresponding codes
        String selectedBrandCode = brandMap.get(selectedBrandName);
        String selectedProductCode = productMap.get(selectedProductName);
        String selectedFitCode = fitMap.get(selectedFitName);
        String selectedItemcode = binding.textItemcode.getText().toString();

        // Pass data to next activity
        Intent intent = new Intent(this, sellthrow_view.class);
        intent.putExtra("season", selectedSeason);
        if (!selectedDesign.equalsIgnoreCase("Select an Option")) {
            intent.putExtra("design", selectedDesign);
        }
        if (selectedBrandCode != null) intent.putExtra("brandCode", selectedBrandCode);
        if (selectedProductCode != null) intent.putExtra("productCode", selectedProductCode);
        if (selectedFitCode != null) intent.putExtra("fitCode", selectedFitCode);
        if (!selectedItemcode.isEmpty()) {
            intent.putExtra("itemcode", selectedItemcode);
        }
        intent.putExtra("strid", strid);
        System.out.println("selectedDesign" +selectedDesign);
        startActivity(intent);

    }
}