package com.example.hospitalmanagement.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    private static FirebaseDatabase database;

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance("https://hospital-management-89c79-default-rtdb.asia-southeast1.firebasedatabase.app");
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    public static DatabaseReference getUsersRef() {
        return getDatabase().getReference("users");
    }

    public static DatabaseReference getDoctorsRef() {
        return getDatabase().getReference("doctors");
    }

    public static DatabaseReference getAppointmentsRef() {
        return getDatabase().getReference("appointments");
    }
}