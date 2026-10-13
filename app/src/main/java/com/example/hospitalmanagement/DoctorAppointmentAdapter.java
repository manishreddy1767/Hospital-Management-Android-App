package com.example.hospitalmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.models.Appointment;
import com.example.hospitalmanagement.utils.FirebaseUtils;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {

    private List<Appointment> appointmentList;

    public DoctorAppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvAppointmentDetails.setText("Date: " + appointment.getDate() + " | Time: " + appointment.getTime());
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        // Update status in Firebase Realtime Database
        holder.btnAccept.setOnClickListener(v ->
                FirebaseUtils.getAppointmentsRef().child(appointment.getAppointmentId()).child("status").setValue("CONFIRMED")
        );

        holder.btnReject.setOnClickListener(v ->
                FirebaseUtils.getAppointmentsRef().child(appointment.getAppointmentId()).child("status").setValue("CANCELLED")
        );
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentDetails, tvStatus;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentDetails = itemView.findViewById(R.id.tvAppointmentDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}