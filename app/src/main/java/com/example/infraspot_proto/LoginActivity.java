package com.example.infraspot_proto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnAdminLogin, btnUserContinue;
    SharedPreferences sharedPreferences;

    private final String ADMIN_EMAIL = "admin@infraspot.com";
    private final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        btnUserContinue = findViewById(R.id.btnUserContinue);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Check if already logged in
        boolean isAdminLoggedIn = sharedPreferences.getBoolean("isAdminLoggedIn", false);
        boolean isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false);

        if (isAdminLoggedIn) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        } else if (isUserLoggedIn) {
            startActivity(new Intent(this, UserActivity.class));  // Open UserActivity instead of MainActivity
            finish();
        }

        btnAdminLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                sharedPreferences.edit().putBoolean("isAdminLoggedIn", true).apply();
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        btnUserContinue.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("isUserLoggedIn", true).apply();
            startActivity(new Intent(LoginActivity.this, UserActivity.class));  // Open UserActivity instead of MainActivity
            finish();
        });
    }
}
