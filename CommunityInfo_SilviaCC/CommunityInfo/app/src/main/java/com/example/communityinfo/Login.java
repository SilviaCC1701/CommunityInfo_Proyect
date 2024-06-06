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
import com.google.firebase.firestore.QuerySnapshot;

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

    // Guardo CifSelected y VerificoRolUsuario
    private void autenticarUsuario(String email, String password, String cif) {
        miAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            guardarCifEnArchivo(cif);
                            verificarRolUsuario(email, cif);
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
            // Archivo creado o sobrescrito con éxito
            fileWriter.write(cif);
        } catch (IOException e) {
            System.err.println("Ocurrió un error al escribir en el archivo: " + e.getMessage());
        }
    }

    private void verificarRolUsuario(String email, String cifSelected) {
        FirebaseUser user = miAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(Login.this, "Error al obtener usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String miUid = user.getUid();

        miDb.collection("users").document(miUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            redirigirUsuarioSegunTipoRol(miUid, email, rol, cifSelected);
                            //verificarResidente(miUid, email, rol, cifSelected);
                        } else {
                            Toast.makeText(Login.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                            cerrarSesion();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                        cerrarSesion();
                    }
                });
    }

    private void verificarResidente(String miUid, String email, String rol, String cifSeleccionado) {
        String cifSelected = cifSeleccionado;

        // Verificamos si la persona pertenece a la comunidad indicada.
        miDb.collection("comunidades").document(cifSelected)
                .collection("residentes")
                .whereEqualTo("uid_user", miUid)
                .whereEqualTo("email", email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot residenteDoc = task.getResult().getDocuments().get(0);
                                if (!TextUtils.isEmpty(residenteDoc.getString("uid_user"))) {
                                    Toast.makeText(Login.this, "Bienvenido Usuario", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, Principal.class));
                                } else {
                                    Toast.makeText(Login.this, "El usuario no está registrado", Toast.LENGTH_SHORT).show();
                                    cerrarSesion();
                                }
                            } else {
                                Toast.makeText(Login.this, "Residente no encontrado o no está registrado", Toast.LENGTH_SHORT).show();
                                cerrarSesion();
                            }
                        } else {
                            Toast.makeText(Login.this, "Error al obtener datos de verificación", Toast.LENGTH_SHORT).show();
                            cerrarSesion();
                        }
                    }
                });
    }

    private void redirigirUsuarioSegunTipoRol(String miUid, String email, String rol, String cifSeleccionado) {
        if ("admin".equals(rol)) {
            // Redirigir a la vista de administrador
            Toast.makeText(Login.this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, PrincipalAdmin.class));
        } else {
            // Verifica que es un residente y redirigir a la vista de usuario
            verificarResidente(miUid, email, rol, cifSeleccionado);
        }
        finish();
    }

    private void cerrarSesion() {
        miAuth.signOut();
        Intent intent = new Intent(Login.this, Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}