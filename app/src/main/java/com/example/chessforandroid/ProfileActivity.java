package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView victorias;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button cerrarSesion, cambiarPass;
        ImageButton ibMisiones;
        TextView username, nivel, elo, jugadas, tablas, derrotas;


        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        int[] datos = intent.getIntArrayExtra("datos");

        username =  findViewById(R.id.pUser);
        nivel =  findViewById(R.id.pNivel);
        elo =  findViewById(R.id.pElo);
        jugadas =  findViewById(R.id.pJugadas);
        victorias =  findViewById(R.id.pGanadas);
        derrotas =  findViewById(R.id.pDerrotas);
        tablas =  findViewById(R.id.pTablas);

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

        ibMisiones = findViewById(R.id.ibMisiones);
        ibMisiones.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bCambiarPass){
            Intent i = new Intent(this, PasswordActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        }
        if (view.getId() == R.id.bCerrarSesion){
            Toast.makeText(ProfileActivity.this, "cerrando sesión...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view.getId() == R.id.ibMisiones){
            Intent i = new Intent(this, AchievementsActivity.class);
            i.putExtra("user", user);
            i.putExtra("victorias", victorias.getText().toString());
            startActivity(i);
        }

    }
}