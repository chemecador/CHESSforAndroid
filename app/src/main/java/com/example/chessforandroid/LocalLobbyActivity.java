package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LocalLobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_lobby);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        Cliente c = new Cliente();
        if (c.isConectado()){
            c.local(this, token);
            //finish(); con callback
        } else {
            Log.i ("************************", "error");
        }
    }
}