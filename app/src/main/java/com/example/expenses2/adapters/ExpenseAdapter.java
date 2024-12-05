package com.example.expenses2.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenses2.R;
import com.example.expenses2.models.Expense;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses = new ArrayList<>();
    private final Consumer<Expense> onDeleteClick;

    public ExpenseAdapter(Consumer<Expense> onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense currentExpense = expenses.get(position);
        holder.textCategory.setText(currentExpense.category);
        holder.textAmount.setText(String.format(Locale.getDefault(), "%.2f ₽", currentExpense.amount));
        holder.textDate.setText(DateFormat.format("dd.MM.yyyy", currentExpense.date));

        // Удаление расхода по клику
        holder.itemView.setOnLongClickListener(v -> {
            onDeleteClick.accept(currentExpense);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textCategory;
        private final TextView textAmount;
        private final TextView textDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category);
            textAmount = itemView.findViewById(R.id.text_amount);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}


