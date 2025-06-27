package com.hasbro.basicslife_lfo;


import static com.hasbro.basicslife_lfo.geturl.retrofit;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.textfield.TextInputEditText;
import com.hasbro.basicslife_lfo.databinding.ActivityLfoSalesViewBinding;
import com.hasbro.basicslife_lfo.pojo.TableRowData;
import com.hasbro.basicslife_lfo.pojo.compSales;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class lfo_sales_view extends AppCompatActivity {
    private ActivityLfoSalesViewBinding binding;
    private RequestQueue mRequestQueue;

    ArrayAdapter<String> adapter;

    private List<String> brandList = new ArrayList<>();


    private double carea = 0;
    private String strid, tmcode, visitdate, user_name, phoneno, bio;

    customProgressBar progressDialog = new customProgressBar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLfoSalesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Fetch and Initialize Data
        mRequestQueue = Volley.newRequestQueue(this);

        initializeIntentData();
        // Initialize ProgressBar
        progressDialog.show(getSupportFragmentManager(), "tag");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        setHeaderText();
        setupEventListeners();
        fetchAllData();
        setupSaveButton();
    }
    // ---------------------------- INITIALIZATION ----------------------------
    private void initializeIntentData() {
        Intent intent = getIntent();
        strid = intent.getStringExtra("strid");
        tmcode = intent.getStringExtra("tmcode");
        user_name = intent.getStringExtra("name");
        phoneno = intent.getStringExtra("mobile");
        bio = intent.getStringExtra("bio");
        carea = Double.parseDouble(intent.getStringExtra("carea"));

        binding.storecode.setText(intent.getStringExtra("strcode"));
        binding.retailer.setText(intent.getStringExtra("grname"));
    }
    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException, IOException {
                collectAndSendTableData();

            }
        });

    }
    private void setHeaderText() {
        String storeName = getIntent().getStringExtra("strname");
        String todayDate = getFormattedDate(getTodayDate(), "yyyy-MM-dd", "dd-MMM-yyyy");
        SpannableString headerText = new SpannableString(storeName + " VISIT REPORT " + todayDate);

        headerText.setSpan(new ForegroundColorSpan(Color.RED), 0, storeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.headerTitle.setText(headerText);
    }
    private void setupEventListeners() {
        binding.btnAddRow.setOnClickListener(v -> addTableRow());
        binding.btnRemoveRow.setOnClickListener(v -> removeTableRow());
        setHeaderClickListeners();
    }

    // ---------------------------- DATA FETCHING ----------------------------

    private void fetchAllData() {

        visitdate = getTodayDate();

        fetchDataFromAPI("getSaleData", strid, this::parseSalesData);
        fetchDataFromAPI("compsales", strid, this::parseCompSalesData);
        fetchDataFromAPI("getSaleBillDetail", strid, this::parseBillDetails);
        fetchDataFromAPI("getBSHBillData", strid, this::parseBSHBillData);
        fetchDataFromAPI("getBTRBillData", strid, this::parseBTRBillData);
    }

    private void fetchDataFromAPI(String endpoint, String strid, ResponseHandler handler) {
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

            if (obj.length() == 0) return;

            // Parse JSON Arrays
            JSONArray getLastBillDate = obj.optJSONArray("lastbilldate");
            JSONArray getBrand = obj.optJSONArray("brand");

            double dmthtgtqty = parseValue(obj, "MTH TRGT", 0);
            double dmthtgtval = parseValue(obj, "MTH TRGT", 1);
            double dmthachqty = parseValue(obj, "MTH ACH", 0);
            double dmthachval = parseValue(obj, "MTH ACH", 1);
            double dmtdtgtqty = parseValue(obj, "MTD TRGT", 0);
            double dmtdtgtval = parseValue(obj, "MTD TRGT", 1);
            double dltlmthqty = parseValue(obj, "LTL MTH", 0);
            double dltlmthnsv = parseValue(obj, "LTL MTH", 1);
            double dltlmtdqty = parseValue(obj, "LTL MTD", 0);
            double dltlmtdnsv = parseValue(obj, "LTL MTD", 1);

            // Populate brand list
            if (getBrand != null) {
                for (int i = 0; i < getBrand.length(); i++) {
                    brandList.add(getBrand.optString(i));
                }
                setupBrandSpinner();
            }

            // Set last bill date
            if (getLastBillDate != null && getLastBillDate.length() > 0) {
                binding.salesHeader.setText("SALES - UPDATED AS OF " + formatDate(getLastBillDate.optString(0, "Invalid Date")));
            }

            // Set data to UI
            setTextFields(dmthtgtqty, dmthtgtval, dmthachqty, dmthachval, dmtdtgtqty, dmtdtgtval, dltlmthqty, dltlmthnsv, dltlmtdqty, dltlmtdnsv);
            setPercentageFields(
                    calculatePercentage(dmthachqty, dmthtgtqty),
                    calculatePercentage(dmthachval, dmthtgtval),
                    calculatePercentage(dmthachqty, dmtdtgtqty),
                    calculatePercentage(dmthachval, dmtdtgtval)
            );
            setCalculatedFields(dltlmtdnsv, dltlmtdqty, dmthachval, dmthachqty, dltlmthqty, dltlmthnsv);

            updateFieldWithCalculation(binding.mtdaspper, binding.mtdtyasp, binding.mtdlyasp);
            updateFieldWithCalculation(binding.mtdaspdper, binding.mtdtyaspd, binding.mtdlyaspd);
            updateFieldWithCalculation(binding.mtdspsfper, binding.mtdtyspsf, binding.mtdlyspsf);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseCompSalesData(JSONObject obj) {

        ArrayList<String> comsalelist = new ArrayList<String>();

        try {
            if (obj.length() == 0) return;
            String rank="",brand="",nsv="",soh="",qty="",remarks="";
            // Parse JSON Arrays
            JSONArray getCompSales = obj.optJSONArray("CompSales");

            // Populate brand list

            if (getCompSales != null && !getCompSales.isNull(0)) {
                for (int i = 0; i < getCompSales.length(); i++) {
                    // Result get column values...
                    JSONArray innerArray = getCompSales.getJSONArray(i);
                    rank = String.valueOf(innerArray.get(0));
                    brand = String.valueOf(innerArray.get(1));
                    qty = String.valueOf(innerArray.get(2));
                    nsv = String.valueOf(innerArray.get(3));
                    soh = String.valueOf(innerArray.get(4));
                    remarks = String.valueOf(innerArray.get(5));

                    comsalelist.add(rank + ":" + brand + ":" + qty + ":" + nsv + ":" + soh + ":" + remarks);

                }
                binding.btnAddRow.setVisibility(View.GONE);
                binding.btnRemoveRow.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.GONE);
                populateTableLayout(comsalelist);
            }else{
                binding.btnAddRow.setVisibility(View.VISIBLE);
                binding.btnRemoveRow.setVisibility(View.VISIBLE);
                binding.btnSave.setVisibility(View.VISIBLE);
            }

            System.out.println("comsalelist" + comsalelist);
            // System.out.println("showbuttons inside " + showbuttons);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBillDetails(JSONObject obj) {
        try {
            if (obj.length() > 0) {
                double dmthbshlynsv = 0, dmtdbshtynsv = 0, dtgtbshtynsv = 0, dmtdbshlynsv = 0,
                        dmthbtrlynsv = 0, dmtdbtrtynsv = 0, dtgtbtrtynsv = 0, dmtdbtrlynsv = 0,
                        dmthbtslynsv = 0, dmtdbtstynsv = 0, dtgtbtstynsv = 0, dmtdbtslynsv = 0,
                        dmthbjnlynsv = 0, dmtdbjntynsv = 0, dtgtbjntynsv = 0, dmtdbjnlynsv = 0,
                        dmthlypromo = 0, dmtdlypromo =0, dtgttypromo =0, dmtdtypromo =0,dlymtdappr=0, dtymtdappr = 0;

                if (obj.has("SH") && !obj.isNull("SH")) {
                    JSONArray getBSH = obj.getJSONArray("SH");
                    if (getBSH.length() > 0) {
                        dmthbshlynsv = getValueFromArray(getBSH, 0);
                        dmtdbshlynsv = getValueFromArray(getBSH, 1);
                        dtgtbshtynsv = getValueFromArray(getBSH, 2);
                        dmtdbshtynsv = getValueFromArray(getBSH, 3);
                    }
                }
                if (obj.has("TR") && !obj.isNull("TR")) {
                    JSONArray getBTR = obj.getJSONArray("TR");
                    if (getBTR.length() > 0) {
                        dmthbtrlynsv = getValueFromArray(getBTR, 0);
                        dmtdbtrlynsv = getValueFromArray(getBTR, 1);
                        dtgtbtrtynsv = getValueFromArray(getBTR, 2);
                        dmtdbtrtynsv = getValueFromArray(getBTR, 3);
                    }
                }
                if (obj.has("TS") && !obj.isNull("TS")) {
                    JSONArray getBTS = obj.getJSONArray("TS");
                    if (getBTS.length() > 0) {
                        dmthbtslynsv = getValueFromArray(getBTS, 0);
                        dmtdbtslynsv = getValueFromArray(getBTS, 1);
                        dtgtbtstynsv = getValueFromArray(getBTS, 2);
                        dmtdbtstynsv = getValueFromArray(getBTS, 3);
                    }
                }
                if (obj.has("JN") && !obj.isNull("JN")) {
                    JSONArray getBJN = obj.getJSONArray("JN");
                    if (getBJN.length() > 0) {
                        dmthbjnlynsv = getValueFromArray(getBJN, 0);
                        dmtdbjnlynsv = getValueFromArray(getBJN, 1);
                        dtgtbjntynsv = getValueFromArray(getBJN, 2);
                        dmtdbjntynsv = getValueFromArray(getBJN, 3);
                    }
                }
                if (obj.has("Promo") && !obj.isNull("Promo")) {
                    JSONArray getPROMO = obj.getJSONArray("Promo");
                    if (getPROMO.length() > 0) {
                        dmthlypromo = getValueFromArray(getPROMO, 0);
                        dmtdlypromo = getValueFromArray(getPROMO, 1);
                        dtgttypromo = getValueFromArray(getPROMO, 2);
                        dmtdtypromo = getValueFromArray(getPROMO, 3);
                    }
                }

                double dlybshper, dlybtsper, dlybtrper, dlybjnper, dlypromoper;
                double dtybshper, dtybtsper, dtybtrper, dtybjnper, dtypromoper;
                double dmtdtvabsh, dmtdtvabtr, dmtdtvabts, dmtdtvabjn, dmtdtvapromo;

                double dtotlymth = dmthbshlynsv + dmthbtrlynsv + dmthbtslynsv + dmthbjnlynsv + dmthlypromo;
                double dtotlyapparels = dmthbshlynsv + dmthbtrlynsv + dmthbtslynsv + dmthbjnlynsv ;

                if (dtotlymth == 0) {
                    dlybshper = 0;
                    dlybtsper = 0;
                    dlybtrper = 0;
                    dlybjnper = 0;
                    dlypromoper = 0;
                } else {
                    dlybshper = (dmthbshlynsv / dtotlymth) * 100;
                    dlybtsper = (dmthbtslynsv / dtotlymth) * 100;
                    dlybtrper = (dmthbtrlynsv / dtotlymth) * 100;
                    dlybjnper = (dmthbjnlynsv / dtotlymth) * 100;
                    dlypromoper = (dmthlypromo / dtotlymth) * 100;
                }

                if (dlypromoper == 0) {
                    dlymtdappr = 0;
                }else {
                    dlymtdappr = dtotlyapparels / dlypromoper;
                }

                double dtottymtd = dmtdbshtynsv + dmtdbtrtynsv + dmtdbtstynsv + dmtdbjntynsv + dmtdtypromo;;
                double dtottyapparels = dmtdbshtynsv + dmtdbtrtynsv + dmtdbtstynsv + dmtdbjntynsv ;

                if (dtottymtd == 0) {
                    dtybshper = 0;
                    dtybtsper = 0;
                    dtybtrper = 0;
                    dtybjnper = 0;
                    dtypromoper = 0;
                } else {
                    dtybshper = (dmtdbshtynsv / dtottymtd) * 100;
                    dtybtsper = (dmtdbtstynsv / dtottymtd) * 100;
                    dtybtrper = (dmtdbtrtynsv / dtottymtd) * 100;
                    dtybjnper = (dmtdbjntynsv / dtottymtd) * 100;
                    dtypromoper = (dmtdtypromo / dtottymtd) * 100;
                }

                if (dtypromoper == 0) {
                    dtymtdappr = 0;
                }else {
                    dtymtdappr = dtottyapparels / dtypromoper;
                }
                dmtdtvabsh = (dtgtbshtynsv == 0) ? 0 : (dmtdbshtynsv / dtgtbshtynsv) * 100;
                dmtdtvabtr = (dtgtbtrtynsv == 0) ? 0 : (dmtdbtrtynsv / dtgtbtrtynsv) * 100;
                dmtdtvabts = (dtgtbtstynsv == 0) ? 0 : (dmtdbtstynsv / dtgtbtstynsv) * 100;
                dmtdtvabjn = (dtgtbjntynsv == 0) ? 0 : (dmtdbjntynsv / dtgtbjntynsv) * 100;
                dmtdtvapromo = (dtgttypromo == 0) ? 0 : (dmtdtypromo / dtgttypromo) * 100;

                double dexcdefbsh = dmtdbshtynsv - dtgtbshtynsv;
                double dexcdefbtr = dmtdbtrtynsv - dtgtbtrtynsv;
                double dexcdefbts = dmtdbtstynsv - dtgtbtstynsv;
                double dexcdefbjn = dmtdbjntynsv - dtgtbjntynsv;
                double dexcdefpromo = dmtdtypromo - dtgttypromo;

                // Check for valid last bill date

                // Set parsed values to UI elements
                setTextFieldsDB(dmthbshlynsv, dmtdbshlynsv, dtgtbshtynsv, dmtdbshtynsv, dmthbtrlynsv,
                        dmtdbtrlynsv, dtgtbtrtynsv, dmtdbtrtynsv, dmthbtslynsv, dmtdbtslynsv, dtgtbtstynsv,
                        dmtdbtstynsv, dmthbjnlynsv, dmtdbjnlynsv, dtgtbjntynsv, dmtdbjntynsv, dlybshper,
                        dlybtsper, dlybtrper, dlybjnper, dtybshper, dtybtsper, dtybtrper, dtybjnper,
                        dmtdtvabsh, dmtdtvabtr, dmtdtvabts, dmtdtvabjn, dexcdefbsh, dexcdefbtr, dexcdefbts, dexcdefbjn,
                        dmthlypromo, dmtdlypromo, dtgttypromo, dmtdtypromo, dlypromoper, dtypromoper, dmtdtvapromo, dexcdefpromo,dlymtdappr,dtymtdappr);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBSHBillData(JSONObject obj) {
        try {
            if (obj.length() > 0) {
                double dmthlypl=0,dmthlych=0,dmthlypr=0,dmthlyst=0,
                        dmtdlypl=0,dmtdlych=0,dmtdlypr=0,dmtdlyst=0,
                        dmtdtypl=0,dmtdtych=0,dmtdtypr=0,dmtdtyst=0;

                double dpllyper, dchlyper, dprlyper, dstlyper,dpltyper,
                        dchtyper,dprtyper,dsttyper ;

                if (obj.has("PLAIN") && !obj.isNull("PLAIN")) {
                    JSONArray getPLAIN = obj.getJSONArray("PLAIN");
                    if (getPLAIN.length() > 0) {
                        dmthlypl    = getValueFromArray(getPLAIN, 0);
                        dmtdlypl    = getValueFromArray(getPLAIN, 1);
                        dmtdtypl    = getValueFromArray(getPLAIN, 2);

                    }
                }
                if (obj.has("CHECKS") && !obj.isNull("CHECKS")) {
                    JSONArray getCHECKS = obj.getJSONArray("CHECKS");
                    if (getCHECKS.length() > 0) {
                        dmthlych = getValueFromArray(getCHECKS, 0);
                        dmtdlych = getValueFromArray(getCHECKS, 1);
                        dmtdtych = getValueFromArray(getCHECKS, 2);
                    }
                }

                if (obj.has("PRINT") && !obj.isNull("PRINT")) {
                    JSONArray getPRINT = obj.getJSONArray("PRINT");
                    if (getPRINT.length() > 0) {
                        dmthlypr = getValueFromArray(getPRINT, 0);
                        dmtdlypr = getValueFromArray(getPRINT, 1);
                        dmtdtypr = getValueFromArray(getPRINT, 2);
                    }
                }
                if (obj.has("STRIPES") && !obj.isNull("STRIPES")) {
                    JSONArray getSTRIPES = obj.getJSONArray("STRIPES");
                    if (getSTRIPES.length() > 0) {
                        dmthlyst = getValueFromArray(getSTRIPES, 0);
                        dmtdlyst = getValueFromArray(getSTRIPES, 1);
                        dmtdtyst = getValueFromArray(getSTRIPES, 2);
                    }
                }



                double dtotlymth = dmthlypl + dmthlych + dmthlypr + dmthlyst;

                if (dtotlymth == 0) {
                    dpllyper = 0;
                    dchlyper = 0;
                    dprlyper = 0;
                    dstlyper = 0;

                } else {
                    dpllyper = (dmthlypl / dtotlymth) * 100;
                    dchlyper = (dmthlych / dtotlymth) * 100;
                    dprlyper = (dmthlypr / dtotlymth) * 100;
                    dstlyper = (dmthlyst / dtotlymth) * 100;

                }

                double dtottymtd = dmtdtypl + dmtdtych + dmtdtypr + dmtdtyst;

                if (dtottymtd == 0) {
                    dpltyper = 0;
                    dchtyper = 0;
                    dprtyper = 0;
                    dsttyper = 0;

                } else {
                    dpltyper = (dmtdtypl / dtottymtd) * 100;
                    dchtyper = (dmtdtych / dtottymtd) * 100;
                    dprtyper = (dmtdtypr / dtottymtd) * 100;
                    dsttyper = (dmtdtyst / dtottymtd) * 100;
                }



                // Check for valid last bill date

                // Set parsed values to UI elements
                setTextFieldsBSH(dmthlypl,dmthlych,dmthlypr,dmthlyst, dmtdlypl,dmtdlych,dmtdlypr,dmtdlyst,
                        dpllyper,dchlyper,dprlyper,dstlyper,
                        dmtdtypl,dmtdtych,dmtdtypr,dmtdtyst,
                        dpltyper, dchtyper,dprtyper,dsttyper
                );


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBTRBillData(JSONObject obj) {

        try {
            if (obj.length() > 0) {
                double dmthlytap=0,dmthlyski=0, dmtdlytap=0,dmtdlyski=0, dmtdtytap=0,dmtdtyski=0;

                if (obj.has("TAPERED") && !obj.isNull("TAPERED")) {
                    JSONArray getTAPERED = obj.getJSONArray("TAPERED");
                    if (getTAPERED.length() > 0) {
                        dmthlytap    = getValueFromArray(getTAPERED, 0);
                        dmtdlytap    = getValueFromArray(getTAPERED, 1);
                        dmtdtytap    = getValueFromArray(getTAPERED, 2);

                    }
                }
                if (obj.has("SKINNY") && !obj.isNull("SKINNY")) {
                    JSONArray getSKINNY = obj.getJSONArray("SKINNY");
                    if (getSKINNY.length() > 0) {
                        dmthlyski = getValueFromArray(getSKINNY, 0);
                        dmtdlyski = getValueFromArray(getSKINNY, 1);
                        dmtdtyski = getValueFromArray(getSKINNY, 2);
                    }
                }

                double dtotlymth = dmthlytap + dmthlyski ;
                double dlytapper,dlyskiper,dtytapper,dtyskiper;

                if (dtotlymth == 0) {
                    dlytapper = 0;
                    dlyskiper = 0;
                } else {
                    dlytapper = (dmthlytap / dtotlymth) * 100;
                    dlyskiper = (dmthlyski / dtotlymth) * 100;
                }


                double dtottymtd = dmtdtytap + dmtdtyski ;
                if (dtottymtd == 0) {
                    dtytapper = 0;
                    dtyskiper = 0;
                } else {
                    dtytapper = (dmtdtytap / dtottymtd) * 100;
                    dtyskiper = (dmtdtyski / dtottymtd) * 100;
                }

                // Set parsed values to UI elements
                setTextFieldsBTR(dmthlytap,dmthlyski, dmtdlytap,dmtdlyski,dlytapper,dlyskiper,dmtdtytap,dmtdtyski,dtytapper,dtyskiper
                );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------- TABLE OPERATIONS ----------------------------

    private void addTableRow() {
        TableRow newRow = createNewTableRow();
        binding.comContent.addView(newRow);
    }

    private void removeLastTableRow() {
        int rowCount = binding.comContent.getChildCount();
        if (rowCount > 2) { // Keep at least one data row
            binding.comContent.removeViewAt(rowCount - 1);
        }
    }

    private TableRow createNewTableRow() {

        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        addTextInputToRow(newRow, R.id.rank, InputType.TYPE_CLASS_NUMBER);
        addSpinnerToRow(newRow, R.id.cbrand);
        addTextInputToRow(newRow, R.id.qty, InputType.TYPE_CLASS_NUMBER);
        addTextInputToRow(newRow, R.id.nsv, InputType.TYPE_CLASS_NUMBER);
        addTextInputToRow(newRow, R.id.soh, InputType.TYPE_CLASS_NUMBER);
        addTextInputToRow(newRow, R.id.remarks, InputType.TYPE_TEXT_VARIATION_NORMAL);

        return newRow;
    }

    private void addTextInputToRow(TableRow row, int id, int inputType) {
        TextInputEditText textField = new TextInputEditText(this);
        textField.setId(id);
        textField.setInputType(inputType);
        row.addView(textField);
    }

    private void addSpinnerToRow(TableRow row, int id) {
        Spinner spinner = new Spinner(this);
        spinner.setId(id);
        spinner.setAdapter(adapter);
        row.addView(spinner);
    }

    // ---------------------------- UTILITIES ----------------------------

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
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

    private interface ResponseHandler {
        void handleResponse(JSONObject response) throws JSONException;
    }

    private void setHeaderClickListeners() {
        setHeaderClickListener(binding.salesHeader, binding.salesContent);
        setHeaderClickListener(binding.ltlHeader, binding.ltlContent);
        setHeaderClickListener(binding.mtdHeader, binding.mtdContent);
        setHeaderClickListener(binding.prdHeader, binding.prdContent);
        setHeaderClickListener(binding.bshHeader, binding.bshContent);
        setHeaderClickListener(binding.btrHeader, binding.btrContent);
        setHeaderClickListener(binding.aprlHeader, binding.aprlContent);
    }

    private void setHeaderClickListener(View header, View content) {
        header.setOnClickListener(v -> content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
    }

        private void collectAndSendTableData() {
        List<TableRowData> tableRowDataList = new ArrayList<>();
        boolean isValid = true;

        // Iterate through all rows except the header row
        for (int i = 1; i < binding.comContent.getChildCount(); i++) {
            View rowView = binding.comContent.getChildAt(i);
            Log.d("RowDebug", "Row index " + i + ": " + rowView.getClass().getSimpleName());
            if (rowView instanceof TableRow) {
                TableRow tableRow = (TableRow) rowView;

                // Extract data from each field
                TextInputEditText rankField = tableRow.findViewById(R.id.rank);
                Spinner brandSpinner = tableRow.findViewById(R.id.cbrand);
                TextInputEditText qtyField = tableRow.findViewById(R.id.qty);
                TextInputEditText nsvField = tableRow.findViewById(R.id.nsv);
                TextInputEditText sohField = tableRow.findViewById(R.id.soh);
                TextInputEditText remarksField = tableRow.findViewById(R.id.remarks);

                String rank = rankField != null ? rankField.getText().toString().trim() : "";
                String brand = brandSpinner != null && brandSpinner.getSelectedItem() != null
                        ? brandSpinner.getSelectedItem().toString() : "";
                String qty = qtyField != null ? qtyField.getText().toString().trim() : "";
                String nsv = nsvField != null ? nsvField.getText().toString().trim() : "";
                String soh = sohField != null ? sohField.getText().toString().trim() : "";
                String remarks = remarksField != null ? remarksField.getText().toString().trim() : "";
                Log.d("RankValueDebug", "Rank value in row " + i + ": " + (rankField != null ? rankField.getText().toString() : "null"));
                // Check for empty fields
                if (rank.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "Rank is empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                } else if (brand.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "Brand is empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                } else if (qty.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "Quantity is empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                } else if (nsv.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "NSV is empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                } else if (soh.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "SOH is empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                } else if (remarks.isEmpty()) {
                    isValid = false;
                    Toast.makeText(this, "Remarks are empty in row " + (i), Toast.LENGTH_SHORT).show();
                    break;
                }

                // Add to the list
                TableRowData rowData = new TableRowData(strid, tmcode, visitdate, rank, brand, qty, nsv, soh, remarks);
                tableRowDataList.add(rowData);
            }
        }

        if (isValid) {
            sendDataToAPI(tableRowDataList);
            progressDialog.show(getSupportFragmentManager(), "tag");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void sendDataToAPI(List<TableRowData> tableRowDataList) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (TableRowData rowData : tableRowDataList) {
                jsonArray.put(rowData.toJSON());
            }
            System.out.println(jsonArray);

            String url = retrofit.baseUrl() + "saveData";
            mRequestQueue = Volley.newRequestQueue(this);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.POST,
                    url,
                    jsonArray, // Request body
                    response -> {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                if (jsonObject.has("message") &&
                                        jsonObject.getString("message").equals("Data saved successfully.")) {
                                    progressDialog.dismiss();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                                    // Navigate to the next activity
                                    Intent intent = new Intent(this, compSaleRecordSaved.class);
                                    intent.putExtra("name",user_name);
                                    intent.putExtra("mobile",phoneno);
                                    intent.putExtra("bio",bio);
                                    intent.putExtra("tmcode",tmcode);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }else{
                                    progressDialog.dismiss();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }
                            Toast.makeText(this, "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            int statusCode = error.networkResponse.statusCode;
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(this, "Error: " + statusCode + " - " + errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(this, "Unknown error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            mRequestQueue.add(jsonArrayRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Preparing Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeTableRow() {
        int rowCount = binding.comContent.getChildCount();
        if (rowCount > 2) { // Keep at least one data row
            binding.comContent.removeViewAt(rowCount - 1);
        }
    }

    private double getOnlyDayValue() {
        return LocalDate.now().getDayOfMonth();
    }

    private void populateTableLayout(ArrayList<String> comsalelist) {
        binding.comContent.removeViews(1, binding.comContent.getChildCount() - 1);

        for (String data : comsalelist) {
            // Split the data string into individual components
            String[] values = data.split(":");
            if (values.length != 6) {
                Log.e("DataError", "Invalid data format: " + data);
                continue;
            }

            // Create a new TableRow
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Add TextViews for each column
            tableRow.addView(createTextView(values[0])); // Rank
            tableRow.addView(createTextView(values[1])); // Brand
            tableRow.addView(createTextView(values[2])); // Qty
            tableRow.addView(createTextView(values[3])); // NSV
            tableRow.addView(createTextView(values[4])); // SOH
            tableRow.addView(createTextView(values[5])); // Remarks

            // Add the TableRow to the TableLayout
            binding.comContent.addView(tableRow);
        }
    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color
        textView.setPadding(5, 5, 5, 5); // Add some padding
        textView.setBackgroundResource(R.drawable.thborder); // Add border
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Center alignment
        return textView;
    }


    private void setTextFieldsBTR(double dmthlytap, double dmthlyski, double dmtdlytap, double dmtdlyski, double dlytapper, double dlyskiper, double dmtdtytap, double dmtdtyski, double dtytapper, double dtyskiper) {
        double[] values = {dmthlytap,dmthlyski, dmtdlytap,dmtdlyski,dlytapper,dlyskiper,dmtdtytap,dmtdtyski,dtytapper,dtyskiper};

        TextView[] textViews = { binding.mthlytap,binding.mthlyski,binding.mtdlytap,binding.mtdlyski,
                binding.lytapper,binding.lyskiper,binding.mtdtytap,binding.mtdtyski,
                binding.tytapper,binding.tyskiper
        };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }

    private void setTextFieldsBSH(double dmthlypl, double dmthlych, double dmthlypr, double dmthlyst,
                                  double dmtdlypl, double dmtdlych, double dmtdlypr, double dmtdlyst,
                                  double dpllyper, double dchlyper, double dprlyper, double dstlyper,
                                  double dmtdtypl, double dmtdtych, double dmtdtypr, double dmtdtyst,
                                  double dpltyper, double dchtyper, double dprtyper, double dsttyper) {
        double[] values = { dmthlypl,dmthlych,dmthlypr,dmthlyst,
                dmtdlypl,dmtdlych,dmtdlypr,dmtdlyst,
                dpllyper,dchlyper,dprlyper,dstlyper,
                dmtdtypl,dmtdtych,dmtdtypr,dmtdtyst,
                dpltyper,dchtyper,dprtyper,dsttyper};

        TextView[] textViews = { binding.mthlypl,binding.mthlych,binding.mthlypr,binding.mthlyst,
                binding.mtdlypl,binding.mtdlych,binding.mtdlypr,binding.mtdlyst,
                binding.pllyper,binding.chlyper,binding.prlyper,binding.stlyper,
                binding.mtdtypl,binding.mtdtych,binding.mtdtypr,binding.mtdtyst,
                binding.pltyper,binding.chtyper,binding.prtyper,binding.sttyper

        };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }
    }


    private void setTextFieldsDB(double dmthbshlynsv, double dmtdbshlynsv, double dtgtbshtynsv, double dmtdbshtynsv, double dmthbtrlynsv,
                                 double dmtdbtrlynsv, double dtgtbtrtynsv, double dmtdbtrtynsv, double dmthbtslynsv, double dmtdbtslynsv,
                                 double dtgtbtstynsv, double dmtdbtstynsv, double dmthbjnlynsv, double dmtdbjnlynsv, double dtgtbjntynsv,
                                 double dmtdbjntynsv, double dlybshper, double dlybtsper, double dlybtrper, double dlybjnper,
                                 double dtybshper, double dtybtsper, double dtybtrper, double dtybjnper,
                                 double dmtdtvabsh, double dmtdtvabtr,double dmtdtvabts, double dmtdtvabjn,
                                 double dexcdefbsh, double dexcdefbtr, double dexcdefbts, double dexcdefbjn, double dmthlypromo,
                                 double dmtdlypromo, double dtgttypromo, double dmtdtypromo, double dlypromoper, double dtypromoper,
                                 double dmtdtvapromo, double dexcdefpromo,double dlymtdappr, double dtymtdappr) {

        double[] values = {dmthbshlynsv, dmtdbshlynsv, dtgtbshtynsv, dmtdbshtynsv, dmthbtrlynsv, dmtdbtrlynsv, dtgtbtrtynsv,
                dmtdbtrtynsv, dmthbtslynsv, dmtdbtslynsv, dtgtbtstynsv, dmtdbtstynsv, dmthbjnlynsv, dmtdbjnlynsv, dtgtbjntynsv,
                dmtdbjntynsv, dlybshper, dlybtsper, dlybtrper, dlybjnper, dtybshper, dtybtsper, dtybtrper, dtybjnper,
                dmtdtvabsh, dmtdtvabtr, dmtdtvabts, dmtdtvabjn, dexcdefbsh, dexcdefbtr, dexcdefbts, dexcdefbjn, dmthlypromo,
                dmtdlypromo, dtgttypromo, dmtdtypromo, dlypromoper, dtypromoper, dmtdtvapromo, dexcdefpromo, dlymtdappr, dtymtdappr };

        TextView[] textViews = {
                binding.mthlybsh,binding.mtdlybsh,binding.tgttybsh,binding.mtdtybsh,
                binding.mthlybtr,binding.mtdlybtr,binding.tgttybtr,binding.mtdtybtr,
                binding.mthlybts,binding.mtdlybts,binding.tgttybts,binding.mtdtybts,
                binding.mthlybjn,binding.mtdlybjn,binding.tgttybjn,binding.mtdtybjn,
                binding.lybshper,binding.lybtsper,binding.lybtrper,binding.lybjnper,
                binding.tybshper,binding.tybtsper,binding.tybtrper,binding.tybjnper,
                binding.mtdtvabsh,binding.mtdtvabtr,binding.mtdtvabts,binding.mtdtvabjn,
                binding.excdefbsh,binding.excdefbtr,binding.excdefbts,binding.excdefbjn,
                binding.mthlypromo,binding.mtdlypromo,binding.tgttypromo,binding.mtdtypromo,
                binding.lypromoper,binding.typromoper,binding.mtdtvapromo,binding.excdefpromo,
                binding.lymtdappr,binding.tymtdappr
        };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }

    }


    private void setTextFields(double dmthtgtqty, double dmthtgtval, double dmthachqty, double dmthachval,
                               double dmtdtgtqty, double dmtdtgtval, double dltlmthqty, double dltlmthnsv,
                               double dltlmtdqty, double dltlmtdnsv) {

        // Helper method to set values with color
        double[] values = {dmthtgtqty, dmthtgtval, dmthachqty, dmthachval,
                dmtdtgtqty, dmtdtgtval, dmthachqty, dmthachval,
                dltlmthqty, dltlmthnsv, dltlmtdqty, dltlmtdnsv};

        TextView[] textViews = {
                binding.mthtgtqty, binding.mthtgtnsv, binding.mthachqty, binding.mthachnsv,
                binding.mtdtgtqty, binding.mtdtgtnsv, binding.mtdachqty, binding.mtdachnsv,
                binding.ltlmthqty, binding.ltlmthnsv, binding.ltlmtdqty, binding.ltlmtdnsv
        };

        // Set text with color for all related TextViews
        for (int i = 0; i < textViews.length; i++) {
            setTextWithColor(textViews[i], values[i]);
        }

    }
    private double parseValue(JSONObject obj, String key, int index) throws JSONException {
        JSONArray array = obj.optJSONArray(key);
        return (array != null && array.length() > index) ? array.optDouble(index, 0) : 0;
    }

    private String formatDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                    .format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr));
        } catch (ParseException e) {
            return "Invalid Date";
        }
    }

    private void setupBrandSpinner() {
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, brandList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cbrand.setAdapter(adapter);
    }
    private void setCalculatedFields(double dltlmtdnsv, double dltlmtdqty, double dmthachval, double dmthachqty,
                                     double dltlmthqty, double dltlmthnsv) {
        setFieldWithCalculation(binding.mtdlyasp, dltlmtdnsv, dltlmtdqty, false);
        setFieldWithCalculation(binding.mtdtyasp, dmthachval, dmthachqty, false);

        setFieldWithCalculation(binding.mtdlyaspd, dltlmtdnsv, getOnlyDayValue(), false);
        setFieldWithCalculation(binding.mtdtyaspd, dmthachval, getOnlyDayValue(), false);

        setFieldWithCalculation(binding.mtdlyspsf, dltlmtdnsv / carea, getOnlyDayValue(), false);
        setFieldWithCalculation(binding.mtdtyspsf, dmthachval / carea, getOnlyDayValue(), false);

        setFieldWithCalculation(binding.ltlmthqtyper, dmthachqty - dltlmthqty, dltlmthqty, true);
        setFieldWithCalculation(binding.ltlmthnsvper, dmthachval - dltlmthnsv, dltlmthnsv, true);
        setFieldWithCalculation(binding.ltlmtdqtyper, dmthachqty - dltlmtdqty, dltlmtdqty, true);
        setFieldWithCalculation(binding.ltlmtdnsvper, dmthachval - dltlmtdnsv, dltlmtdnsv, true);

    }

    private void setFieldWithCalculation(TextView textView, double numerator, double denominator, boolean isPercentage) {
        double value = denominator != 0 ? (isPercentage ? (numerator / denominator) * 100 : numerator / denominator) : 0;
        setTextWithColor(textView, value);
    }

    private void setPercentageFields(double dmthqtyper, double dmthnsvper, double dmtdqtyper, double dmtdnsvper) {
        setTextWithColor(binding.mthqtyper, dmthqtyper);
        setTextWithColor(binding.mthnsvper, dmthnsvper);
        setTextWithColor(binding.mtdqtyper, dmtdqtyper);
        setTextWithColor(binding.mtdnsvper, dmtdnsvper);

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
    private void updateFieldWithCalculation(TextView field, TextView target1, TextView target2) {
        try {
            double value1 = Double.parseDouble(target1.getText().toString());
            double value2 = Double.parseDouble(target2.getText().toString());
            double difference = value1 - value2;
            setFieldWithCalculation(field, difference, value2, true);
        } catch (NumberFormatException e) {
            // Handle the exception, for example, by setting default values or logging the error
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

    private double calculatePercentage(double part, double total) {
        return total != 0 ? (part / total) * 100 : 0;
    }

    private void interneterror() {
        new SweetAlertDialog(lfo_sales_view.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Turn On Internet")
                .setConfirmText("Settings")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();

    }


}