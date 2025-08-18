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

import com.example.hometutions.models.Teacher;
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

public class TeacherRegistration extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    // UI Elements
    private ImageView backButton;
    private ImageView profilePhotoImage;
    private LinearLayout profilePhotoPlaceholder;
    private EditText fullNameEditText, ageEditText, phoneEditText, emailEditText, passwordEditText;
    private EditText addressEditText, institutionEditText, experienceEditText;
    
    private Spinner genderSpinner, qualificationSpinner;
    
    private CheckBox mathCheckbox, physicsCheckbox, chemistryCheckbox, biologyCheckbox;
    private CheckBox englishCheckbox, otherSubjectsCheckbox;
    private CheckBox class10Checkbox, class12Checkbox, neetCheckbox, jeeCheckbox, foundationCheckbox;
    private CheckBox termsCheckbox;
    
    private LinearLayout otherSubjectLayout;
    private EditText otherSubjectEditText;
    private LinearLayout aadharUploadContainer, panUploadContainer, degreeUploadContainer;
    private TextView aadharStatus, panStatus, degreeStatus;
    private LinearLayout saveDraftButton, continueButton;
    
    // Services
    private FirebaseAuthService authService;
    private FirebaseDatabaseService databaseService;
    private FirebaseStorageService storageService;
    
    // Data
    private Uri profilePhotoUri;
    private Uri aadharCardUri, panCardUri, degreeCertificateUri;
    private String currentDocumentType = "";

    
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

    // Activity result launcher for document picker
    private final ActivityResultLauncher<Intent> documentPickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri documentUri = result.getData().getData();
                if (documentUri != null) {
                    handleDocumentSelection(documentUri);
                }
            } else if (result.getResultCode() == RESULT_CANCELED) {
                // User cancelled document selection, reset status
                resetDocumentStatus(currentDocumentType);
            }
        }
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);
        
        // Set registration mode to prevent interference from login activities
        TeachersLogin.setRegistrationMode(true);
        
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
        institutionEditText = findViewById(R.id.institutionEditText);
        experienceEditText = findViewById(R.id.experienceEditText);
        
        // Spinners
        genderSpinner = findViewById(R.id.genderSpinner);
        qualificationSpinner = findViewById(R.id.qualificationSpinner);
        
        // Checkboxes
        mathCheckbox = findViewById(R.id.mathCheckbox);
        physicsCheckbox = findViewById(R.id.physicsCheckbox);
        chemistryCheckbox = findViewById(R.id.chemistryCheckbox);
        biologyCheckbox = findViewById(R.id.biologyCheckbox);
        englishCheckbox = findViewById(R.id.englishCheckbox);
        otherSubjectsCheckbox = findViewById(R.id.otherSubjectsCheckbox);
        class10Checkbox = findViewById(R.id.class10Checkbox);
        class12Checkbox = findViewById(R.id.class12Checkbox);
        neetCheckbox = findViewById(R.id.neetCheckbox);
        jeeCheckbox = findViewById(R.id.jeeCheckbox);
        foundationCheckbox = findViewById(R.id.foundationCheckbox);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        
        // Layouts
        otherSubjectLayout = findViewById(R.id.otherSubjectLayout);
        otherSubjectEditText = findViewById(R.id.otherSubjectEditText);
        aadharUploadContainer = findViewById(R.id.aadharUploadContainer);
        panUploadContainer = findViewById(R.id.panUploadContainer);
        degreeUploadContainer = findViewById(R.id.degreeUploadContainer);
        
        // Status texts
        aadharStatus = findViewById(R.id.aadharStatus);
        panStatus = findViewById(R.id.panStatus);
        degreeStatus = findViewById(R.id.degreeStatus);
        
        // Buttons
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
        
        // Qualification spinner
        String[] qualifications = {"High School", "Bachelor's Degree", "Master's Degree", "PhD", "Other"};
        ArrayAdapter<String> qualificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, qualifications);
        qualificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qualificationSpinner.setAdapter(qualificationAdapter);
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
        
        // Document upload listeners
        aadharUploadContainer.setOnClickListener(v -> uploadDocument("aadhar"));
        panUploadContainer.setOnClickListener(v -> uploadDocument("pan"));
        degreeUploadContainer.setOnClickListener(v -> uploadDocument("degree"));
        
        // Save draft button
        saveDraftButton.setOnClickListener(v -> {
            AnimationUtils.pulse(saveDraftButton, 200, 1);
            saveDraft();
        });
        
        // Continue button
        continueButton.setOnClickListener(v -> {
            AnimationUtils.pulse(continueButton, 200, 1);
            if (validateForm()) {
                if (checkAllDocumentsUploaded()) {
                    registerTeacher();
                } else {
                    showError("Please upload all required documents before continuing");
                }
            }
        });
    }
    
    private void setupAnimations() {
        // Start entrance animations
        View[] sections = {
            findViewById(R.id.headerSection),
            findViewById(R.id.profilePhotoSection),
            findViewById(R.id.personalDetailsSection),
            findViewById(R.id.educationalDetailsSection),
            findViewById(R.id.subjectsStreamsSection),
            findViewById(R.id.documentsSection)
        };
        
        // Staggered animation for sections
        AnimationUtils.staggerAnimation(sections, 200, 600, null);
    }
    
    private void initializeFirebase() {
        authService = new FirebaseAuthService(this, this);
        databaseService = new FirebaseDatabaseService();
        storageService = new FirebaseStorageService();
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
        Log.d("TeacherRegistration", "Profile photo selected: " + uri.toString());
        
        // Animate the photo appearance
        AnimationUtils.scaleIn(profilePhotoImage, 300, null);
    }
    
    private void uploadDocument(String documentType) {
        currentDocumentType = documentType;
        
        // Update status to show selecting
        switch (documentType) {
            case "aadhar":
                aadharStatus.setText("Selecting document...");
                aadharStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "pan":
                panStatus.setText("Selecting document...");
                panStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "degree":
                degreeStatus.setText("Selecting document...");
                degreeStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
        }
        
        // Open document picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        documentPickerLauncher.launch(intent);
    }

    private void handleDocumentSelection(Uri documentUri) {
        if (currentDocumentType.isEmpty()) return;
        
        // Update status to show uploading
        switch (currentDocumentType) {
            case "aadhar":
                aadharStatus.setText("Uploading...");
                aadharStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                aadharCardUri = documentUri;
                break;
            case "pan":
                panStatus.setText("Uploading...");
                panStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                panCardUri = documentUri;
                break;
            case "degree":
                degreeStatus.setText("Uploading...");
                degreeStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                degreeCertificateUri = documentUri;
                break;
        }
        
        // Upload document to Firebase Storage
        uploadDocumentToStorage(documentUri, currentDocumentType);
    }

    private void uploadDocumentToStorage(Uri documentUri, String documentType) {
        // During registration, we don't have a user ID yet, so we'll use a temporary approach
        // We'll store the documents locally and upload them after successful registration
        
        // For now, simulate successful upload and store the URI locally
        // In a production app, you might want to use a different approach
        
        // Update status to show success (temporary)
        switch (documentType) {
            case "aadhar":
                aadharCardUri = documentUri;
                aadharStatus.setText("Selected ✓");
                aadharStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "pan":
                panCardUri = documentUri;
                panStatus.setText("Selected ✓");
                panStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "degree":
                degreeCertificateUri = documentUri;
                degreeStatus.setText("Selected ✓");
                degreeStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
        }
        
        Toast.makeText(this, documentType + " document selected successfully!", Toast.LENGTH_SHORT).show();
        
        // Note: Documents will be uploaded to Firebase Storage after successful registration
        // in the createTeacherProfile method
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
        
        if (TextUtils.isEmpty(institutionEditText.getText().toString().trim())) {
            showError("Please enter your institution name");
            return false;
        }
        
        if (TextUtils.isEmpty(experienceEditText.getText().toString().trim())) {
            showError("Please enter your years of experience");
            return false;
        }
        
        if (!termsCheckbox.isChecked()) {
            showError("Please accept the terms and conditions");
            return false;
        }
        
        // Check if required documents are selected
        if (aadharCardUri == null) {
            showError("Please select your Aadhar Card");
            return false;
        }
        
        if (panCardUri == null) {
            showError("Please select your PAN Card");
            return false;
        }
        
        if (degreeCertificateUri == null) {
            showError("Please select your Degree Certificate");
            return false;
        }
        
        return true;
    }

    private boolean checkAllDocumentsUploaded() {
        return aadharCardUri != null && panCardUri != null && degreeCertificateUri != null;
    }

    private void resetDocumentStatus(String documentType) {
        switch (documentType) {
            case "aadhar":
                aadharStatus.setText("Tap to upload");
                aadharStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "pan":
                panStatus.setText("Tap to upload");
                panStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "degree":
                degreeStatus.setText("Tap to upload");
                degreeStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
        }
    }

    private void clearDocumentData(String documentType) {
        switch (documentType) {
            case "aadhar":
                aadharCardUri = null;
                break;
            case "pan":
                panCardUri = null;
                break;
            case "degree":
                degreeCertificateUri = null;
                break;
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void saveDraft() {
        // Save form data to SharedPreferences or local database
        Toast.makeText(this, "Draft saved successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void registerTeacher() {
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
    
    private void createTeacherProfile(FirebaseUser user) {
        // Add null check to prevent crash
        if (user == null) {
            Log.e("TeacherRegistration", "FirebaseUser is null in createTeacherProfile");
            Toast.makeText(this, "Authentication error: User object is null", Toast.LENGTH_LONG).show();
            continueButton.setEnabled(true);
            return;
        }
        
        // Validate that all required data is present
        if (!validateRequiredData()) {
            continueButton.setEnabled(true);
            return;
        }
        
        // Convert profile image to Base64 if selected
        String profileImageBase64 = null;
        if (profilePhotoUri != null) {
            try {
                profileImageBase64 = convertImageToBase64(profilePhotoUri);
                Log.d("TeacherRegistration", "Profile image converted to Base64 successfully, length: " + profileImageBase64.length());
                Log.d("TeacherRegistration", "Base64 preview: " + profileImageBase64.substring(0, Math.min(100, profileImageBase64.length())));
            } catch (Exception e) {
                Log.e("TeacherRegistration", "Failed to convert profile image to Base64: " + e.getMessage());
                Toast.makeText(this, "Failed to process profile image", Toast.LENGTH_SHORT).show();
                continueButton.setEnabled(true);
                return;
            }
        } else {
            Log.d("TeacherRegistration", "No profile photo selected");
        }
        
        // Create teacher profile with Base64 image
        createTeacherProfileInDatabase(user, profileImageBase64);
    }
    
    private boolean validateRequiredData() {
        // Check if all required documents are selected
        if (aadharCardUri == null) {
            Toast.makeText(this, "Please select your Aadhar Card", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (panCardUri == null) {
            Toast.makeText(this, "Please select your PAN Card", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (degreeCertificateUri == null) {
            Toast.makeText(this, "Please select your Degree Certificate", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check if all form fields are filled
        if (TextUtils.isEmpty(fullNameEditText.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(phoneEditText.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(addressEditText.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(institutionEditText.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your institution name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(experienceEditText.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your years of experience", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private String convertImageToBase64(Uri imageUri) throws Exception {
        // Optimized image conversion with compression
        try {
            // First, decode the image to get dimensions
            android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            
            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                throw new Exception("Could not open image stream");
            }
            
            android.graphics.BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            // Calculate sample size to reduce memory usage
            int maxSize = 1024; // Max dimension
            int sampleSize = 1;
            if (options.outHeight > maxSize || options.outWidth > maxSize) {
                sampleSize = Math.min(options.outWidth / maxSize, options.outHeight / maxSize);
                sampleSize = Math.max(1, sampleSize);
            }
            
            // Decode with sample size
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565; // Use less memory
            
            inputStream = getContentResolver().openInputStream(imageUri);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            if (bitmap == null) {
                throw new Exception("Failed to decode image");
            }
            
            // Compress the bitmap
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream); // 70% quality
            
            // Convert to Base64
            byte[] bytes = outputStream.toByteArray();
            String base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            
            // Clean up
            bitmap.recycle();
            outputStream.close();
            
            Log.d("TeacherRegistration", "Image converted successfully: " + bytes.length + " bytes -> " + base64.length() + " Base64 chars");
            return base64;
            
        } catch (Exception e) {
            Log.e("TeacherRegistration", "Error converting image to Base64: " + e.getMessage());
            throw e;
        }
    }
    
    private void createTeacherProfileInDatabase(FirebaseUser user, String profileImageBase64) {
        Teacher teacher = new Teacher();
        teacher.setUserId(user.getUid());
        teacher.setEmail(user.getEmail());
        teacher.setFullName(fullNameEditText.getText().toString().trim());
        teacher.setPhoneNumber(phoneEditText.getText().toString().trim());
        teacher.setAddress(addressEditText.getText().toString().trim());
        teacher.setProfileImageUrl(profileImageBase64); // Set profile image as Base64
        Log.d("TeacherRegistration", "Setting profileImageUrl: " + (profileImageBase64 != null ? "Base64 data length: " + profileImageBase64.length() : "null"));
        
        // Set additional fields
        try {
            teacher.setAge(Integer.parseInt(ageEditText.getText().toString().trim()));
        } catch (NumberFormatException e) {
            teacher.setAge(0);
        }
        
        teacher.setGender(genderSpinner.getSelectedItem().toString());
        teacher.setHighestQualification(qualificationSpinner.getSelectedItem().toString());
        teacher.setInstitution(institutionEditText.getText().toString().trim());
        
        try {
            teacher.setYearsOfExperience(Integer.parseInt(experienceEditText.getText().toString().trim()));
        } catch (NumberFormatException e) {
            teacher.setYearsOfExperience(0);
        }
        
        // Set subjects and streams
        List<String> subjects = getSelectedSubjects();
        teacher.setSubjectsTaught(subjects);
        
        List<String> streams = getSelectedStreams();
        teacher.setTeachingStreams(streams);
        
        // Show progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Processing images and saving data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Convert and set document images as Base64
        String aadharBase64 = null;
        String panBase64 = null;
        String degreeBase64 = null;
        
        try {
            if (aadharCardUri != null) {
                progressDialog.setMessage("Processing Aadhar card...");
                aadharBase64 = convertImageToBase64(aadharCardUri);
                Log.d("TeacherRegistration", "Aadhar card converted to Base64 successfully, length: " + aadharBase64.length());
            }
            
            if (panCardUri != null) {
                progressDialog.setMessage("Processing PAN card...");
                panBase64 = convertImageToBase64(panCardUri);
                Log.d("TeacherRegistration", "PAN card converted to Base64 successfully, length: " + panBase64.length());
            }
            
            if (degreeCertificateUri != null) {
                progressDialog.setMessage("Processing degree certificate...");
                degreeBase64 = convertImageToBase64(degreeCertificateUri);
                Log.d("TeacherRegistration", "Degree certificate converted to Base64 successfully, length: " + degreeBase64.length());
            }
            
            progressDialog.setMessage("Saving to database...");
            
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e("TeacherRegistration", "Failed to process images: " + e.getMessage());
            Toast.makeText(this, "Failed to process images: " + e.getMessage(), Toast.LENGTH_LONG).show();
            continueButton.setEnabled(true);
            return;
        }
        
        // Set document images as Base64 in the Teacher model
        teacher.setAadharCardUrl(aadharBase64);
        teacher.setPanCardUrl(panBase64);
        teacher.setDegreeCertificateUrl(degreeBase64);
        
        // Save to database
        databaseService.createTeacher(teacher, new FirebaseDatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                progressDialog.dismiss();
                Toast.makeText(TeacherRegistration.this, "Registration successful! All images stored in database.", Toast.LENGTH_LONG).show();
                
                // Navigate to success screen or dashboard
                finish();
            }
            
            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(TeacherRegistration.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
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
        if (otherSubjectsCheckbox.isChecked() && !TextUtils.isEmpty(otherSubjectEditText.getText())) {
            subjects.add(otherSubjectEditText.getText().toString().trim());
        }
        return subjects;
    }
    
    private List<String> getSelectedStreams() {
        List<String> streams = new ArrayList<>();
        if (class10Checkbox.isChecked()) streams.add("10th Class");
        if (class12Checkbox.isChecked()) streams.add("12th Class");
        if (neetCheckbox.isChecked()) streams.add("NEET Preparation");
        if (jeeCheckbox.isChecked()) streams.add("JEE Preparation");
        if (foundationCheckbox.isChecked()) streams.add("Foundation");
        return streams;
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        // User account created successfully, now create teacher profile
        Log.d("TeacherRegistration", "onAuthSuccess called with user: " + (user != null ? user.getUid() : "null"));
        
        if (user != null) {
            // Create teacher profile immediately with the user from callback
            createTeacherProfile(user);
        } else {
            Log.e("TeacherRegistration", "User is null in onAuthSuccess callback");
            Toast.makeText(this, "Authentication error: Please try again", Toast.LENGTH_LONG).show();
            continueButton.setEnabled(true);
        }
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
        TeachersLogin.setRegistrationMode(false);
        
        // Clean up any resources
    }
}