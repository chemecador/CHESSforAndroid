package com.example.chessforandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chessforandroid.util.Cliente;

/*
 *
 * TAREAS:
 *
 * -recreate() para volver a empezar la partida
 * -Mudar la base de datos a pg si es necesario
 * -Cuando juegas online con negras, que las negras aparezcan abajo
 * -Desarrollar algoritmo de jaque mate
 * -Un único socket
 * -Solucionar el problema de que cuando entra al catch, el servidor no puede atender a más peticiones
 *
 *
 */



/**
 * MainActivity. Activity principal donde el usuario decide lo que quiere hacer en la app.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Nombre del usuario que esta usando la aplicacion
     */
    private String user;

    /**
     * Token identificativo del usuario que esta usando la aplicacion
     */
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        token = intent.getStringExtra("token");

        Button offline = findViewById(R.id.bOffline);
        Button friend = findViewById(R.id.bPlayFriend);
        Button mm = findViewById(R.id.bMatchmaking);
        Button ranking = findViewById(R.id.bRanking);

        offline.setOnClickListener(this);
        friend.setOnClickListener(this);
        mm.setOnClickListener(this);
        ranking.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // el inflater pinta el menu introducido como parametro
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_perfil:
                if (user.length() > 0) {
                    Cliente cliente = new Cliente();
                    if (cliente.isConectado()) {
                        cliente.pedirDatos(this, user, token);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bOffline:
                Intent offlineIntent = new Intent(this, OfflineActivity.class);
                startActivity(offlineIntent);
                break;

            case R.id.bMatchmaking:
                if (user.length() > 0) {
                    Intent redLocalIntent = new Intent(this, OnlineWaitingActivity.class);
                    redLocalIntent.putExtra("token", token);
                    startActivity(redLocalIntent);
                    break;
                }
                startActivity(new Intent(this, LoginActivity.class));

            case R.id.bPlayFriend:
                if (user.length() > 0) {
                    Intent friendIntent = new Intent(this, FriendActivity.class);
                    friendIntent.putExtra("token", token);
                    startActivity(friendIntent);
                    break;
                }
                startActivity(new Intent(this, LoginActivity.class));

            case R.id.bRanking:
                if (user.length() > 0) {
                    Intent rankingIntent = new Intent(this, RankingActivity.class);
                    rankingIntent.putExtra("token", token);
                    startActivity(rankingIntent);
                    break;
                } else {
                    Toast.makeText(this, "Inicia sesión para ver la clasificación", Toast.LENGTH_SHORT).show();
                }
        }
    }
}