package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.hospitalmanagement.models.User;
import com.example.hospitalmanagement.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        checkUserRoleAndRedirect(currentUser.getUid());
    }

    private void checkUserRoleAndRedirect(String userId) {
        FirebaseUtils.getUsersRef().child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
                                Intent intent = new Intent(MainActivity.this, DoctorDashboardActivity.class);
                                intent.putExtra("userName", user.getFullName());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, PatientDashboardActivity.class);
                                intent.putExtra("userName", user.getFullName());
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}