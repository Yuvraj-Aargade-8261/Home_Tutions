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

public class TeachersLogin extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    // Static flag to track if we're in registration mode
    private static boolean isInRegistrationMode = false;
    
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
    
    // State
    private boolean isLoading = false;
    private boolean isLoginSuccessful = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_login);
        
        try {
            initializeViews();
            setupClickListeners();
            setupAnimations();
            initializeFirebase();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to initialize activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.teacherIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.signUpText);
        loginFormContainer = findViewById(R.id.loginFormSection);
        loadingContainer = findViewById(R.id.loadingContainer);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        
        // Check if views are found; don't crash, just warn for easier diagnostics
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (backButton == null) missing.add("backButton");
        if (emailEditText == null) missing.add("teacherIdEditText");
        if (passwordEditText == null) missing.add("passwordEditText");
        if (loginButton == null) missing.add("loginButton");
        if (registerButton == null) missing.add("signUpText");
        if (loginFormContainer == null) missing.add("loginFormSection");
        if (loadingContainer == null) missing.add("loadingContainer");
        if (forgotPasswordText == null) missing.add("forgotPasswordText");
        if (!missing.isEmpty()) {
            android.util.Log.e("TeachersLogin", "Missing views: " + missing);
            Toast.makeText(this, "Some views not found: " + missing, Toast.LENGTH_LONG).show();
        }
        
        // Initially hide loading container
        loadingContainer.setVisibility(View.GONE);
    }
    
    private void setupClickListeners() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                if (validateForm()) {
                    performLogin();
                }
            });
        }
        
        if (registerButton != null) {
            registerButton.setOnClickListener(v -> {
                // Navigate to teacher registration
                Intent intent = new Intent(TeachersLogin.this, TeacherRegistration.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            });
        }
        
        if (forgotPasswordText != null) {
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
    }
    
    private void setupAnimations() {
        // Start entrance animations
        if (loginFormContainer != null && loginButton != null && registerButton != null) {
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
    }
    
    private void initializeFirebase() {
        try {
            authService = new FirebaseAuthService(this, this);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to initialize Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private boolean validateForm() {
        if (emailEditText == null || passwordEditText == null) {
            return false;
        }
        
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
        if (emailEditText == null || passwordEditText == null) {
            return;
        }
        
        showLoading(true);
        
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        
        authService.signInWithEmailAndPassword(email, password);
    }
    
    private void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        
        showLoading(true);
        authService.sendPasswordResetEmail(email);
    }
    
    private void showLoading(boolean show) {
        isLoading = show;
        if (loadingContainer != null && loginFormContainer != null) {
            if (show) {
                loadingContainer.setVisibility(View.VISIBLE);
                loginFormContainer.setVisibility(View.GONE);
            } else {
                loadingContainer.setVisibility(View.GONE);
                loginFormContainer.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        showLoading(false);
        
        if (user != null) {
            isLoginSuccessful = true;
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Navigate to teacher home with bottom navigation (default to Profile tab)
            Intent intent = new Intent(TeachersLogin.this, TeacherHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("open_profile", true);
            intent.putExtra("teacher_id", user.getUid());
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onAuthFailure(String error) {
        showLoading(false);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // CRITICAL: Do NOT automatically login users or interfere with registration
        // Users must manually enter credentials every time
        if (user != null) {
            // Check if this is during registration process
            if (isInRegistrationMode) {
                Log.d("TeachersLogin", "User detected during registration - not interfering");
                return;
            }
            
            // Don't interfere if login was successful
            if (isLoginSuccessful) {
                Log.d("TeachersLogin", "Login successful, not interfering with auth state");
                return;
            }
            
            // Only force sign out if this is a regular login attempt and we're not in the middle of login
            if (!isLoading) {
                Log.d("TeachersLogin", "User detected but forcing manual login");
                authService.signOut();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}