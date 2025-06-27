package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.hasbro.basicslife_lfo.adapter.SellThroughAdapter;
import com.hasbro.basicslife_lfo.databinding.SellthrowViewBinding;
import com.hasbro.basicslife_lfo.pojo.SellThroughItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class sellthrow_view extends AppCompatActivity {
private SellthrowViewBinding binding;
    private RecyclerView recyclerView;
    private SellThroughAdapter adapter;
    private List<SellThroughItem> itemList;
    private RequestQueue requestQueue;

    String strcode, season, design, brandCode, productCode, fitCode, itemcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SellthrowViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fetch data from API
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        adapter = new SellThroughAdapter(sellthrow_view.this, itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        initializeIntentData();
        fetchData();
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(sellthrow_view.this, sell_throw_details.class);
            intent.putExtra("itemCode", item.getItemCode());
            intent.putExtra("season", item.getSeason());
            intent.putExtra("mrp", item.getMrp());
            intent.putExtra("soldQty", item.getSoldQty());
            intent.putExtra("stockQty", item.getStockQty());
            intent.putExtra("sellThrough", item.getSellThrough());
            intent.putExtra("design", item.getDesign());
            intent.putExtra("colour", item.getColour());
            intent.putExtra("fit_name", item.getFit_name());
            intent.putExtra("collar", item.getCollar());
            intent.putExtra("grn_qty", item.getGrn_qty());
            intent.putExtra("dis_qty", item.getDis_qty());
            intent.putExtra("mrp_value", item.getMrp_value());
            intent.putExtra("net_sale", item.getNet_sale());
            intent.putExtra("discount_value", item.getDiscount_value());
            intent.putExtra("discount_perc", item.getDispercen());
            intent.putExtra("rate_of_sale", item.getRateofsale());
            intent.putExtra("no_of_days", item.getNoddays());
            startActivity(intent);
        });

    }
    private void initializeIntentData() {
        // Fetch Intent Data

        strcode = getIntent().getStringExtra("strid");
        season = getIntent().getStringExtra("season");
        design = getIntent().getStringExtra("design");
        brandCode = getIntent().getStringExtra("brandCode");
        productCode = getIntent().getStringExtra("productCode");
        fitCode = getIntent().getStringExtra("fitCode");
        itemcode = getIntent().getStringExtra("itemcode");
        // Pass fetched data to helper methods
    }

    private void fetchData() {
        String API_URL = retrofit.baseUrl() + "getSellThroughData";
        String url = API_URL + "?strid=" + strcode + "&season=" + season;

        if (design != null) url += "&design=" + design;
        if (brandCode != null) url += "&brandCode=" + brandCode;
        if (productCode != null) url += "&productCode=" + productCode;
        if (fitCode != null) url += "&fitCode=" + fitCode;
        if (itemcode != null) url += "&itemCode=" + itemcode;
        System.out.println("abdul "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> parseJsonResponse(response),
                error -> {
                    Toast.makeText(this, "Error Fetching Data", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", error.toString());
                });

        requestQueue.add(request);
    }


    private void parseJsonResponse(JSONObject response) {

        try {
            Iterator<String> keys = response.keys(); // Get keys as an Iterator
            double totalDispatch = 0, totalGRN = 0, totalSold = 0, totalStock = 0;
            double totalMRP = 0, totalNetSale = 0, totalDiscount = 0, totalROS = 0;

            while (keys.hasNext()) { // Iterate using while loop
                String key = keys.next();
                JSONArray jsonArray = response.getJSONArray(key);

                SellThroughItem item = new SellThroughItem(
                        key, // Item Code
                        jsonArray.getString(0), // Season
                        jsonArray.getInt(1), // MRP
                        jsonArray.getInt(6), // Sold Qty
                        jsonArray.getInt(7), // Stock Qty
                        jsonArray.getDouble(13), // Sell Through
                        jsonArray.getString(2), // design
                        jsonArray.getString(3), // colour
                        jsonArray.getString(4), // fit_name
                        jsonArray.getString(5), // collar
                        jsonArray.getDouble(8),// grn_qty
                        jsonArray.getDouble(9), // dis_qty
                        jsonArray.getDouble(10), // mrp_value
                        jsonArray.getDouble(11), // net_sale
                        jsonArray.getDouble(12), // discount_value
                        jsonArray.getDouble(14), // dispercen
                        jsonArray.getDouble(15), // rateofsale
                        jsonArray.getDouble(16) // noddays


                );

                itemList.add(item);

                // **Accumulate totals**
                totalDispatch += jsonArray.getDouble(9); // Dispatch Qty
                totalGRN += jsonArray.getDouble(8); // GRN Qty
                totalSold += jsonArray.getDouble(6); // Sold Qty
                totalStock += jsonArray.getDouble(7); // Stock Qty
                totalMRP += jsonArray.getDouble(10); // MRP Value
                totalNetSale += jsonArray.getDouble(11); // Net Sale
                totalDiscount += jsonArray.getDouble(12); // Discount Value
                totalROS += jsonArray.getDouble(15); // Rate of Sale

            }

            // **Set values to TextViews in TableLayout**
            binding.textTotalDispatch.setText(String.valueOf(totalDispatch));
            binding.textTotalGRN.setText(String.valueOf(totalGRN));
            binding.textActualGRN.setText(String.valueOf(totalSold+totalStock)); // Assuming actual GRN is the same as total GRN
            binding.textTotalSold.setText(String.valueOf(totalSold));
            binding.textTotalStock.setText(String.valueOf(totalStock));
            binding.textTotalROS.setText(String.format("%.2f",totalROS));
            binding.textsellThru.setText(String.format("%.2f",(totalSold / totalGRN) * 100) + " %"); // Sell Through Percentage
            binding.textTotalMRP.setText(String.valueOf(totalMRP));
            binding.textTotalNetSale.setText(String.valueOf(totalNetSale));
            binding.textTotalDiscount.setText(String.format("%.2f",totalDiscount));
            binding.textTotalDiscountperc.setText(String.format("%.2f", (totalDiscount / totalMRP) * 100) + " %"); // Discount %


            adapter.notifyDataSetChanged(); // Update RecyclerView
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}