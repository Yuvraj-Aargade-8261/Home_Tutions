package com.example.hometutions.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometutions.R;
import com.example.hometutions.StudentLogin;
import com.example.hometutions.adapters.SubjectChipAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentProfileFragment extends Fragment {

    private static final String TAG = "StudentProfileFragment";
    
    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    
    // UI Elements
    private ImageView profilePhoto, editProfileButton, settingsButton;
    private TextView studentNameText, studentClassText, ageText, locationText;
    private TextView currentClassText, boardText, schoolNameText;
    private TextView teacherGenderPrefText, timeSlotPrefText;
    private TextView budgetRangeText, activeRequestsText;
    private TextView emailText, phoneText, parentContactText;
    private TextView additionalRequirementsText;
    private LinearLayout searchTeachersButton, myRequestsButton;
    private LinearLayout categoriesContainer;
    private RecyclerView subjectsRecyclerView;
    private SubjectChipAdapter subjectChipAdapter;
    private List<String> subjectsList;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        subjectsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_student_profile, container, false);
        return rootView;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadStudentProfile();
    }
    
    private void initializeViews() {
        // Profile photo and buttons
        profilePhoto = rootView.findViewById(R.id.profilePhoto);
        editProfileButton = rootView.findViewById(R.id.editProfileButton);
        settingsButton = rootView.findViewById(R.id.settingsButton);
        
        // Basic info
        studentNameText = rootView.findViewById(R.id.studentNameText);
        studentClassText = rootView.findViewById(R.id.studentClassText);
        ageText = rootView.findViewById(R.id.ageText);
        locationText = rootView.findViewById(R.id.locationText);
        
        // Academic info
        currentClassText = rootView.findViewById(R.id.currentClassText);
        boardText = rootView.findViewById(R.id.boardText);
        schoolNameText = rootView.findViewById(R.id.schoolNameText);
        
        // Preferences
        teacherGenderPrefText = rootView.findViewById(R.id.teacherGenderPrefText);
        timeSlotPrefText = rootView.findViewById(R.id.timeSlotPrefText);
        
        // Stats
        budgetRangeText = rootView.findViewById(R.id.budgetRangeText);
        activeRequestsText = rootView.findViewById(R.id.activeRequestsText);
        
        // Contact info
        emailText = rootView.findViewById(R.id.emailText);
        phoneText = rootView.findViewById(R.id.phoneText);
        parentContactText = rootView.findViewById(R.id.parentContactText);
        
        // Additional info
        additionalRequirementsText = rootView.findViewById(R.id.additionalRequirementsText);
        
        // Action buttons
        searchTeachersButton = rootView.findViewById(R.id.searchTeachersButton);
        myRequestsButton = rootView.findViewById(R.id.myRequestsButton);
        categoriesContainer = rootView.findViewById(R.id.categoriesContainer);
        
        // RecyclerView
        subjectsRecyclerView = rootView.findViewById(R.id.subjectsRecyclerView);
    }
    
    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        settingsButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        searchTeachersButton.setOnClickListener(v -> {
            // Navigate to search teachers (could be another fragment or activity)
            Toast.makeText(requireContext(), "Search Teachers - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        myRequestsButton.setOnClickListener(v -> {
            // Navigate to my requests
            Toast.makeText(requireContext(), "My Requests - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        // Contact click listeners
        emailText.setOnClickListener(v -> {
            // Copy email to clipboard
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Email", emailText.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Email copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        
        phoneText.setOnClickListener(v -> {
            // Make phone call
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + phoneText.getText().toString()));
            startActivity(intent);
        });
        
        parentContactText.setOnClickListener(v -> {
            // Make phone call to parent
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + parentContactText.getText().toString()));
            startActivity(intent);
        });
    }
    
    private void setupRecyclerView() {
        subjectChipAdapter = new SubjectChipAdapter(subjectsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        subjectsRecyclerView.setLayoutManager(layoutManager);
        subjectsRecyclerView.setAdapter(subjectChipAdapter);
    }
    
    private void loadStudentProfile() {
        if (currentUser == null) {
            Log.w(TAG, "No current user found");
            return;
        }
        
        // Set basic info from Firebase Auth
        emailText.setText(currentUser.getEmail());
        
        final String uid = currentUser.getUid();
        databaseRef.child("students").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Load profile image
                    String profilePhotoUrl = getStringSafely(dataSnapshot, "profilePhotoUrl");
                    if (profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
                        displayProfileImage(profilePhotoUrl);
                    } else {
                        profilePhoto.setImageResource(R.drawable.ic_student_placeholder);
                    }

                    // Load basic information
                    String fullName = getStringSafely(dataSnapshot, "fullName");
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        studentNameText.setText(fullName);
                    } else {
                        studentNameText.setText("Student Name");
                    }

                                        // Load age
                    String age = getStringSafely(dataSnapshot, "age");
                    if (age != null && !age.trim().isEmpty()) {
                        ageText.setText(age + " years");
                    } else {
                        ageText.setText("Age not set");
                    }
                    
                    // Load location/address
                    String location = getStringSafely(dataSnapshot, "address");
                    if (location != null && !location.trim().isEmpty()) {
                        locationText.setText(location);
                    } else {
                        locationText.setText("Location not set");
                    }

                    // Load academic information
                    String currentClass = getStringSafely(dataSnapshot, "currentClass");
                    if (currentClass != null && !currentClass.trim().isEmpty()) {
                        currentClassText.setText(currentClass);
                        studentClassText.setText(currentClass + " • CBSE Board");
                    } else {
                        currentClassText.setText("Class not set");
                        studentClassText.setText("Class not set • CBSE Board");
                    }

                    String board = getStringSafely(dataSnapshot, "board");
                    if (board != null && !board.trim().isEmpty()) {
                        boardText.setText(board);
                    } else {
                        boardText.setText("CBSE");
                    }

                    String schoolName = getStringSafely(dataSnapshot, "schoolName");
                    if (schoolName != null && !schoolName.trim().isEmpty()) {
                        schoolNameText.setText(schoolName);
                    } else {
                        schoolNameText.setText("School not set");
                    }

                                        // Load contact information
                    String phone = getStringSafely(dataSnapshot, "phoneNumber");
                    if (phone != null && !phone.trim().isEmpty()) {
                        phoneText.setText(phone);
                    } else {
                        phoneText.setText("Phone not set");
                    }
                    
                    String parentContact = getStringSafely(dataSnapshot, "parentContact");
                    if (parentContact != null && !parentContact.trim().isEmpty()) {
                        parentContactText.setText(parentContact);
                    } else {
                        parentContactText.setText("Parent contact not set");
                    }

                                        // Load preferences
                    String teacherGenderPref = getStringSafely(dataSnapshot, "preferredTeacherGender");
                    if (teacherGenderPref != null && !teacherGenderPref.trim().isEmpty()) {
                        teacherGenderPrefText.setText(teacherGenderPref);
                    } else {
                        teacherGenderPrefText.setText("No preference");
                    }
                    
                    String timeSlotPref = getStringSafely(dataSnapshot, "preferredTimeSlot");
                    if (timeSlotPref != null && !timeSlotPref.trim().isEmpty()) {
                        timeSlotPrefText.setText(timeSlotPref);
                    } else {
                        timeSlotPrefText.setText("Flexible");
                    }
                    
                    // Load budget range
                    String minBudget = getStringSafely(dataSnapshot, "minBudget");
                    String maxBudget = getStringSafely(dataSnapshot, "maxBudget");
                    if (minBudget != null && maxBudget != null && !minBudget.trim().isEmpty() && !maxBudget.trim().isEmpty()) {
                        budgetRangeText.setText("₹" + minBudget + " - ₹" + maxBudget);
                    } else {
                        budgetRangeText.setText("₹2000 - ₹5000");
                    }
                    
                    // Load active requests count (not in DB, set default)
                    activeRequestsText.setText("0");

                    // Load subjects
                    loadSubjects(dataSnapshot);

                    // Load additional requirements
                    String additionalRequirements = getStringSafely(dataSnapshot, "additionalRequirements");
                    if (additionalRequirements != null && !additionalRequirements.trim().isEmpty()) {
                        additionalRequirementsText.setText(additionalRequirements);
                    } else {
                        additionalRequirementsText.setText("No additional requirements specified.");
                    }
                    
                } else {
                    Log.w(TAG, "Student data not found for UID. Trying userId, then email.");
                    databaseRef.child("students").orderByChild("userId").equalTo(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot byUserId) {
                                    if (byUserId.exists()) {
                                        for (DataSnapshot child : byUserId.getChildren()) { onDataChange(child); return; }
                                    } else {
                                        String email = currentUser.getEmail();
                                        if (email == null) { setDefaultProfileData(); return; }
                                        databaseRef.child("students").orderByChild("email").equalTo(email)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot byEmail) {
                                                        if (byEmail.exists()) {
                                                            for (DataSnapshot child : byEmail.getChildren()) { onDataChange(child); return; }
                                                        } else {
                                                            // Final fallback: scan all children and match manually
                                                            databaseRef.child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot all) {
                                                                    for (DataSnapshot child : all.getChildren()) {
                                                                        String key = child.getKey();
                                                                        String childUserId = String.valueOf(child.child("userId").getValue());
                                                                        String childEmail = String.valueOf(child.child("email").getValue());
                                                                        if ((key != null && key.equals(uid)) ||
                                                                            (childUserId != null && childUserId.equals(uid)) ||
                                                                            (childEmail != null && childEmail.equals(email))) {
                                                                            onDataChange(child);
                                                                            return;
                                                                        }
                                                                    }
                                                                    setDefaultProfileData();
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) { setDefaultProfileData(); }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        setDefaultProfileData();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { setDefaultProfileData(); }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching student data: " + databaseError.getMessage());
                setDefaultProfileData();
            }
        });
    }

    private String getStringSafely(DataSnapshot parent, String key) {
        try {
            Object value = parent.child(key).getValue();
            if (value == null) {
                return null;
            }
            return String.valueOf(value);
        } catch (Exception e) {
            Log.w(TAG, "Failed to read key '" + key + "' as String: " + e.getMessage());
            return null;
        }
    }
    
    private void loadSubjects(DataSnapshot dataSnapshot) {
        subjectsList.clear();
        
        // Try to get subjects from subjectsNeeded field (as per database structure)
        DataSnapshot subjectsSnapshot = dataSnapshot.child("subjectsNeeded");
        if (subjectsSnapshot.exists()) {
            for (DataSnapshot subjectSnapshot : subjectsSnapshot.getChildren()) {
                String subject = subjectSnapshot.getValue(String.class);
                if (subject != null && !subject.trim().isEmpty()) {
                    subjectsList.add(subject);
                }
            }
        } else {
            // Try getting from subjects field
            DataSnapshot subjectsAltSnapshot = dataSnapshot.child("subjects");
            if (subjectsAltSnapshot.exists()) {
                for (DataSnapshot subjectSnapshot : subjectsAltSnapshot.getChildren()) {
                    String subject = subjectSnapshot.getValue(String.class);
                    if (subject != null && !subject.trim().isEmpty()) {
                        subjectsList.add(subject);
                    }
                }
            } else {
                // Try getting from subjectsTaught field
                String subjectsTaught = getStringSafely(dataSnapshot, "subjectsTaught");
                if (subjectsTaught != null && !subjectsTaught.trim().isEmpty()) {
                    String[] subjects = subjectsTaught.split(",");
                    for (String subject : subjects) {
                        if (!subject.trim().isEmpty()) {
                            subjectsList.add(subject.trim());
                        }
                    }
                } else {
                    // Default subjects
                    subjectsList.add("Mathematics");
                    subjectsList.add("Physics");
                    subjectsList.add("Chemistry");
                }
            }
        }
        
        subjectChipAdapter.notifyDataSetChanged();
    }
    
    private void displayProfileImage(String profileImageData) {
        try {
            // Check if it's a Base64 string (starts with data:image or is a long Base64 string)
            if (profileImageData.startsWith("data:image") || profileImageData.length() > 100) {
                // Handle Base64 image data
                if (profileImageData.startsWith("data:image")) {
                    // Extract Base64 part from data URL
                    String base64Data = profileImageData.substring(profileImageData.indexOf(",") + 1);
                    byte[] imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePhoto.setImageBitmap(bitmap);
                } else {
                    // Direct Base64 string
                    byte[] imageBytes = android.util.Base64.decode(profileImageData, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePhoto.setImageBitmap(bitmap);
                }
                Log.d(TAG, "Profile image displayed successfully from Base64 data");
            } else {
                // Regular URL, set default for now
                profilePhoto.setImageResource(R.drawable.ic_student_placeholder);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode profile image: " + e.getMessage());
            profilePhoto.setImageResource(R.drawable.ic_student_placeholder);
        }
    }
    
    private void setDefaultProfileData() {
        profilePhoto.setImageResource(R.drawable.ic_student_placeholder);
        studentNameText.setText("Student Name");
        studentClassText.setText("Class not set • State Board");
        ageText.setText("Age not set");
        locationText.setText("Location not set");
        currentClassText.setText("Class not set");
        boardText.setText("State Board");
        schoolNameText.setText("School not set");
        teacherGenderPrefText.setText("No preference");
        timeSlotPrefText.setText("Flexible");
        budgetRangeText.setText("₹2000 - ₹5000");
        activeRequestsText.setText("0");
        phoneText.setText("Phone not set");
        parentContactText.setText("Parent contact not set");
        additionalRequirementsText.setText("No additional requirements specified.");
        
        // Set default subjects
        subjectsList.clear();
        subjectsList.add("Mathematics");
        subjectsList.add("Physics");
        subjectsList.add("Chemistry");
        subjectChipAdapter.notifyDataSetChanged();
    }
}
