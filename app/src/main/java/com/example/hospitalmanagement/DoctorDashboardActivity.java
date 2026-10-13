package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.hospitalmanagement.models.Appointment;
import com.example.hospitalmanagement.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeDoctor;
    private Button btnLogoutDoctor;
    private RecyclerView rvDoctorAppointments;

    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        tvWelcomeDoctor = findViewById(R.id.tvWelcomeDoctor);
        btnLogoutDoctor = findViewById(R.id.btnLogoutDoctor);
        rvDoctorAppointments = findViewById(R.id.rvDoctorAppointments);

        rvDoctorAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAppointmentAdapter(appointmentList);
        rvDoctorAppointments.setAdapter(adapter);

        String userName = getIntent().getStringExtra("userName");
        if (userName != null) {
            tvWelcomeDoctor.setText("Welcome, Dr. " + userName);
        }

        if (btnLogoutDoctor != null) {
            btnLogoutDoctor.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DoctorDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        loadAppointments();
    }

    private void loadAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        FirebaseUtils.getAppointmentsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot appSnapshot : snapshot.getChildren()) {
                    Appointment appointment = appSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorDashboardActivity.this, "Failed to load appointments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}