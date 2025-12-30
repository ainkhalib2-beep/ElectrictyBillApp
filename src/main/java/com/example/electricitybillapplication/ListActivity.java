package com.example.electricitybillapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    ArrayList<String> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        dbHelper = new DBHelper(this);
        billList = new ArrayList<>();

        // Load bills from database
        loadBills();

        // Make each item clickable → opens DetailActivity
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the corresponding bill ID
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT id FROM bill LIMIT 1 OFFSET ?",
                    new String[]{String.valueOf(position)}
            );

            if(cursor.moveToFirst()){
                int billId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("billId", billId);
                startActivity(intent);
            } else {
                Toast.makeText(ListActivity.this, "Cannot find bill details", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            db.close();
        });
    }

    private void loadBills() {
        billList.clear(); // Clear previous data
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT month, finalCost FROM bill", null);

        if(cursor.getCount() == 0){
            Toast.makeText(this, "No bills saved yet", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()){
                String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"));
                billList.add(month + " : RM " + String.format("%.2f", finalCost));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                billList
        );
        listView.setAdapter(adapter);

        cursor.close();
        db.close();
    }
}
