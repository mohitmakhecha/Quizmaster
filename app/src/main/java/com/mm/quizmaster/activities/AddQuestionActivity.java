package com.mm.quizmaster.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mm.quizmaster.R;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Question;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    private Spinner spinnerSem, spinnerSub;
    private RadioGroup rgLevel;
    private EditText etQuestion, etOpA, etOpB, etOpC, etOpD, etAnswer;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    
    private int selectedSubjectId = -1;
    private String selectedLevel = "Easy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_add_question);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);

        spinnerSem = findViewById(R.id.spinner_sem);
        spinnerSub = findViewById(R.id.spinner_sub);
        rgLevel = findViewById(R.id.rg_level);
        etQuestion = findViewById(R.id.et_question);
        etOpA = findViewById(R.id.et_opt_a);
        etOpB = findViewById(R.id.et_opt_b);
        etOpC = findViewById(R.id.et_opt_c);
        etOpD = findViewById(R.id.et_opt_d);
        etAnswer = findViewById(R.id.et_answer);
        btnSave = findViewById(R.id.btn_save_question);

        setupSpinners();
        setupLevel();

        btnSave.setOnClickListener(v -> saveQuestion());
    }

    private void setupSpinners() {
        // Semesters 1 to 6
        String[] sems = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sems);
        spinnerSem.setAdapter(semAdapter);

        spinnerSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int semester = position + 1;
                loadSubjects(semester);
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

        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectNames);
        spinnerSub.setAdapter(subAdapter);

        spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < subjectIds.size()) {
                    selectedSubjectId = subjectIds.get(position);
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
        // Default
        rgLevel.check(R.id.rb_easy);
    }

    private void saveQuestion() {
        String qText = etQuestion.getText().toString().trim();
        String opA = etOpA.getText().toString().trim();
        String opB = etOpB.getText().toString().trim();
        String opC = etOpC.getText().toString().trim();
        String opD = etOpD.getText().toString().trim();
        String ans = etAnswer.getText().toString().trim().toUpperCase();

        // Validate all fields
        if (qText.isEmpty() || opA.isEmpty() || opB.isEmpty() || opC.isEmpty() || opD.isEmpty() || ans.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate subject selection
        if (selectedSubjectId == -1) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate answer is A, B, C, or D
        if (!ans.equals("A") && !ans.equals("B") && !ans.equals("C") && !ans.equals("D")) {
            Toast.makeText(this, "Answer must be A, B, C, or D", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Question q = new Question();
            q.setSubjectId(selectedSubjectId);
            q.setQuestion(qText);
            q.setOptionA(opA);
            q.setOptionB(opB);
            q.setOptionC(opC);
            q.setOptionD(opD);
            q.setAnswer(ans);
            q.setLevel(selectedLevel);
            q.setMarks(1); // Default marks
            q.setCreatedBy(0); // 0 for admin/super admin

            long id = dbHelper.addQuestion(q);
            if (id > 0) {
                Toast.makeText(this, "Question Added Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add question. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
