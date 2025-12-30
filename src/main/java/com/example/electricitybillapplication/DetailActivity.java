package com.example.electricitybillapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvMonth, tvUnit, tvTotal, tvRebate, tvFinal;

    DBHelper dbHelper;
    int billId; // ID of the clicked bill

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvMonth = findViewById(R.id.tvMonth);
        tvUnit = findViewById(R.id.tvUnit);
        tvTotal = findViewById(R.id.tvTotal);
        tvRebate = findViewById(R.id.tvRebate);
        tvFinal = findViewById(R.id.tvFinal);

        dbHelper = new DBHelper(this);

        // Get bill ID from intent
        Intent intent = getIntent();
        billId = intent.getIntExtra("billId", -1);

        if(billId != -1){
            loadBillDetails(billId);
        } else {
            Toast.makeText(this, "No bill data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBillDetails(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill WHERE id=?", new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()){
            tvMonth.setText("Month: " + cursor.getString(cursor.getColumnIndexOrThrow("month")));
            tvUnit.setText("Unit: " + cursor.getInt(cursor.getColumnIndexOrThrow("unit")) + " kWh");
            tvTotal.setText("Total Charge: RM " + String.format("%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow("total"))));
            tvRebate.setText("Rebate: " + (cursor.getDouble(cursor.getColumnIndexOrThrow("rebate"))*100) + "%");
            tvFinal.setText("Final Cost: RM " + String.format("%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"))));
        }
        cursor.close();
        db.close();
    }
}
