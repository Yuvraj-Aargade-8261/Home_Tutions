package com.example.hometutions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometutions.adapters.TeacherAdapter;
import com.example.hometutions.models.Teacher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends Fragment implements TeacherAdapter.OnTeacherClickListener {

    private static final String TAG = "StudentDashboard";
    
    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    
    // UI Elements
    private TextView studentNameText;
    private ImageView profilePhoto;
    private Spinner tuitionStreamSpinner;
    private Spinner subjectFilterSpinner;
    private Spinner distanceRangeSpinner;
    private TextInputEditText locationEditText;
    private RecyclerView recommendedTeachersRecyclerView;
    private TeacherAdapter teacherAdapter;
    private List<Teacher> teachersList;
    private List<Teacher> allTeachersList; // master list for filtering

    public StudentDashboard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        teachersList = new ArrayList<>();
        allTeachersList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_student_dashboard, container, false);
        return rootView;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews();
        setupStudentName();
        setupSpinners();
        setupRecyclerView();
        setupClickListeners();
        loadRecommendedTeachers();
    }
    
    private void initializeViews() {
        studentNameText = rootView.findViewById(R.id.studentNameText);
        profilePhoto = rootView.findViewById(R.id.profilePhoto);
        tuitionStreamSpinner = rootView.findViewById(R.id.tuitionStreamSpinner);
        subjectFilterSpinner = rootView.findViewById(R.id.subjectFilterSpinner);
        distanceRangeSpinner = rootView.findViewById(R.id.distanceRangeSpinner);
        locationEditText = rootView.findViewById(R.id.locationEditText);
        recommendedTeachersRecyclerView = rootView.findViewById(R.id.recommendedTeachersRecyclerView);
    }
    
    private void setupStudentName() {
        if (currentUser != null) {
            // First try to get name from database
            fetchStudentNameFromDatabase();
        }
    }
    
    private void fetchStudentNameFromDatabase() {
        if (currentUser == null) return;
        
        final String uid = currentUser.getUid();
        DatabaseReference studentRef = databaseRef.child("students").child(uid);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch student name (handle non-string types safely)
                    String fullName = getStringSafely(dataSnapshot, "fullName");
                    if (fullName == null || fullName.trim().isEmpty()) {
                        fullName = getStudentNameFromEmail();
                    }
                    studentNameText.setText(fullName);

                    // Fetch and display profile image (Base64 or URL)
                    String profilePhotoUrl = getStringSafely(dataSnapshot, "profilePhotoUrl");
                    if (profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
                        displayProfileImage(profilePhotoUrl);
                    } else {
                        profilePhoto.setImageResource(R.drawable.ic_student_white);
                    }
                } else {
                    // Fallback 1: search by userId field equal to auth UID
                    databaseRef.child("students").orderByChild("userId").equalTo(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot byUserId) {
                                if (byUserId.exists()) {
                                    for (DataSnapshot child : byUserId.getChildren()) {
                                        // Reuse same handling
                                        onDataChange(child);
                                        return;
                                    }
                                } else {
                                    // Fallback 2: search by email field
                                    String email = currentUser.getEmail();
                                    databaseRef.child("students").orderByChild("email").equalTo(email)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot byEmail) {
                                                    if (byEmail.exists()) {
                                                        for (DataSnapshot child : byEmail.getChildren()) {
                                                            onDataChange(child);
                                                            return;
                                                        }
                                                    } else {
                                                        // Final fallback: scan all and match uid/email/userId
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
                                                                String fallbackName = getStudentNameFromEmail();
                                                                studentNameText.setText(fallbackName);
                                                                profilePhoto.setImageResource(R.drawable.ic_student_white);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                String fallbackName = getStudentNameFromEmail();
                                                                studentNameText.setText(fallbackName);
                                                                profilePhoto.setImageResource(R.drawable.ic_student_white);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    String fallbackName = getStudentNameFromEmail();
                                                    studentNameText.setText(fallbackName);
                                                    profilePhoto.setImageResource(R.drawable.ic_student_white);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                String fallbackName = getStudentNameFromEmail();
                                studentNameText.setText(fallbackName);
                                profilePhoto.setImageResource(R.drawable.ic_student_white);
                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching student data: " + databaseError.getMessage());
                // Fallback to email extraction
                String fallbackName = getStudentNameFromEmail();
                studentNameText.setText(fallbackName);
                profilePhoto.setImageResource(R.drawable.ic_student_white);
            }
        });
    }
    
    private String getStudentNameFromEmail() {
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.trim().isEmpty()) {
                return displayName;
            } else {
                // Extract name from email if display name is not set
                String email = currentUser.getEmail();
                if (email != null && email.contains("@")) {
                    String nameFromEmail = email.substring(0, email.indexOf("@"));
                    // Remove numbers and capitalize first letter
                    nameFromEmail = nameFromEmail.replaceAll("[0-9]", "");
                    if (!nameFromEmail.isEmpty()) {
                        return nameFromEmail.substring(0, 1).toUpperCase() + nameFromEmail.substring(1);
                    }
                }
            }
        }
        return "Student";
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
                // Regular URL, load with Glide
                try {
                    com.bumptech.glide.Glide.with(this)
                            .load(profileImageData)
                            .placeholder(R.drawable.ic_student_white)
                            .error(R.drawable.ic_student_white)
                            .circleCrop()
                            .into(profilePhoto);
                } catch (Exception e) {
                    profilePhoto.setImageResource(R.drawable.ic_student_white);
                }
                Log.d(TAG, "Profile image loaded from URL");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode profile image: " + e.getMessage());
            // Set default student icon on error
            profilePhoto.setImageResource(R.drawable.ic_student_white);
        }
    }

    private String getStringSafely(DataSnapshot parent, String key) {
        try {
            Object value = parent.child(key).getValue();
            if (value == null) return null;
            return String.valueOf(value);
        } catch (Exception e) {
            Log.w(TAG, "Failed to read '" + key + "' as String: " + e.getMessage());
            return null;
        }
    }
    
    private void setupRecyclerView() {
        teacherAdapter = new TeacherAdapter(teachersList, this);
        
        // Use horizontal layout manager for horizontal scrolling
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recommendedTeachersRecyclerView.setLayoutManager(layoutManager);
        recommendedTeachersRecyclerView.setAdapter(teacherAdapter);
        
        // Set fixed height for the RecyclerView to prevent layout issues
        recommendedTeachersRecyclerView.setHasFixedSize(true);
        
        Log.d(TAG, "RecyclerView setup completed");
    }
    
    private void loadRecommendedTeachers() {
        DatabaseReference teachersRef = databaseRef.child("teachers");
        teachersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTeachersList.clear();
                teachersList.clear();
                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = teacherSnapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teacher.setId(teacherSnapshot.getKey());
                        allTeachersList.add(teacher);
                        
                        // Debug logging
                        Log.d(TAG, "Loaded teacher: " + teacher.getFullName());
                        Log.d(TAG, "  - Qualification: " + teacher.getHighestQualification());
                        Log.d(TAG, "  - Experience: " + teacher.getYearsOfExperience());
                        Log.d(TAG, "  - Address: " + teacher.getAddress());
                        Log.d(TAG, "  - Subjects: " + teacher.getSubjectsTaught());
                        Log.d(TAG, "  - Streams: " + teacher.getTeachingStreams());
                    } else {
                        Log.e(TAG, "Failed to parse teacher data for: " + teacherSnapshot.getKey());
                    }
                }
                
                // Apply current filters to fill visible list
                applyFilters();
                Log.d(TAG, "Loaded " + allTeachersList.size() + " teachers from database");
                
                // If no teachers loaded from database, load sample data
                if (allTeachersList.isEmpty()) {
                    Log.d(TAG, "No teachers found in database, loading sample data");
                    loadSampleTeachers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading teachers: " + databaseError.getMessage());
                // Load sample data if database fails
                loadSampleTeachers();
            }
        });
    }
    
    private void loadSampleTeachers() {
        teachersList.clear();
        allTeachersList.clear();
        
        // Create sample teachers with proper data structure
        Teacher teacher1 = new Teacher();
        teacher1.setId("1");
        teacher1.setFullName("Dr. Priya Sharma");
        teacher1.setEmail("priya@example.com");
        teacher1.setPhoneNumber("9876543210");
        teacher1.setHighestQualification("PhD");
        teacher1.setYearsOfExperience(8);
        teacher1.setAddress("Mumbai, Maharashtra");
        teacher1.setSubjectsTaught(java.util.Arrays.asList("Physics", "Chemistry"));
        teacher1.setTeachingStreams(java.util.Arrays.asList("JEE Preparation", "NEET Preparation"));
        teacher1.setRating("4.8");
        teacher1.setVerified(true);
        
        Teacher teacher2 = new Teacher();
        teacher2.setId("2");
        teacher2.setFullName("Prof. Rajesh Kumar");
        teacher2.setEmail("rajesh@example.com");
        teacher2.setPhoneNumber("9876543211");
        teacher2.setHighestQualification("M.Tech");
        teacher2.setYearsOfExperience(12);
        teacher2.setAddress("Delhi, NCR");
        teacher2.setSubjectsTaught(java.util.Arrays.asList("Mathematics", "Physics"));
        teacher2.setTeachingStreams(java.util.Arrays.asList("JEE Preparation", "12th Class"));
        teacher2.setRating("4.9");
        teacher2.setVerified(true);
        
        Teacher teacher3 = new Teacher();
        teacher3.setId("3");
        teacher3.setFullName("Ms. Anjali Patel");
        teacher3.setEmail("anjali@example.com");
        teacher3.setPhoneNumber("9876543212");
        teacher3.setHighestQualification("M.Sc");
        teacher3.setYearsOfExperience(5);
        teacher3.setAddress("Pune, Maharashtra");
        teacher3.setSubjectsTaught(java.util.Arrays.asList("Biology", "Chemistry"));
        teacher3.setTeachingStreams(java.util.Arrays.asList("NEET Preparation", "12th Class"));
        teacher3.setRating("4.7");
        teacher3.setVerified(false);
        
        allTeachersList.add(teacher1);
        allTeachersList.add(teacher2);
        allTeachersList.add(teacher3);
        applyFilters();
        Log.d(TAG, "Loaded " + allTeachersList.size() + " sample teachers");
    }
    
    private void setupSpinners() {
        // Setup tuition stream spinner
        String[] streams = {"All Streams", "NEET Preparation", "JEE Preparation", "12th Class", "10th Class", "Primary Education"};
        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, streams);
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tuitionStreamSpinner.setAdapter(streamAdapter);
        
        // Setup subject filter spinner
        String[] subjects = {"All Subjects", "Physics", "Chemistry", "Mathematics", "Biology", "English", "History", "Geography"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectFilterSpinner.setAdapter(subjectAdapter);
        
        // Setup distance range spinner
        String[] distances = {"Any Distance", "0-5 km", "5-10 km", "10-20 km", "20+ km"};
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, distances);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceRangeSpinner.setAdapter(distanceAdapter);
    }
    
    private void setupClickListeners() {
        // Search button click listener
        rootView.findViewById(R.id.searchButton).setOnClickListener(v -> performSearch());
        
        // View all buttons
        rootView.findViewById(R.id.viewAllRecommended).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "View all recommended teachers", Toast.LENGTH_SHORT).show();
        });

        
        // Notification button
        rootView.findViewById(R.id.notificationButton).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Notifications coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Auto-apply filters on spinner changes
        subjectFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        tuitionStreamSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void performSearch() {
        applyFilters();
    }

    private void applyFilters() {
        String selectedStream = tuitionStreamSpinner.getSelectedItem() != null ? tuitionStreamSpinner.getSelectedItem().toString() : "All Streams";
        String selectedSubject = subjectFilterSpinner.getSelectedItem() != null ? subjectFilterSpinner.getSelectedItem().toString() : "All Subjects";
        String locationQuery = locationEditText != null && locationEditText.getText() != null ? locationEditText.getText().toString().trim() : "";

        teachersList.clear();
        for (Teacher teacher : allTeachersList) {
            if (teacher == null) continue;

            boolean matchesStream = true;
            if (!"All Streams".equalsIgnoreCase(selectedStream)) {
                List<String> streams = teacher.getTeachingStreams();
                matchesStream = streams != null && containsIgnoreCase(streams, selectedStream);
            }

            boolean matchesSubject = true;
            if (!"All Subjects".equalsIgnoreCase(selectedSubject)) {
                List<String> subjects = teacher.getSubjectsTaught();
                String subjectsStr = teacher.getSubjects();
                matchesSubject = (subjects != null && containsIgnoreCase(subjects, selectedSubject))
                        || (subjectsStr != null && subjectsStr.toLowerCase().contains(selectedSubject.toLowerCase()));
            }

            boolean matchesLocation = true;
            if (!locationQuery.isEmpty()) {
                String address = teacher.getAddress();
                String location = teacher.getLocation();
                matchesLocation = (address != null && address.toLowerCase().contains(locationQuery.toLowerCase()))
                        || (location != null && location.toLowerCase().contains(locationQuery.toLowerCase()));
            }

            if (matchesStream && matchesSubject && matchesLocation) {
                teachersList.add(teacher);
            }
        }

        teacherAdapter.notifyDataSetChanged();
        Log.d(TAG, "Search filters applied. Results: " + teachersList.size());
        if (teachersList.isEmpty()) {
            Toast.makeText(requireContext(), "No teachers found. Try different filters.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean containsIgnoreCase(List<String> list, String query) {
        if (list == null || query == null) return false;
        for (String item : list) {
            if (item != null && item.equalsIgnoreCase(query)) return true;
        }
        return false;
    }
    
    @Override
    public void onTeacherClick(Teacher teacher) {
        Toast.makeText(requireContext(), "Selected: " + teacher.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to teacher detail page
    }
}
