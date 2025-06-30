package com.example.infraspot_proto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    Button btnLogout;
    RecyclerView recyclerReports;
    ReportAdapter adapter;
    ArrayList<Report> reportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnLogout = findViewById(R.id.btnLogout);
        recyclerReports = findViewById(R.id.reportsRecyclerView);
        TextView txtEmptyMessage = findViewById(R.id.txtEmptyMessage);  // 👈 Moved here

        recyclerReports.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReportAdapter(this, reportList);
        recyclerReports.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference("reports")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reportList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Report report = snap.getValue(Report.class);
                            if (report != null) {
                                report.setReportId(snap.getKey());
                                reportList.add(report);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        // 👇 Show/hide empty message
                        if (reportList.isEmpty()) {
                            txtEmptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            txtEmptyMessage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            preferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
