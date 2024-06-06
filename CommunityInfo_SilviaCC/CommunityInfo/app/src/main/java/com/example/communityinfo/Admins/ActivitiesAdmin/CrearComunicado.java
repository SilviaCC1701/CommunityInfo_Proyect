package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearComunicado extends AppCompatActivity {
    private String cifSelected;
    private EditText etFechaComunicado, etTitulo, etAsunto, etContenido;
    private Button btnCrearComunicado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_comunicado);

        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada

        Toolbar toolbar = findViewById(R.id.myToolbarCreaComunicado);
        setSupportActionBar(toolbar);

        etFechaComunicado = findViewById(R.id.etFechaComunicado);
        etTitulo = findViewById(R.id.etTituloNewComunicado);
        etAsunto = findViewById(R.id.etAsuntoNewComunicado);
        etContenido = findViewById(R.id.etContenidoNewComunicado);
        btnCrearComunicado = findViewById(R.id.btnCrearComunicado);

        btnCrearComunicado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearComunicado();
            }
        });
    }

    private void crearComunicado() {
        String fechaStr = etFechaComunicado.getText().toString().trim();
        String titulo = etTitulo.getText().toString().trim();
        String asunto = etAsunto.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();

        if (fechaStr.isEmpty() || titulo.isEmpty() || asunto.isEmpty() || contenido.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsear la fecha a long
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        long fechaLong;
        try {
            Date date = sdf.parse(fechaStr);
            fechaLong = date.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comunicadosRef = db.collection("comunidades").document(cifSelected).collection("comunicados");

        Comunicado nuevoComunicado = new Comunicado(null, fechaLong, titulo, asunto, contenido);

        comunicadosRef.add(nuevoComunicado)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CrearComunicado.this, "Comunicado añadido con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CrearComunicado.this, "Error al añadir el comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(CrearComunicado.this.getFilesDir(), "Database");
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