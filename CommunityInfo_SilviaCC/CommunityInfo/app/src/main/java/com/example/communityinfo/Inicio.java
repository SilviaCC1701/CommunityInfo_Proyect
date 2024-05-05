package com.example.communityinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Inicio extends AppCompatActivity {
    Button btn_Login, btn_Register;
    TextView nombre, eslogan, desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Asignación de id del xml con variables locales
        btn_Login=findViewById(R.id.btn_iniciosesion);
        btn_Register=findViewById(R.id.btn_registrarse);
        nombre=findViewById(R.id.txtview_nombre);
        eslogan=findViewById(R.id.txtview_descripcion_inicio);
        desc=findViewById(R.id.textview_desc);

        // Al hacer click, redirige "Login"
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, Login.class));
                finish();
            }
        });

        // Al hacer click, redirige "Register"
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, Registro.class));
                finish();
            }
        });
    }
}