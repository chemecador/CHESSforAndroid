package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.util.Cliente;
import com.example.chessforandroid.util.Constantes;

/**
 * Activity que se lanza cuando un usuario ha creado sala y esta esperando a su amigo.
 */
public class FriendWaitingActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_waiting);


        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        int idPartida = intent.getIntExtra("id", -1);

        TextView txt = findViewById(R.id.txtLobby);
        txt.setText(R.string.your_code_is + " " + idPartida);

        if (idPartida == -1) {
            Toast.makeText(this, "Ha habido un error en la creación de la sala", Toast.LENGTH_SHORT).show();
            finish();
        }

        Cliente c = new Cliente(Constantes.PUERTO_PARTIDA);

        if (c.isConectado()) {
            c.esperarAmigo(this, token);
        } else {
            Toast.makeText(this, "Error al esperar amigo", Toast.LENGTH_SHORT).show();
        }
    }
}