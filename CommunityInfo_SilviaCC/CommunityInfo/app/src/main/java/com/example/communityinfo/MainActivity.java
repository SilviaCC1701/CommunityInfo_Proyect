package com.example.communityinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creo un hilo mientras se esta ejecutando esta actividad a modo de splash screen
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Verifica si el usuario ya esta logeado
                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                if(firebaseUser!=null){
                    //Ya hay una cuenta iniciada y lo manda a la actividad principal
                    Intent intent=new Intent(getApplicationContext(), Principal.class);
                    startActivity(intent);
                    finish();
                }else{
                    //No hay ninguna cuenta iniciada y lo lleva al inicio de la app
                    Intent intent=new Intent(MainActivity.this, Inicio.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }
}