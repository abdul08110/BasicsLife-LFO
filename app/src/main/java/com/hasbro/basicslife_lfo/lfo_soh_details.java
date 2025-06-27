package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.databinding.LfoSohDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class lfo_soh_details extends AppCompatActivity {
    private LfoSohDetailsBinding binding;
    private String strcode,grname,storname,design,data,totdata,strid,tmcode,phoneno,user_name,bio,carea;
    private TableLayout tableMain;
    private JSONObject SOHObj = new JSONObject();
    private JSONObject SOHOthObj = new JSONObject();
    private JSONObject SOHProObj = new JSONObject();
    private JSONObject MTHObj = new JSONObject();
    private JSONObject MTHOthObj = new JSONObject();
    private JSONObject MTHProObj = new JSONObject();
    private String prdtype,result;
    customProgressBar progressDialog = new customProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoSohDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tableMain = binding.tableMain;

        // Initialize data
        initializeIntentData();

        // Initialize ProgressBar
        progressDialog.show(getSupportFragmentManager(), "tag");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


//        String type = design.equals("BTR") ? "fit_name" : "design";
        if(design.equalsIgnoreCase("BTR")){
            prdtype ="fit_name";
            result="TR";
        }else if (design.equals("BSH")){
            prdtype ="design";
            result="SH";
        }else if (design.equals("BTS")){
            prdtype ="design";
            result="TS";
        }else if (design.equals("OTHERS")){
            prdtype = "";
            result = "OTHERS";
        }else if (design.equals("PROMO")){
            prdtype = "";
            result = "PROMO";
        }

        // Fetch data using Volley
        fetchSOHData(result,prdtype,strid);
        fetchThreeMonthSalesAndPopulate(result,prdtype,strid);

    }

    private void fetchData(String endpoint, String product, String type, String strid, Response.Listener<JSONObject> responseListener) {
        String url = retrofit.baseUrl() + endpoint + "?Ptype=" + product + "&type=" + type + "&strid=" + strid;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                responseListener,
                error -> Toast.makeText(lfo_soh_details.this, "Error fetching data", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }


    private void fetchSOHData(String product, String type, String strid) {
        fetchData("getSOHDetail", product, type, strid, response -> {
            SOHObj = response;
            checkAndPopulateTable();
        });
    }

    private void fetchThreeMonthSalesAndPopulate(String product, String type, String strid) {
        fetchData("getMTHDetail", product, type, strid, response -> {
            MTHObj = response;
            checkAndPopulateTable();
        });
    }
    private void checkAndPopulateTable() {
        if (SOHObj != null && SOHObj.length() > 0 && MTHObj != null && MTHObj.length() > 0) {
            try {
                // Combine both arrays or process as needed
                JSONObject combinedResponse = combineResponses(SOHObj,MTHObj);

                // Call populate table method
                populateTableFromResponse(combinedResponse);

                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            } catch (JSONException e) {
                progressDialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                e.printStackTrace();
                Toast.makeText(this, "Error combining responses", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private JSONObject combineResponses(JSONObject sohObj, JSONObject mthObj) throws JSONException {
        JSONObject combinedResponse = new JSONObject();
        combinedResponse.put("SOH", sohObj);
        combinedResponse.put("MTH", mthObj);
        return combinedResponse;
    }

    private void populateTableFromResponse(JSONObject response) throws JSONException {
        JSONObject sohObject = response.getJSONObject("SOH");
        JSONObject mthObject = response.getJSONObject("MTH");
        JSONArray keys = sohObject.names();

        setHeaderText();

        TableRow headerRow = createHeaderRow(keys);
        binding.tableMain.addView(headerRow);

        String[] rows = {"SOH", "%", "3 MTH Sales", "LY %", "REQ.MDQ", "OTB"};
        int[] columnTotals = new int[keys.length()];
        int sohGrandTotal = 0, mthGrandTotal = 0;

        for (String rowName : rows) {
            TableRow dataRow = new TableRow(this);
            addCellToRow(dataRow, rowName, Color.WHITE, false);

            int rowTotal = 0;

            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i);
                JSONArray sohValues = sohObject.optJSONArray(key);
                JSONArray mthValues = mthObject.optJSONArray(key);

                int sohValue = (sohValues != null && sohValues.length() > 0) ? sohValues.getInt(0) : 0;
                int mthValue = (mthValues != null && mthValues.length() > 0) ? mthValues.getInt(0) : 0;
                int value = calculateValue(rowName, sohValue, mthValue, sohGrandTotal, mthGrandTotal, columnTotals, i);

                if ("SOH".equals(rowName)) {
                    columnTotals[i] += sohValue;
                    sohGrandTotal += sohValue;
                } else if ("3 MTH Sales".equals(rowName)) {
                    mthGrandTotal += mthValue;
                }

                rowTotal += value;
                boolean isNegative = value < 0;
                addCellToRow(dataRow, formatValue(rowName, value), Color.WHITE, isNegative);
            }

            addCellToRow(dataRow, calculateRowTotal(rowName, rowTotal), Color.WHITE, rowTotal < 0);
            binding.tableMain.addView(dataRow);
        }
    }

    private TableRow createHeaderRow(JSONArray keys) throws JSONException {
        TableRow headerRow = new TableRow(this);
        addCellToRow(headerRow, "-", Color.GRAY, false);

        for (int i = 0; i < keys.length(); i++) {
            addCellToRow(headerRow, keys.getString(i), Color.GRAY, false);
        }

        addCellToRow(headerRow, "TOT", Color.GRAY, false);
        return headerRow;
    }

    private int calculateValue(String rowName, int sohValue, int mthValue, int sohGrandTotal, int mthGrandTotal, int[] columnTotals, int index) {
        switch (rowName) {
            case "SOH":
                return sohValue;
            case "%":
                return sohGrandTotal == 0 ? 0 : (int) Math.round((columnTotals[index] / (double) sohGrandTotal) * 100);
            case "3 MTH Sales":
                return mthValue;
            case "LY %":
                return mthGrandTotal == 0 ? 0 : (int) Math.round((mthValue / (double) mthGrandTotal) * 100);
            case "REQ.MDQ":
                return mthGrandTotal == 0 ? 0 : (int) Math.round((mthValue / (double) mthGrandTotal) * 100);
            case "OTB":
                return mthValue - sohValue;
            default:
                return 0;
        }
    }

    private String formatValue(String rowName, int value) {
        if ("%".equals(rowName) || "LY %".equals(rowName)) {
            return value + "%";
        }
        return String.valueOf(value);
    }

    private String calculateRowTotal(String rowName, int rowTotal) {
        if ("%".equals(rowName) || "LY %".equals(rowName)) {
            return "100%";
        }
        return String.valueOf(rowTotal);
    }


    private void addCellToRow(TableRow row, String text, int bgColor,boolean isNegative) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        // Set background color
        textView.setBackgroundColor(bgColor);
        if (isNegative) {
            textView.setTextColor(Color.RED); // Red for negative values
        } else {
            textView.setTextColor(getColor(R.color.black)); // Default black
        }

        // Optional: Set text color to contrast with the background

        textView.setTypeface(null, Typeface.BOLD);

        row.addView(textView);
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
    private void setHeaderText() {
        binding.salesHeader.setText("STOCK ON HAND-DETAILS");
    }

//    private void norecordfound() {
//        new SweetAlertDialog(lfo_soh_details.this, SweetAlertDialog.ERROR_TYPE)
//                .setTitleText("Basics Life LFO Portal")
//                .setContentText("No Record Found")
//                .setConfirmText("OK")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//                        Intent intent = new Intent(getApplicationContext(), lfo_stocks_view.class);
//                        intent.putExtra("strname", storname);
//                        intent.putExtra("strcode", strcode);
//                        intent.putExtra("grname", grname);
//                        intent.putExtra("strid", strid);
//                        intent.putExtra("carea", carea);
//                        intent.putExtra("tmcode", tmcode);
//                        intent.putExtra("mobile",phoneno);
//                        intent.putExtra("name",user_name);
//                        intent.putExtra("bio",bio);
//
//                        startActivity(intent);
//                    }
//                }).show();
//
//    }

}