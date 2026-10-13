package com.example.hospitalmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.hospitalmanagement.models.Appointment;
import com.example.hospitalmanagement.models.Doctor;
import com.example.hospitalmanagement.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spinnerDoctors;
    private Button btnSelectDate, btnSelectTime, btnConfirmBooking;
    private TextView tvSelectedDate, tvSelectedTime;

    private List<Doctor> doctorList = new ArrayList<>();
    private List<String> doctorNames = new ArrayList<>();
    private String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        spinnerDoctors = findViewById(R.id.spinnerDoctors);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        loadDoctors();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnConfirmBooking.setOnClickListener(v -> bookAppointment());
    }

    private void loadDoctors() {
        FirebaseUtils.getDoctorsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();
                doctorNames.clear();

                for (DataSnapshot docSnapshot : snapshot.getChildren()) {
                    Doctor doctor = docSnapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        doctorList.add(doctor);
                        String name = doctor.getFullName() != null ? doctor.getFullName() : "Doctor";
                        String spec = doctor.getSpecialization() != null ? doctor.getSpecialization() : "General";
                        doctorNames.add(name + " (" + spec + ")");
                    }
                }

                if (doctorNames.isEmpty()) {
                    doctorNames.add("No doctors available");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        BookAppointmentActivity.this,
                        android.R.layout.simple_spinner_item,
                        doctorNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDoctors.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookAppointmentActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    tvSelectedDate.setText("Selected Date: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    tvSelectedTime.setText("Selected Time: " + selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private void bookAppointment() {
        if (doctorList.isEmpty()) {
            Toast.makeText(this, "No doctor available to book", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(this, "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedDoctorPos = spinnerDoctors.getSelectedItemPosition();
        if (selectedDoctorPos < 0 || selectedDoctorPos >= doctorList.size()) {
            Toast.makeText(this, "Please select a valid doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor selectedDoctor = doctorList.get(selectedDoctorPos);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String patientId = currentUser.getUid();
        DatabaseReference appointmentsRef = FirebaseUtils.getAppointmentsRef();
        String appointmentId = appointmentsRef.push().getKey();

        Appointment appointment = new Appointment(
                appointmentId,
                patientId,
                selectedDoctor.getDoctorId(),
                selectedDate,
                selectedTime,
                "PENDING"
        );

        if (appointmentId != null) {
            appointmentsRef.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BookAppointmentActivity.this, "Appointment Booked Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(BookAppointmentActivity.this, "Booking Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}