package com.example.expenses2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expenses2.models.Expense;
import com.example.expenses2.models.ExpenseDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Expense.class}, version = 1)
public abstract class ExpenseDatabase extends RoomDatabase {
    private static volatile ExpenseDatabase INSTANCE;

    public abstract ExpenseDao expenseDao();

    // Executor для выполнения операций в фоновом потоке
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4); // Настройка с 4 потоками

    public static ExpenseDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (ExpenseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ExpenseDatabase.class,
                            "expense_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    // Метод для получения Executor
    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}


