package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModificarComunicado extends AppCompatActivity {
    private EditText etFechaEditComunicado, etTituloEditComunicado, etAsuntoEditComunicado, etContenidoEditComunicado;
    private Button btnModificarComunicado;
    private FirebaseFirestore miDb;
    private String comunicadoId;
    private String cifSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_comunicado);

        etFechaEditComunicado = findViewById(R.id.etFechaEditComunicado);
        etTituloEditComunicado = findViewById(R.id.etTituloEditComunicado);
        etAsuntoEditComunicado = findViewById(R.id.etAsuntoEditComunicado);
        etContenidoEditComunicado = findViewById(R.id.etContenidoEditComunicado);
        btnModificarComunicado = findViewById(R.id.btnModificarComunicado);

        miDb = FirebaseFirestore.getInstance();

        // Recoger el ID del comunicado desde el intent
        comunicadoId = getIntent().getStringExtra("comunicadoId");
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada

        // Cargar los datos del comunicado
        cargarDatosComunicado(comunicadoId);

        // Configurar el botón de modificar
        btnModificarComunicado.setOnClickListener(v -> {
            actualizarComunicado(comunicadoId);
            ModificarComunicado.this.finish();
        });
    }

    private void cargarDatosComunicado(String comunicadoId) {
        DocumentReference docRef = miDb.collection("comunidades").document(cifSelected).collection("comunicados").document(comunicadoId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Comunicado comunicado = documentSnapshot.toObject(Comunicado.class);
                if (comunicado != null) {
                    etFechaEditComunicado.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(comunicado.getFecha())));
                    etTituloEditComunicado.setText(comunicado.getTitulo());
                    etAsuntoEditComunicado.setText(comunicado.getAsunto());
                    etContenidoEditComunicado.setText(comunicado.getContenido());
                }
            } else {
                Toast.makeText(ModificarComunicado.this, "Error al cargar los datos del comunicado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ModificarComunicado.this, "Error al obtener los datos del comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarComunicado(String comunicadoId) {
        String fecha = etFechaEditComunicado.getText().toString().trim();
        String titulo = etTituloEditComunicado.getText().toString().trim();
        String asunto = etAsuntoEditComunicado.getText().toString().trim();
        String contenido = etContenidoEditComunicado.getText().toString().trim();

        if (fecha.isEmpty() || titulo.isEmpty() || asunto.isEmpty() || contenido.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsear la fecha a long
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long fechaLong;
        try {
            Date date = sdf.parse(fecha);
            fechaLong = date.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        Comunicado comunicadoActualizado = new Comunicado(comunicadoId, fechaLong, titulo, asunto, contenido);

        miDb.collection("comunidades").document(cifSelected).collection("comunicados").document(comunicadoId)
                .set(comunicadoActualizado)
                .addOnSuccessListener(aVoid -> Toast.makeText(ModificarComunicado.this, "Comunicado actualizado con éxito", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ModificarComunicado.this, "Error al actualizar el comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(ModificarComunicado.this.getFilesDir(), "Database");
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