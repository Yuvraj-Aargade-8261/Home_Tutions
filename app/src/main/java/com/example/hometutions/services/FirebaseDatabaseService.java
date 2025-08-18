package com.example.hometutions.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hometutions.models.Student;
import com.example.hometutions.models.Teacher;
import com.example.hometutions.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseService {
    private static final String TAG = "FirebaseDatabaseService";
    
    private DatabaseReference mDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference studentsRef;
    private DatabaseReference teachersRef;
    
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
    
    public FirebaseDatabaseService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersRef = mDatabase.child("users");
        studentsRef = mDatabase.child("students");
        teachersRef = mDatabase.child("teachers");
    }
    
    // User operations
    public void createUser(User user, DatabaseCallback<Void> callback) {
        String userId = user.getUserId();
        usersRef.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User created successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to create user", e);
                        callback.onFailure("Failed to create user: " + e.getMessage());
                    }
                });
    }
    
    public void getUser(String userId, DatabaseCallback<User> callback) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("User not found");
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to get user: " + databaseError.getMessage());
            }
        });
    }
    
    public void updateUser(String userId, Map<String, Object> updates, DatabaseCallback<Void> callback) {
        usersRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User updated successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update user", e);
                        callback.onFailure("Failed to update user: " + e.getMessage());
                    }
                });
    }
    
    // Student operations
    public void createStudent(Student student, DatabaseCallback<Void> callback) {
        String userId = student.getUserId();
        studentsRef.child(userId).setValue(student)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Student created successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to create student", e);
                        callback.onFailure("Failed to create student: " + e.getMessage());
                    }
                });
    }
    
    public void getStudent(String userId, DatabaseCallback<Student> callback) {
        studentsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                if (student != null) {
                    callback.onSuccess(student);
                } else {
                    callback.onFailure("Student not found");
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to get student: " + databaseError.getMessage());
            }
        });
    }
    
    public void updateStudent(String userId, Map<String, Object> updates, DatabaseCallback<Void> callback) {
        studentsRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Student updated successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update student", e);
                        callback.onFailure("Failed to update student: " + e.getMessage());
                    }
                });
    }
    
    // Teacher operations
    public void createTeacher(Teacher teacher, DatabaseCallback<Void> callback) {
        String userId = teacher.getUserId();
        teachersRef.child(userId).setValue(teacher)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Teacher created successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to create teacher", e);
                        callback.onFailure("Failed to create teacher: " + e.getMessage());
                    }
                });
    }
    
    public void getTeacher(String userId, DatabaseCallback<Teacher> callback) {
        teachersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                if (teacher != null) {
                    callback.onSuccess(teacher);
                } else {
                    callback.onFailure("Teacher not found");
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to get teacher: " + databaseError.getMessage());
            }
        });
    }
    
    public void updateTeacher(String userId, Map<String, Object> updates, DatabaseCallback<Void> callback) {
        teachersRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Teacher updated successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update teacher", e);
                        callback.onFailure("Failed to update teacher: " + e.getMessage());
                    }
                });
    }
    
    // Search operations
    public void searchTeachersBySubject(String subject, DatabaseCallback<List<Teacher>> callback) {
        teachersRef.orderByChild("subjectsTaught").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Teacher> teachers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null && teacher.getSubjectsTaught() != null && 
                        teacher.getSubjectsTaught().contains(subject)) {
                        teachers.add(teacher);
                    }
                }
                callback.onSuccess(teachers);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to search teachers: " + databaseError.getMessage());
            }
        });
    }
    
    public void searchTeachersByLocation(String location, DatabaseCallback<List<Teacher>> callback) {
        teachersRef.orderByChild("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Teacher> teachers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null && teacher.getAddress() != null && 
                        teacher.getAddress().toLowerCase().contains(location.toLowerCase())) {
                        teachers.add(teacher);
                    }
                }
                callback.onSuccess(teachers);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to search teachers: " + databaseError.getMessage());
            }
        });
    }
    
    // Delete operations
    public void deleteUser(String userId, DatabaseCallback<Void> callback) {
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User deleted successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete user", e);
                        callback.onFailure("Failed to delete user: " + e.getMessage());
                    }
                });
    }
    
    public void deleteStudent(String userId, DatabaseCallback<Void> callback) {
        studentsRef.child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Student deleted successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete student", e);
                        callback.onFailure("Failed to delete student: " + e.getMessage());
                    }
                });
    }
    
    public void deleteTeacher(String userId, DatabaseCallback<Void> callback) {
        teachersRef.child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Teacher deleted successfully");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete teacher", e);
                        callback.onFailure("Failed to delete teacher: " + e.getMessage());
                    }
                });
    }
}
