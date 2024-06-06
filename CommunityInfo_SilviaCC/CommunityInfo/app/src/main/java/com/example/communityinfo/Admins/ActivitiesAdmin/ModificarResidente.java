package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.communityinfo.Modelos.Residente;
import com.example.communityinfo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class ModificarResidente extends AppCompatActivity {
    private EditText etDni, etNombre, etNombreUsuario, etTelefono, etDireccion, etEmail, etUid_user;
    private Button btnModificarResidente;
    private FirebaseFirestore db;
    private String residenteId;
    private String cifSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_residente);
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada

        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etEmail = findViewById(R.id.etEmail);
        btnModificarResidente = findViewById(R.id.btnModificarResidente);
        etUid_user = findViewById(R.id.etUIDuser);

        db = FirebaseFirestore.getInstance();

        // Recoger el ID del residente desde el intent
        residenteId = getIntent().getStringExtra("residenteId");

        // Cargar los datos del residente
        cargarDatosResidente(residenteId);

        btnModificarResidente.setOnClickListener(v -> {
            String dni = etDni.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String nombreUsuario = etNombreUsuario.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String uid = etUid_user.getText().toString().trim();

            if (dni.isEmpty() || nombre.isEmpty() || nombreUsuario.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || email.isEmpty()) {
                Toast.makeText(ModificarResidente.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Residente residenteActualizado = new Residente(residenteId, dni, nombre, nombreUsuario, telefono, direccion, email, uid.isEmpty() ? null : uid);

            db.collection("comunidades").document(cifSelected).collection("residentes").document(residenteId)
                    .set(residenteActualizado)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ModificarResidente.this, "Residente actualizado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ModificarResidente.this, "Error al actualizar residente: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void cargarDatosResidente(String residenteId) {
        DocumentReference docRef = db.collection("comunidades").document(cifSelected).collection("residentes").document(residenteId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Residente residente = documentSnapshot.toObject(Residente.class);
                if (residente != null) {
                    etDni.setText(residente.getDni());
                    etNombre.setText(residente.getNombre());
                    etNombreUsuario.setText(residente.getNombreUsuario());
                    etTelefono.setText(residente.getTelefono());
                    etDireccion.setText(residente.getDireccion());
                    etEmail.setText(residente.getEmail());
                    if(residente.getUid_user()==null || residente.getUid_user().isEmpty()){
                        etUid_user.setText("");
                    }else{
                        etUid_user.setText(residente.getUid_user());
                    }
                }
            } else {
                Toast.makeText(ModificarResidente.this, "Error al cargar los datos del residente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(ModificarResidente.this, "Error al obtener los datos del residente: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(ModificarResidente.this.getFilesDir(), "Database");
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
        return content.toString().trim(); // Elimina el salto de línea final
    }
}