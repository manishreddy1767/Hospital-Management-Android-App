package com.example.hospitalmanagement.models;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String time;
    private String status; // "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"

    public Appointment() {}

    public Appointment(String appointmentId, String patientId, String doctorId, String date, String time, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}