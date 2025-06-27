package com.hasbro.basicslife_lfo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.pojo.SellThroughItem;

import java.util.List;

public class SellThroughAdapter extends RecyclerView.Adapter<SellThroughAdapter.ViewHolder> {
    private List<SellThroughItem> itemList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public SellThroughAdapter(Context context, List<SellThroughItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    // Define an interface for click events
    public interface OnItemClickListener {
        void onItemClick(SellThroughItem item);
    }
    // Setter method for click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = (OnItemClickListener) listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sell_through, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellThroughItem item = itemList.get(position);
        holder.textItemCode.setText(item.getItemCode());
        holder.textSeason.setText(item.getSeason());
        holder.textMrp.setText(String.valueOf(item.getMrp()));
        holder.textSoldQty.setText(String.valueOf(item.getSoldQty()));
        holder.textStockQty.setText(String.valueOf(item.getStockQty()));
        holder.textSellThrough.setText(String.format("%.2f%%", item.getSellThrough()));
        // Load Image Using Glide
        if (context != null && holder.itemImage.getContext() != null) {
            Glide.with(holder.itemImage.getContext()) //
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.empty) // Show while loading
                    .error(R.drawable.empty) // Show if URL fails
                    .into(holder.itemImage);
        } else {
            Log.e("GlideError", "Context is null or ImageView is detached");
        }

        // Set click listener on itemView
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView textItemCode, textSeason, textMrp, textSoldQty, textStockQty, textSellThrough;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            textItemCode = itemView.findViewById(R.id.text_item_code);
            textSeason = itemView.findViewById(R.id.text_season);
            textMrp = itemView.findViewById(R.id.text_mrp);
            textSoldQty = itemView.findViewById(R.id.text_sold_qty);
            textStockQty = itemView.findViewById(R.id.text_stock_qty);
            textSellThrough = itemView.findViewById(R.id.text_sell_through);
        }
    }
}

