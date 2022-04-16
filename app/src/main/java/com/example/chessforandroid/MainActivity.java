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
 *
 * DUDAS:
 * excesivo uso del return (DosJugadoresActivity)

 *
 * BUGS CONOCIDOS:


 * imposibilidad de iniciar sesión en el segundo intento (si fallas el primero o entras sin iniciar
   sesión, se congela la pantalla)


 * TAREAS EXTRA:
 * ver tabla con usuarios con más ELO / más nivel
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button dosJugadores;
    private Button online;
    private String user;
    private Cliente c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        if (user.length() > 0) {
            c = new Cliente();
        }


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
                if (user.length() > 0) {
                    int[] datos = c.pedirDatos(user);
                    Intent i = new Intent(this, PerfilActivity.class);
                    i.putExtra("user", user);
                    i.putExtra("datos",datos);
                    startActivity(i); //lanzo la Activity de preferencias
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
                if (user.length() > 0) {
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