package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.communityinfo.Modelos.Comunidad;
import com.example.communityinfo.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class CrearComunidad extends AppCompatActivity {
    private EditText etCif, etNombre, etDireccion;
    private Button btnCrearComunidad;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_comunidad);

        etCif = findViewById(R.id.etCif);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        btnCrearComunidad = findViewById(R.id.btnCrearComunidad);

        db = FirebaseFirestore.getInstance();

        btnCrearComunidad.setOnClickListener(v -> {
            String cif = etCif.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();

            if (cif.isEmpty() || nombre.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(CrearComunidad.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Comunidad nuevaComunidad = new Comunidad(cif, nombre, direccion);

            db.collection("comunidades").document(cif)
                    .set(nuevaComunidad)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CrearComunidad.this, "Comunidad creada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CrearComunidad.this, "Error al crear comunidad: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}