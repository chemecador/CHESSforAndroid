package com.example.chessforandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/***
 *
 * DUDAS:
 * ¿buenas idea el sistema de misiones?
 * ¿base de datos para iniciar sesión? (contraseñas hash, etc - ¿sqlite? - ¿en el servidor de java?)
 * ¿problema al jugar dos usuarios de distinta wifi?
 *
 * IDEAS:
 * gestionar registro e inicio de sesión para guardar nivel, elo y editar perfil
 * ver tabla con usuarios con más ELO / más nivel
 * botón de recordar contraseña con 4 preguntas definidas desplegables con una respuesta correcta (¿mayor seguridad con nº tel, email, ... ?)
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button dosJugadores;
    private Button online;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        isLogged = intent.getBooleanExtra("isLogged", false);

        dosJugadores = findViewById(R.id.bMain2P);
        online = findViewById(R.id.bMainOnline);

        dosJugadores.setOnClickListener(this);
        online.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Método que se ejecuta cuando se va a crear la barra de menú
         * de la activity (la barra superior)
         */
        getMenuInflater().inflate(R.menu.main_menu, menu);//le digo al inflater correspondiente cual es el menú que debe 'inflar'
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*Método que se ejecuta cuando el usuario selecciona un elemento
         * de la barra de menú de la aplicación*/
        switch (item.getItemId()) {/*Según la opción seleccionada ejecutaré un código u otro*/
            case R.id.menu_preferencias:
                if (isLogged) {
                    startActivity(new Intent(this, PerfilActivity.class)); //lanzo la Activity de preferencias
                    return true;
                }
                startActivity(new Intent(this, LoginActivity.class));
                return true;

            case R.id.menu_acercade:
                Toast.makeText(this, "Aplicación desarrollada por Alejandro Gata"
                        , Toast.LENGTH_LONG).show();//Creo un 'Toast', un texto que aparecerá en pantalla durante un instante y lo muestro
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bMain2P:
                Intent dosJugadoresIntent = new Intent(this, DosJugadoresActivity.class);
                startActivity(dosJugadoresIntent);
                break;
            case R.id.bMainOnline:
                if (isLogged) {
                    Toast.makeText(getApplicationContext(),
                            "Función no disponible :(",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }
    }
}