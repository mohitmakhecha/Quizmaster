package com.mm.quizmaster.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mm.quizmaster.R;
import com.mm.quizmaster.adapters.SubjectAdapter;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Subject;
import java.util.List;

/**
 * Manage Subjects Activity
 * Allows Admin and Super Admin to add, edit, and delete subjects
 */
public class ManageSubjectsActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectActionListener {

    private RecyclerView rvSubjects;
    private FloatingActionButton fabAddSubject;
    private DatabaseHelper dbHelper;
    private SubjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_manage_subjects);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        
        rvSubjects = findViewById(R.id.rv_subjects);
        fabAddSubject = findViewById(R.id.fab_add_subject);

        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        
        loadSubjects();

        fabAddSubject.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void loadSubjects() {
        List<Subject> subjects = dbHelper.getAllSubjects();
        if (adapter == null) {
            adapter = new SubjectAdapter(subjects, this);
            rvSubjects.setAdapter(adapter);
        } else {
            adapter.updateList(subjects);
        }
    }

    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_subject, null);
        builder.setView(dialogView);

        final EditText etSubjectName = dialogView.findViewById(R.id.et_subject_name);
        final Spinner spinnerSemester = dialogView.findViewById(R.id.spinner_semester);

        // Setup semester spinner
        String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        spinnerSemester.setAdapter(semAdapter);

        builder.setTitle("Add New Subject")
                .setPositiveButton("Add", (dialog, which) -> {
                    String subjectName = etSubjectName.getText().toString().trim();
                    int semester = spinnerSemester.getSelectedItemPosition() + 1;

                    if (subjectName.isEmpty()) {
                        Toast.makeText(ManageSubjectsActivity.this, "Please enter subject name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Subject newSubject = new Subject(subjectName, semester);
                    long id = dbHelper.addSubject(newSubject);
                    
                    if (id > 0) {
                        loadSubjects();
                        Toast.makeText(ManageSubjectsActivity.this, "Subject Added Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageSubjectsActivity.this, "Failed to add subject (may already exist)", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                
        builder.create().show();
    }

    @Override
    public void onEdit(Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_subject, null);
        builder.setView(dialogView);

        final EditText etSubjectName = dialogView.findViewById(R.id.et_subject_name);
        final Spinner spinnerSemester = dialogView.findViewById(R.id.spinner_semester);

        // Pre-fill data
        etSubjectName.setText(subject.getSubjectName());
        
        String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        spinnerSemester.setAdapter(semAdapter);
        spinnerSemester.setSelection(subject.getSemester() - 1);

        builder.setTitle("Edit Subject")
                .setPositiveButton("Update", (dialog, which) -> {
                    String subjectName = etSubjectName.getText().toString().trim();
                    int semester = spinnerSemester.getSelectedItemPosition() + 1;
    
                    if (subjectName.isEmpty()) {
                        Toast.makeText(ManageSubjectsActivity.this, "Please enter subject name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update existing subject
                    Subject updatedSubject = new Subject(subjectName, semester);
                    updatedSubject.setId(subject.getId());
                    
                    int rows = dbHelper.updateSubject(updatedSubject);
                    
                    if (rows > 0) {
                        loadSubjects();
                        Toast.makeText(ManageSubjectsActivity.this, "Subject Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageSubjectsActivity.this, "Failed to update subject", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                
        builder.create().show();
    }

    @Override
    public void onDelete(int subjectId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure? This will also delete all questions for this subject.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteSubject(subjectId);
                    loadSubjects();
                    Toast.makeText(this, "Subject Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
