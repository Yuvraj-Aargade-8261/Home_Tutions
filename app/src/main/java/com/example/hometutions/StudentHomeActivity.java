package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hometutions.fragments.StudentProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentHomeActivity extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        
        if (currentUser == null) {
            // Redirect to login if not authenticated
            startActivity(new Intent(this, StudentLogin.class));
            finish();
            return;
        }
        
        initializeViews();
        setupBottomNavigation();
        
        // Load default fragment (Dashboard)
        loadFragment(new StudentDashboard());
    }
    
    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new StudentDashboard();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new StudentProfileFragment();
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            
            return false;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // Replace the current fragment
        transaction.replace(R.id.fragment_container, fragment);
        
        // Don't add to back stack for bottom navigation
        transaction.commit();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Check if we're on the home fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof StudentDashboard) {
            // Show exit confirmation
            showExitConfirmation();
        } else {
            // Navigate back to home
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
    
    private void showExitConfirmation() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
