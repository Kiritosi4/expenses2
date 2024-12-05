package com.example.expenses2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.expenses2.models.Expense;
import com.example.expenses2.viewmodels.ExpenseViewModel;

public class AddExpenseActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText editAmount;
    private ExpenseViewModel expenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        spinnerCategory = findViewById(R.id.spinner_category);
        editAmount = findViewById(R.id.edit_amount);
        Button btnSave = findViewById(R.id.btn_save);

        // Получение ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Сохранение расхода
        btnSave.setOnClickListener(v -> {
            String category = spinnerCategory.getSelectedItem().toString();
            String amountStr = editAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.equals("Без категории")) {
                Toast.makeText(this, "Выберите категорию", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            Expense expense = new Expense();
            expense.category = category;
            expense.amount = amount;
            expense.date = System.currentTimeMillis();

            expenseViewModel.insert(expense);
            Toast.makeText(this, "Расход добавлен", Toast.LENGTH_SHORT).show();
            finish(); // Закрыть текущую активность
        });
    }
}

