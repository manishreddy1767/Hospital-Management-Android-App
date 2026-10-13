# 🏥 Hospital Management & Appointment Android Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Backend](https://img.shields.io/badge/Backend-Firebase-FFCA28.svg)](https://firebase.google.com/)

A robust, multi-role native Android application built to streamline patient appointment scheduling and doctor request management. Developed using **Java**, **Android XML Layouts**, and **Firebase (Authentication & Realtime Database)**, this application demonstrates secure role-based navigation, real-time sync, and modern Android development practices.

---

## 📑 Table of Contents
- [Architecture & Design Overview](#-architecture--design-overview)
- [Key Features](#-key-features)
  - [🔐 Authentication & Session Management](#-authentication--session-management)
  - [👤 Patient Dashboard & Booking Flow](#-patient-dashboard--booking-flow)
  - [👨‍⚕️ Doctor Dashboard & Request Actions](#️-doctor-dashboard--request-actions)
- [Database Schema](#-database-schema)
- [App Workflow & Navigation](#-app-workflow--navigation)
- [Tech Stack & Dependencies](#-tech-stack--dependencies)
- [Installation & Local Setup](#-installation--local-setup)
- [Security Considerations](#-security-considerations)
- [Future Enhancements](#-future-enhancements)
- [License](#-license)

---

## 🏗 Architecture & Design Overview

The application follows a modular, Model-View-Controller (MVC) inspired pattern to separate UI, data handling, and backend operations:

* **UI Layer (View)**: Defined using native Android XML files (`activity_*.xml`, `item_*.xml`), utilizing `RecyclerView` adapters for efficient list rendering and UI feedback via native dialogs (`DatePickerDialog`, `TimePickerDialog`).
* **Data Model Layer (Model)**: Clean, encapsulates data structures (`User`, `Doctor`, `Appointment`) with public empty constructors for seamless Firebase Object-Relational Deserialization.
* **Utility Layer (Controller/Service)**: A unified `FirebaseUtils` class encapsulates root references (`users`, `doctors`, `appointments`), standardizing database queries and maintaining persistent reference configurations.
* **Navigation Router**: `MainActivity` serves as a gatekeeper during launch, determining active authentication states and dynamically routing users based on stored database roles (`PATIENT` vs `DOCTOR`).

---

## ✨ Key Features

### 🔐 Authentication & Session Management
* **Firebase Authentication**: Email and password-based registration and sign-in.
* **Role-Based Storage**: New accounts are categorized and written into specific database nodes upon creation.
* **Auto-Login & Routing**: On app launch, `MainActivity` evaluates session state and routes instantly to the role-specific dashboard without user intervention.
* **Clean Session Termination**: Logout functionality clears Firebase Auth credentials and resets the activity task stack (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`) to prevent unauthorized back-navigation.

### 👤 Patient Dashboard & Booking Flow
* **Dynamic Doctor Selection**: Pulls live registered doctor entries from Firebase into an interactive Spinner selection.
* **Interactive Date & Time Pickers**: Native `DatePickerDialog` and `TimePickerDialog` inputs ensure valid timestamp formats.
* **Live Appointment Feed**: A real-time `RecyclerView` displays all appointments tied to the patient's UID.
* **Real-time Status Sync**: Listens for state mutations (e.g., `PENDING` $\rightarrow$ `CONFIRMED` / `CANCELLED`) pushed from the doctor side.

### 👨‍⚕️ Doctor Dashboard & Request Actions
* **Aggregated Request Feed**: Displays all incoming appointment requests across patients.
* **Interactive Controls**: Embedded list controls within `DoctorAppointmentAdapter` allow one-tap status updates (`CONFIRMED` or `CANCELLED`).
* **Real-time Mutation**: Status changes reflect immediately in the patient's UI without manual refresh calls.

---

## 🗄 Database Schema

The Firebase Realtime Database is organized under three centralized root nodes:

```json
{
  "users": {
    "PATIENT_UID_123": {
      "fullName": "Manish Reddy",
      "email": "patient@example.com",
      "role": "PATIENT"
    },
    "DOCTOR_UID_456": {
      "fullName": "Dr. Sarah Smith",
      "email": "doctor@example.com",
      "role": "DOCTOR"
    }
  },
  "doctors": {
    "DOCTOR_UID_456": {
      "doctorId": "DOCTOR_UID_456",
      "fullName": "Dr. Sarah Smith",
      "specialization": "Cardiology",
      "phone": "+1234567890"
    }
  },
  "appointments": {
    "APPOINTMENT_PUSH_ID_789": {
      "appointmentId": "APPOINTMENT_PUSH_ID_789",
      "patientId": "PATIENT_UID_123",
      "doctorId": "DOCTOR_UID_456",
      "date": "15/10/2026",
      "time": "10:30",
      "status": "PENDING"
    }
  }
}
