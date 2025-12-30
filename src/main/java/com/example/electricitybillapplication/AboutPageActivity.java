package com.example.electricitybillapplication;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page); // Make sure this matches your layout file

        // Make GitHub URL clickable
        TextView tvGit = findViewById(R.id.tvGit);
        tvGit.setMovementMethod(LinkMovementMethod.getInstance());
    }
}