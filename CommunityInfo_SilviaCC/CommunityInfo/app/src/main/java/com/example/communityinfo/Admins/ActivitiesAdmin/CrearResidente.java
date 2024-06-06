package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.communityinfo.Modelos.Residente;
import com.example.communityinfo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class CrearResidente extends AppCompatActivity {
    private EditText etDni, etNombre, etNombreUsuario, etTelefono, etDireccion, etEmail;
    private Button btnCrearResidente;
    private FirebaseFirestore db;
    private String cifSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_residente);
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada

        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etEmail = findViewById(R.id.etEmail);
        btnCrearResidente = findViewById(R.id.btnCrearResidente);

        db = FirebaseFirestore.getInstance();

        btnCrearResidente.setOnClickListener(v -> {
            String dni = etDni.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String nombreUsuario = etNombreUsuario.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (dni.isEmpty() || nombre.isEmpty() || nombreUsuario.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || email.isEmpty()) {
                Toast.makeText(CrearResidente.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Residente nuevoResidente = new Residente(null, dni, nombre, nombreUsuario, telefono, direccion, email, null);

            db.collection("comunidades").document(cifSelected).collection("residentes")
                    .add(nuevoResidente)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CrearResidente.this, "Residente creado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CrearResidente.this, "Error al crear residente: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(CrearResidente.this.getFilesDir(), "Database");
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
            // Elimina el archivo en caso de error de lectura
            try {
                Files.delete(file.toPath());
                System.out.println("Archivo eliminado debido a un error de lectura.");
            } catch (IOException deleteException) {
                System.err.println("No se pudo eliminar el archivo: " + deleteException.getMessage());
            }
            return null;
        }
        return content.toString().trim(); // Elimina el salto de la línea final
    }
}