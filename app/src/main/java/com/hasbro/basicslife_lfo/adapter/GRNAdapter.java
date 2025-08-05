package com.hasbro.basicslife_lfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.pojo.GRNModel;

import java.util.List;

public class GRNAdapter extends RecyclerView.Adapter<GRNAdapter.GRNViewHolder> {

    private final List<GRNModel> grnList;

    public GRNAdapter(List<GRNModel> grnList) {
        this.grnList = grnList;
    }

    @NonNull
    @Override
    public GRNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grn, parent, false);
        return new GRNViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GRNViewHolder holder, int position) {
        GRNModel grn = grnList.get(position);

        holder.txtGrnTitle.setText("GRN " + (position + 1));
        holder.editPoNo.setText(grn.getPoNo());
        holder.editInvoiceNo.setText(grn.getInvoiceNo());
        holder.editInvoiceQty.setText(grn.getInvoiceQty());
        holder.editLrNo.setText(grn.getLrNo());
        holder.editGrnQty.setText(grn.getGrnQty());
        holder.editGrnDate.setText(grn.getGrnDate());
        holder.editGrnNo.setText(grn.getGrnNo());
    }

    @Override
    public int getItemCount() {
        return grnList.size();
    }

    public List<GRNModel> getUpdatedGRNList() {
        return grnList;
    }

    static class GRNViewHolder extends RecyclerView.ViewHolder {

        TextView txtGrnTitle;
        TextView editPoNo, editInvoiceNo, editInvoiceQty, editLrNo;
        TextView editGrnQty, editGrnDate, editGrnNo;

        public GRNViewHolder(@NonNull View itemView) {
            super(itemView);

            txtGrnTitle = itemView.findViewById(R.id.txtGrnTitle);
            editPoNo = itemView.findViewById(R.id.editPoNo);
            editInvoiceNo = itemView.findViewById(R.id.editInvoiceNo);
            editInvoiceQty = itemView.findViewById(R.id.editInvoiceQty);
            editLrNo = itemView.findViewById(R.id.editLrNo);
            editGrnQty = itemView.findViewById(R.id.editGrnQty);
            editGrnDate = itemView.findViewById(R.id.editGrnDate);
            editGrnNo = itemView.findViewById(R.id.editGrnNo);
        }
    }
}
