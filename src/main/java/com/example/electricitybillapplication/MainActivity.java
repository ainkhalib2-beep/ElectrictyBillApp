package com.example.electricitybillapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView actMonth;
    EditText etUnit;
    RadioGroup radioGroupRebate;
    Button btnCalculate, btnViewBills, btnAbout;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI
        actMonth = findViewById(R.id.actMonth);
        etUnit = findViewById(R.id.etUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewBills = findViewById(R.id.btnViewBills);
        btnAbout = findViewById(R.id.btnAbout);

        dbHelper = new DBHelper(this);

        // Month list for AutoCompleteTextView
        String[] months = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                months
        );
        actMonth.setAdapter(adapter);
        actMonth.setOnClickListener(v -> actMonth.showDropDown());

        // Calculate & Save
        btnCalculate.setOnClickListener(v -> calculateAndSave());

        // View Bills → open ListActivity
        btnViewBills.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });

        // About → open AboutActivity
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void calculateAndSave() {
        String month = actMonth.getText().toString();
        String unitStr = etUnit.getText().toString();

        if(month.isEmpty()) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return;
        }

        if(unitStr.isEmpty()) {
            Toast.makeText(this, "Please enter electricity usage", Toast.LENGTH_SHORT).show();
            return;
        }

        int unit = Integer.parseInt(unitStr);
        if(unit <= 0){
            Toast.makeText(this, "Invalid unit value", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalCharge = calculateCharge(unit);
        double rebate = getRebate();
        double finalCost = totalCharge - (totalCharge * rebate);

        // Save to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("unit", unit);
        cv.put("total", totalCharge);
        cv.put("rebate", rebate);
        cv.put("finalCost", finalCost);
        db.insert("bill", null, cv);
        db.close();

        Toast.makeText(this,
                "Saved successfully!\nFinal Cost: RM " + String.format("%.2f", finalCost),
                Toast.LENGTH_LONG).show();

        // Clear inputs
        etUnit.setText("");
        radioGroupRebate.clearCheck();
    }

    private double getRebate() {
        int checkedId = radioGroupRebate.getCheckedRadioButtonId();
        if(checkedId == R.id.rb1) return 0.01;
        if(checkedId == R.id.rb3) return 0.03;
        if(checkedId == R.id.rb5) return 0.05;
        return 0.0; // default 0%
    }

    private double calculateCharge(int unit){
        double total = 0;
        int remaining = unit;
// First 200 kWh
        if(remaining > 200){
            total += 200 * 0.218;
            remaining -= 200;
        } else {
            return remaining * 0.218;
        }

        // Next 100 kWh
        if(remaining > 100){
            total += 100 * 0.334;
            remaining -= 100;
        } else {
            return total + remaining * 0.334;
        }

        // Next 300 kWh
        if(remaining > 300){
            total += 300 * 0.516;
            remaining -= 300;
        } else {
            return total + remaining * 0.516;
        }

        // Above 600 kWh
        return total + remaining * 0.546;
    }
}
