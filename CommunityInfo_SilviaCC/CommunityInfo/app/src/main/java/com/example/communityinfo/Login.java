package com.example.communityinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communityinfo.Admins.ActivitiesAdmin.PrincipalAdmin;
import com.example.communityinfo.Users.ActivitiesUser.Principal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Login extends AppCompatActivity {
    Button btn_login;
    TextView txt_noCuenta;
    EditText editTextEmail, editTextPassword, editTextCif;
    FirebaseAuth miAuth;
    FirebaseFirestore miDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();

        // Asignación de id del xml con variables locales
        btn_login = findViewById(R.id.btn_iniciosesion);
        txt_noCuenta = findViewById(R.id.txt_sincuenta);
        editTextEmail = findViewById(R.id.editxt_email);
        editTextPassword = findViewById(R.id.edittxt_password);
        editTextCif = findViewById(R.id.edittxt_cif);

        // Si la cuenta está registrada se redirige a la vista Principal
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Al hacer click, si no tiene una cuenta lo redirige al Registro
        txt_noCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
                finish();
            }
        });
    }

    private void iniciarSesion() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cif = editTextCif.getText().toString().trim();

        if (!validarCampos(email, password, cif)) return;

        autenticarUsuario(email, password, cif);
    }

    private boolean validarCampos(String email, String password, String cif) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cif)) {
            Toast.makeText(Login.this, "Rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void autenticarUsuario(String email, String password, String cif) {
        miAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Inicio de Sesión Exitoso", Toast.LENGTH_SHORT).show();
                            guardarCifEnArchivo(cif);
                            verificarRolUsuario();
                        } else {
                            Toast.makeText(Login.this, "Error. Intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarCifEnArchivo(String cif) {
        File directory = new File(getFilesDir(), "Database");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory, "ComunidadCif.txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(cif);
            System.out.println("Archivo creado o sobrescrito con éxito.");
        } catch (IOException e) {
            System.err.println("Ocurrió un error al escribir en el archivo: " + e.getMessage());
        }
    }

    private void verificarRolUsuario() {
        FirebaseUser user = miAuth.getCurrentUser();
        String uid = user.getUid();

        miDb.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            redirigirUsuarioSegunRol(rol);
                        } else {
                            Toast.makeText(Login.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirigirUsuarioSegunRol(String rol) {
        if ("admin".equals(rol)) {
            // Redirigir a la vista de administrador
            startActivity(new Intent(Login.this, PrincipalAdmin.class));
        } else {
            // Redirigir a la vista de usuario
            startActivity(new Intent(Login.this, Principal.class));
        }
        finish();
    }
}