package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.hasbro.basicslife_lfo.databinding.LfoStocksViewBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class lfo_stocks_view extends AppCompatActivity {
    private LfoStocksViewBinding binding;

    private final int[] values = {0, 1, 2, 3, 4, 5}; // bsh, btr, bts, OTH, PR, TOT
    private TextView[] gitViews, sohViews,totSohViews;
    private String strcode,grname,storname,strid,tmcode,phoneno,user_name,bio,carea;
    private RequestQueue mRequestQueue;
    private double totsohbsh=0, totsohbtr=0, totsohbts=0, totsohoth=0, totsohpromo=0, totsohtot=0;
    private double gitshValue=0, gittrValue=0, gittsValue =0, gitothersValue=0, gitpromoValue=0, totgit=0;
    customProgressBar progressDialog = new customProgressBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoStocksViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRequestQueue = Volley.newRequestQueue(this);
        // Initialize views
        initializeViews();

        // Fetch and Initialize Data
        initializeIntentData();
// Initialize ProgressBar
        progressDialog.show(getSupportFragmentManager(), "tag");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Set click listeners for GIT views
        setClickListeners(gitViews,1);
        // Set click listeners for SOH views
        setClickListeners(sohViews,2);
        // Set click listeners for TotSOH views
        setClickListeners(totSohViews,3);

        setHeaderText();

        fetchAllData();
    }
    private void initializeViews() {
        // Initialize GIT views array
        gitViews = new TextView[]{
                binding.gitbsh,
                binding.gitbtr,
                binding.gitbts,
                binding.gitoth,
                binding.gitpro,
                binding.gittot
        };

        // Initialize SOH views array
        sohViews = new TextView[]{
                binding.sohbsh,
                binding.sohbtr,
                binding.sohbts,
                binding.sohoth,
                binding.sohpro,
                binding.sohtot
        };

        // Initialize TOTSOH views array
        totSohViews = new TextView[]{
                binding.totsohbsh,
                binding.totsohbtr,
                binding.totsohbts,
                binding.totsohoth,
                binding.totsohpr,
                binding.totsohtot
        };
    }


    // Method to set click listeners dynamically
    private void setClickListeners(TextView[] views,int touch) {
        for (int i = 0; i < views.length; i++) {
            final int index = i; // Necessary for lambda
            views[i].setOnClickListener(new SafeClikcListener() {
                @Override
                public void performClick(View v) throws ParseException, SQLException, IOException {
                    String data = views[index].getText().toString();
                    String totdata = views[5].getText().toString();
                    String design="";
                    if(index==0){
                        design="BSH";
                    }else if(index==1){
                        design="BTR";
                    } else if(index==2){
                        design="BTS";
                    } else if(index==3){
                        design="OTHERS";
                    } else if(index==4){
                        design="PROMO";
                    }
                    System.out.println("data" +data);
                    if ((index != 5) ) {
                        if(!data.equalsIgnoreCase("0")) {
                            if (touch==1) {
                                gotoGITscreen(design, data, totdata);
                            } else if(touch==2) {
                                gotoSOHscreen(design, data, totdata);
                            }else{
                                gotoTOTSOHscreen(design, data, totdata);
                            }
                        }
                    }

                }
            });
        }
    }
    private void gotoSOHscreen(String design, String data, String totdata) {
        Intent intent = new Intent(getApplicationContext(), lfo_soh_details.class);
        intent.putExtra("design", design);
        intent.putExtra("data", data);
        intent.putExtra("totdata", totdata);
        intent.putExtra("strname", storname);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("strid", strid);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile",phoneno);
        intent.putExtra("name",user_name);
        intent.putExtra("bio",bio);
        intent.putExtra("carea",carea);
        startActivity(intent);
    }

    private void gotoGITscreen(String design, String data, String totdata) {
        Intent intent = new Intent(getApplicationContext(), lfo_git_details.class);
        intent.putExtra("design", design);
        intent.putExtra("data", data);
        intent.putExtra("totdata", totdata);
        intent.putExtra("strname", storname);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("strid", strid);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile",phoneno);
        intent.putExtra("name",user_name);
        intent.putExtra("bio",bio);
        intent.putExtra("carea",carea);


        startActivity(intent);
    }
    private void gotoTOTSOHscreen(String design, String data, String totdata) {
        Intent intent = new Intent(getApplicationContext(), lfo_totsoh_details.class);
        intent.putExtra("design", design);
        intent.putExtra("data", data);
        intent.putExtra("totdata", totdata);
        intent.putExtra("strname", storname);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("strid", strid);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile",phoneno);
        intent.putExtra("name",user_name);
        intent.putExtra("bio",bio);
        intent.putExtra("carea",carea);
        startActivity(intent);
    }
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private void setHeaderText() {
        String todayDate = getFormattedDate(getTodayDate(), "yyyy-MM-dd", "dd-MMM-yyyy");
        SpannableString headerText = new SpannableString(storname + " STOCK REPORT " + todayDate);

        headerText.setSpan(new ForegroundColorSpan(Color.RED), 0, storname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.headerTitle.setText(headerText);
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
        binding.storecode.setText(strcode);
        binding.retailer.setText(grname);
    }

    private void fetchAllData() {

        fetchDataFromAPI("getStockDetail", strid, this::parseSalesData);
        fetchDataFromAPI("getGrnDetail", strid, this::parseGrnData);
        fetchDataFromAPI("getGitDetail", strid, this::parseGitData);
        fetchDataFromAPI("getGrnPDetail", strid, this::parseGrnPData);
    }
    private void fetchDataFromAPI(String endpoint, String strid, lfo_stocks_view.ResponseHandler handler) {
        String url = retrofit.baseUrl() + endpoint + "?strid=" + strid;
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

    private void parseSalesData(JSONObject obj) {
        try {
            double mdqshValue=0,mdqtrValue=0,mdqtsValue=0,mdqothersValue=0,mdqpromoValue=0,totmdq=0;
            double sohshValue=0,sohtrValue=0,sohtsValue=0,sohothersValue=0,sohpromoValue=0,totsoh=0;
            double gitshValue=0,gittrValue=0,gittsValue=0,gitothersValue=0,gitpromoValue=0,totgit=0;
            double popshValue=0,poptrValue=0,poptsValue=0,popothersValue=0,poppromoValue=0,totpop=0;

            if (obj.length() == 0) return;

            // Parse JSON Arrays
            if (obj.has("MDQ") && !obj.isNull("MDQ")) {
                JSONArray getMDQ = obj.getJSONArray("MDQ");
                if (getMDQ.length() > 0) {
                    Map<String, Double> mdqValues = new HashMap<>();
                    for (int i = 0; i < getMDQ.length(); i++) {
                        String entry = getMDQ.getString(i);

                        // Split the entry into product and value
                        String[] parts = entry.split(" - ");
                        if (parts.length == 2) {
                            String product = parts[0].trim();
                            Double value = Double.valueOf(parts[1].trim());
                            // Store in the map
                            mdqValues.put(product, value);
                        }
                    }


                     mdqshValue = mdqValues.getOrDefault("SH", 0.0);
                     mdqtrValue = mdqValues.getOrDefault("TR", 0.0);
                     mdqtsValue = mdqValues.getOrDefault("TS", 0.0);
                     mdqothersValue = mdqValues.getOrDefault("Others", 0.0);
                     mdqpromoValue = mdqValues.getOrDefault("Promo", 0.0);
                     totmdq=mdqshValue+mdqtrValue+mdqtsValue+mdqothersValue+mdqpromoValue;
                    setTextMDQ(mdqshValue,mdqtrValue,mdqtsValue,mdqothersValue,mdqpromoValue,totmdq);

                }
            }

            if (obj.has("STOCK") && !obj.isNull("STOCK")) {
                JSONArray getSOH = obj.getJSONArray("STOCK");
                if (getSOH.length() > 0) {
                    Map<String, Double> sohValues = new HashMap<>();
                    for (int i = 0; i < getSOH.length(); i++) {
                        String entry = getSOH.getString(i);

                        // Split the entry into product and value
                        String[] parts = entry.split(" - ");
                        if (parts.length == 2) {
                            String product = parts[0].trim();
                            Double value = Double.valueOf(parts[1].trim());
                            // Store in the map
                            sohValues.put(product, value);
                        }
                    }

                     sohshValue = sohValues.getOrDefault("SH", 0.0);
                     sohtrValue = sohValues.getOrDefault("TR", 0.0);
                     sohtsValue = sohValues.getOrDefault("TS", 0.0);
                     sohothersValue = sohValues.getOrDefault("Others", 0.0);
                     sohpromoValue = sohValues.getOrDefault("Promo", 0.0);
                     totsoh=sohshValue+sohtrValue+sohtsValue+sohothersValue+sohpromoValue;
                     setTextSOH(sohshValue,sohtrValue,sohtsValue,sohothersValue,sohpromoValue,totsoh);

                }
            }

            if (obj.has("PO_PENDING") && !obj.isNull("PO_PENDING")) {
                JSONArray getPOP = obj.getJSONArray("PO_PENDING");
                if (getPOP.length() > 0) {
                    Map<String, Double> popValues = new HashMap<>();
                    for (int i = 0; i < getPOP.length(); i++) {
                        String entry = getPOP.getString(i);

                        // Split the entry into product and value
                        String[] parts = entry.split(" - ");
                        if (parts.length == 2) {
                            String product = parts[0].trim();
                            Double value = Double.valueOf(parts[1].trim());
                            // Store in the map
                            popValues.put(product, value);
                        }
                    }

                     popshValue = popValues.getOrDefault("SH", 0.0);
                     poptrValue = popValues.getOrDefault("TR", 0.0);
                     poptsValue = popValues.getOrDefault("TS", 0.0);
                     popothersValue = popValues.getOrDefault("Others", 0.0);
                     poppromoValue = popValues.getOrDefault("Promo", 0.0);
                     totpop=popshValue+poptrValue+poptsValue+popothersValue+poppromoValue;
                    setTextPOP(popshValue,poptrValue,poptsValue,popothersValue,poppromoValue,totpop);

                }
            }

             totsohbsh=sohshValue+gitshValue+popshValue;
             totsohbtr=sohtrValue+gittrValue+poptrValue;
             totsohbts=sohtsValue+gittsValue+poptsValue;
             totsohoth=sohothersValue+gitothersValue+popothersValue;
             totsohpromo=sohpromoValue+gitpromoValue+poppromoValue;
             totsohtot=totsoh+totgit+totpop;


            double fillperbsh=0,fillperbtr=0,fillperbts=0,fillperoth=0,fillperpromo=0,fillpertot=0;

            if (mdqshValue == 0) {
                fillperbsh = 0;
            } else {
                fillperbsh = (totsohbsh/mdqshValue)*100;
            }
            if ( mdqtrValue == 0) {
                fillperbtr = 0;
            } else {
                fillperbtr = (totsohbtr/mdqtrValue)*100;
            }
            if (mdqtsValue == 0) {
                fillperbts = 0;
            } else {
                fillperbts = (totsohbts/mdqtsValue)*100;
            }
            if (mdqothersValue == 0) {
                fillperoth = 0;
            } else {
                fillperoth = (totsohoth/mdqothersValue)*100;
            }
            if (mdqpromoValue == 0) {
                fillperpromo = 0;
            } else {
                fillperpromo = (totsohpromo/mdqpromoValue)*100;
            }
            if (totmdq == 0) {
                fillpertot = 0;
            } else {
                fillpertot = (totsohtot/totmdq)*100;
            }

            setTextFillPer(fillperbsh,fillperbtr,fillperbts,fillperoth,fillperpromo,fillpertot);




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTextMDQ(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totmdq) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totmdq };

        TextView[] textViews = { binding.mdqbsh, binding.mdqbtr, binding.mdqbts, binding.mdqoth, binding.mdqpro, binding.mdqtot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextSOH(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totsoh) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totsoh };

        TextView[] textViews = { binding.sohbsh, binding.sohbtr, binding.sohbts, binding.sohoth, binding.sohpro, binding.sohtot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextGIT(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totgit) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totgit };

        TextView[] textViews = { binding.gitbsh, binding.gitbtr, binding.gitbts, binding.gitoth, binding.gitpro, binding.gittot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextPOP(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totpop) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totpop };

        TextView[] textViews = { binding.popbsh, binding.popbtr, binding.popbts, binding.popoth, binding.poppro, binding.poptot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextTOTSOH(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totpop) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totpop };

        TextView[] textViews = { binding.totsohbsh, binding.totsohbtr, binding.totsohbts, binding.totsohoth, binding.totsohpr, binding.totsohtot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextFillPer(double shValue, double trValue, double tsValue, double othersValue, double promoValue, double totpop) {
        double[] values = {shValue, trValue, tsValue, othersValue, promoValue, totpop };

        TextView[] textViews = { binding.fillperbsh, binding.fillperbtr, binding.fillperbts, binding.fillperoth, binding.fillperpr, binding.fillpertot };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextWithColor(TextView textView, double value) {
        // Check if value is negative and change text color accordingly
        if (value < 0) {
            textView.setTextColor(Color.RED); // Negative value, set text color to red
        } else {
            textView.setTextColor(Color.BLACK); // Positive value, set text color to black
        }
        // Set the value to the text view (formatted to 2 decimal places)
        textView.setText(String.valueOf(Math.round(value)));
    }
    private void parseGrnData(JSONObject obj) {
        try {
            double dshinvno = 0, dshpono = 0, dshlrno = 0, dshqty = 0,
                    dtrinvno = 0, dtrpono = 0, dtrlrno = 0, dtrqty = 0,
                    dtsinvno = 0, dtspono = 0, dtslrno = 0, dtsqty = 0,
                    dothinvno = 0, dothpono = 0, dothlrno = 0, dothqty = 0,
                    dprinvno = 0, dprpono = 0, dprlrno = 0, dprqty = 0;

            if (obj.length() > 0){

            if (obj.has("SH") && !obj.isNull("SH")) {
                JSONArray getSH = obj.getJSONArray("SH");
                if (getSH.length() > 0) {
                    dshinvno = getValueFromArray(getSH, 0);
                    dshpono = getValueFromArray(getSH, 1);
                    dshlrno = getValueFromArray(getSH, 2);
                    dshqty = getValueFromArray(getSH, 3);
                    setTextWithColor(binding.grnbsh,dshqty);

                }
            }
            if (obj.has("TR") && !obj.isNull("TR")) {
                JSONArray getTR = obj.getJSONArray("TR");
                if (getTR.length() > 0) {
                    dtrinvno = getValueFromArray(getTR, 0);
                    dtrpono = getValueFromArray(getTR, 1);
                    dtrlrno = getValueFromArray(getTR, 2);
                    dtrqty = getValueFromArray(getTR, 3);

                    setTextWithColor(binding.grnbtr,dtrqty);
                }
            }
            if (obj.has("TS") && !obj.isNull("TS")) {
                JSONArray getTS = obj.getJSONArray("TS");
                if (getTS.length() > 0) {
                    dtsinvno = getValueFromArray(getTS, 0);
                    dtspono = getValueFromArray(getTS, 1);
                    dtslrno = getValueFromArray(getTS, 2);
                    dtsqty = getValueFromArray(getTS, 3);
                    binding.grnbts.setText(String.valueOf((int) dtsqty));
                }
            }
            if (obj.has("Others") && !obj.isNull("Others")) {
                JSONArray getOth = obj.getJSONArray("Others");
                if (getOth.length() > 0) {
                    dothinvno = getValueFromArray(getOth, 0);
                    dothpono = getValueFromArray(getOth, 1);
                    dothlrno = getValueFromArray(getOth, 2);
                    dothqty = getValueFromArray(getOth, 3);

                    setTextWithColor(binding.grnoth,dothqty);
                }
            }
            if (obj.has("Promo") && !obj.isNull("Promo")) {
                JSONArray getPR = obj.getJSONArray("Promo");
                if (getPR.length() > 0) {
                    dprinvno = getValueFromArray(getPR, 0);
                    dprpono = getValueFromArray(getPR, 1);
                    dprlrno = getValueFromArray(getPR, 2);
                    dprqty = getValueFromArray(getPR, 3);

                    setTextWithColor(binding.grnpro,dprqty);
                }
            }

            double totgrn = dshqty + dtrqty + dtsqty + dothqty + dprqty;
            setTextWithColor(binding.grntot,totgrn);

            totsohbsh +=dshqty;
            totsohbtr +=dtrqty;
            totsohbts +=dtsqty;
            totsohoth +=dothqty;
            totsohpromo +=dprqty;
            totsohtot +=totgrn;
            setTextTOTSOH(totsohbsh,totsohbtr,totsohbts,totsohoth,totsohpromo,totsohtot);
            }else{
            setTextTOTSOH(totsohbsh,totsohbtr,totsohbts,totsohoth,totsohpromo,totsohtot);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseGitData(JSONObject obj) {
        try {

            if (obj.length() > 0){

                if (obj.has("SH") && !obj.isNull("SH")) {
                    JSONArray getSH = obj.getJSONArray("SH");
                    if (getSH.length() > 0) {
                        gitshValue = getValueFromArray(getSH, 0);
                        setTextWithColor(binding.gitbsh,gitshValue);

                    }
                }
                if (obj.has("TR") && !obj.isNull("TR")) {
                    JSONArray getTR = obj.getJSONArray("TR");
                    if (getTR.length() > 0) {

                        gittrValue = getValueFromArray(getTR, 0);
                        setTextWithColor(binding.gitbtr,gittrValue);
                    }
                }
                if (obj.has("TS") && !obj.isNull("TS")) {
                    JSONArray getTS = obj.getJSONArray("TS");
                    if (getTS.length() > 0) {
                        gittsValue = getValueFromArray(getTS, 0);
                        setTextWithColor(binding.gitbts,gittrValue);

                    }
                }
                if (obj.has("Others") && !obj.isNull("Others")) {
                    JSONArray getOth = obj.getJSONArray("Others");
                    if (getOth.length() > 0) {

                        gitothersValue = getValueFromArray(getOth, 0);
                        setTextWithColor(binding.gitoth,gitothersValue);
                    }
                }
                if (obj.has("Promo") && !obj.isNull("Promo")) {
                    JSONArray getPR = obj.getJSONArray("Promo");
                    if (getPR.length() > 0) {

                        gitpromoValue = getValueFromArray(getPR, 0);
                        setTextWithColor(binding.gitpro,gitpromoValue);
                    }
                }

                totgit = gitshValue + gittrValue + gittsValue + gitothersValue + gitpromoValue;
                setTextWithColor(binding.gittot,totgit);

                totsohbsh +=gitshValue;
                totsohbtr +=gittrValue;
                totsohbts +=gittsValue;
                totsohoth +=gitothersValue;
                totsohpromo +=gitpromoValue;
                totsohtot +=totgit;
                setTextTOTSOH(totsohbsh,totsohbtr,totsohbts,totsohoth,totsohpromo,totsohtot);
            }else{
                setTextTOTSOH(totsohbsh,totsohbtr,totsohbts,totsohoth,totsohpromo,totsohtot);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void parseGrnPData(JSONObject obj) {
        try {

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
                String qty = detailsArray.getString(3);
                String transport = detailsArray.getString(4);

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
                invTextView.setTextSize(20);
                invTextView.setTextColor(getColor(R.color.black));
                invTextView.setPadding(5,5,5,5);
                invTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                invTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView poTextView = new TextView(this);
                poTextView.setText(po);
                poTextView.setTextSize(20);
                poTextView.setTextColor(getColor(R.color.black));
                poTextView.setPadding(5,5,5,5);
                poTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                poTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView lrTextView = new TextView(this);
                lrTextView.setText(lrNo);
                lrTextView.setTextSize(20);
                lrTextView.setTextColor(getColor(R.color.black));
                lrTextView.setPadding(5,5,5,5);
                lrTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                lrTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView trTextView = new TextView(this);
                trTextView.setText(transport);
                trTextView.setTextSize(20);
                trTextView.setTextColor(getColor(R.color.black));
                trTextView.setPadding(5,5,5,5);
                trTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                trTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.cellborder1dp));

                TextView qtyTextView = new TextView(this);
                qtyTextView.setText(qty);
                qtyTextView.setTextSize(20);
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

                tableRow.setOnClickListener(new SafeClikcListener() {
                    @Override
                    public void performClick(View v) throws ParseException, SQLException, IOException {
                        String[] rowData = (String[]) v.getTag();

                        // Create an Intent to navigate to the next activity
                        Intent intent = new Intent(lfo_stocks_view.this, lfo_pending_grn_view.class);
                        intent.putExtra("invNo", rowData[0]);
                        intent.putExtra("poNo", rowData[1]);
                        intent.putExtra("lrNo", rowData[2]);
                        intent.putExtra("qty", rowData[3]);
                        intent.putExtra("transport", rowData[4]);

                        // Start the next activity
                        startActivity(intent);
                    }
                });


                // Add the TableRow to the TableLayout
                binding.pgd.addView(tableRow);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private double getValueFromArray(JSONArray array, int index) {
        try {
            return array.isNull(index) ? 0 : array.getDouble(index);
        } catch (JSONException e) {
            return 0;
        }
    }
    private interface ResponseHandler {
        void handleResponse(JSONObject response) throws JSONException;
    }






}