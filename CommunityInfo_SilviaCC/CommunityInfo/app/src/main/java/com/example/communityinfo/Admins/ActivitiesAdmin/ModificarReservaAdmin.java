package com.example.communityinfo.Admins.ActivitiesAdmin;

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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

public class ModificarReservaAdmin extends AppCompatActivity {
    private Spinner spinnerAreaUbicacion;
    private EditText etFechaDeReserva, etHoraInicio, etHoraFin, etMotivo;
    private Button btnModificarReserva;
    private FirebaseFirestore miDb;
    private FirebaseAuth miAuth;
    private String reservaId;
    private String idUsuarioCreator;
    private String cifSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_reserva_admin);

        spinnerAreaUbicacion = findViewById(R.id.spinnerAreaUbiAdmin);
        etFechaDeReserva = findViewById(R.id.etFechaDeReserva);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        etMotivo = findViewById(R.id.etMotivo);
        btnModificarReserva = findViewById(R.id.btnModificarReserva);

        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();
        cifSelected = readFile();
        cargarListaAreas();
        reservaId = getIntent().getStringExtra("reservaId");

        // Método que carga los datos de la reserva seleccionada.
        cargarDatosReserva(reservaId);

        btnModificarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método que actualiza los datos de la reserva seleccionada.
                actualizarReserva(reservaId);
            }
        });
    }

    private void cargarDatosReserva(String reservaId) {
        DocumentReference reservaRef = miDb.collection("comunidades").document(cifSelected).collection("reservas").document(reservaId);
        reservaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Reserva reserva = documentSnapshot.toObject(Reserva.class);
                if (reserva != null) {
                    setSpinnerSelection(spinnerAreaUbicacion, reserva.getAreaUbicacion());
                    etFechaDeReserva.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(reserva.getFechaReserva())));
                    etHoraInicio.setText(reserva.getHoraInicio());
                    etHoraFin.setText(reserva.getHoraFin());
                    etMotivo.setText(reserva.getMotivo());
                    idUsuarioCreator = reserva.getUsuarioId(); // Guardar el usuario que creó la reserva
                }
            } else {
                Toast.makeText(ModificarReservaAdmin.this, "Error al cargar los datos de la reserva", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ModificarReservaAdmin.this, "Error al obtener los datos de la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Metodo que actualiza las Reservas
    private void actualizarReserva(String reservaId) {
        String areaUbicacion = spinnerAreaUbicacion.getSelectedItem().toString().trim();
        String fechaDeReserva = etFechaDeReserva.getText().toString().trim();
        String horaInicio = etHoraInicio.getText().toString().trim();
        String horaFin = etHoraFin.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();

        if (areaUbicacion.isEmpty() || fechaDeReserva.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty() || motivo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parseo de fecha a long milisegundos
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long fechaReserva;
        try {
            Date date = sdf.parse(fechaDeReserva);
            fechaReserva = date.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificación de conflictos con otras reservas
        verificarConflictoReserva(cifSelected, areaUbicacion, fechaReserva, horaInicio, horaFin).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()) {
                Toast.makeText(this, "Conflicto con otra reserva en el mismo horario", Toast.LENGTH_SHORT).show();
            } else {
                // Al no tener conflictos de reservas se actualiza la Reserva.
                Reserva reservaActualizada = new Reserva(reservaId, fechaReserva, areaUbicacion, horaInicio, horaFin, motivo, idUsuarioCreator);

                miDb.collection("comunidades").document(cifSelected).collection("reservas").document(reservaId)
                        .set(reservaActualizada)
                        .addOnSuccessListener(aVoid -> Toast.makeText(ModificarReservaAdmin.this, "Reserva actualizada con éxito", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ModificarReservaAdmin.this, "Error al actualizar la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Reserva modificada sin verificar", Toast.LENGTH_SHORT).show());
    }

    private Task<Boolean> verificarConflictoReserva(String cif, String areaUbicacion, long fechaReserva, String horaInicio, String horaFin) {
        CollectionReference reservasRef = miDb.collection("comunidades").document(cif).collection("reservas");
        long startOfDay = getStartOfDay(new Date(fechaReserva)).getTime();
        long endOfDay = getEndOfDay(new Date(fechaReserva)).getTime();

        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        reservasRef.whereEqualTo("areaUbicacion", areaUbicacion)
                .whereGreaterThanOrEqualTo("fechaReserva", startOfDay)
                .whereLessThanOrEqualTo("fechaReserva", endOfDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean conflicto = false;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Reserva reserva = doc.toObject(Reserva.class);
                            if (!reserva.getId().equals(reservaId) && hayConflictoDeHorarios(reserva, horaInicio, horaFin)) {
                                conflicto = true;
                                break;
                            }
                        }
                        tcs.setResult(conflicto);
                    } else {
                        tcs.setException(task.getException());
                    }
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
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
                        Toast.makeText(ModificarReservaAdmin.this, "Error al cargar áreas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ModificarReservaAdmin.this, "Error al obtener áreas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(value)) {
                spinner.setSelection(position);
                return;
            }
        }
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