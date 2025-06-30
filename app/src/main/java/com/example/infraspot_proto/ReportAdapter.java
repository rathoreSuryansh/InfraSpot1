package com.example.infraspot_proto;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final Context context;
    private final List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        Report report = reportList.get(position);

        holder.txtDescription.setText(report.getDescription());

        // Geocode
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(report.getLatitude(), report.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                holder.txtLocation.setText(addresses.get(0).getAddressLine(0));
            } else {
                holder.txtLocation.setText("Lat: " + report.getLatitude() + ", Lng: " + report.getLongitude());
            }
        } catch (IOException e) {
            holder.txtLocation.setText("Lat: " + report.getLatitude() + ", Lng: " + report.getLongitude());
        }

        Glide.with(context)
                .load(report.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgReport);

        holder.btnOpenMap.setOnClickListener(v -> {
            String uri = "geo:0,0?q=" + report.getLatitude() + "," + report.getLongitude() + "(Reported Location)";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        });

        holder.btnResolve.setOnClickListener(v -> {
            if (report.getReportId() != null) {
                FirebaseDatabase.getInstance().getReference("reports")
                        .child(report.getReportId())
                        .removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Report resolved!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to resolve: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "Report ID missing!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescription, txtLocation;
        ImageView imgReport;
        Button btnOpenMap, btnResolve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            imgReport = itemView.findViewById(R.id.imgReport);
            btnOpenMap = itemView.findViewById(R.id.btnOpenMap);
            btnResolve = itemView.findViewById(R.id.btnResolve);
        }
    }
}
