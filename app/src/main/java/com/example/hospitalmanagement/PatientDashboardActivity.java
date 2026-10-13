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

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomePatient;
    private Button btnBookAppointment, btnLogout;
    private RecyclerView rvPatientAppointments;

    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        tvWelcomePatient = findViewById(R.id.tvWelcomePatient);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnLogout = findViewById(R.id.btnLogout);
        rvPatientAppointments = findViewById(R.id.rvPatientAppointments);

        rvPatientAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(appointmentList);
        rvPatientAppointments.setAdapter(adapter);

        String userName = getIntent().getStringExtra("userName");
        if (userName != null) {
            tvWelcomePatient.setText("Welcome, " + userName);
        }

        btnBookAppointment.setOnClickListener(v ->
                startActivity(new Intent(PatientDashboardActivity.this, BookAppointmentActivity.class))
        );

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PatientDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        loadPatientAppointments();
    }

    private void loadPatientAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String currentUserId = currentUser.getUid();

        FirebaseUtils.getAppointmentsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot appSnapshot : snapshot.getChildren()) {
                    Appointment appointment = appSnapshot.getValue(Appointment.class);
                    if (appointment != null && currentUserId.equals(appointment.getPatientId())) {
                        appointmentList.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientDashboardActivity.this, "Error loading appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}