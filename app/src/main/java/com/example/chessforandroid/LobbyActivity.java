package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class LobbyActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        Intent intent = getIntent();
        int idPartida = intent.getIntExtra("id", -1);

        TextView txt = findViewById(R.id.txtLobby);
        txt.setText("Tu código es " + idPartida);

        if (idPartida == -1){
            Toast.makeText(this, "Ha habido un error en la creación de la sala", Toast.LENGTH_SHORT).show();
            finish();
        }
        Cliente c = new Cliente();
        if (!c.isConectado()) {
            Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        } /*else {
            //c.lobby(this, tokenAnf, idPartida);
        }*/
    }
}