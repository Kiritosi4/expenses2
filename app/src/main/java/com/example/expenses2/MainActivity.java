package com.example.expenses2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenses2.adapters.ExpenseAdapter;
import com.example.expenses2.models.Expense;
import com.example.expenses2.viewmodels.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter adapter;
    private TextView textTotalExpense;
    private Spinner categorySpinnerFilter;
    private PieChart pieChart;

    static final long oneDayMillis = 24 * 60 * 60 * 1000;
    private long selectedTimeRange = oneDayMillis;
    private String selectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerFilter = findViewById(R.id.spinner_filter);
        categorySpinnerFilter = findViewById(R.id.spinner_category);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fabAddExpense = findViewById(R.id.fab_add_expense);

        pieChart = findViewById(R.id.chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Расходы");

        // Настройка RecyclerView
        adapter = new ExpenseAdapter(expense  -> {
            expenseViewModel.delete(expense);
            Toast.makeText(this, "Расход удалён", Toast.LENGTH_SHORT).show();
            UpdateUI();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Получение ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.getAllExpenses().observe(this, adapter::setExpenses);

        textTotalExpense = findViewById(R.id.text_total_expense);

        // Фильтр времени
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long now = System.currentTimeMillis();
                long oneDayMillis = 24 * 60 * 60 * 1000;

                switch (position) {
                    case 0: // Сегодня
                        selectedTimeRange = now - oneDayMillis;
                        break;
                    case 1: // Эта неделя
                        selectedTimeRange = now - oneDayMillis * 7;
                        break;
                    case 2: // Этот месяц
                        selectedTimeRange = now - oneDayMillis * 30;
                        break;
                    default:
                        selectedTimeRange = 0;
                }

                UpdateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Фильтр категорий
        categorySpinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory =  categorySpinnerFilter.getSelectedItem().toString();
                UpdateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Переход на экран добавления
        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    private void UpdateUI() {
        if(selectedTimeRange != 0) {
            long now = System.currentTimeMillis();
            expenseViewModel.getExpensesByDateRange(selectedTimeRange, now).observe(MainActivity.this, this::UpdateUIElements);
        }else{
            expenseViewModel.getAllExpenses().observe(MainActivity.this, this::UpdateUIElements);
        }
    }

    private void UpdateUIElements(List<Expense> expenses){
        List<Expense> filteredExpenses = new ArrayList<>();
        if(Objects.equals(selectedCategory, "") || Objects.equals(selectedCategory, "Без категории")){
            filteredExpenses = expenses;
        }else{
            for (Expense expense : expenses) {
                if(Objects.equals(expense.category, selectedCategory)){
                    filteredExpenses.add(expense);
                }
            }
        }

        adapter.setExpenses(filteredExpenses);
        UpdateSummaryTextAndCharts(filteredExpenses);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUI();
    }

    private void UpdateSummaryTextAndCharts(List<Expense> expensesList) {
        double totalExpense = 0;

        Map<String, Double> expensesMap = new HashMap<String, Double>();

        if(expensesList != null){
            for (Expense expense : expensesList) {
                totalExpense += expense.amount;
                expensesMap.put(expense.category, expensesMap.getOrDefault(expense.category, 0.0) + expense.amount);
            }
        }

        textTotalExpense.setText(String.format(Locale.getDefault(), "Итого: %.2f ₽", totalExpense));

        List<PieEntry> pieDataList = new ArrayList<>();
        for(Map.Entry<String, Double> item : expensesMap.entrySet()){
            double val = item.getValue();
            String key = item.getKey();
            pieDataList.add(new PieEntry((float) val, key));
        }

        PieDataSet pieDataSet = new PieDataSet(pieDataList, "Расходы");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(10f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

}
