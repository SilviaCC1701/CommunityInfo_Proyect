package com.example.communityinfo.Users.ActivitiesUser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communityinfo.Modelos.Reserva;
import com.example.communityinfo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModificarReserva extends AppCompatActivity {
    private EditText etAreaUbicacion, etFechaDeReserva, etHoraInicio, etHoraFin, etMotivo;
    private Button btnModificarReserva;
    private FirebaseFirestore miDb;
    private FirebaseAuth miAuth;
    private String reservaId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_reserva);

        etAreaUbicacion = findViewById(R.id.etAreaUbicacion);
        etFechaDeReserva = findViewById(R.id.etFechaDeReserva);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        etMotivo = findViewById(R.id.etMotivo);
        btnModificarReserva = findViewById(R.id.btnModificarReserva);

        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();

        reservaId = getIntent().getStringExtra("reservaId");

        // Metodo que carga los datos de la reserva seleccionada.
        cargarDatosReserva(reservaId);

        btnModificarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Metodo que actualiza los datos de la reserva seleccionada.
                actualizarReserva();
            }
        });
    }

    private void cargarDatosReserva(String reservaId) {
        DocumentReference reservaRef = miDb.collection("comunidades").document(reservaId);
        reservaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Reserva reserva = documentSnapshot.toObject(Reserva.class);
                if (reserva != null) {
                    etAreaUbicacion.setText(reserva.getAreaUbicacion());
                    etFechaDeReserva.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(reserva.getFechaReserva())));
                    etHoraInicio.setText(reserva.getHoraInicio());
                    etHoraFin.setText(reserva.getHoraFin());
                    etMotivo.setText(reserva.getMotivo());
                }
            } else {
                Toast.makeText(ModificarReserva.this, "Error al cargar los datos de la reserva", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ModificarReserva.this, "Error al obtener los datos de la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarReserva() {
        String areaUbicacion = etAreaUbicacion.getText().toString().trim();
        String fechaDeReserva = etFechaDeReserva.getText().toString().trim();
        String horaInicio = etHoraInicio.getText().toString().trim();
        String horaFin = etHoraFin.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();

        if (areaUbicacion.isEmpty() || fechaDeReserva.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty() || motivo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long fechaReserva;
        try {
            Date date = sdf.parse(fechaDeReserva);
            fechaReserva = date.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        Reserva reservaActualizada = new Reserva(reservaId, fechaReserva, areaUbicacion, horaInicio, horaFin, motivo, miAuth.getCurrentUser().getUid());

        miDb.collection("comunidades").document(reservaId)
                .set(reservaActualizada)
                .addOnSuccessListener(aVoid -> Toast.makeText(ModificarReserva.this, "Reserva actualizada con éxito", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ModificarReserva.this, "Error al actualizar la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}