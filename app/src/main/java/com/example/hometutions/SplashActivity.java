package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.utils.AnimationUtils;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity implements FirebaseAuthService.AuthCallback {
    
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final String TAG = "SplashActivity";
    
    private ImageView appIcon;
    private TextView appName;
    private TextView appTagline;
    private ProgressBar progressBar;
    private View topDecorativeCircle;
    private View bottomDecorativeCircle;
    private View[] floatingElements;
    
    private FirebaseAuthService authService;
    private Handler handler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // CRITICAL: Set system properties BEFORE any other operations to prevent callGcSupression crashes
        try {
            System.setProperty("java.awt.headless", "true");
            System.setProperty("sun.awt.disableMixing", "true");
            System.setProperty("sun.java2d.opengl", "false");
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.xrender", "false");
            System.setProperty("java.awt.useSystemAAFontSettings", "on");
            System.setProperty("sun.java2d.pmoffscreen", "false");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set system properties", e);
        }
        
        // Set up aggressive crash prevention for this specific activity
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception in thread: " + thread.getName(), throwable);
                
                // Check if this is the specific callGcSupression error
                if (throwable instanceof NullPointerException && 
                    throwable.getStackTrace().length > 0 &&
                    throwable.getStackTrace()[0].getMethodName().contains("callGcSupression")) {
                    
                    Log.w(TAG, "Detected callGcSupression error, applying aggressive cleanup");
                    
                    // Apply aggressive cleanup for this specific error
                    try {
                        // Force multiple garbage collections with delays
                        for (int i = 0; i < 5; i++) {
                            System.gc();
                            Thread.sleep(200); // Longer delay between GC calls
                        }
                        
                        // Set additional system properties
                        System.setProperty("sun.awt.disableMixing", "true");
                        System.setProperty("sun.java2d.opengl", "false");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error during aggressive cleanup", e);
                    }
                }
                
                // Always try to continue with the app
                try {
                    // Force garbage collection to prevent system-level issues
                    System.gc();
                    
                    // Continue with normal flow
                    runOnUiThread(() -> {
                        try {
                            setContentView(R.layout.activity_splash);
                            initializeViews();
                            setupAnimations();
                            initializeFirebase();
                            startSplashSequence();
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to reinitialize after crash", e);
                            // If all else fails, just navigate to main activity
                            navigateToMainActivity();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to handle crash recovery", e);
                    // Last resort: navigate to main activity
                    navigateToMainActivity();
                }
            }
        });
        
        try {
            setContentView(R.layout.activity_splash);
            initializeViews();
            setupAnimations();
            initializeFirebase();
            startSplashSequence();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize SplashActivity", e);
            // If initialization fails, navigate to main activity
            navigateToMainActivity();
        }
    }
    
    private void initializeViews() {
        try {
            appIcon = findViewById(R.id.appIcon);
            appName = findViewById(R.id.appName);
            appTagline = findViewById(R.id.appTagline);
            progressBar = findViewById(R.id.progressBar);
            topDecorativeCircle = findViewById(R.id.topDecorativeCircle);
            bottomDecorativeCircle = findViewById(R.id.bottomDecorativeCircle);
            
            floatingElements = new View[]{
                findViewById(R.id.floatingElement1),
                findViewById(R.id.floatingElement2),
                findViewById(R.id.floatingElement3)
            };
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize views", e);
        }
        
        // Initially hide all elements safely
        if (appIcon != null) appIcon.setVisibility(View.INVISIBLE);
        if (appName != null) appName.setVisibility(View.INVISIBLE);
        if (appTagline != null) appTagline.setVisibility(View.INVISIBLE);
        if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
    }
    
    private void setupAnimations() {
        try {
            // Start floating animations for decorative elements
            if (floatingElements != null) {
                for (View element : floatingElements) {
                    if (element != null) {
                        try {
                            AnimationUtils.startFloatingAnimation(element, 3000);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to start floating animation", e);
                        }
                    }
                }
            }
            
            // Start decorative circles animations
            if (topDecorativeCircle != null) {
                try {
                    AnimationUtils.startFloatingAnimation(topDecorativeCircle, 4000);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start top circle animation", e);
                }
            }
            
            if (bottomDecorativeCircle != null) {
                try {
                    AnimationUtils.startFloatingAnimation(bottomDecorativeCircle, 5000);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start bottom circle animation", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup animations", e);
        }
    }
    
    private void initializeFirebase() {
        try {
            authService = new FirebaseAuthService(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase", e);
        }
    }
    
    private void startSplashSequence() {
        try {
            handler = new Handler(Looper.getMainLooper());
            
            // Start entrance animations safely
            if (appIcon != null) {
                AnimationUtils.scaleIn(appIcon, 800, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (appName != null) {
                            AnimationUtils.slideUp(appName, 600, new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if (appTagline != null) {
                                        AnimationUtils.fadeIn(appTagline, 500, new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {}

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                // Start progress bar animation
                                                if (progressBar != null) {
                                                    try {
                                                        AnimationUtils.startLoadingAnimation(progressBar, 1000);
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "Failed to start loading animation", e);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {}
                                        });
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {}
                            });
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start splash sequence", e);
        }
        
        // CRITICAL CHANGE: Always navigate to MainActivity for role selection
        // Users must manually login every time - no automatic login
        handler.postDelayed(() -> {
            try {
                // Check authentication status without interfering with registration
                if (authService != null) {
                    // Use the splash-specific method that doesn't interfere with registration
                    authService.isUserSignedInForSplash();
                }
                
                // Always navigate to main activity for role selection
                Log.d(TAG, "Navigating to MainActivity for manual login");
                navigateToRoleSelection();
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to navigate after splash", e);
                // If there's any error, just navigate to main activity
                navigateToRoleSelection();
            }
        }, SPLASH_DURATION);
    }
    
    private void navigateToRoleSelection() {
        try {
            // Always navigate to MainActivity for role selection
            // This ensures users must choose their role and login manually
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            Log.e(TAG, "Failed to navigate to role selection", e);
            finish();
        }
    }
    
    private void navigateToMainActivity() {
        // Use the role selection navigation method
        navigateToRoleSelection();
    }
    
    @Override
    public void onAuthSuccess(FirebaseUser user) {
        // This should not be called during splash since we're not doing any auth
        Log.d(TAG, "Auth success during splash - unexpected");
        navigateToMainActivity();
    }
    
    @Override
    public void onAuthFailure(String error) {
        // This should not be called during splash since we're not doing any auth
        Log.d(TAG, "Auth failure during splash - unexpected: " + error);
        navigateToMainActivity();
    }
    
    @Override
    public void onAuthStateChanged(FirebaseUser user) {
        // Auth state changed during splash - ignore this
        Log.d(TAG, "Auth state changed during splash - ignoring");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Force garbage collection to prevent system-level crashes
        try {
            System.gc();
            Thread.sleep(50); // Small delay to allow GC to complete
        } catch (Exception e) {
            Log.e(TAG, "Failed to force GC in onResume", e);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Clean up resources to prevent memory issues
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to cleanup handler in onPause", e);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            
            // Stop all animations safely
            if (floatingElements != null) {
                for (View element : floatingElements) {
                    if (element != null) {
                        try {
                            AnimationUtils.stopAnimations(element);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to stop floating element animation", e);
                        }
                    }
                }
            }
            
            if (topDecorativeCircle != null) {
                try {
                    AnimationUtils.stopAnimations(topDecorativeCircle);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to stop top circle animation", e);
                }
            }
            
            if (bottomDecorativeCircle != null) {
                try {
                    AnimationUtils.stopAnimations(bottomDecorativeCircle);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to stop bottom circle animation", e);
                }
            }
            
            if (appIcon != null) {
                try {
                    AnimationUtils.stopAnimations(appIcon);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to stop app icon animation", e);
                }
            }
            
            // Final cleanup - force garbage collection
            System.gc();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to cleanup in onDestroy", e);
        }
    }
}