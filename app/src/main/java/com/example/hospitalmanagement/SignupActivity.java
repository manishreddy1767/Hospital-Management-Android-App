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

import com.example.hospitalmanagement.models.Doctor;
import com.example.hospitalmanagement.models.User;
import com.example.hospitalmanagement.utils.FirebaseUtils;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private RadioGroup rgRole;
    private RadioButton rbPatient, rbDoctor;
    private Button btnSignup;
    private TextView tvGoToLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Updated XML View IDs matching Material Layout
        etName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        rgRole = findViewById(R.id.rgRole);
        rbPatient = findViewById(R.id.rbPatient);
        rbDoctor = findViewById(R.id.rbDoctor);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnSignup.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Full Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        String role = rbDoctor.isChecked() ? "DOCTOR" : "PATIENT";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        User newUser = new User(userId, name, email, role);

                        // Store in 'users' node
                        FirebaseUtils.getUsersRef().child(userId).setValue(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    // If Doctor, register entry in 'doctors' node as well
                                    if ("DOCTOR".equals(role)) {
                                        Doctor newDoctor = new Doctor(userId, name, "General Physician", "");
                                        FirebaseUtils.getDoctorsRef().child(userId).setValue(newDoctor);
                                    }

                                    Toast.makeText(SignupActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(SignupActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(SignupActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}