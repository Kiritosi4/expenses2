package com.example.expenses2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category;
    public double amount;
    public long date; // В миллисекундах
}

