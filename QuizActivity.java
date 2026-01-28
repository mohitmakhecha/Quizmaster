package com.mm.quizmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mm.quizmaster.R;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Question;
import com.mm.quizmaster.models.QuizAnswer;
import com.mm.quizmaster.models.Result;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvTitle, tvCount, tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;

    private List<Question> questionList;
    private List<QuizAnswer> userAnswers;
    private int currentQuestionIndex = 0;
    private int score = 0;
    
    private int userId;
    private int subjectId;
    private int semester;
    private String subjectName;
    private String level;
    private String userName;
    
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_quiz);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        
        // Get Intent Data
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        subjectId = intent.getIntExtra("SUBJECT_ID", -1);
        subjectName = intent.getStringExtra("SUBJECT_NAME");
        level = intent.getStringExtra("LEVEL");
        semester = intent.getIntExtra("SEMESTER", 1);
        userName = intent.getStringExtra("USER_NAME");
        
        userAnswers = new ArrayList<>();

        tvTitle = findViewById(R.id.tv_title);
        tvCount = findViewById(R.id.tv_question_count);
        tvQuestion = findViewById(R.id.tv_question);
        rgOptions = findViewById(R.id.rg_options);
        rbA = findViewById(R.id.rb_opt_a);
        rbB = findViewById(R.id.rb_opt_b);
        rbC = findViewById(R.id.rb_opt_c);
        rbD = findViewById(R.id.rb_opt_d);
        btnNext = findViewById(R.id.btn_next);

        tvTitle.setText(subjectName + " (" + level + ")");

        loadQuestions();

        btnNext.setOnClickListener(v -> {
            if (rgOptions.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            } else {
                checkAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    displayQuestion();
                } else {
                    finishQuiz();
                }
            }
        });
    }

    private void loadQuestions() {
        questionList = dbHelper.getQuestions(subjectId, level);
        if (questionList.isEmpty()) {
            Toast.makeText(this, "No questions found for this selection", Toast.LENGTH_LONG).show();
            finish();
        } else {
            displayQuestion();
        }
    }

    private void displayQuestion() {
        rgOptions.clearCheck();
        Question q = questionList.get(currentQuestionIndex);
        tvQuestion.setText(q.getQuestion());
        rbA.setText(q.getOptionA());
        rbB.setText(q.getOptionB());
        rbC.setText(q.getOptionC());
        rbD.setText(q.getOptionD());
        
        tvCount.setText((currentQuestionIndex + 1) + "/" + questionList.size());
        
        if (currentQuestionIndex == questionList.size() - 1) {
            btnNext.setText("Submit");
        } else {
            btnNext.setText("Next");
        }
    }

    private void checkAnswer() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        String selectedAns = "";
        
        if (selectedId == R.id.rb_opt_a) selectedAns = "A";
        else if (selectedId == R.id.rb_opt_b) selectedAns = "B";
        else if (selectedId == R.id.rb_opt_c) selectedAns = "C";
        else if (selectedId == R.id.rb_opt_d) selectedAns = "D";
        
        Question currentQ = questionList.get(currentQuestionIndex);
        boolean isCorrect = selectedAns.equals(currentQ.getCorrectAnswer());
        
        if (isCorrect) {
            score++;
        }
        
        // Save temporary answer
        QuizAnswer answer = new QuizAnswer();
        answer.setQuestionId(currentQ.getId());
        answer.setSelectedAnswer(selectedAns); // Save "A", "B", etc.
        answer.setCorrect(isCorrect);
        userAnswers.add(answer);
    }

    private void finishQuiz() {
        // Calculate stats
        int totalQuestions = questionList.size();
        int wrongAnswers = totalQuestions - score;
        double percentage = ((double) score / totalQuestions) * 100;
        String passFail = percentage >= 40 ? "Pass" : "Fail";
        
        // 1. Create Result Object
        Result result = new Result();
        result.setUserId(userId);
        result.setSubjectId(subjectId);
        result.setSemester(semester);
        result.setDifficultyLevel(level);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(score);
        result.setWrongAnswers(wrongAnswers);
        result.setScore(score);
        result.setPercentage(percentage);
        result.setPassFail(passFail);
        
        // 2. Save Result to DB
        long resultId = dbHelper.saveResult(result);
        
        if (resultId != -1) {
            // 3. Save All Answers linked to this Result ID
            for (QuizAnswer ans : userAnswers) {
                ans.setResultId((int) resultId);
                dbHelper.saveQuizAnswer(ans);
            }
            
            // 4. Navigate to ResultActivity
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("RESULT_ID", (int) resultId);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", totalQuestions);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SUBJECT_ID", subjectId);
            intent.putExtra("SUBJECT_NAME", subjectName);
            intent.putExtra("SEMESTER", semester);
            intent.putExtra("LEVEL", level);
            intent.putExtra("PERCENTAGE", percentage);
            intent.putExtra("USER_NAME", userName);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error saving results", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
