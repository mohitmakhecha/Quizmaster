package com.mm.quizmaster.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mm.quizmaster.R;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Subject;
import com.mm.quizmaster.models.User;
import com.mm.quizmaster.utils.PdfGenerator;
import com.mm.quizmaster.utils.SessionManager;

/**
 * Result Activity
 * Displays quiz results and allows PDF download
 */
public class ResultActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private TextView tvScore;
    private Button btnHome, btnDownloadPdf, btnCertificate;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    
    private int resultId, userId, subjectId, semester, score, totalQuestions;
    private String subjectName, level, studentName;
    private double percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_result);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        
        // Get data from intent
        resultId = getIntent().getIntExtra("RESULT_ID", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);
        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        semester = getIntent().getIntExtra("SEMESTER", -1);
        score = getIntent().getIntExtra("SCORE", 0);
        totalQuestions = getIntent().getIntExtra("TOTAL", 0);
        level = getIntent().getStringExtra("LEVEL");
        percentage = getIntent().getDoubleExtra("PERCENTAGE", 0.0);

        // Get student and subject names
        User user = dbHelper.getUserById(userId);
        
        // Priority: 1. DB, 2. Intent (passed from chain), 3. Session Manager (fallback)
        if (user != null) {
            studentName = user.getName();
        } else {
            studentName = getIntent().getStringExtra("USER_NAME");
            if (studentName == null || studentName.isEmpty()) {
                studentName = sessionManager.getUserName();
            }
        }
        
        Subject subject = dbHelper.getSubjectById(subjectId);
        subjectName = subject != null ? subject.getSubjectName() : "Unknown Subject";

        tvScore = findViewById(R.id.tv_score);
        btnHome = findViewById(R.id.btn_home);
        btnDownloadPdf = findViewById(R.id.btn_download_pdf);
        btnCertificate = findViewById(R.id.btn_certificate);

        // Display score
        tvScore.setText("Your Score: " + score + "/" + totalQuestions + 
                       "\nPercentage: " + String.format("%.2f", percentage) + "%");

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, StudentDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        // Download detailed PDF with all questions and answers
        btnDownloadPdf.setOnClickListener(v -> {
            if (checkPermission()) {
                generateDetailedPdf();
            } else {
                requestPermission();
            }
        });
        
        // Download certificate (only if passed)
        btnCertificate.setOnClickListener(v -> {
            if (percentage >= 40) {
                if (checkPermission()) {
                    generateCertificate();
                } else {
                    requestPermission();
                }
            } else {
                Toast.makeText(this, "Certificate available only for passing students (40%+)", Toast.LENGTH_LONG).show();
            }
        });
        
        // Hide certificate button if failed
        if (percentage < 40) {
            btnCertificate.setVisibility(android.view.View.GONE);
        }
    }

    private void generateDetailedPdf() {
        boolean success = PdfGenerator.generateQuizResultPdf(
            this, resultId, studentName, subjectName, semester, 
            level, score, totalQuestions, percentage
        );
        
        if (success) {
            Toast.makeText(this, "Quiz result PDF downloaded successfully!", Toast.LENGTH_LONG).show();
        }
    }

    private void generateCertificate() {
        boolean success = PdfGenerator.generateCertificate(
            this, studentName, subjectName, score, totalQuestions, percentage
        );
        
        if (success) {
            Toast.makeText(this, "Certificate downloaded successfully!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        // Android 10+ (API 29+) uses MediaStore which doesn't require runtime permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        // For Android 9 and below, check WRITE_EXTERNAL_STORAGE permission
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // Only request permission for Android 9 and below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
