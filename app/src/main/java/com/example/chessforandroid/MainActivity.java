package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/***
 *
 * DUDAS:
 * ¿es buena idea Casilla extends ImageButton?
 * ¿cómo añadir los listeners?
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button dosJugadores;
    private Button online;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dosJugadores = findViewById(R.id.bMain2P);
        online = findViewById(R.id.bMainOnline);

        dosJugadores.setOnClickListener(this);
        online.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bMain2P:
                Intent dosJugadoresIntent = new Intent(this, DosJugadoresActivity.class);
                startActivity(dosJugadoresIntent);
                break;
            case R.id.bMainOnline:
                Toast.makeText(getApplicationContext(),
                        "Función no disponible :(",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}