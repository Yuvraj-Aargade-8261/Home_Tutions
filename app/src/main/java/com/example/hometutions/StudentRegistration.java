package com.example.hometutions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hometutions.models.Student;
import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.services.FirebaseDatabaseService;
import com.example.hometutions.services.FirebaseStorageService;
import com.example.hometutions.utils.AnimationUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class StudentRegistration extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    // UI Elements
    private ImageView backButton;
    private ImageView profilePhotoImage;
    private LinearLayout profilePhotoPlaceholder;
    private EditText fullNameEditText, ageEditText, phoneEditText, emailEditText, passwordEditText;
    private EditText addressEditText, parentContactEditText, schoolNameEditText, otherSubjectEditText;
    private EditText minBudgetEditText, maxBudgetEditText, additionalRequirementsEditText;
    
    private Spinner genderSpinner, currentClassSpinner, boardSpinner, teacherGenderPreferenceSpinner, timeSlotSpinner;
    
    private CheckBox mathCheckbox, physicsCheckbox, chemistryCheckbox, biologyCheckbox;
    private CheckBox englishCheckbox, hindiCheckbox, socialStudiesCheckbox, otherSubjectsCheckbox;
    private CheckBox foundationCheckbox, class10Checkbox, class12Checkbox, neetCheckbox, jeeCheckbox, collegeLevelCheckbox;
    private CheckBox termsCheckbox;
    
    private LinearLayout otherSubjectLayout;
    private LinearLayout saveDraftButton, continueButton;
    
    // Services
    private FirebaseAuthService authService;
    private FirebaseDatabaseService databaseService;
    
    // Data
    private Uri profilePhotoUri;
    private String selectedGender, selectedClass, selectedBoard, selectedTeacherGender, selectedTimeSlot;
    
    // Activity result launcher for image picker
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    setProfilePhoto(imageUri);
                }
            }
        }
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        
        // Set registration mode to prevent interference from login activities
        StudentLogin.setRegistrationMode(true);
        
        initializeViews();
        setupSpinners();
        setupClickListeners();
        setupAnimations();
        initializeFirebase();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        profilePhotoImage = findViewById(R.id.profilePhotoImage);
        profilePhotoPlaceholder = findViewById(R.id.profilePhotoPlaceholder);
        
        // EditText fields
        fullNameEditText = findViewById(R.id.fullNameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        parentContactEditText = findViewById(R.id.parentContactEditText);
        schoolNameEditText = findViewById(R.id.schoolNameEditText);
        otherSubjectEditText = findViewById(R.id.otherSubjectEditText);
        minBudgetEditText = findViewById(R.id.minBudgetEditText);
        maxBudgetEditText = findViewById(R.id.maxBudgetEditText);
        additionalRequirementsEditText = findViewById(R.id.additionalRequirementsEditText);
        
        // Spinners
        genderSpinner = findViewById(R.id.genderSpinner);
        currentClassSpinner = findViewById(R.id.currentClassSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        teacherGenderPreferenceSpinner = findViewById(R.id.teacherGenderPreferenceSpinner);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        
        // Checkboxes
        mathCheckbox = findViewById(R.id.mathCheckbox);
        physicsCheckbox = findViewById(R.id.physicsCheckbox);
        chemistryCheckbox = findViewById(R.id.chemistryCheckbox);
        biologyCheckbox = findViewById(R.id.biologyCheckbox);
        englishCheckbox = findViewById(R.id.englishCheckbox);
        hindiCheckbox = findViewById(R.id.hindiCheckbox);
        socialStudiesCheckbox = findViewById(R.id.socialStudiesCheckbox);
        otherSubjectsCheckbox = findViewById(R.id.otherSubjectsCheckbox);
        foundationCheckbox = findViewById(R.id.foundationCheckbox);
        class10Checkbox = findViewById(R.id.class10Checkbox);
        class12Checkbox = findViewById(R.id.class12Checkbox);
        neetCheckbox = findViewById(R.id.neetCheckbox);
        jeeCheckbox = findViewById(R.id.jeeCheckbox);
        collegeLevelCheckbox = findViewById(R.id.collegeLevelCheckbox);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        
        // Layouts
        otherSubjectLayout = findViewById(R.id.otherSubjectLayout);
        saveDraftButton = findViewById(R.id.saveDraftButton);
        continueButton = findViewById(R.id.continueButton);
        
        // Initially hide other subject layout
        otherSubjectLayout.setVisibility(View.GONE);
    }
    
    private void setupSpinners() {
        // Gender spinner
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        
        // Current class spinner
        String[] classes = {"6th Class", "7th Class", "8th Class", "9th Class", "10th Class", "11th Class", "12th Class"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classes);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentClassSpinner.setAdapter(classAdapter);
        
        // Board spinner
        String[] boards = {"CBSE", "ICSE", "State Board", "Other"};
        ArrayAdapter<String> boardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boards);
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(boardAdapter);
        
        // Teacher gender preference spinner
        String[] teacherGenders = {"Any", "Male", "Female"};
        ArrayAdapter<String> teacherGenderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherGenders);
        teacherGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherGenderPreferenceSpinner.setAdapter(teacherGenderAdapter);
        
        // Time slot spinner
        String[] timeSlots = {"Morning (6 AM - 12 PM)", "Afternoon (12 PM - 6 PM)", "Evening (6 PM - 9 PM)", "Flexible"};
        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(timeSlotAdapter);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            AnimationUtils.pulse(backButton, 200, 1);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        
        // Profile photo click listener
        View profilePhotoContainer = findViewById(R.id.profilePhotoContainer);
        profilePhotoContainer.setOnClickListener(v -> openImagePicker());
        
        // Other subjects checkbox listener
        otherSubjectsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                otherSubjectLayout.setVisibility(View.VISIBLE);
                AnimationUtils.slideUp(otherSubjectLayout, 300, null);
            } else {
                otherSubjectLayout.setVisibility(View.GONE);
            }
        });
        
        // Save draft button
        saveDraftButton.setOnClickListener(v -> {
            AnimationUtils.pulse(saveDraftButton, 200, 1);
            saveDraft();
        });
        
        // Continue button
        continueButton.setOnClickListener(v -> {
            AnimationUtils.pulse(continueButton, 200, 1);
            if (validateForm()) {
                registerStudent();
            }
        });
    }
    
    private void setupAnimations() {
        // Start entrance animations
        View[] sections = {
            findViewById(R.id.headerSection),
            findViewById(R.id.profilePhotoSection),
            findViewById(R.id.personalDetailsSection),
            findViewById(R.id.academicDetailsSection),
            findViewById(R.id.tuitionRequirementsSection),
            findViewById(R.id.preferencesSection)
        };
        
        // Staggered animation for sections
        AnimationUtils.staggerAnimation(sections, 200, 600, null);
    }
    
    private void initializeFirebase() {
        authService = new FirebaseAuthService(this, this);
        databaseService = new FirebaseDatabaseService();
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    
    private void setProfilePhoto(Uri uri) {
        profilePhotoUri = uri;
        profilePhotoImage.setImageURI(uri);
        profilePhotoImage.setVisibility(View.VISIBLE);
        profilePhotoPlaceholder.setVisibility(View.GONE);
        
        // Log the selected image
        Log.d("StudentRegistration", "Profile photo selected: " + uri.toString());
        
        // Animate the photo appearance
        AnimationUtils.scaleIn(profilePhotoImage, 300, null);
    }
    
    private boolean validateForm() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString().trim())) {
            showError("Please enter your full name");
            return false;
        }
        
        if (TextUtils.isEmpty(ageEditText.getText().toString().trim())) {
            showError("Please enter your age");
            return false;
        }
        
        if (TextUtils.isEmpty(phoneEditText.getText().toString().trim())) {
            showError("Please enter your phone number");
            return false;
        }
        
        if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
            showError("Please enter your email address");
            return false;
        }
        
        if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
            showError("Please enter a password");
            return false;
        }
        
        if (TextUtils.isEmpty(addressEditText.getText().toString().trim())) {
            showError("Please enter your address");
            return false;
        }
        
        if (!termsCheckbox.isChecked()) {
            showError("Please accept the terms and conditions");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void saveDraft() {
        // Save form data to SharedPreferences or local database
        Toast.makeText(this, "Draft saved successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void registerStudent() {
        // Show loading state
        continueButton.setEnabled(false);
        
        // Get form data
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        
        // Create user account
        authService.createUserWithEmailAndPassword(email, password);
    }
    
    private void createStudentProfile(FirebaseUser user) {
        // Add null check to prevent crash
        if (user == null) {
            Log.e("StudentRegistration", "FirebaseUser is null in createStudentProfile");
            Toast.makeText(this, "Authentication error: User object is null", Toast.LENGTH_LONG).show();
            continueButton.setEnabled(true);
            return;
        }
        
        // Convert profile image to Base64 if selected
        String profileImageBase64 = null;
        if (profilePhotoUri != null) {
            try {
                profileImageBase64 = convertImageToBase64(profilePhotoUri);
                Log.d("StudentRegistration", "Profile image converted to Base64 successfully, length: " + profileImageBase64.length());
                Log.d("StudentRegistration", "Base64 preview: " + profileImageBase64.substring(0, Math.min(100, profileImageBase64.length())));
            } catch (Exception e) {
                Log.e("StudentRegistration", "Failed to convert profile image to Base64: " + e.getMessage());
                Toast.makeText(this, "Failed to process profile image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("StudentRegistration", "No profile photo selected");
        }
        
        // Create student profile with Base64 image
        createStudentProfileInDatabase(user, profileImageBase64);
    }
    
    private String convertImageToBase64(Uri imageUri) throws Exception {
        // Convert image to Base64 string
        java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            throw new Exception("Could not open image stream");
        }
        
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }
    
    private void createStudentProfileInDatabase(FirebaseUser user, String profileImageBase64) {
        Student student = new Student();
        student.setUserId(user.getUid());
        student.setEmail(user.getEmail());
        student.setFullName(fullNameEditText.getText().toString().trim());
        student.setPhoneNumber(phoneEditText.getText().toString().trim());
        student.setAddress(addressEditText.getText().toString().trim());
        student.setProfilePhotoUrl(profileImageBase64); // Set profile image as Base64
        Log.d("StudentRegistration", "Setting profilePhotoUrl: " + (profileImageBase64 != null ? "Base64 data length: " + profileImageBase64.length() : "null"));
        
        // Set additional fields
        try {
            student.setAge(Integer.parseInt(ageEditText.getText().toString().trim()));
        } catch (NumberFormatException e) {
            student.setAge(0);
        }
        
        student.setGender(genderSpinner.getSelectedItem().toString());
        student.setCurrentClass(currentClassSpinner.getSelectedItem().toString());
        student.setBoard(boardSpinner.getSelectedItem().toString());
        student.setSchoolName(schoolNameEditText.getText().toString().trim());
        student.setParentContact(parentContactEditText.getText().toString().trim());
        
        // Set subjects and streams
        List<String> subjects = getSelectedSubjects();
        student.setSubjectsNeeded(subjects);
        
        List<String> streams = getSelectedStreams();
        student.setTuitionStreams(streams);
        
        student.setPreferredTeacherGender(teacherGenderPreferenceSpinner.getSelectedItem().toString());
        student.setPreferredTimeSlot(timeSlotSpinner.getSelectedItem().toString());
        
        try {
            student.setMinBudget(Integer.parseInt(minBudgetEditText.getText().toString().trim()));
        } catch (NumberFormatException e) {
            student.setMinBudget(0);
        }
        
        try {
            student.setMaxBudget(Integer.parseInt(maxBudgetEditText.getText().toString().trim()));
        } catch (NumberFormatException e) {
            student.setMaxBudget(0);
        }
        
        student.setAdditionalRequirements(additionalRequirementsEditText.getText().toString().trim());
        
        // Save to database
        databaseService.createStudent(student, new FirebaseDatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(StudentRegistration.this, "Registration successful!", Toast.LENGTH_LONG).show();
                // Navigate to success screen or dashboard
                finish();
            }
            
            @Override
            public void onFailure(String error) {
                Toast.makeText(StudentRegistration.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                continueButton.setEnabled(true);
            }
        });
    }
    
    private List<String> getSelectedSubjects() {
        List<String> subjects = new ArrayList<>();
        if (mathCheckbox.isChecked()) subjects.add("Mathematics");
        if (physicsCheckbox.isChecked()) subjects.add("Physics");
        if (chemistryCheckbox.isChecked()) subjects.add("Chemistry");
        if (biologyCheckbox.isChecked()) subjects.add("Biology");
        if (englishCheckbox.isChecked()) subjects.add("English");
        if (hindiCheckbox.isChecked()) subjects.add("Hindi");
        if (socialStudiesCheckbox.isChecked()) subjects.add("Social Studies");
        if (otherSubjectsCheckbox.isChecked() && !TextUtils.isEmpty(otherSubjectEditText.getText())) {
            subjects.add(otherSubjectEditText.getText().toString().trim());
        }
        return subjects;
    }
    
    private List<String> getSelectedStreams() {
        List<String> streams = new ArrayList<>();
        if (foundationCheckbox.isChecked()) streams.add("Foundation");
        if (class10Checkbox.isChecked()) streams.add("10th Class");
        if (class12Checkbox.isChecked()) streams.add("12th Class");
        if (neetCheckbox.isChecked()) streams.add("NEET Preparation");
        if (jeeCheckbox.isChecked()) streams.add("JEE Preparation");
        if (collegeLevelCheckbox.isChecked()) streams.add("College Level");
        return streams;
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        // User account created successfully, now create student profile
        Log.d("StudentRegistration", "onAuthSuccess called with user: " + (user != null ? user.getUid() : "null"));
        
        // Add a small delay to ensure Firebase state is stable
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check user again after delay
            FirebaseUser currentUser = authService.getCurrentUser();
            Log.d("StudentRegistration", "Current user after delay: " + (currentUser != null ? currentUser.getUid() : "null"));
            
            if (currentUser != null) {
                createStudentProfile(currentUser);
            } else {
                Log.e("StudentRegistration", "User is still null after delay, showing error");
                Toast.makeText(this, "Authentication error: Please try again", Toast.LENGTH_LONG).show();
                continueButton.setEnabled(true);
            }
        }, 1000); // 1 second delay
    }
    
    @Override
    public void onAuthFailure(String error) {
        Toast.makeText(this, "Authentication failed: " + error, Toast.LENGTH_LONG).show();
        continueButton.setEnabled(true);
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // Handle auth state changes if needed
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Reset registration mode
        StudentLogin.setRegistrationMode(false);
        
        // Clean up any resources
    }
}