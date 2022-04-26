package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LocalLobbyActivity extends AppCompatActivity {

    private String token;
    private Cliente c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_lobby);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        c = new Cliente();
        if (c.isConectado()){
            c.local(this, token);
        }
    }
}