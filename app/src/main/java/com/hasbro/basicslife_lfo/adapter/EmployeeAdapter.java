package com.hasbro.basicslife_lfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.lfo_Employee_Details;
import com.hasbro.basicslife_lfo.pojo.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private List<Employee> employeeList;
    private Context context;

    public EmployeeAdapter(List<Employee> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.firstName.setText(employee.getFirstName());
        holder.empCode.setText(employee.getEmpCode());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, lfo_Employee_Details.class);
            intent.putExtra("employeeDetails", employee.getFullDetails().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, empCode;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.firstname);
            empCode = itemView.findViewById(R.id.empcode);
        }
    }
}
