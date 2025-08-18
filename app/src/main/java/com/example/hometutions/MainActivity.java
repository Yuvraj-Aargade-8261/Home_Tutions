package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.utils.AnimationUtils;
import com.google.firebase.auth.FirebaseUser;
import com.example.hometutions.services.FirebaseAuthService;

public class MainActivity extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    private ImageView appLogoMain;
    private TextView welcomeText;
    private TextView appNameMain;
    private TextView subtitleText;
    private LinearLayout studentRoleButton;
    private LinearLayout teacherRoleButton;
    private LinearLayout headerSection;
    private LinearLayout roleSelectionContainer;
    private LinearLayout bottomInfoSection;
    
    private View[] floatingElements;
    private View[] topDecorativeShapes;
    
    private FirebaseAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupClickListeners();
        setupAnimations();
        initializeFirebase();
    }
    
    private void initializeViews() {
        appLogoMain = findViewById(R.id.appLogoMain);
        welcomeText = findViewById(R.id.welcomeText);
        appNameMain = findViewById(R.id.appNameMain);
        subtitleText = findViewById(R.id.subtitleText);
        studentRoleButton = findViewById(R.id.studentRoleButton);
        teacherRoleButton = findViewById(R.id.teacherRoleButton);
        headerSection = findViewById(R.id.headerSection);
        roleSelectionContainer = findViewById(R.id.roleSelectionContainer);
        bottomInfoSection = findViewById(R.id.bottomInfoSection);
        
        floatingElements = new View[]{
            findViewById(R.id.floatingElement1),
            findViewById(R.id.floatingElement2),
            findViewById(R.id.floatingElement3)
        };
        
        topDecorativeShapes = new View[]{
            findViewById(R.id.topDecorativeShape1),
            findViewById(R.id.topDecorativeShape2)
        };
        
        // Initially hide elements for animation
        headerSection.setVisibility(View.INVISIBLE);
        roleSelectionContainer.setVisibility(View.INVISIBLE);
        bottomInfoSection.setVisibility(View.INVISIBLE);
    }
    
    private void setupClickListeners() {
        // Set up click listeners for role selection
        studentRoleButton.setOnClickListener(v -> {
            // Navigate to Student Login
            Intent intent = new Intent(MainActivity.this, StudentLogin.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        teacherRoleButton.setOnClickListener(v -> {
            // Navigate to Teacher Login
            Intent intent = new Intent(MainActivity.this, TeachersLogin.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
    
    private void setupAnimations() {
        // Start floating animations for decorative elements
        for (View element : floatingElements) {
            AnimationUtils.startFloatingAnimation(element, 3000);
        }
        
        // Start decorative shapes animations
        for (View shape : topDecorativeShapes) {
            AnimationUtils.startFloatingAnimation(shape, 4000);
        }
        
        // Start staggered entrance animations
        AnimationUtils.slideUp(headerSection, 800, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationUtils.slideUp(roleSelectionContainer, 600, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationUtils.slideUp(bottomInfoSection, 400, null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    private void startEntranceAnimations() {
        // Animate header section
        AnimationUtils.slideUp(headerSection, 800, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // Start role selection container animation
                AnimationUtils.slideUp(roleSelectionContainer, 600, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Start bottom info section animation
                        AnimationUtils.fadeIn(bottomInfoSection, 500, null);
                        
                        // Add subtle animations to role buttons
                        addRoleButtonAnimations();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    private void addRoleButtonAnimations() {
        // Add hover effects and subtle animations to role buttons
        studentRoleButton.setOnHoverListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_HOVER_ENTER) {
                AnimationUtils.pulse(studentRoleButton, 300, 1);
            }
            return false;
        });
        
        teacherRoleButton.setOnHoverListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_HOVER_ENTER) {
                AnimationUtils.pulse(teacherRoleButton, 300, 1);
            }
            return false;
        });
        
        // Add subtle floating animation to app logo
        AnimationUtils.startFloatingAnimation(appLogoMain, 6000);
    }
    
    private void initializeFirebase() {
        authService = new FirebaseAuthService(this, this);
        
        // Check if user is already signed in and prevent automatic login
        if (authService != null && authService.isUserSignedIn()) {
            Log.d("MainActivity", "User already signed in, preventing automatic login");
            // Force sign out to ensure manual login
            authService.signOut();
            // Show a message that they need to login again
            Toast.makeText(this, "Please login again for security", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        // Handle successful authentication
        // This could navigate to a dashboard or profile activity
    }
    
    @Override
    public void onAuthFailure(String error) {
        // Handle authentication failure
        // Could show error message or retry options
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // Handle authentication state changes
        // This could update UI based on login status
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Stop all animations
        for (View element : floatingElements) {
            AnimationUtils.stopAnimations(element);
        }
        for (View shape : topDecorativeShapes) {
            AnimationUtils.stopAnimations(shape);
        }
        AnimationUtils.stopAnimations(appLogoMain);
    }
    
    @Override
    public void onBackPressed() {
        // Override back press to prevent going back to splash
        // You could show exit confirmation dialog here
        super.onBackPressed();
    }
}