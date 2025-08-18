package com.example.hometutions.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometutions.R;
import com.example.hometutions.models.Teacher;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teachers;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onTeacherClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teachers, OnTeacherClickListener listener) {
        this.teachers = teachers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_horizontal, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.bind(teacher);
        Log.d("TeacherAdapter", "Binding teacher at position " + position + ": " + teacher.getFullName());
    }

    @Override
    public int getItemCount() {
        return teachers != null ? teachers.size() : 0;
    }

    public void updateTeachers(List<Teacher> newTeachers) {
        this.teachers = newTeachers;
        notifyDataSetChanged();
    }

            class TeacherViewHolder extends RecyclerView.ViewHolder {
            private TextView teacherName;
            private TextView teacherQualification;
            private TextView teacherExperience;
            private TextView teacherLocation;
            private TextView teachingStreams;
            private TextView ratingText;
            private RatingBar teacherRating;
            private ImageView favoriteButton;
            private ImageView teacherProfileImage;
            private LinearLayout viewProfileButton;
            private LinearLayout contactButton;
            private TextView subject1, subject2, moreSubjects;

                    public TeacherViewHolder(@NonNull View itemView) {
                super(itemView);
                teacherName = itemView.findViewById(R.id.teacherName);
                teacherQualification = itemView.findViewById(R.id.teacherQualification);
                teacherExperience = itemView.findViewById(R.id.teacherExperience);
                teacherLocation = itemView.findViewById(R.id.teacherLocation);
                teachingStreams = itemView.findViewById(R.id.teachingStreams);
                ratingText = itemView.findViewById(R.id.ratingText);
                teacherRating = itemView.findViewById(R.id.teacherRating);
                favoriteButton = itemView.findViewById(R.id.favoriteButton);
                teacherProfileImage = itemView.findViewById(R.id.teacherProfilePhoto);
                viewProfileButton = itemView.findViewById(R.id.viewProfileButton);
                contactButton = itemView.findViewById(R.id.contactButton);
                subject1 = itemView.findViewById(R.id.subject1);
                subject2 = itemView.findViewById(R.id.subject2);
                moreSubjects = itemView.findViewById(R.id.moreSubjects);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeacherClick(teachers.get(position));
                }
            });

            viewProfileButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeacherClick(teachers.get(position));
                }
            });

            contactButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeacherClick(teachers.get(position));
                }
            });
        }

        public void bind(Teacher teacher) {
            // Add null checks for all views to prevent crashes
            if (teacherName == null || teacherQualification == null || teacherExperience == null || 
                teacherLocation == null || teachingStreams == null || ratingText == null || 
                teacherRating == null || favoriteButton == null || teacherProfileImage == null || 
                viewProfileButton == null || contactButton == null || subject1 == null || 
                subject2 == null || moreSubjects == null) {
                Log.e("TeacherAdapter", "Some views are null, skipping binding");
                return;
            }
            
            // Handle null values safely
            String teacherNameStr = teacher.getFullName();
            teacherName.setText(teacherNameStr != null ? teacherNameStr : "Teacher Name");
            
            // Set profile image
            String profileImageData = teacher.getProfileImageUrl();
            if (profileImageData != null && !profileImageData.isEmpty() && !profileImageData.startsWith("temp_")) {
                // Check if it's a Base64 string (starts with data:image or is a long Base64 string)
                if (profileImageData.startsWith("data:image") || profileImageData.length() > 100) {
                    try {
                        // Handle Base64 image data
                        if (profileImageData.startsWith("data:image")) {
                            // Extract Base64 part from data URL
                            String base64Data = profileImageData.substring(profileImageData.indexOf(",") + 1);
                            byte[] imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            teacherProfileImage.setImageBitmap(bitmap);
                        } else {
                            // Direct Base64 string
                            byte[] imageBytes = android.util.Base64.decode(profileImageData, android.util.Base64.DEFAULT);
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            teacherProfileImage.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        Log.e("TeacherAdapter", "Failed to decode Base64 image: " + e.getMessage());
                        teacherProfileImage.setImageResource(R.drawable.ic_teacher);
                    }
                } else {
                    // Regular URL, try to load with Glide
                    Glide.with(teacherProfileImage.getContext())
                        .load(profileImageData)
                        .placeholder(R.drawable.ic_teacher)
                        .error(R.drawable.ic_teacher)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .circleCrop()
                        .into(teacherProfileImage);
                }
            } else {
                // Set default teacher icon
                teacherProfileImage.setImageResource(R.drawable.ic_teacher);
            }
            
            // Set qualification - use highestQualification from database
            String qualification = teacher.getHighestQualification();
            if (qualification == null || qualification.isEmpty()) {
                qualification = teacher.getQualification(); // fallback
            }
            teacherQualification.setText(qualification != null ? qualification : "Qualification not specified");
            
            // Set experience - use yearsOfExperience from database
            String experience = String.valueOf(teacher.getYearsOfExperience());
            if (experience.equals("0")) {
                experience = teacher.getExperience(); // fallback
            }
            teacherExperience.setText(experience + "+ Years Experience");
            
            // Set location - use address from database
            String location = teacher.getAddress();
            if (location == null || location.isEmpty()) {
                location = teacher.getLocation(); // fallback
            }
            teacherLocation.setText(location != null ? location : "Location not specified");
            
            // Set teaching streams - use teachingStreams from database
            String streams = "";
            if (teacher.getTeachingStreams() != null && !teacher.getTeachingStreams().isEmpty()) {
                streams = String.join(" • ", teacher.getTeachingStreams());
            }
            if (streams.isEmpty()) {
                streams = "Teaching streams not specified";
            }
            teachingStreams.setText(streams);
            
            // Set rating with null safety
            try {
                String ratingStr = teacher.getRating();
                if (ratingStr != null && !ratingStr.trim().isEmpty()) {
                    float rating = Float.parseFloat(ratingStr);
                    teacherRating.setRating(rating);
                    ratingText.setText(ratingStr);
                } else {
                    teacherRating.setRating(4.5f); // Default rating
                    ratingText.setText("4.5");
                }
            } catch (NumberFormatException e) {
                teacherRating.setRating(4.5f); // Default rating on error
                ratingText.setText("4.5");
            }

            // Note: Verification badge not available in horizontal layout
            // Teacher verification status can be shown in other ways if needed
            
            // Set subjects in chips - use subjectsTaught from database
            String subjects = "";
            if (teacher.getSubjectsTaught() != null && !teacher.getSubjectsTaught().isEmpty()) {
                subjects = String.join(", ", teacher.getSubjectsTaught());
            }
            if (subjects.isEmpty()) {
                subjects = teacher.getSubjects(); // fallback
            }
            setupSubjectChips(subjects);
        }
        
        private void setupSubjectChips(String subjects) {
            // Add null checks for subject views
            if (subject1 == null || subject2 == null || moreSubjects == null) {
                Log.e("TeacherAdapter", "Subject views are null, skipping subject chip setup");
                return;
            }
            
            if (subjects != null && !subjects.isEmpty()) {
                String[] subjectArray = subjects.split(",");
                
                // Show first subject
                if (subjectArray.length > 0) {
                    subject1.setText(subjectArray[0].trim());
                    subject1.setVisibility(View.VISIBLE);
                } else {
                    subject1.setVisibility(View.GONE);
                }
                
                // Show second subject
                if (subjectArray.length > 1) {
                    subject2.setText(subjectArray[1].trim());
                    subject2.setVisibility(View.VISIBLE);
                } else {
                    subject2.setVisibility(View.GONE);
                }
                
                // Show more subjects indicator
                if (subjectArray.length > 2) {
                    moreSubjects.setText("+" + (subjectArray.length - 2));
                    moreSubjects.setVisibility(View.VISIBLE);
                } else {
                    moreSubjects.setVisibility(View.GONE);
                }
            } else {
                // Hide all subject chips if no subjects
                subject1.setVisibility(View.GONE);
                subject2.setVisibility(View.GONE);
                moreSubjects.setVisibility(View.GONE);
            }
        }
    }
}
