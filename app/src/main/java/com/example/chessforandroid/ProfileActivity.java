package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ProfileActivity. Muestra la informacion de la cuenta de un usuario.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView victorias, nivel;
    private String user;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button cerrarSesion, cambiarPass;
        ImageButton ibMisiones;
        TextView username, elo, jugadas, tablas, derrotas;


        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        user = intent.getStringExtra("user");

        // se recogen los datos del intent
        int[] datos = intent.getIntArrayExtra("datos");

        username = findViewById(R.id.pUser);
        nivel = findViewById(R.id.pNivel);
        elo = findViewById(R.id.pElo);
        jugadas = findViewById(R.id.pJugadas);
        victorias = findViewById(R.id.pGanadas);
        derrotas = findViewById(R.id.pDerrotas);
        tablas = findViewById(R.id.pTablas);

        username.setText(user);
        nivel.setText(String.valueOf(datos[0]));
        elo.setText(String.valueOf(datos[1]));
        jugadas.setText(String.valueOf(datos[2]));
        victorias.setText(String.valueOf(datos[3]));
        derrotas.setText(String.valueOf(datos[4]));
        tablas.setText(String.valueOf(datos[5]));

        cambiarPass = findViewById(R.id.bCambiarPass);
        cambiarPass.setOnClickListener(this);

        cerrarSesion = findViewById(R.id.bCerrarSesion);
        cerrarSesion.setOnClickListener(this);

        ibMisiones = findViewById(R.id.ibLogros);
        ibMisiones.setOnClickListener(this);

        Button personalizar = findViewById(R.id.bPersonalizarTablero);
        personalizar.setOnClickListener(this);
        if (Integer.parseInt(nivel.getText().toString()) > 1){
            personalizar.setBackgroundColor(Color.parseColor("#358C0E"));
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bCambiarPass) {
            Intent i = new Intent(this, PasswordActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        }

        if (view.getId() == R.id.bCerrarSesion) {
            Toast.makeText(ProfileActivity.this, "cerrando sesión...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (view.getId() == R.id.ibLogros) {
            Intent i = new Intent(this, AchievementsActivity.class);
            i.putExtra("token", token);
            i.putExtra("victorias", victorias.getText().toString());
            startActivity(i);
        }
        if (view.getId() == R.id.bPersonalizarTablero) {
           if (Integer.parseInt(nivel.getText().toString()) > 1){
               startActivity(new Intent(this, CustomActivity.class));
           } else {
               Toast.makeText(this, R.string.must_reach_level_2, Toast.LENGTH_SHORT).show();
            }

        }

    }
}