package com.example.communityinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.communityinfo.Admins.ActivitiesAdmin.PrincipalAdmin;
import com.example.communityinfo.Users.ActivitiesUser.Principal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore miDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();

        // Verifica si el usuario ya esta logeado
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null){
            // Ya hay una cuenta iniciada y lo manda a la actividad principal
            verificarRolUsuario(firebaseUser.getUid());
            /*Intent intent=new Intent(getApplicationContext(), Principal.class);
            startActivity(intent);
            finish();*/
        }else {
            // No hay ninguna cuenta iniciada y lo lleva al inicio de la app
            Intent intent = new Intent(MainActivity.this, Inicio.class);
            startActivity(intent);
            finish();
        }
    }

    private void verificarRolUsuario(String uid) {
        miDb.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            redirigirUsuarioSegunRol(rol);
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Inicio.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Inicio.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void redirigirUsuarioSegunRol(String rol) {
        if ("admin".equals(rol)) {
            // Redirigir a la vista de administrador
            Toast.makeText(MainActivity.this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, PrincipalAdmin.class));
        } else {
            // Redirigir a la vista de usuario
            startActivity(new Intent(MainActivity.this, Principal.class));
        }
        finish();
    }
}