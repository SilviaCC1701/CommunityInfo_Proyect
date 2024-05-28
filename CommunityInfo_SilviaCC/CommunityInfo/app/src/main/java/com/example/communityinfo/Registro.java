package com.example.communityinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private TextView txt_cuentaCreada;
    private Button btn_registrarse;
    private EditText editTextName, editTextNameUser, editTextEmail, editTextPassword, editTextCifComunity;
    private FirebaseAuth miAuth;
    private FirebaseFirestore miDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Obtengo Instance de Firebase Authentication y Firestore
        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();

        // Asignación de id del xml con variables locales
        txt_cuentaCreada = findViewById(R.id.txtview_cuentacreada);
        btn_registrarse = findViewById(R.id.btn_registrarse_register);
        editTextName = findViewById(R.id.edittxt_name);
        editTextNameUser = findViewById(R.id.edittxt_user);
        editTextEmail = findViewById(R.id.edittxt_emailregister);
        editTextPassword = findViewById(R.id.edittxt_passwordRegister);
        editTextCifComunity = findViewById(R.id.edittxt_cifComunity);

        // Metodo al hacer click, si ya tiene una cuenta se redirige a Login
        txt_cuentaCreada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registro.this, Login.class));
                finish();
            }
        });

        // Al hacer click intenta crear una cuenta con los datos del campo y se redirige a Login
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String nombre = editTextName.getText().toString().trim();
        String nombreUsuario = editTextNameUser.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cifComunidad = editTextCifComunity.getText().toString().trim();

        if (!validarCampos(nombre, nombreUsuario, email, password, cifComunidad)) return;

        verificarComunidad(nombre, nombreUsuario, email, password, cifComunidad);
    }

    private boolean validarCampos(String nombre, String nombreUsuario, String email, String password, String cifComunidad) {
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(nombreUsuario) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cifComunidad)) {
            Toast.makeText(Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Registro.this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void verificarComunidad(String nombre, String nombreUsuario, String email, String password, String cifComunidad) {
        miDb.collection("comunidades").document(cifComunidad).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        verificarResidente(nombre, nombreUsuario, email, password, cifComunidad);
                    } else {
                        Toast.makeText(Registro.this, "Comunidad no encontrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Registro.this, "Error al verificar la comunidad", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarResidente(String nombre, String nombreUsuario, String email, String password, String cifComunidad) {
        miDb.collection("comunidades").document(cifComunidad)
                .collection("residentes")
                .whereEqualTo("nombreUsuario", nombreUsuario)
                .whereEqualTo("email", email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot residenteDoc = task.getResult().getDocuments().get(0);
                                if (TextUtils.isEmpty(residenteDoc.getString("uid_user"))) {
                                    registrarUsuarioEnFirebaseAuth(nombre, nombreUsuario, email, password, cifComunidad, residenteDoc.getId());
                                } else {
                                    Toast.makeText(Registro.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Registro.this, "Residente no encontrado o correo electrónico no coincide", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Registro.this, "Error al verificar el residente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registrarUsuarioEnFirebaseAuth(String nombre, String nombreUsuario, String email, String password, String cifComunidad, String residenteId) {
        miAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = miAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        guardarInformacionAdicionalEnFirestore(nombre, nombreUsuario, email, cifComunidad, uid, residenteId);
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Registro.this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Registro.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void guardarInformacionAdicionalEnFirestore(String nombre, String nombreUsuario, String email, String cifComunidad, String uid, String residenteId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("nombreUsuario", nombreUsuario);
        userData.put("CifComunidad", cifComunidad);
        userData.put("email", email);
        userData.put("rol", "user"); // Asumiendo que el rol por defecto es "user"

        miDb.collection("users").document(uid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    actualizarUidEnResidentes(cifComunidad, uid, residenteId);
                } else {
                    Toast.makeText(Registro.this, "Error al registrar el usuario en Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUidEnResidentes(String cifComunidad, String uid, String residenteId) {
        miDb.collection("comunidades").document(cifComunidad)
                .collection("residentes").document(residenteId)
                .update("uid_user", uid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registro.this, "Registro completado", Toast.LENGTH_SHORT).show();
                            // redirige a la vista de Login
                            startActivity(new Intent(Registro.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Registro.this, "Error al actualizar el usuario en la comunidad", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}