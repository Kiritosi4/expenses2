package com.example.expenses2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expenses2.ExpenseDatabase;
import com.example.expenses2.models.Expense;
import com.example.expenses2.models.ExpenseDao;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseDao expenseDao;
    private final LiveData<List<Expense>> allExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        ExpenseDatabase db = ExpenseDatabase.getDatabase(application);
        expenseDao = db.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public void insert(Expense expense) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> expenseDao.insert(expense));
    }

    public void delete(Expense expense) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getExpensesByCategory(String category) {
        return expenseDao.getExpensesByCategory(category);
    }

    public LiveData<List<Expense>> getExpensesByDateRange(long startDate, long endDate) {
        return expenseDao.getExpensesByDateRange(startDate, endDate);
    }
}

