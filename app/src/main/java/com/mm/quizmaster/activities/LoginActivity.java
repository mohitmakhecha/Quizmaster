package com.mm.quizmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mm.quizmaster.R;
import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.User;
import com.mm.quizmaster.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_login);

        // Handle window insets for edge-to-edge
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        SessionManager sessionManager = new SessionManager(this);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Super Admin Check (Hardcoded)
            if (email.equals("mohit@admin.com") && password.equals("mohit@123")) {
                sessionManager.createLoginSession(0, "Super Admin", email, "super_admin");
                Toast.makeText(this, "Welcome Super Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, SuperAdminDashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // Check Admin Table First
            com.mm.quizmaster.models.Admin admin = dbHelper.getAdminByEmailPassword(email, password);
            if (admin != null) {
                sessionManager.createLoginSession(admin.getId(), admin.getName(), admin.getEmail(), "admin");
                Toast.makeText(this, "Welcome Admin " + admin.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                intent.putExtra("USER_ID", admin.getId());
                intent.putExtra("USER_NAME", admin.getName());
                startActivity(intent);
                finish();
                return;
            }

            // Check Student/User Table
            User user = dbHelper.checkLogin(email, password);
            if (user != null) {
                sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail(), "student");
                Toast.makeText(this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                intent.putExtra("USER_ID", user.getId());
                intent.putExtra("USER_NAME", user.getName());
                startActivity(intent);
                finish();
                return;
            }

            // If no match found
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Login Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
