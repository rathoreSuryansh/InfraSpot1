package com.example.infraspot_proto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportIssueActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText edtDescription;
    Button btnSelectImage, btnSubmitReport, btnPickLocation;
    ImageView imagePreview;
    TextView txtSelectedLocation;

    private static final int PICK_IMAGE_REQUEST = 1;
    static final int MAP_REQUEST_CODE = 1001;
    private Uri imageUri;
    private String imagePath = "";

    private double selectedLat = 0.0;
    private double selectedLng = 0.0;

    private GoogleMap mMap;

    // Firebase Database and Storage references
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reportsRef = database.getReference("reports");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference("report_images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        txtSelectedLocation = findViewById(R.id.txtSelectedLocation);
        edtDescription = findViewById(R.id.edtDescription);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnPickLocation = findViewById(R.id.btnPickLocation);
        imagePreview = findViewById(R.id.imagePreview);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssueActivity.this, MapsActivity.class);
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });

        btnSubmitReport.setOnClickListener(v -> {
            String description = edtDescription.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedLat == 0.0 && selectedLng == 0.0) {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                uploadImageAndData(description, selectedLat, selectedLng);
            } else {
                // Upload data without an image
                uploadData(description, selectedLat, selectedLng, null);
            }
        });
    }

    private void uploadImageAndData(String description, double latitude, double longitude) {
        // Create a unique filename for the image
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Compress the image
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Upload the rest of the data with the image URL
                    uploadData(description, latitude, longitude, imageUrl);
                }).addOnFailureListener(e -> {
                    Toast.makeText(ReportIssueActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Still upload other data without the image URL
                    uploadData(description, latitude, longitude, null);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ReportIssueActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // Still upload other data without the image URL
                uploadData(description, latitude, longitude, null);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Still upload other data without the image URL
            uploadData(description, latitude, longitude, null);
        }
    }

    private void uploadData(String description, double latitude, double longitude, String imageUrl) {
        // Create a unique ID for the report
        String reportId = reportsRef.push().getKey();

        // Create a map to store the report data
        Map<String, Object> report = new HashMap<>();
        report.put("description", description);
        report.put("latitude", latitude);
        report.put("longitude", longitude);
        if (imageUrl != null) {
            report.put("imageUrl", imageUrl);
        }
        report.put("timestamp", System.currentTimeMillis()); // Optional: Add a timestamp

        // Upload the report data to the Firebase Realtime Database
        reportsRef.child(reportId)
                .setValue(report)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReportIssueActivity.this, "Issue reported successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after successful upload
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReportIssueActivity.this, "Failed to report issue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePath = imageUri.toString();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(ImageView.VISIBLE);
        } else if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedLat = data.getDoubleExtra("lat", 0.0);
            selectedLng = data.getDoubleExtra("lng", 0.0);

            txtSelectedLocation.setText("Selected Location:\nLatitude: " + selectedLat + "\nLongitude: " + selectedLng);

            if (mMap != null) {
                mMap.clear();
                LatLng latLng = new LatLng(selectedLat, selectedLng);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}