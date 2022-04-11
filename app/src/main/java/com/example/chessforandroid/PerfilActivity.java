package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.cliente.Cliente;

public class PerfilActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cerrarSesion;
    private TextView username, nivel, elo, jugadas, victorias, tablas, derrotas;
    private String user;
    private int[] datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        datos = intent.getIntArrayExtra("datos");

        username = (TextView) findViewById(R.id.pUser);
        nivel = (TextView) findViewById(R.id.pNivel);
        elo = (TextView) findViewById(R.id.pElo);
        jugadas = (TextView) findViewById(R.id.pJugadas);
        victorias = (TextView) findViewById(R.id.pGanadas);
        derrotas = (TextView) findViewById(R.id.pDerrotas);
        tablas = (TextView) findViewById(R.id.pTablas);
        username.setText(user);
        nivel.setText(String.valueOf(datos[0]));
        elo.setText(String.valueOf(datos[1]));
        jugadas.setText(String.valueOf(datos[2]));
        victorias.setText(String.valueOf(datos[3]));
        derrotas.setText(String.valueOf(datos[4]));
        tablas.setText(String.valueOf(datos[5]));

        cerrarSesion = findViewById(R.id.bCerrarSesion);
        cerrarSesion.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Toast.makeText(PerfilActivity.this, "cerrando sesión...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }
}