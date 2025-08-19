package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hometutions.fragments.TeacherDashboardFragment;
import com.example.hometutions.fragments.TeacherProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeacherHomeActivity extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private FirebaseUser currentUser;
	private BottomNavigationView bottomNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher_home);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();
		if (currentUser == null) {
			startActivity(new Intent(this, TeachersLogin.class));
			finish();
			return;
		}

		bottomNavigationView = findViewById(R.id.bottom_navigation_teacher);
		setupBottomNavigation();

		boolean openProfile = getIntent().getBooleanExtra("open_profile", false);
		String teacherId = getIntent().getStringExtra("teacher_id");
		if (openProfile) {
			bottomNavigationView.setSelectedItemId(R.id.nav_profile_teacher);
			Fragment f = new TeacherProfileFragment();
			if (teacherId != null) {
				Bundle b = new Bundle();
				b.putString("teacher_id", teacherId);
				f.setArguments(b);
			}
			loadFragment(f);
		} else {
			loadFragment(new TeacherDashboardFragment());
		}
	}

	private void setupBottomNavigation() {
		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			Fragment selectedFragment = null;
			int itemId = item.getItemId();
			if (itemId == R.id.nav_home_teacher) {
				selectedFragment = new TeacherDashboardFragment();
			} else if (itemId == R.id.nav_profile_teacher) {
				selectedFragment = new TeacherProfileFragment();
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
		transaction.replace(R.id.fragment_container_teacher, fragment);
		transaction.commit();
	}
}


