package com.hasbro.basicslife_lfo;


import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.databinding.LfoGitDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class lfo_git_details extends AppCompatActivity {
    private LfoGitDetailsBinding binding;
    private RequestQueue mRequestQueue;
    private String strcode,grname,storname,design,data,totdata,strid,tmcode,phoneno,user_name,bio,carea,product;

    customProgressBar progressDialog = new customProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoGitDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRequestQueue = Volley.newRequestQueue(this);
        // Fetch and Initialize Data
        initializeIntentData();

        // Initialize ProgressBar
        progressDialog.show(getSupportFragmentManager(), "tag");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

       // setHeaderText();

        if(design.equalsIgnoreCase("BTR")){
            product="TR";
        }else if (design.equals("BSH")){
            product="SH";
        }else if (design.equals("BTS")){
            product="TS";
        }else if (design.equals("OTHERS")){
            product = "OTHERS";
        }else if (design.equals("PROMO")){
            product = "PROMO";
        }


        // Fetch data using Volley
        fetchGITData();


    }
    public String removeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input as-is if it's null or empty
        }
        return input.substring(1); // Remove the first letter
    }
    private void fetchGITData() {
        fetchDataFromAPI("getGitPrdDetail", strid,product, this::parseGitData);

    }
    private void fetchDataFromAPI(String endpoint, String strid,String product, lfo_git_details.ResponseHandler handler) {
        String url = retrofit.baseUrl() + endpoint + "?strid=" + strid + "&product=" + product;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                handler.handleResponse(new JSONObject(response));
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } catch (JSONException e) {
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.e("FetchDataError", "Error parsing response", e);
            }
        }, error -> Log.e("FetchDataError", "API request failed", error));

        mRequestQueue.add(request);
    }
    private void parseGitData(JSONObject obj) {
        try {
            System.out.println(obj.length());
            if (obj.length() == 0) return;
            Iterator<String> keys = obj.keys();

            // Parse JSON Arrays
            while (keys.hasNext()) {
                String key = keys.next(); // Get the current key

                // Get the JSONArray corresponding to the key
                JSONArray detailsArray = obj.getJSONArray(key);

                // Access elements in the JSONArray
                String invNo = detailsArray.getString(0);
                String inv = invNo.contains("-") ? invNo.split("-")[1] : invNo;
                String poNo = detailsArray.getString(1);
                String po = poNo.contains("-") ? poNo.split("-")[1] : poNo;
                String lrNo = detailsArray.getString(2);
                String transport = detailsArray.getString(3);
                String qty = detailsArray.getString(4);


                // Print the values (or process them as needed)
                // Create a new TableRow
                TableRow tableRow = new TableRow(this);

                // Set the tag to store data
                tableRow.setTag(new String[]{invNo, poNo, lrNo, qty, transport});

                // Set layout parameters for the TableRow
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                );
                tableRow.setLayoutParams(params);

                // Create TextViews for each column
                TextView invTextView = new TextView(this);
                invTextView.setText(inv);
                invTextView.setTextColor(getColor(R.color.black));
                invTextView.setPadding(5,5,5,5);
                invTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                invTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView poTextView = new TextView(this);
                poTextView.setText(po);
                poTextView.setTextColor(getColor(R.color.black));
                poTextView.setPadding(5,5,5,5);
                poTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                poTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView lrTextView = new TextView(this);
                lrTextView.setText(lrNo);
                lrTextView.setTextColor(getColor(R.color.black));
                lrTextView.setPadding(5,5,5,5);
                lrTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                lrTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView trTextView = new TextView(this);
                trTextView.setText(transport);
                trTextView.setTextColor(getColor(R.color.black));
                trTextView.setPadding(5,5,5,5);
                trTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                trTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView qtyTextView = new TextView(this);
                qtyTextView.setText(qty);
                qtyTextView.setTextColor(getColor(R.color.black));
                qtyTextView.setPadding(5,5,5,5);
                qtyTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                qtyTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));


                // Add all views to the TableRow
                tableRow.addView(invTextView);
                tableRow.addView(poTextView);
                tableRow.addView(lrTextView);
                tableRow.addView(trTextView);
                tableRow.addView(qtyTextView);


                // Add the TableRow to the TableLayout
                binding.pgd.addView(tableRow);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void initializeIntentData() {
        // Fetch Intent Data
        design = getIntent().getStringExtra("design");
        data = getIntent().getStringExtra("data");
        totdata = getIntent().getStringExtra("totdata");
        strcode = getIntent().getStringExtra("strcode");
        grname = getIntent().getStringExtra("grname");
        storname = getIntent().getStringExtra("strname");
        strid   = getIntent().getStringExtra("strid");
        tmcode   = getIntent().getStringExtra("tmcode");
        user_name = getIntent().getStringExtra("name");
        phoneno = getIntent().getStringExtra("mobile");
        bio = getIntent().getStringExtra("bio");
        carea= getIntent().getStringExtra("carea");

    }

    private String getFormattedDate(String dateStr, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat input = new SimpleDateFormat(inputFormat);
            SimpleDateFormat output = new SimpleDateFormat(outputFormat);
            return output.format(input.parse(dateStr));
        } catch (ParseException e) {
            return "Invalid Date";
        }
    }
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
    private interface ResponseHandler {
        void handleResponse(JSONObject response) throws JSONException;
    }
}