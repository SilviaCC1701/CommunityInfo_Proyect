package com.example.communityinfo.Users.ActivitiesUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communityinfo.Modelos.Reserva;
import com.example.communityinfo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrearReserva extends AppCompatActivity {
    private Spinner spinnerAreaUbicacion;
    private EditText etFechaDeReserva, etHoraInicio, etHoraFin, etMotivo;
    private Button btnCrearReserva;
    private FirebaseFirestore miDb;
    private FirebaseAuth miAuth;
    private String cifSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reserva);

        spinnerAreaUbicacion = findViewById(R.id.spinnerAreaUbicacion);
        etFechaDeReserva = findViewById(R.id.etFechaDeReserva);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        etMotivo = findViewById(R.id.etMotivo);
        btnCrearReserva = findViewById(R.id.btnCrearReserva);

        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();

        cifSelected = readFile();
        cargarListaAreas();

        btnCrearReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearNuevaReserva();
            }
        });
    }

    private void crearNuevaReserva() {
        String areaUbicacion = spinnerAreaUbicacion.getSelectedItem().toString().trim();
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

        FirebaseUser user = miAuth.getCurrentUser();
        if (user != null) {
            String usuarioId = user.getUid();

            if (verificarConflictoReserva(cifSelected, areaUbicacion, fechaReserva, horaInicio, horaFin)) {
                Toast.makeText(this, "Conflicto con otra reserva en el mismo horario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Al no tener conflictos de reservas se crea la Reserva.
            Reserva nuevaReserva = new Reserva(null, fechaReserva, areaUbicacion, horaInicio, horaFin, motivo, usuarioId);

            miDb.collection("comunidades").document(cifSelected).collection("reservas")
                    .add(nuevaReserva)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CrearReserva.this, "Reserva creada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CrearReserva.this, "Error al crear la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private boolean verificarConflictoReserva(String cif, String areaUbicacion, long fechaReserva, String horaInicio, String horaFin) {
        CollectionReference reservasRef = miDb.collection("comunidades").document(cif).collection("reservas");
        long startOfDay = getStartOfDay(new Date(fechaReserva)).getTime();
        long endOfDay = getEndOfDay(new Date(fechaReserva)).getTime();

        boolean[] conflicto = {false};

        reservasRef.whereEqualTo("areaUbicacion", areaUbicacion)
                .whereGreaterThanOrEqualTo("fechaReserva", startOfDay)
                .whereLessThanOrEqualTo("fechaReserva", endOfDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Reserva reserva = doc.toObject(Reserva.class);
                            if (hayConflictoDeHorarios(reserva, horaInicio, horaFin)) {
                                conflicto[0] = true;
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Reserva creada sin verificar", Toast.LENGTH_SHORT).show());

        return conflicto[0];
    }

    private boolean hayConflictoDeHorarios(Reserva reserva, String horaInicio, String horaFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date inicioActual = sdf.parse(reserva.getHoraInicio());
            Date finActual = sdf.parse(reserva.getHoraFin());
            Date inicioNueva = sdf.parse(horaInicio);
            Date finNueva = sdf.parse(horaFin);

            return (inicioNueva.before(finActual) && inicioActual.before(finNueva));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private void cargarListaAreas() {
        miDb.collection("comunidades").document(cifSelected).collection("areas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> areas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String area = document.getString("nombreArea");
                            areas.add(area);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areas);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAreaUbicacion.setAdapter(adapter);
                    } else {
                        Toast.makeText(CrearReserva.this, "Error al cargar áreas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CrearReserva.this, "Error al obtener áreas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(getFilesDir(), "Database");
        File file = new File(directory, "ComunidadCif.txt");
        StringBuilder content = new StringBuilder();

        if (!file.exists()) {
            System.err.println("El archivo no existe.");
            return null;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            try {
                Files.delete(file.toPath());
                System.out.println("Archivo eliminado debido a un error de lectura.");
            } catch (IOException deleteException) {
                System.err.println("No se pudo eliminar el archivo: " + deleteException.getMessage());
            }
            return null;
        }
        return content.toString().trim();
    }
}