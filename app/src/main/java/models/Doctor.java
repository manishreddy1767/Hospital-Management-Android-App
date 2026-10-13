package com.example.hospitalmanagement.models;

public class Doctor {
    private String doctorId;
    private String fullName;
    private String specialization;
    private String phone;

    public Doctor() {} // Required for Firebase

    public Doctor(String doctorId, String fullName, String specialization, String phone) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
    }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}