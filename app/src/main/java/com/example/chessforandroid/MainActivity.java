package com.example.chessforandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/***
 *
 *
 * TAREA ACTUAL:
 * gestionar postpartida en la bbdd
 *
 *
 * BUGS CONOCIDOS:
 * servidor crashea cuando cierras la app en medio de la partida y quieres volver a jugar
 *
 * IDEAS:
 * pausar partida guardando la posición de las piezas
 *
 * TAREAS EXTRA:
 * ver tabla con usuarios con más ELO / más nivel
 * cambiar contraseña
 * hash contraseñas
 * cifrado
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button offline;
    private Button online;
    private Button redLocal;
    private String user;
    private String token;
    private Cliente c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        token = intent.getStringExtra("token");

        offline = findViewById(R.id.bOffline);
        online = findViewById(R.id.bJugarOnline);
        redLocal = findViewById(R.id.bRedLocal);

        offline.setOnClickListener(this);
        online.setOnClickListener(this);
        redLocal.setOnClickListener(this);
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
            case R.id.menu_perfil:
                if (user.length() > 0) {
                    c = new Cliente();
                    if (c.isConectado()){
                        c.pedirDatos(this, user);
                    }
                    return true;
                }
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_ajustes:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;

            case R.id.menu_acercade:
                startActivity(new Intent(this, AboutUsActivity.class));
                 break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bOffline:
                Intent offlineIntent = new Intent(this, OfflineActivity.class);
                startActivity(offlineIntent);
                break;
            case R.id.bRedLocal:
                if (user.length() > 0) {
                    Intent redLocalIntent = new Intent(this, LocalLobbyActivity.class);
                    redLocalIntent.putExtra("token", token);
                    startActivity(redLocalIntent);
                    break;
                }
                startActivity(new Intent(this, LoginActivity.class));

            case R.id.bJugarOnline:
                if (user.length() > 0) {
                    Intent onlineIntent = new Intent(this, OnlineActivity.class);
                    onlineIntent.putExtra("token", token);
                    startActivity(onlineIntent);
                    break;
                }
                startActivity(new Intent(this, LoginActivity.class));
        }
    }
}