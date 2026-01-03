package com.mm.quizmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mm.quizmaster.R;
import com.mm.quizmaster.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Spinner spinnerSem, spinnerSub;
    private RadioGroup rgLevel;
    private Button btnStart;
    private DatabaseHelper dbHelper;
    private int userId;
    private String userName;
    
    private int selectedSubjectId = -1;
    private String selectedLevel = "Easy";
    private String selectedSubjectName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_student_dashboard);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");
        
        tvWelcome = findViewById(R.id.tv_welcome);
        spinnerSem = findViewById(R.id.spinner_semester);
        spinnerSub = findViewById(R.id.spinner_subject);
        rgLevel = findViewById(R.id.rg_level);
        btnStart = findViewById(R.id.btn_start_quiz);

        setupSpinners();
        setupLevel();
        
        btnStart.setOnClickListener(v -> startQuiz());
    }

    private void setupSpinners() {
        String[] sems = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sems);
        spinnerSem.setAdapter(semAdapter);

        spinnerSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSubjects(position + 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSubjects(int semester) {
        List<com.mm.quizmaster.models.Subject> subjects = dbHelper.getSubjectsBySemester(semester);
        List<String> subjectNames = new ArrayList<>();
        final List<Integer> subjectIds = new ArrayList<>();

        for (com.mm.quizmaster.models.Subject subject : subjects) {
            subjectIds.add(subject.getId());
            subjectNames.add(subject.getSubjectName());
        }

        if (subjectNames.isEmpty()) {
            subjectNames.add("No Subjects Found");
            subjectIds.add(-1);
        }

        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectNames);
        spinnerSub.setAdapter(subAdapter);

        spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < subjectIds.size()) {
                    selectedSubjectId = subjectIds.get(position);
                    selectedSubjectName = subjectNames.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectId = -1;
            }
        });
    }

    private void setupLevel() {
        rgLevel.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_easy) selectedLevel = "Easy";
            else if (checkedId == R.id.rb_medium) selectedLevel = "Medium";
            else if (checkedId == R.id.rb_hard) selectedLevel = "Hard";
        });
        rgLevel.check(R.id.rb_easy);
    }

    private void startQuiz() {
        if (selectedSubjectId == -1) {
            Toast.makeText(this, "Please select a valid subject", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(StudentDashboardActivity.this, QuizActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("SUBJECT_ID", selectedSubjectId);
        intent.putExtra("SUBJECT_NAME", selectedSubjectName);
        intent.putExtra("LEVEL", selectedLevel);
        intent.putExtra("SEMESTER", spinnerSem.getSelectedItemPosition() + 1);
        startActivity(intent);
    }
}
