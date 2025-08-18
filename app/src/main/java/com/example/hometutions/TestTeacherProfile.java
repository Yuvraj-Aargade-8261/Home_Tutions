package com.example.hometutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TestTeacherProfile extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_teacher_profile);
        
        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> {
            // Launch TeacherProfile with test data
            Intent intent = new Intent(this, TeacherProfile.class);
            intent.putExtra("teacher_id", "test_teacher_id");
            startActivity(intent);
        });
    }
}
