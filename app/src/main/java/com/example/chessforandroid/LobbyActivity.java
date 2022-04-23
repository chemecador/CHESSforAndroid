package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LobbyActivity extends AppCompatActivity {

    private String tokenAnf;
    private int idPartida;
    private TextView txt;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        Intent intent = getIntent();
        tokenAnf = intent.getStringExtra("token");
        idPartida = intent.getIntExtra("id", -1);
        pb = findViewById(R.id.progressBar);

        txt = findViewById(R.id.txtLobby);
        txt.setText("Tu código es " + idPartida);

        if (idPartida == -1){
            Toast.makeText(this, "Ha habido un error en la creación de la sala", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}