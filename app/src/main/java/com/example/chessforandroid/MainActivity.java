package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button twoPlayers;
    Button online;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twoPlayers = findViewById(R.id.bMain2P);
        online = findViewById(R.id.bMainOnline);

        twoPlayers.setOnClickListener(this);
        online.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bMain2P:
                Toast.makeText(getApplicationContext(),
                        "Función no disponible :(",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.bMainOnline:
                Toast.makeText(getApplicationContext(),
                        "Función tampoco disponible :(",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}