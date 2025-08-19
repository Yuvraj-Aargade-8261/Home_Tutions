package com.example.hometutions;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hometutions.models.Teacher;
import com.example.hometutions.services.FirebaseDatabaseService;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class ViewProfileOfTeacher extends AppCompatActivity {

    private static final String TAG = "ViewProfileOfTeacher";

    private ImageView backButton, profilePhoto;
    private TextView teacherNameText, ratingText, experienceText, locationText;
    private TextView ageText, genderText, emailText, addressText;
    private TextView phoneNumberText;
    private TextView qualificationText, institutionText;
    private FlexboxLayout subjectsContainer, streamsContainer;
    private LinearLayout mainContent;
    private LinearLayout phoneContactLayout, emailContactLayout, whatsappContactLayout;

    private FirebaseDatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_profile_of_teacher);

        databaseService = new FirebaseDatabaseService();
        initViews();

        String teacherId = getIntent().getStringExtra("teacher_id");
        if (teacherId == null || teacherId.isEmpty()) {
            Toast.makeText(this, "Missing teacher id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTeacher(teacherId);
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        // ImageView id in this layout is teacherProfilePhoto
        profilePhoto = findViewById(R.id.teacherProfilePhoto);
        teacherNameText = findViewById(R.id.teacherNameText);
        ratingText = findViewById(R.id.ratingText);
        experienceText = findViewById(R.id.experienceText);
        locationText = findViewById(R.id.locationText);
        mainContent = findViewById(R.id.mainContent);
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        // Bind direct value TextViews from layout
        emailText = findViewById(R.id.emailValueText);
        phoneNumberText = findViewById(R.id.phoneValueText);
        addressText = findViewById(R.id.addressText);
        qualificationText = findViewById(R.id.qualificationText);
        institutionText = findViewById(R.id.institutionText);
        // The layout doesn't expose ids for chip containers; use parent sections
        // Bind new explicit ids for chips containers in layout
        subjectsContainer = findViewById(R.id.subjectsChipsContainer);
        streamsContainer = findViewById(R.id.streamsChipsContainer);

        backButton.setOnClickListener(v -> onBackPressed());
        phoneContactLayout = findViewById(R.id.phoneContactLayout);
        emailContactLayout = findViewById(R.id.emailContactLayout);
        whatsappContactLayout = findViewById(R.id.whatsappContactLayout);
    }

    private void loadTeacher(String id) {
        databaseService.getTeacher(id, new FirebaseDatabaseService.DatabaseCallback<Teacher>() {
            @Override public void onSuccess(Teacher teacher) { updateUI(teacher); }
            @Override public void onFailure(String error) {
                Log.w(TAG, "Direct get failed: " + error + ", trying userId lookup");
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                        .child("teachers").orderByChild("userId").equalTo(id)
                        .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                                        updateUI(child.getValue(Teacher.class));
                                        return;
                                    }
                                } else {
                                    Toast.makeText(ViewProfileOfTeacher.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            @Override public void onCancelled(com.google.firebase.database.DatabaseError databaseError) { finish(); }
                        });
            }
        });
    }

    private void updateUI(Teacher teacher) {
        if (teacher == null) return;
        setText(teacherNameText, nn(teacher.getFullName(), "Teacher Name"));
        setText(ratingText, nn(teacher.getRating(), "4.5"));

        String exp;
        if (teacher.getYearsOfExperience() > 0) exp = teacher.getYearsOfExperience() + " years exp.";
        else if (teacher.getExperience() != null && teacher.getExperience().matches("\\d+")) exp = teacher.getExperience() + " years exp.";
        else exp = nn(teacher.getExperience(), "5 years exp.");
        setText(experienceText, exp);

        setText(locationText, nn(teacher.getLocation(), nn(teacher.getAddress(), "Location")));
        setText(ageText, teacher.getAge() > 0 ? teacher.getAge() + " years" : "Age not specified");
        setText(genderText, nn(teacher.getGender(), "Not specified"));
        setText(emailText, nn(teacher.getEmail(), "Email not available"));
        setText(phoneNumberText, nn(nn(teacher.getPhoneNumber(), null), nn(teacher.getPhone(), "Not available")));

        // Wire contact actions
        if (phoneContactLayout != null) {
            String phone = nn(nn(teacher.getPhoneNumber(), null), teacher.getPhone());
            phoneContactLayout.setOnClickListener(v -> {
                if (phone != null && !phone.isEmpty()) {
                    android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                    i.setData(android.net.Uri.parse("tel:" + phone));
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Phone not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (whatsappContactLayout != null) {
            String phone = nn(nn(teacher.getPhoneNumber(), null), teacher.getPhone());
            whatsappContactLayout.setOnClickListener(v -> {
                if (phone != null && !phone.isEmpty()) {
                    String url = "https://wa.me/" + phone;
                    android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                    startActivity(i);
                } else {
                    Toast.makeText(this, "WhatsApp number not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (emailContactLayout != null) {
            String email = nn(teacher.getEmail(), null);
            emailContactLayout.setOnClickListener(v -> {
                if (email != null && !email.isEmpty()) {
                    android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:" + email));
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tuition Inquiry");
                    startActivity(android.content.Intent.createChooser(i, "Send email"));
                } else {
                    Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
                }
            });
        }
        setText(addressText, nn(teacher.getAddress(), "Address not available"));
        setText(qualificationText, nn(nn(teacher.getHighestQualification(), null), nn(teacher.getQualification(), "Qualification not specified")));
        setText(institutionText, nn(teacher.getInstitution(), "Institution not specified"));

        renderChips(subjectsContainer, teacher.getSubjectsTaught(), R.drawable.subject_chip_background, getResources().getColor(R.color.white));
        renderChips(streamsContainer, teacher.getTeachingStreams(), R.drawable.stream_chip_background, getResources().getColor(R.color.white));

        String url = teacher.getProfileImageUrl();
        if (url != null && !url.isEmpty()) { displayProfileImage(url); } else { profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder); }
    }

    private void renderChips(FlexboxLayout container, List<String> items, int bgRes, int textColor) {
        container.removeAllViews();
        if (items == null || items.isEmpty()) { addChip(container, "Not specified", bgRes, textColor); return; }
        for (String s : items) addChip(container, s, bgRes, textColor);
    }

    private void addChip(FlexboxLayout container, String text, int bgRes, int textColor) {
        TextView chip = new TextView(this);
        chip.setText(text);
        chip.setTextSize(12);
        chip.setTextColor(textColor);
        chip.setPadding(24, 12, 24, 12);
        chip.setBackgroundResource(bgRes);
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(12, 8, 12, 8);
        chip.setLayoutParams(lp);
        container.addView(chip);
    }

    private void displayProfileImage(String data) {
        try {
            if (data.startsWith("data:image") || data.length() > 100) {
                String base64 = data.startsWith("data:image") ? data.substring(data.indexOf(',') + 1) : data;
                byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePhoto.setImageBitmap(bmp);
            } else {
                Glide.with(this).load(data).placeholder(R.drawable.ic_teacher_placeholder).error(R.drawable.ic_teacher_placeholder).circleCrop().into(profilePhoto);
            }
        } catch (Exception e) { profilePhoto.setImageResource(R.drawable.ic_teacher_placeholder); }
    }

    private String nn(String v, String def) { return v != null && !v.isEmpty() ? v : def; }
    private void setText(TextView v, String t) { if (v != null) v.setText(t); }
}