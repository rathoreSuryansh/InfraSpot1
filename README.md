# InfraSpot 🚧

InfraSpot is an Android application designed to streamline the reporting and resolution of public infrastructure issues. Users can submit complaints with photos, location data, and descriptions, while administrators manage and resolve these issues through an integrated dashboard. The app leverages Firebase for real-time data storage and Google Maps SDK for accurate geolocation.

## 🌟 Features

### 👤 User Side
- 📍 Report issues with image, description, and precise location
- 🗺️ Google Maps integration to pin and view problem areas
- 🔒 Firebase Authentication for secure sign-in
- 🔄 Real-time complaint submission to Firebase

### 🔧 Admin Side
- 📋 View all submitted reports in a RecyclerView
- 🖼️ Preview report images and open locations directly in Google Maps
- ✅ Mark issues as resolved (removes report from Firebase)
- 🔐 Admin authentication with Firebase Auth

## 🔧 Tech Stack
- **Frontend:** Java, Android Studio, XML
- **Backend:** Firebase Realtime Database, Firebase Authentication
- **Libraries:** Google Maps SDK, Glide (for image loading)

InfraSpot/
├── app/
│   ├── java/com/yourpackage/
│   │   ├── MainActivity.java
│   │   ├── AdminActivity.java
│   │   ├── LoginActivity.java
│   │   ├── ReportAdapter.java
│   ├── res/layout/
│   │   ├── activity_main.xml
│   │   ├── item_report.xml
│   └── google-services.json
Future Enhancements
Admin-side analytics dashboard

Status tracking for each report

Push notifications for updates

Cloud Firestore integration for scalability

🤝 Contributing
Feel free to fork this project and submit a pull request. Contributions are welcome!
Made by Suryansh Singh Rathore | MIT, Manipal
