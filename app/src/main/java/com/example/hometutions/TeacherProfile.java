package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hometutions.models.Teacher;
import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.services.FirebaseDatabaseService;
import com.example.hometutions.utils.AnimationUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

/**
 * TeacherProfile Activity
 * 
 * This activity displays a teacher's profile with the following features:
 * - Beautiful UI with animations and gradients
 * - Backend integration with Firebase
 * - Profile image loading with Glide
 * - Contact functionality (phone calls)
 * - Messaging placeholder
 * - Share profile functionality
 * - More options menu
 * - Loading states and error handling
 * - Proper navigation from login
 * 
 * The activity can be launched in two ways:
 * 1. From TeachersLogin after successful authentication
 * 2. With a specific teacher_id passed as intent extra
 */
public class TeacherProfile extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    private static final String TAG = "TeacherProfile";
    
    // UI Elements
    private ImageView backButton, moreOptionsButton, profilePhoto;
    private TextView teacherNameText, ratingText, experienceText, locationText;
    private TextView ageText, genderText, emailText, addressText;
    private TextView qualificationText, institutionText;
    private LinearLayout subjectsContainer, streamsContainer;
    private LinearLayout contactButton, messageButton;
    private CardView profilePhotoCard;
    private LinearLayout mainContent;
    
    // Services
    private FirebaseAuthService authService;
    private FirebaseDatabaseService databaseService;
    
    // Data
    private Teacher currentTeacher;
    private String teacherId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile);
        
        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        try {
            initializeServices();
            initializeViews();
            setupClickListeners();
            loadTeacherData();
            setupAnimations();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize activity", e);
            Toast.makeText(this, "Failed to initialize activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initializeServices() {
        authService = new FirebaseAuthService(this, this);
        databaseService = new FirebaseDatabaseService();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        moreOptionsButton = findViewById(R.id.moreOptionsButton);
        profilePhoto = findViewById(R.id.profilePhoto);
        teacherNameText = findViewById(R.id.teacherNameText);
        ratingText = findViewById(R.id.ratingText);
        experienceText = findViewById(R.id.experienceText);
        locationText = findViewById(R.id.locationText);
        contactButton = findViewById(R.id.contactButton);
        messageButton = findViewById(R.id.messageButton);
        profilePhotoCard = findViewById(R.id.profilePhotoCard);
        mainContent = findViewById(R.id.mainContent);
        
        // Additional UI elements
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        emailText = findViewById(R.id.emailText);
        addressText = findViewById(R.id.addressText);
        qualificationText = findViewById(R.id.qualificationText);
        institutionText = findViewById(R.id.institutionText);
        subjectsContainer = findViewById(R.id.subjectsContainer);
        streamsContainer = findViewById(R.id.streamsContainer);
        
        // Check if views are found
        if (backButton == null || moreOptionsButton == null || profilePhoto == null || 
            teacherNameText == null || ratingText == null || experienceText == null || 
            locationText == null || contactButton == null || messageButton == null || 
            profilePhotoCard == null || mainContent == null || ageText == null || 
            genderText == null || emailText == null || addressText == null || 
            qualificationText == null || institutionText == null || 
            subjectsContainer == null || streamsContainer == null) {
            throw new IllegalStateException("One or more required views not found in layout");
        }
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            AnimationUtils.fadeOut(backButton, 200, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
        
        moreOptionsButton.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });
        
        contactButton.setOnClickListener(v -> {
            String phoneNumber = null;
            if (currentTeacher != null) {
                // Try multiple phone number fields
                if (currentTeacher.getPhoneNumber() != null && !currentTeacher.getPhoneNumber().isEmpty()) {
                    phoneNumber = currentTeacher.getPhoneNumber();
                } else if (currentTeacher.getPhone() != null && !currentTeacher.getPhone().isEmpty()) {
                    phoneNumber = currentTeacher.getPhone();
                }
            }
            
            if (phoneNumber != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to initiate phone call", e);
                    Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
        
        messageButton.setOnClickListener(v -> {
            // TODO: Implement messaging functionality
            Toast.makeText(this, "Messaging feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadTeacherData() {
        // Show loading state
        showLoading(true);
        
        // Get teacher ID from intent or current user
        teacherId = getIntent().getStringExtra("teacher_id");
        if (teacherId == null) {
            // If no teacher ID provided, use current user's ID
            FirebaseUser currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                teacherId = currentUser.getUid();
            } else {
                Toast.makeText(this, "No teacher data available", Toast.LENGTH_LONG).show();
                showLoading(false);
                finish();
                return;
            }
        }
        
        // Load teacher data from Firebase
        databaseService.getTeacher(teacherId, new FirebaseDatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onSuccess(Teacher teacher) {
                currentTeacher = teacher;
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        showLoading(false);
                        updateUIWithTeacherData(teacher);
                    }
                });
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load teacher data: " + error);
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        showLoading(false);
                        Toast.makeText(TeacherProfile.this, "Failed to load teacher data: " + error, Toast.LENGTH_LONG).show();
                        // Show default data or placeholder
                        showDefaultData();
                    }
                });
            }
        });
    }
    
    private void showLoading(boolean show) {
        // Remove loading animation - just show placeholder
        if (show) {
            profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
        }
    }
    
    private void updateUIWithTeacherData(Teacher teacher) {
        if (teacher == null) {
            Log.w(TAG, "Teacher data is null, showing default data");
            showDefaultData();
            return;
        }
        
        Log.d(TAG, "Updating UI with teacher data:");
        Log.d(TAG, "Name: " + teacher.getFullName());
        Log.d(TAG, "Email: " + teacher.getEmail());
        Log.d(TAG, "Phone: " + teacher.getPhone());
        Log.d(TAG, "Phone Number: " + teacher.getPhoneNumber());
        Log.d(TAG, "Address: " + teacher.getAddress());
        Log.d(TAG, "Location: " + teacher.getLocation());
        Log.d(TAG, "Age: " + teacher.getAge());
        Log.d(TAG, "Gender: " + teacher.getGender());
        Log.d(TAG, "Experience: " + teacher.getExperience());
        Log.d(TAG, "Years of Experience: " + teacher.getYearsOfExperience());
        Log.d(TAG, "Qualification: " + teacher.getQualification());
        Log.d(TAG, "Highest Qualification: " + teacher.getHighestQualification());
        Log.d(TAG, "Institution: " + teacher.getInstitution());
        Log.d(TAG, "Subjects: " + teacher.getSubjects());
        Log.d(TAG, "Subjects Taught: " + teacher.getSubjectsTaught());
        Log.d(TAG, "Teaching Streams: " + teacher.getTeachingStreams());
        Log.d(TAG, "Profile Image URL: " + teacher.getProfileImageUrl());
        
        // Update basic info
        if (teacher.getFullName() != null && !teacher.getFullName().isEmpty()) {
            teacherNameText.setText(teacher.getFullName());
        } else {
            teacherNameText.setText("Teacher Name");
        }
        
        if (teacher.getRating() != null && !teacher.getRating().isEmpty()) {
            ratingText.setText(teacher.getRating());
        } else {
            ratingText.setText("4.5");
        }
        
        // Update experience - try multiple sources
        String experienceText = "";
        if (teacher.getYearsOfExperience() > 0) {
            experienceText = teacher.getYearsOfExperience() + " years exp.";
        } else if (teacher.getExperience() != null && !teacher.getExperience().isEmpty()) {
            String experience = teacher.getExperience();
            if (experience.matches("\\d+")) {
                experienceText = experience + " years exp.";
            } else {
                experienceText = experience;
            }
        } else {
            experienceText = "5 years exp.";
        }
        this.experienceText.setText(experienceText);
        
        if (teacher.getLocation() != null && !teacher.getLocation().isEmpty()) {
            locationText.setText(teacher.getLocation());
        } else {
            locationText.setText("Location");
        }
        
        // Update personal information
        if (teacher.getAge() > 0) {
            ageText.setText(teacher.getAge() + " years");
        } else {
            ageText.setText("Age not specified");
        }
        
        if (teacher.getGender() != null && !teacher.getGender().isEmpty()) {
            genderText.setText(teacher.getGender());
        } else {
            genderText.setText("Not specified");
        }
        
        if (teacher.getEmail() != null && !teacher.getEmail().isEmpty()) {
            emailText.setText(teacher.getEmail());
        } else {
            emailText.setText("Email not available");
        }
        
        if (teacher.getAddress() != null && !teacher.getAddress().isEmpty()) {
            addressText.setText(teacher.getAddress());
        } else {
            addressText.setText("Address not available");
        }
        
        // Update educational qualifications
        if (teacher.getHighestQualification() != null && !teacher.getHighestQualification().isEmpty()) {
            qualificationText.setText(teacher.getHighestQualification());
        } else if (teacher.getQualification() != null && !teacher.getQualification().isEmpty()) {
            qualificationText.setText(teacher.getQualification());
        } else {
            qualificationText.setText("Qualification not specified");
        }
        
        if (teacher.getInstitution() != null && !teacher.getInstitution().isEmpty()) {
            institutionText.setText(teacher.getInstitution());
        } else {
            institutionText.setText("Institution not specified");
        }
        
        // Update subjects and streams
        updateSubjectsAndStreams(teacher);
        
        // Load profile image if available
        if (teacher.getProfileImageUrl() != null && !teacher.getProfileImageUrl().isEmpty()) {
            Log.d(TAG, "Loading profile image from: " + teacher.getProfileImageUrl());
            displayProfileImage(teacher.getProfileImageUrl());
        } else {
            Log.d(TAG, "No profile image URL available, using placeholder");
            profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
        }
        
        // Animate the content appearance
        AnimationUtils.fadeIn(mainContent, 500, null);
    }
    
    private void showDefaultData() {
        teacherNameText.setText("Teacher Name");
        ratingText.setText("4.5");
        experienceText.setText("5 years exp.");
        locationText.setText("Location");
        
        // Set default personal information
        ageText.setText("Age not specified");
        genderText.setText("Not specified");
        emailText.setText("Email not available");
        addressText.setText("Address not available");
        
        // Set default educational information
        qualificationText.setText("Qualification not specified");
        institutionText.setText("Institution not specified");
        
        // Set default subjects and streams
        updateSubjectsAndStreams(null);
        
        // Animate the content appearance
        AnimationUtils.fadeIn(mainContent, 500, null);
    }
    
    private void setupAnimations() {
        // Initial animations
        mainContent.setAlpha(0f);
        profilePhotoCard.setScaleX(0.8f);
        profilePhotoCard.setScaleY(0.8f);
        
        // Animate profile photo card
        AnimationUtils.scaleInWithOvershoot(profilePhotoCard, 800, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // Start content animation after profile photo animation
                AnimationUtils.fadeIn(mainContent, 600, null);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        
        // Animate action buttons with staggered delay
        AnimationUtils.slideUpWithDelay(contactButton, 300, 200, null);
        AnimationUtils.slideUpWithDelay(messageButton, 300, 300, null);
    }
    
    private void showMoreOptionsMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, moreOptionsButton);
        popup.getMenu().add("Share Profile");
        popup.getMenu().add("Report Issue");
        popup.getMenu().add("View Full Profile");
        popup.getMenu().add("Test Profile Image");
        
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Share Profile":
                    shareTeacherProfile();
                    return true;
                case "Report Issue":
                    reportIssue();
                    return true;
                case "View Full Profile":
                    viewFullProfile();
                    return true;
                case "Test Profile Image":
                    testProfileImage();
                    return true;
                default:
                    return false;
            }
        });
        
        popup.show();
    }
    
    private void shareTeacherProfile() {
        if (currentTeacher == null) {
            Toast.makeText(this, "Teacher data not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String shareText = "Check out this teacher: " + currentTeacher.getFullName();
        if (currentTeacher.getSubjects() != null) {
            shareText += " - " + currentTeacher.getSubjects();
        }
        if (currentTeacher.getLocation() != null) {
            shareText += " (" + currentTeacher.getLocation() + ")";
        }
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Teacher Profile"));
    }
    
    private void reportIssue() {
        Toast.makeText(this, "Report feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void viewFullProfile() {
        Toast.makeText(this, "Full profile view coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void testProfileImage() {
        Toast.makeText(this, "Testing profile image loading...", Toast.LENGTH_SHORT).show();
        loadSampleProfileImage();
    }
    
    public void refreshProfile() {
        if (teacherId != null) {
            loadTeacherData();
        }
    }
    
    private void updateSubjectsAndStreams(Teacher teacher) {
        // Clear existing subjects
        subjectsContainer.removeAllViews();
        
        // Add subjects from multiple sources
        java.util.List<String> allSubjects = new java.util.ArrayList<>();
        
        // Add from subjectsTaught list
        if (teacher.getSubjectsTaught() != null && !teacher.getSubjectsTaught().isEmpty()) {
            allSubjects.addAll(teacher.getSubjectsTaught());
        }
        
        // Add from subjects string (comma-separated)
        if (teacher.getSubjects() != null && !teacher.getSubjects().isEmpty()) {
            String[] subjectsArray = teacher.getSubjects().split(",");
            for (String subject : subjectsArray) {
                String trimmedSubject = subject.trim();
                if (!trimmedSubject.isEmpty() && !allSubjects.contains(trimmedSubject)) {
                    allSubjects.add(trimmedSubject);
                }
            }
        }
        
        // If no subjects found, add default
        if (allSubjects.isEmpty()) {
            allSubjects.add("Mathematics");
            allSubjects.add("Physics");
            allSubjects.add("Chemistry");
        }
        
        // Create subject chips
        for (String subject : allSubjects) {
            TextView subjectChip = new TextView(this);
            subjectChip.setText(subject);
            subjectChip.setBackgroundResource(R.drawable.subject_chip_background);
            subjectChip.setTextColor(getResources().getColor(R.color.black));
            subjectChip.setTextSize(12);
            subjectChip.setPadding(32, 16, 32, 16);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 4, 8, 4);
            subjectChip.setLayoutParams(params);
            
            subjectsContainer.addView(subjectChip);
        }
        
        // Clear existing streams
        streamsContainer.removeAllViews();
        
        // Add teaching streams
        java.util.List<String> allStreams = new java.util.ArrayList<>();
        
        if (teacher.getTeachingStreams() != null && !teacher.getTeachingStreams().isEmpty()) {
            allStreams.addAll(teacher.getTeachingStreams());
        }
        
        // If no streams found, add default
        if (allStreams.isEmpty()) {
            allStreams.add("10th Class");
            allStreams.add("12th Class");
            allStreams.add("JEE");
            allStreams.add("NEET");
        }
        
        // Create stream chips
        for (String stream : allStreams) {
            TextView streamChip = new TextView(this);
            streamChip.setText(stream);
            streamChip.setBackgroundResource(R.drawable.stream_chip_background);
            streamChip.setTextColor(getResources().getColor(R.color.white));
            streamChip.setTextSize(12);
            streamChip.setPadding(32, 16, 32, 16);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 4, 8, 4);
            streamChip.setLayoutParams(params);
            
            streamsContainer.addView(streamChip);
        }
    }
    
    private void displayProfileImage(String profileImageData) {
        try {
            // Check if it's a Base64 string (starts with data:image or is a long Base64 string)
            if (profileImageData.startsWith("data:image") || profileImageData.length() > 100) {
                // Handle Base64 image data
                if (profileImageData.startsWith("data:image")) {
                    // Extract Base64 part from data URL
                    String base64Data = profileImageData.substring(profileImageData.indexOf(",") + 1);
                    byte[] imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePhoto.setImageBitmap(bitmap);
                } else {
                    // Direct Base64 string
                    byte[] imageBytes = android.util.Base64.decode(profileImageData, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePhoto.setImageBitmap(bitmap);
                }
                Log.d(TAG, "Profile image displayed successfully from Base64 data");
            } else {
                // Regular URL, try to load with Glide
                if (!isFinishing() && !isDestroyed()) {
                    Glide.with(this)
                        .load(profileImageData)
                        .placeholder(R.drawable.ic_teacher_placeholder)
                        .error(R.drawable.ic_teacher_placeholder)
                        .timeout(10000)
                        .circleCrop()
                        .into(profilePhoto);
                    Log.d(TAG, "Profile image loading started from URL");
                } else {
                    profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode profile image: " + e.getMessage());
            // Set default teacher icon on error
            profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
        }
    }
    
    private void loadSampleProfileImage() {
        // For testing purposes, load a sample profile image
        try {
            if (!isFinishing() && !isDestroyed()) {
                // Use a sample image URL for testing
                String sampleImageUrl = "https://via.placeholder.com/300x300/667eea/ffffff?text=Teacher";
                Log.d(TAG, "Loading sample profile image from: " + sampleImageUrl);
                
                Glide.with(this)
                    .load(sampleImageUrl)
                    .placeholder(R.drawable.ic_teacher_placeholder)
                    .error(R.drawable.ic_teacher_placeholder)
                    .timeout(10000)
                    .circleCrop()
                    .into(profilePhoto);
            } else {
                profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load sample profile image", e);
            profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
        }
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        // This callback is not used in this activity
    }
    
    @Override
    public void onAuthFailure(String error) {
        // This callback is not used in this activity
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // Handle auth state changes if needed
        if (user == null && !isFinishing()) {
            Log.d(TAG, "User signed out, redirecting to login");
            // User signed out, redirect to login
            Intent intent = new Intent(this, TeachersLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "TeacherProfile resumed");
        // Refresh data when activity resumes
        if (currentTeacher == null && teacherId != null) {
            loadTeacherData();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "TeacherProfile paused");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "TeacherProfile stopped");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any pending operations
        Log.d(TAG, "TeacherProfile destroyed");
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed in TeacherProfile");
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}