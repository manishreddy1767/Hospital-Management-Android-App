package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.hospitalmanagement.models.User;
import com.example.hospitalmanagement.utils.FirebaseUtils;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private RadioGroup rgRole;
    private RadioButton rbPatient;
    private Button btnSignup;
    private TextView tvGoToLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rgRole = findViewById(R.id.rgRole);
        rbPatient = findViewById(R.id.rbPatient);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnSignup.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = rbPatient.isChecked() ? "PATIENT" : "DOCTOR";

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User user = new User(userId, fullName, email, role);

                            FirebaseUtils.getUsersRef().child(userId).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignupActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        finishAffinity();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}