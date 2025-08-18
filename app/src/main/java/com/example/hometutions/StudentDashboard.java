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
    private RecyclerView recommendedTeachersRecyclerView;
    private TeacherAdapter teacherAdapter;
    private List<Teacher> teachersList;

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
        
        DatabaseReference studentRef = databaseRef.child("students").child(currentUser.getUid());
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch student name
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        studentNameText.setText(fullName);
                    } else {
                        // Fallback to email extraction if no name in database
                        String fallbackName = getStudentNameFromEmail();
                        studentNameText.setText(fallbackName);
                    }
                    
                    // Fetch and display profile image
                    String profilePhotoUrl = dataSnapshot.child("profilePhotoUrl").getValue(String.class);
                    if (profilePhotoUrl != null && !profilePhotoUrl.trim().isEmpty()) {
                        displayProfileImage(profilePhotoUrl);
                    } else {
                        // Set default student icon if no profile image
                        profilePhoto.setImageResource(R.drawable.ic_student_white);
                    }
                } else {
                    // Fallback to email extraction if no data in database
                    String fallbackName = getStudentNameFromEmail();
                    studentNameText.setText(fallbackName);
                    profilePhoto.setImageResource(R.drawable.ic_student_white);
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
                // Regular URL, try to load with Glide
                // For now, set default icon since we're not using Glide for student profile
                profilePhoto.setImageResource(R.drawable.ic_student_white);
                Log.d(TAG, "Profile image is URL, using default icon");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode profile image: " + e.getMessage());
            // Set default student icon on error
            profilePhoto.setImageResource(R.drawable.ic_student_white);
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
                teachersList.clear();
                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = teacherSnapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teacher.setId(teacherSnapshot.getKey());
                        teachersList.add(teacher);
                        
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
                
                // Always notify adapter after loading data
                teacherAdapter.notifyDataSetChanged();
                Log.d(TAG, "Loaded " + teachersList.size() + " teachers from database");
                
                // If no teachers loaded from database, load sample data
                if (teachersList.isEmpty()) {
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
        
        teachersList.add(teacher1);
        teachersList.add(teacher2);
        teachersList.add(teacher3);
        
        teacherAdapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded " + teachersList.size() + " sample teachers");
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
        rootView.findViewById(R.id.searchButton).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Search functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // View all buttons
        rootView.findViewById(R.id.viewAllRecommended).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "View all recommended teachers", Toast.LENGTH_SHORT).show();
        });

        
        // Notification button
        rootView.findViewById(R.id.notificationButton).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Notifications coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onTeacherClick(Teacher teacher) {
        Toast.makeText(requireContext(), "Selected: " + teacher.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to teacher detail page
    }
}
