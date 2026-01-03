package com.mm.quizmaster.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.mm.quizmaster.adapters.AdminAdapter;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Admin;
import com.mm.quizmaster.utils.SessionManager;
import java.util.List;

/**
 * Super Admin Dashboard Activity
 * Allows Super Admin to manage admins and add questions
 */
public class SuperAdminDashboardActivity extends AppCompatActivity implements AdminAdapter.OnAdminDeleteListener {

    private RecyclerView rvAdmins;
    private FloatingActionButton fabAddAdmin;
    private Button btnAddQuestion, btnManageSubjects, btnLogout;
    private DatabaseHelper dbHelper;
    private AdminAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_super_admin_dashboard);
        
        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        
        rvAdmins = findViewById(R.id.rv_admins);
        fabAddAdmin = findViewById(R.id.fab_add_admin);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnManageSubjects = findViewById(R.id.btn_manage_subjects);
        btnLogout = findViewById(R.id.btn_logout);

        rvAdmins.setLayoutManager(new LinearLayoutManager(this));
        
        loadAdmins();

        fabAddAdmin.setOnClickListener(v -> showAddAdminDialog());
        
        // Super Admin can also add questions
        btnAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(SuperAdminDashboardActivity.this, AddQuestionActivity.class);
            intent.putExtra("IS_SUPER_ADMIN", true);
            startActivity(intent);
        });
        
        // Navigate to Manage Subjects
        btnManageSubjects.setOnClickListener(v -> {
            startActivity(new Intent(SuperAdminDashboardActivity.this, ManageSubjectsActivity.class));
        });
        
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(SuperAdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadAdmins() {
        List<Admin> admins = dbHelper.getAllAdmins();
        if (adapter == null) {
            adapter = new AdminAdapter(admins, this);
            rvAdmins.setAdapter(adapter);
        } else {
            adapter.updateList(admins);
        }
    }

    private void showAddAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_admin, null);
        builder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.et_admin_name);
        final EditText etEmail = dialogView.findViewById(R.id.et_admin_email);
        final EditText etPassword = dialogView.findViewById(R.id.et_admin_password);

        builder.setTitle("Add New Admin")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(SuperAdminDashboardActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dbHelper.checkAdminEmailExists(email)) {
                        Toast.makeText(SuperAdminDashboardActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Admin newAdmin = new Admin();
                    newAdmin.setName(name);
                    newAdmin.setEmail(email);
                    newAdmin.setPassword(password);
                    newAdmin.setCreatedBy("Super Admin");

                    dbHelper.addAdmin(newAdmin);
                    loadAdmins();
                    Toast.makeText(SuperAdminDashboardActivity.this, "Admin Added Successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                
        builder.create().show();
    }

    @Override
    public void onDelete(int adminId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Admin")
                .setMessage("Are you sure you want to delete this admin?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteAdmin(adminId);
                    loadAdmins();
                    Toast.makeText(this, "Admin Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
