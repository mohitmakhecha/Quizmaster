package com.mm.quizmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mm.quizmaster.R;
import com.mm.quizmaster.adapters.QuestionAdapter;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Question;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionDeleteListener {

    private RecyclerView rvQuestions;
    private FloatingActionButton fabAdd;
    private Button btnManageSubjects;
    private DatabaseHelper dbHelper;
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_admin_dashboard);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        rvQuestions = findViewById(R.id.rv_questions);
        fabAdd = findViewById(R.id.fab_add_question);
        btnManageSubjects = findViewById(R.id.btn_manage_subjects);

        rvQuestions.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddQuestionActivity.class));
        });
        
        // Navigate to Manage Subjects
        btnManageSubjects.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageSubjectsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }

    private void loadQuestions() {
        List<Question> list = dbHelper.getAllQuestions();
        if (adapter == null) {
            adapter = new QuestionAdapter(list, this);
            rvQuestions.setAdapter(adapter);
        } else {
            adapter.updateList(list);
        }
    }

    @Override
    public void onDelete(int questionId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteQuestion(questionId);
                    loadQuestions();
                    Toast.makeText(this, "Question Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
