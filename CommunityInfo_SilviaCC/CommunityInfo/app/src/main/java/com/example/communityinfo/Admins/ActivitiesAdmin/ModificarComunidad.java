package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.communityinfo.Modelos.Comunidad;
import com.example.communityinfo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModificarComunidad extends AppCompatActivity {
    private EditText etCif, etNombre, etDireccion;
    private Button btnModificarComunidad;
    private FirebaseFirestore db;
    private String comunidadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_comunidad);

        etCif = findViewById(R.id.etCif);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        btnModificarComunidad = findViewById(R.id.btnModificarComunidad);

        db = FirebaseFirestore.getInstance();

        // Recoger el ID de la comunidad desde el intent
        comunidadId = getIntent().getStringExtra("comunidadId");

        // Cargar los datos de la comunidad
        cargarDatosComunidad(comunidadId);

        btnModificarComunidad.setOnClickListener(v -> {
            String cif = etCif.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();

            if (cif.isEmpty() || nombre.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(ModificarComunidad.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Comunidad comunidadActualizada = new Comunidad(cif, nombre, direccion);

            db.collection("comunidades").document(cif)
                    .set(comunidadActualizada)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ModificarComunidad.this, "Comunidad actualizada con éxito", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ModificarComunidad.this, "Error al actualizar comunidad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void cargarDatosComunidad(String comunidadId) {
        DocumentReference docRef = db.collection("comunidades").document(comunidadId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Comunidad comunidad = documentSnapshot.toObject(Comunidad.class);
                if (comunidad != null) {
                    etCif.setText(comunidad.getCif());
                    etNombre.setText(comunidad.getNombreComunidad());
                    etDireccion.setText(comunidad.getDireccion());
                }
            } else {
                Toast.makeText(ModificarComunidad.this, "Error al cargar los datos de la comunidad", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(ModificarComunidad.this, "Error al obtener los datos de la comunidad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}