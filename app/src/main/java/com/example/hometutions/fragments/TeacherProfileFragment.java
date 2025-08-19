package com.example.hometutions.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hometutions.R;
import com.example.hometutions.models.Teacher;
import com.example.hometutions.services.FirebaseAuthService;
import com.example.hometutions.services.FirebaseDatabaseService;
import com.example.hometutions.utils.AnimationUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class TeacherProfileFragment extends Fragment implements FirebaseAuthService.AuthCallback {

	private static final String TAG = "TeacherProfileFragment";

	// UI Elements
	private ImageView backButton, moreOptionsButton, profilePhoto;
	private TextView teacherNameText, ratingText, experienceText, locationText;
	private TextView ageText, genderText, emailText, addressText;
	private TextView qualificationText, institutionText;
	private LinearLayout subjectsContainer, streamsContainer;
	private CardView profilePhotoCard;
	private LinearLayout mainContent;

	// Services
	private FirebaseAuthService authService;
	private FirebaseDatabaseService databaseService;

	// Data
	private Teacher currentTeacher;
	private String teacherId;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_teacher_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			initializeServices();
			initializeViews(view);
			setupClickListeners();
			loadTeacherData();
			setupAnimations();
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize fragment", e);
			Toast.makeText(requireContext(), "Failed to initialize: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void initializeServices() {
		authService = new FirebaseAuthService(requireActivity(), this);
		databaseService = new FirebaseDatabaseService();
	}

	private void initializeViews(View root) {
		backButton = root.findViewById(R.id.backButton);
		moreOptionsButton = root.findViewById(R.id.moreOptionsButton);
		profilePhoto = root.findViewById(R.id.profilePhoto);
		teacherNameText = root.findViewById(R.id.teacherNameText);
		ratingText = root.findViewById(R.id.ratingText);
		experienceText = root.findViewById(R.id.experienceText);
		locationText = root.findViewById(R.id.locationText);
		profilePhotoCard = root.findViewById(R.id.profilePhotoCard);
		mainContent = root.findViewById(R.id.mainContent);
		ageText = root.findViewById(R.id.ageText);
		genderText = root.findViewById(R.id.genderText);
		emailText = root.findViewById(R.id.emailText);
		addressText = root.findViewById(R.id.addressText);
		qualificationText = root.findViewById(R.id.qualificationText);
		institutionText = root.findViewById(R.id.institutionText);
		subjectsContainer = root.findViewById(R.id.subjectsContainer);
		streamsContainer = root.findViewById(R.id.streamsContainer);

		// Hide back button inside a bottom-nav flow
		if (backButton != null) backButton.setVisibility(View.GONE);

		List<String> missingViews = new ArrayList<>();
		if (teacherNameText == null) missingViews.add("teacherNameText");
		if (ratingText == null) missingViews.add("ratingText");
		if (experienceText == null) missingViews.add("experienceText");
		if (locationText == null) missingViews.add("locationText");
		if (profilePhotoCard == null) missingViews.add("profilePhotoCard");
		if (mainContent == null) missingViews.add("mainContent");
		if (ageText == null) missingViews.add("ageText");
		if (genderText == null) missingViews.add("genderText");
		if (emailText == null) missingViews.add("emailText");
		if (addressText == null) missingViews.add("addressText");
		if (qualificationText == null) missingViews.add("qualificationText");
		if (institutionText == null) missingViews.add("institutionText");
		if (subjectsContainer == null) missingViews.add("subjectsContainer");
		if (streamsContainer == null) missingViews.add("streamsContainer");
		if (!missingViews.isEmpty()) {
			Log.e(TAG, "Missing views: " + missingViews);
		}
	}

	private void setupClickListeners() {
		if (moreOptionsButton != null) {
			moreOptionsButton.setOnClickListener(v -> showMoreOptionsMenu());
		}
	}

	private void showMoreOptionsMenu() {
		if (!isAdded()) return;
		android.widget.PopupMenu popup = new android.widget.PopupMenu(requireContext(), moreOptionsButton);
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
		if (!isAdded()) return;
		if (currentTeacher == null) {
			Toast.makeText(requireContext(), "Teacher data not available", Toast.LENGTH_SHORT).show();
			return;
		}
		String shareText = "Check out this teacher: " + currentTeacher.getFullName();
		if (currentTeacher.getSubjects() != null) shareText += " - " + currentTeacher.getSubjects();
		if (currentTeacher.getLocation() != null) shareText += " (" + currentTeacher.getLocation() + ")";
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
		startActivity(Intent.createChooser(shareIntent, "Share Teacher Profile"));
	}

	private void reportIssue() {
		if (!isAdded()) return;
		Toast.makeText(requireContext(), "Report feature coming soon!", Toast.LENGTH_SHORT).show();
	}

	private void viewFullProfile() {
		if (!isAdded()) return;
		Toast.makeText(requireContext(), "Full profile view coming soon!", Toast.LENGTH_SHORT).show();
	}

	private void testProfileImage() {
		if (!isAdded()) return;
		Toast.makeText(requireContext(), "Testing profile image loading...", Toast.LENGTH_SHORT).show();
		loadSampleProfileImage();
	}

	private void loadSampleProfileImage() {
		if (!isAdded()) return;
		String sampleImageUrl = "https://via.placeholder.com/300x300/667eea/ffffff?text=Teacher";
		Glide.with(this)
			.load(sampleImageUrl)
			.placeholder(R.drawable.ic_teacher_placeholder)
			.error(R.drawable.ic_teacher_placeholder)
			.timeout(10000)
			.circleCrop()
			.into(profilePhoto);
	}

	private void loadTeacherData() {
		// Determine teacher id from arguments or current user
		Bundle args = getArguments();
		teacherId = args != null ? args.getString("teacher_id") : null;
		if (teacherId == null) {
			FirebaseUser currentUser = authService.getCurrentUser();
			if (currentUser != null) {
				teacherId = currentUser.getUid();
			} else {
				Toast.makeText(requireContext(), "No teacher data available", Toast.LENGTH_LONG).show();
				return;
			}
		}

		// Show lightweight loading placeholder
		if (profilePhoto != null) profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);

		databaseService.getTeacher(teacherId, new FirebaseDatabaseService.DatabaseCallback<Teacher>() {
			@Override
			public void onSuccess(Teacher teacher) {
				currentTeacher = teacher;
				if (!isAdded()) return;
				requireActivity().runOnUiThread(() -> updateUIWithTeacherData(teacher));
			}

			@Override
			public void onFailure(String error) {
				Log.e(TAG, "Failed to load teacher data: " + error);
				if (!isAdded()) return;
				requireActivity().runOnUiThread(() -> {
					Toast.makeText(requireContext(), "Failed to load teacher data: " + error, Toast.LENGTH_LONG).show();
					showDefaultData();
				});
			}
		});
	}

	private void setupAnimations() {
		if (mainContent == null || profilePhotoCard == null) return;
		mainContent.setAlpha(0f);
		profilePhotoCard.setScaleX(0.8f);
		profilePhotoCard.setScaleY(0.8f);
		AnimationUtils.scaleInWithOvershoot(profilePhotoCard, 800, new Animation.AnimationListener() {
			@Override public void onAnimationStart(Animation animation) {}
			@Override public void onAnimationEnd(Animation animation) { AnimationUtils.fadeIn(mainContent, 600, null); }
			@Override public void onAnimationRepeat(Animation animation) {}
		});
	}

	private void updateUIWithTeacherData(Teacher teacher) {
		if (teacher == null) { showDefaultData(); return; }

		setTextSafely(teacherNameText, nn(teacher.getFullName(), "Teacher Name"));
		setTextSafely(ratingText, nn(teacher.getRating(), "4.5"));

		String experienceLabel;
		if (teacher.getYearsOfExperience() > 0) experienceLabel = teacher.getYearsOfExperience() + " years exp.";
		else if (teacher.getExperience() != null && teacher.getExperience().matches("\\d+")) experienceLabel = teacher.getExperience() + " years exp.";
		else experienceLabel = nn(teacher.getExperience(), "5 years exp.");
		setTextSafely(experienceText, experienceLabel);

		setTextSafely(locationText, nn(teacher.getLocation(), nn(teacher.getAddress(), "Location")));
		setTextSafely(ageText, teacher.getAge() > 0 ? teacher.getAge() + " years" : "Age not specified");
		setTextSafely(genderText, nn(teacher.getGender(), "Not specified"));
		setTextSafely(emailText, nn(teacher.getEmail(), "Email not available"));
		setTextSafely(addressText, nn(teacher.getAddress(), "Address not available"));

		if (teacher.getHighestQualification() != null && !teacher.getHighestQualification().isEmpty()) {
			setTextSafely(qualificationText, teacher.getHighestQualification());
		} else {
			setTextSafely(qualificationText, nn(teacher.getQualification(), "Qualification not specified"));
		}
		setTextSafely(institutionText, nn(teacher.getInstitution(), "Institution not specified"));

		updateSubjectsAndStreams(teacher);

		String imageUrl = teacher.getProfileImageUrl();
		if (imageUrl != null && !imageUrl.isEmpty()) displayProfileImage(imageUrl);
		else if (profilePhoto != null) profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
	}

	private void showDefaultData() {
		setTextSafely(teacherNameText, "Teacher Name");
		setTextSafely(ratingText, "4.5");
		setTextSafely(experienceText, "5 years exp.");
		setTextSafely(locationText, "Location");
		setTextSafely(ageText, "Age not specified");
		setTextSafely(genderText, "Not specified");
		setTextSafely(emailText, "Email not available");
		setTextSafely(addressText, "Address not available");
		setTextSafely(qualificationText, "Qualification not specified");
		setTextSafely(institutionText, "Institution not specified");
		updateSubjectsAndStreams(null);
		if (mainContent != null) AnimationUtils.fadeIn(mainContent, 500, null);
	}

	private void updateSubjectsAndStreams(Teacher teacher) {
		if (subjectsContainer == null || streamsContainer == null) return;
		subjectsContainer.removeAllViews();
		streamsContainer.removeAllViews();

		List<String> subjects = new ArrayList<>();
		if (teacher != null) {
			if (teacher.getSubjectsTaught() != null) subjects.addAll(teacher.getSubjectsTaught());
			if (teacher.getSubjects() != null) {
				String[] parts = teacher.getSubjects().split(",");
				for (String p : parts) {
					String s = p.trim();
					if (!s.isEmpty() && !subjects.contains(s)) subjects.add(s);
				}
			}
		}
		if (subjects.isEmpty()) {
			subjects.add("Mathematics");
			subjects.add("Physics");
			subjects.add("Chemistry");
		}
		for (String s : subjects) {
			TextView chip = new TextView(requireContext());
			chip.setText(s);
			chip.setBackgroundResource(R.drawable.subject_chip_background);
			chip.setTextColor(getResources().getColor(R.color.black));
			chip.setTextSize(12);
			chip.setPadding(32, 16, 32, 16);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 4, 8, 4);
			chip.setLayoutParams(lp);
			subjectsContainer.addView(chip);
		}

		List<String> streams = new ArrayList<>();
		if (teacher != null && teacher.getTeachingStreams() != null) {
			streams.addAll(teacher.getTeachingStreams());
		}
		if (streams.isEmpty()) {
			streams.add("10th Class");
			streams.add("12th Class");
			streams.add("JEE");
			streams.add("NEET");
		}
		for (String s : streams) {
			TextView chip = new TextView(requireContext());
			chip.setText(s);
			chip.setBackgroundResource(R.drawable.stream_chip_background);
			chip.setTextColor(getResources().getColor(R.color.white));
			chip.setTextSize(12);
			chip.setPadding(32, 16, 32, 16);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 4, 8, 4);
			chip.setLayoutParams(lp);
			streamsContainer.addView(chip);
		}
	}

	private void displayProfileImage(String profileImageData) {
		try {
			if (profileImageData.startsWith("data:image") || profileImageData.length() > 100) {
				String base64 = profileImageData.startsWith("data:image") ? profileImageData.substring(profileImageData.indexOf(',') + 1) : profileImageData;
				byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
				android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				profilePhoto.setImageBitmap(bmp);
			} else {
				Glide.with(this)
					.load(profileImageData)
					.placeholder(R.drawable.ic_teacher_placeholder)
					.error(R.drawable.ic_teacher_placeholder)
					.timeout(10000)
					.circleCrop()
					.into(profilePhoto);
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to decode profile image: " + e.getMessage());
			profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder);
		}
	}

	private void setTextSafely(TextView view, String text) { if (view != null) view.setText(text); }
	private String nn(String v, String def) { return v != null && !v.isEmpty() ? v : def; }

	@Override public void onAuthSuccess(FirebaseUser user) {}
	@Override public void onAuthFailure(String error) {}
	@Override public void onAuthStateChanged(FirebaseUser user) {
		if (user == null && isAdded()) {
			Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
		}
	}
}


