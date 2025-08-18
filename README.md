# HomeTutions - Android App

A comprehensive Android application for connecting students with qualified home tutors. Built with modern Android development practices, Firebase backend, and beautiful animations.

## 🚀 Features

### Core Functionality
- **User Authentication**: Firebase Authentication with email/password and Google Sign-In
- **Role-based Registration**: Separate registration flows for students and teachers
- **Profile Management**: Complete user profiles with photo uploads
- **Document Verification**: Document upload system for teacher verification
- **Real-time Database**: Firebase Realtime Database for data storage
- **File Storage**: Firebase Storage for profile photos and documents

### User Experience
- **Beautiful Animations**: Smooth entrance animations and interactive elements
- **Modern UI/UX**: Material Design with custom gradients and components
- **Responsive Design**: Optimized for various screen sizes
- **Smooth Navigation**: Seamless transitions between activities

### Student Features
- **Academic Profile**: Current class, board, school information
- **Subject Selection**: Choose subjects needing help with
- **Tuition Preferences**: Budget range, preferred time slots, teacher preferences
- **Requirements**: Additional requirements and specifications

### Teacher Features
- **Professional Profile**: Qualifications, experience, institution details
- **Teaching Specialization**: Subjects and streams they can teach
- **Document Verification**: Aadhar, PAN, and degree certificate uploads
- **Availability**: Teaching schedule and availability information

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/hometutions/
│   │   ├── models/           # Data models
│   │   │   ├── User.java
│   │   │   ├── Student.java
│   │   │   └── Teacher.java
│   │   ├── services/         # Firebase services
│   │   │   ├── FirebaseAuthService.java
│   │   │   ├── FirebaseDatabaseService.java
│   │   │   └── FirebaseStorageService.java
│   │   ├── utils/            # Utility classes
│   │   │   ├── AnimationUtils.java
│   │   │   └── ValidationUtils.java
│   │   ├── MainActivity.java
│   │   ├── SplashActivity.java
│   │   ├── StudentRegistration.java
│   │   └── TeacherRegistration.java
│   └── res/
│       ├── layout/           # UI layouts
│       ├── drawable/         # Custom drawables and backgrounds
│       ├── values/           # Colors, strings, styles
│       └── mipmap/          # App icons
```

## 🛠️ Technical Stack

- **Language**: Java 11
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 14)
- **Architecture**: MVC with service layer
- **Backend**: Firebase
  - Authentication
  - Realtime Database
  - Storage
- **UI Components**: Material Design Components
- **Animations**: Custom animation utilities with ObjectAnimator

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Firebase project setup

### Firebase Setup
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the project
3. Download `google-services.json` and place it in the `app/` directory
4. Enable Authentication, Realtime Database, and Storage in Firebase Console
5. Update the Google Sign-In web client ID in `FirebaseAuthService.java`

### Build Configuration
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the project

### Database Rules
Configure Firebase Realtime Database rules:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "students": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "teachers": {
      "$uid": {
        ".read": "auth != null",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

### Storage Rules
Configure Firebase Storage rules:

```rules
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /profile_photos/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /documents/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 🎨 Design System

### Color Palette
- **Primary**: #667eea (Blue gradient)
- **Secondary**: #764ba2 (Purple gradient)
- **Accent**: #f093fb (Pink accent)
- **Text**: #FFFFFF (White), #E8E8E8 (Light gray)
- **Input**: Custom input styling with icons

### Typography
- **Headings**: Sans-serif-medium, bold
- **Body**: Sans-serif-light, regular
- **Buttons**: Sans-serif-medium, bold

### Animations
- **Entrance**: Staggered slide-up animations
- **Interactive**: Pulse and scale effects
- **Decorative**: Floating elements with continuous motion
- **Transitions**: Smooth activity transitions

## 📱 Screenshots

The app includes the following main screens:
1. **Splash Screen**: Animated app introduction
2. **Main Screen**: Role selection with beautiful gradients
3. **Student Registration**: Comprehensive student profile creation
4. **Teacher Registration**: Professional teacher profile with document uploads

## 🔒 Security Features

- Firebase Authentication with email verification
- Secure document upload with user-specific access
- Input validation and sanitization
- Role-based data access control

## 🚀 Future Enhancements

- **Chat System**: Real-time messaging between students and teachers
- **Booking System**: Schedule management for tuition sessions
- **Payment Integration**: Secure payment processing
- **Rating System**: Teacher reviews and ratings
- **Push Notifications**: Real-time updates and reminders
- **Offline Support**: Local data caching and offline functionality

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the Firebase documentation for backend issues

## 🔄 Version History

- **v1.0.0**: Initial release with core functionality
  - User authentication
  - Student and teacher registration
  - Basic profile management
  - Document upload system
  - Beautiful animations and UI

---

**Built with ❤️ for the education community**
