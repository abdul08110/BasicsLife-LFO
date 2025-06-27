package com.hasbro.basicslife_lfo;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.hasbro.basicslife_lfo.databinding.SellThrowDetailsBinding;
import com.hasbro.basicslife_lfo.databinding.SellthrowViewBinding;

public class sell_throw_details extends AppCompatActivity {
String itemcode, season, design, colour, fit_name, collar;
double mrp, soldQty, stockQty, sellThrough, grn_qty, dis_qty, mrp_value, net_sale, discount_value, dispercen, rateofsale, noddays;
private SellThrowDetailsBinding binding;

ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SellThrowDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemcode = getIntent().getStringExtra("itemCode");
        season = getIntent().getStringExtra("season");
        mrp = getIntent().getDoubleExtra("mrp", 0);
        soldQty = getIntent().getIntExtra("soldQty", 0);
        stockQty = getIntent().getIntExtra("stockQty", 0);
        sellThrough = getIntent().getDoubleExtra("sellThrough", 0);
        design = getIntent().getStringExtra("design");
        colour = getIntent().getStringExtra("colour");
        fit_name = getIntent().getStringExtra("fit_name");
        collar = getIntent().getStringExtra("collar");
        grn_qty = getIntent().getDoubleExtra("grn_qty", 0);
        dis_qty = getIntent().getDoubleExtra("dis_qty", 0);
        mrp_value = getIntent().getDoubleExtra("mrp_value", 0);
        net_sale = getIntent().getDoubleExtra("net_sale", 0);
        discount_value = getIntent().getDoubleExtra("discount_value", 0);
        dispercen = getIntent().getDoubleExtra("discount_perc", 0);
        rateofsale = getIntent().getDoubleExtra("rate_of_sale", 0);
        noddays = getIntent().getDoubleExtra("no_of_days", 0);

        binding.textItemCode.setText(itemcode);
        binding.textSeason.setText(season);
        binding.textMrp.setText(String.valueOf(mrp));
        binding.textSoldQty.setText(String.valueOf(soldQty));
        binding.textStockQty.setText(String.valueOf(stockQty));
        binding.textSellThrough.setText(String.format("%.2f%%", sellThrough));
        binding.textDesign.setText(design);
        binding.textColour.setText(colour);
        binding.textFitName.setText(fit_name);
        binding.textCollar.setText(collar);
        binding.textGrnQty.setText(String.valueOf(grn_qty));
        binding.textActualGrn.setText(String.valueOf((int) (soldQty + stockQty)));
        binding.textDisQty.setText(String.valueOf(dis_qty));
        binding.textMrpValue.setText(String.valueOf(mrp_value));
        binding.textNetSale.setText(String.format("%.2f%%", net_sale));
        binding.textDiscountValue.setText(String.format("%.2f%%",discount_value));
        binding.textDiscountPerc.setText(String.format("%.2f%%", dispercen));
        binding.textRateOfSale.setText(String.format("%.2f%%", rateofsale));
        binding.textNod.setText(String.valueOf(noddays));


        String urlString = "https://bl-pim.s3.ap-southeast-1.amazonaws.com/web/1000x1500/f/" + itemcode + "F.jpg";
        Glide.with(sell_throw_details.this).load(urlString).fallback(R.drawable.empty).error(R.drawable.empty).into(binding.productImage);
    }
}