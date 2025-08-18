package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.utils.AnimationUtils;
import com.example.hometutions.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;

public class StudentLogin extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    // Static flag to track if we're in registration mode
    private static boolean isInRegistrationMode = false;
    
    // Flag to track if we're in password reset mode
    private boolean isInPasswordResetMode = false;
    
    // Flag to track if we're in a legitimate login attempt
    private boolean isInLoginAttempt = false;
    
    // Method to set registration mode
    public static void setRegistrationMode(boolean mode) {
        isInRegistrationMode = mode;
    }
    
    // UI Elements
    private ImageView backButton;
    private EditText emailEditText, passwordEditText;
    private LinearLayout loginButton;
    private TextView registerButton;
    private LinearLayout loginFormContainer, loadingContainer;
    private TextView forgotPasswordText;
    
    // Services
    private FirebaseAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        
        initializeViews();
        setupClickListeners();
        setupAnimations();
        initializeFirebase();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.emailPhoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.signUpText);
        loginFormContainer = findViewById(R.id.loginFormSection);
        loadingContainer = findViewById(R.id.loadingContainer);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        
        // Initially hide loading container
        loadingContainer.setVisibility(View.GONE);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        loginButton.setOnClickListener(v -> {
            if (validateForm()) {
                performLogin();
            }
        });
        
        registerButton.setOnClickListener(v -> {
            // Navigate to student registration
            Intent intent = new Intent(StudentLogin.this, StudentRegistration.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        
        forgotPasswordText.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (ValidationUtils.isValidEmail(email)) {
                resetPassword(email);
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupAnimations() {
        // Start entrance animations
        AnimationUtils.slideUp(loginFormContainer, 600, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // Add subtle animations to form elements
                AnimationUtils.fadeIn(loginButton, 300, null);
                AnimationUtils.fadeIn(registerButton, 300, null);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    private void initializeFirebase() {
        authService = new FirebaseAuthService(this, this);
    }
    
    private boolean validateForm() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void performLogin() {
        showLoading(true);
        isInPasswordResetMode = false;
        isInLoginAttempt = true;
        
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        
        authService.signInWithEmailAndPassword(email, password);
    }
    
    private void resetPassword(String email) {
        showLoading(true);
        isInPasswordResetMode = true;
        authService.sendPasswordResetEmail(email);
    }
    
    private void showLoading(boolean show) {
        if (show) {
            loadingContainer.setVisibility(View.VISIBLE);
            loginFormContainer.setVisibility(View.GONE);
        } else {
            loadingContainer.setVisibility(View.GONE);
            loginFormContainer.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        showLoading(false);
        isInLoginAttempt = false; // Reset the flag
        
        Log.d("StudentLogin", "onAuthSuccess called with user: " + (user != null ? user.getUid() : "null"));
        
        if (isInPasswordResetMode) {
            // This was a password reset request
            Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_LONG).show();
            isInPasswordResetMode = false;
        } else if (user != null) {
            // This was a successful login
            Log.d("StudentLogin", "Login successful, navigating to StudentHomeActivity");
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            
            // Navigate to student dashboard
            Intent intent = new Intent(StudentLogin.this, StudentHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // This shouldn't happen, but handle it gracefully
            Log.e("StudentLogin", "User is null after successful authentication - this indicates a Firebase issue");
            Toast.makeText(this, "Authentication issue: Please try again", Toast.LENGTH_LONG).show();
            
            // Try to get the current user from Firebase as a fallback
            FirebaseUser currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                Log.d("StudentLogin", "Fallback: Found current user, proceeding with navigation");
                Intent intent = new Intent(StudentLogin.this, StudentHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Log.e("StudentLogin", "Fallback failed: No current user found");
            }
        }
    }
    
    @Override
    public void onAuthFailure(String error) {
        showLoading(false);
        isInLoginAttempt = false; // Reset the flag
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // CRITICAL: Do NOT automatically login users or interfere with registration
        // Users must manually enter credentials every time
        if (user != null) {
            // Check if this is during registration process
            if (isInRegistrationMode) {
                Log.d("StudentLogin", "User detected during registration - not interfering");
                return;
            }
            
            // Check if this is during a legitimate login attempt
            if (isInLoginAttempt) {
                Log.d("StudentLogin", "User detected during login attempt - allowing authentication to complete");
                return;
            }
            
            // Only force sign out if this is not during registration or login
            Log.d("StudentLogin", "User detected but forcing manual login");
            authService.signOut();
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}