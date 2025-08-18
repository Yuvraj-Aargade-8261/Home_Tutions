package com.example.hometutions.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {
    private static final String TAG = "FirebaseAuthService";
    
    private FirebaseAuth mAuth;
    private AuthCallback authCallback;
    
    public interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthFailure(String error);
        void onAuthStateChanged(FirebaseUser user);
    }
    
    public FirebaseAuthService(Context context, AuthCallback callback) {
        try {
            this.authCallback = callback;
            this.mAuth = FirebaseAuth.getInstance();
            
            // Set up auth state listener
            if (mAuth != null) {
                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        try {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (authCallback != null) {
                                authCallback.onAuthStateChanged(currentUser);
                            }
                        } catch (Exception e) {
                            // If auth state change handling fails, just log the error
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // If Firebase initialization fails, log the error
            e.printStackTrace();
        }
    }
    
    public void signInWithEmailAndPassword(String email, String password) {
        try {
            Log.d(TAG, "Signing in user with email: " + email);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                // Get user from the task result instead of mAuth.getCurrentUser()
                                FirebaseUser user = task.getResult().getUser();
                                Log.d(TAG, "User signed in successfully: " + (user != null ? user.getUid() : "null"));
                                if (authCallback != null) {
                                    authCallback.onAuthSuccess(user);
                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                if (authCallback != null) {
                                    authCallback.onAuthFailure("Authentication failed: " + task.getException().getMessage());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in signInWithEmailAndPassword", e);
            if (authCallback != null) {
                authCallback.onAuthFailure("Authentication failed: " + e.getMessage());
            }
        }
    }
    
    public void createUserWithEmailAndPassword(String email, String password) {
        try {
            Log.d(TAG, "Creating user with email: " + email);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                // Get user from the task result instead of mAuth.getCurrentUser()
                                FirebaseUser user = task.getResult().getUser();
                                Log.d(TAG, "User created successfully: " + (user != null ? user.getUid() : "null"));
                                if (authCallback != null) {
                                    authCallback.onAuthSuccess(user);
                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                if (authCallback != null) {
                                    authCallback.onAuthFailure("Registration failed: " + task.getException().getMessage());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in createUserWithEmailAndPassword", e);
            if (authCallback != null) {
                authCallback.onAuthFailure("Registration failed: " + e.getMessage());
            }
        }
    }
    
    public void signOut() {
        try {
            if (mAuth != null) {
                mAuth.signOut();
                Log.d(TAG, "User signed out successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error signing out user", e);
        }
    }
    
    public void forceSignOut() {
        try {
            if (mAuth != null) {
                // Force sign out and clear any cached authentication
                mAuth.signOut();
                
                // Clear any persistent authentication state without using emulator
                // This ensures no automatic re-authentication
                Log.d(TAG, "User forcefully signed out and authentication state cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during force sign out", e);
        }
    }
    
    public FirebaseUser getCurrentUser() {
        try {
            if (mAuth != null) {
                return mAuth.getCurrentUser();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean isUserSignedIn() {
        try {
            if (mAuth != null) {
                FirebaseUser user = mAuth.getCurrentUser();
                // Allow normal authentication flow during registration/login
                if (user != null) {
                    Log.d(TAG, "User detected, authentication status: " + (user != null));
                    return true; // Allow normal flow
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user sign in status", e);
            return false;
        }
    }
    
    public boolean isUserSignedInForSplash() {
        try {
            if (mAuth != null) {
                FirebaseUser user = mAuth.getCurrentUser();
                // This method is specifically for splash screen to prevent automatic login
                // But don't force sign out if user is in the middle of registration
                if (user != null) {
                    Log.d(TAG, "User detected during splash, checking if in registration process");
                    // Don't force sign out during registration - just return false
                    return false; // Always return false for splash
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user sign in status for splash", e);
            return false;
        }
    }
    
    public void preventAutomaticLogin() {
        try {
            if (mAuth != null) {
                // This method is called when the app is fully loaded and we want to prevent automatic login
                // It's safer than forcing sign out during splash
                Log.d(TAG, "Preventing automatic login for future sessions");
                // We'll handle this in the MainActivity instead
            }
        } catch (Exception e) {
            Log.e(TAG, "Error preventing automatic login", e);
        }
    }
    
    public void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            if (authCallback != null) {
                                // Don't call onAuthSuccess for password reset - let the UI handle it
                                // The UI will check the isInPasswordResetMode flag
                            }
                        } else {
                            Log.w(TAG, "Password reset email failed", task.getException());
                            if (authCallback != null) {
                                authCallback.onAuthFailure("Password reset failed: " + task.getException().getMessage());
                            }
                        }
                    }
                });
    }
}
