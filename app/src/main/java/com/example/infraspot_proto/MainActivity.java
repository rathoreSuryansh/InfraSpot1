package com.example.infraspot_proto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            preferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
